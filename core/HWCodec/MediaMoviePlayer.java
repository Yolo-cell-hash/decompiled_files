package com.eseeiot.core.HWCodec;

import android.annotation.TargetApi;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CodecException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import androidx.annotation.NonNull;
import com.eseeiot.core.pojo.FrameBuffer;
import com.eseeiot.core.pojo.MediaBufferQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaMoviePlayer {
   private static final boolean DEBUG = true;
   private static final String TAG_STATIC = "MediaMoviePlayer:";
   private static final int TIMEOUT_USEC = 1000;
   private static final int STATE_STOP = 0;
   private static final int STATE_PREPARED = 1;
   private static final int STATE_PLAYING = 2;
   private static final int STATE_PAUSED = 3;
   private static final int REQ_NON = 0;
   private static final int REQ_PREPARE = 1;
   private static final int REQ_START = 2;
   private static final int REQ_SEEK = 3;
   private static final int REQ_STOP = 4;
   private static final int REQ_PAUSE = 5;
   private static final int REQ_RESUME = 6;
   private static final int REQ_QUIT = 9;
   private static final String TAG_VIDEO = "video";
   private static final String TAG_AUDIO = "audio";
   private final String TAG = "MediaMoviePlayer:" + this.getClass().getSimpleName();
   private final IFrameCallback mCallback;
   private final boolean mAudioEnabled;
   private final Object mSync = new Object();
   private final Object mVideoSync = new Object();
   private final Surface mOutputSurface;
   private final Object mAudioSync = new Object();
   protected MediaMetadataRetriever mMetadata;
   private boolean mPlayAudio = false;
   private volatile boolean mIsRunning;
   private int mState;
   private String mSourcePath;
   private long mDuration;
   private long mVideoStartTime;
   private int mVideoWidth;
   private int mVideoHeight;
   private int mBitrate;
   private float mFrameRate;
   private int mRotation;
   private long mAudioStartTime;
   private int mAudioChannels;
   private int mAudioSampleRate;
   private int mAudioInputBufSize;
   private AudioTrack mAudioTrack;
   private MediaHelper mVideoHelper;
   private MediaHelper mAudioHelper;
   private MediaMoviePlayer.InputThread mAudioInputThread;
   private long mSeekTimeUs;
   private long mTimeToSeek;
   private int mDecodeSomeFrame;
   private int mNotSyncCount = 0;
   private long lastVideoTimeUs = -1L;
   private final Runnable mPlayTask = new Runnable() {
      public void run() {
         while(MediaMoviePlayer.this.checkPlayState()) {
            if (MediaMoviePlayer.this.mVideoHelper.trackIndex >= 0 && (MediaMoviePlayer.this.mDecodeSomeFrame > 0 || MediaMoviePlayer.this.mVideoHelper.waitOver()) && !MediaMoviePlayer.this.handleMedia(MediaMoviePlayer.this.mVideoHelper)) {
               if (MediaMoviePlayer.this.mCallback != null) {
                  MediaMoviePlayer.this.mCallback.onFrameAvailable(-1L, false);
               }
               break;
            }

            if (MediaMoviePlayer.this.mAudioHelper.trackIndex >= 0 && (MediaMoviePlayer.this.mDecodeSomeFrame > 0 || MediaMoviePlayer.this.mAudioHelper.waitOver()) && !MediaMoviePlayer.this.handleMedia(MediaMoviePlayer.this.mAudioHelper)) {
               if (MediaMoviePlayer.this.mCallback != null) {
                  MediaMoviePlayer.this.mCallback.onFrameAvailable(-1L, false);
               }
               break;
            }

            if (MediaMoviePlayer.this.mDecodeSomeFrame == 0) {
               if (MediaMoviePlayer.this.mVideoHelper.trackIndex >= 0 && MediaMoviePlayer.this.mAudioHelper.trackIndex >= 0 && !MediaMoviePlayer.this.mVideoHelper.outputDone && !MediaMoviePlayer.this.mAudioHelper.outputDone && !MediaMoviePlayer.this.mVideoHelper.inputDone && !MediaMoviePlayer.this.mAudioHelper.inputDone) {
                  MediaMoviePlayer.this.syncMedia();
               } else if (MediaMoviePlayer.this.mVideoHelper.inputDone && !MediaMoviePlayer.this.mAudioHelper.inputDone) {
                  MediaMoviePlayer.this.mAudioHelper.inputDone = true;
               }

               MediaMoviePlayer.this.preRender();
            }
         }

         MediaMoviePlayer.this.mVideoHelper.release();
         MediaMoviePlayer.this.mAudioHelper.release();
      }
   };

   public MediaMoviePlayer(Surface outputSurface, IFrameCallback callback, boolean audio_enable) throws NullPointerException {
      Log.v(this.TAG, "Constructor:");
      if (outputSurface != null && callback != null) {
         this.mSeekTimeUs = -1L;
         this.mTimeToSeek = -1L;
         this.mOutputSurface = outputSurface;
         this.mCallback = callback;
         this.mAudioEnabled = audio_enable;
         this.mState = 0;
         this.mVideoHelper = new MediaHelper();
         this.mVideoHelper.tag = "video";
         this.mAudioHelper = new MediaHelper();
         this.mAudioHelper.tag = "audio";
      } else {
         throw new NullPointerException("outputSurface and callback should not be null");
      }
   }

   protected static final int selectTrack(MediaExtractor extractor, String mimeType) {
      int numTracks = extractor.getTrackCount();

      for(int i = 0; i < numTracks; ++i) {
         MediaFormat format = extractor.getTrackFormat(i);
         String mime = format.getString("mime");
         if (mime.startsWith(mimeType)) {
            Log.d("MediaMoviePlayer:", "Extractor selected track " + i + " (" + mime + "): " + format);
            return i;
         }
      }

      return -1;
   }

   public final int getWidth() {
      return this.mVideoWidth;
   }

   public final int getHeight() {
      return this.mVideoHeight;
   }

   public final int getBitRate() {
      return this.mBitrate;
   }

   public final float getFramerate() {
      return this.mFrameRate;
   }

   public final int getRotation() {
      return this.mRotation;
   }

   public final long getDurationUs() {
      return this.mDuration;
   }

   public final int getSampleRate() {
      return this.mAudioSampleRate;
   }

   public final boolean hasAudio() {
      return this.mAudioHelper.trackIndex >= 0;
   }

   public final void prepare(String src_movie) {
      Log.v(this.TAG, "prepare:");
      boolean prepare = false;
      synchronized(this.mSync) {
         if (this.mState == 0) {
            try {
               this.handlePrepare(src_movie);
               prepare = true;
               this.mState = 1;
               this.mSync.notifyAll();
            } catch (Exception var6) {
               prepare = false;
            }
         }
      }

      if (prepare) {
         this.mCallback.onPrepared();
      } else {
         this.mCallback.onFrameAvailable(-3L, false);
      }

   }

   public void EnabledAudio(boolean value) {
      synchronized(this.mSync) {
         Log.d(this.TAG, "EnabledAudio: ------->" + value);
         this.mPlayAudio = value;
      }
   }

   public final void play() {
      Log.v(this.TAG, "play:");
      boolean handleStart = false;
      synchronized(this.mSync) {
         if (this.mState == 1) {
            this.mState = 2;
            handleStart = true;
            this.mSync.notifyAll();
         }
      }

      if (handleStart) {
         this.handleStart();
      }

   }

   public final void seek(long newTime) {
      Log.v(this.TAG, "seek");
      synchronized(this.mSync) {
         if (newTime >= 0L) {
            this.mSeekTimeUs = newTime;
            this.mSync.notifyAll();
         }

      }
   }

   public final void release() {
      Log.v(this.TAG, "release:");
      this.stop();
      this.mCallback.onFinished();
   }

   public final void stop() {
      Log.v(this.TAG, "stop:");
      synchronized(this.mSync) {
         if (this.mState != 0) {
            this.handleStop();
            this.mState = 0;
            this.mSync.notifyAll();
         }

      }
   }

   public final void pause() {
      Log.v(this.TAG, "pasuse:");
      synchronized(this.mSync) {
         if (this.mState == 2) {
            this.mState = 3;
            this.mSync.notifyAll();
         }

      }
   }

   public final void resume() {
      Log.v(this.TAG, "resume:");
      synchronized(this.mSync) {
         if (this.mState != 3) {
            throw new RuntimeException("invalid state:" + this.mState);
         } else {
            this.mState = 2;
            this.mSync.notifyAll();
         }
      }
   }

   private boolean handleMedia(MediaHelper helper) {
      try {
         if (!helper.inputDone) {
            this.handleInputMedia(helper);
         }

         if (!helper.outputDone) {
            this.handleOutputMedia(helper);
         }

         return true;
      } catch (IllegalStateException var3) {
         return false;
      }
   }

   private void handleInputMedia(MediaHelper helper) throws IllegalStateException {
      boolean advanceSuccess = true;
      long sampleTimeUs = helper.extractor.getSampleTime();
      if (this.mDecodeSomeFrame > 0) {
         Log.d(this.TAG, "handleInputMedia: seekTimeUs = " + this.mTimeToSeek + ", sampleTimeUs = " + sampleTimeUs);
      }

      if (this.mTimeToSeek >= 0L) {
         helper.seekSampleTimeUs = sampleTimeUs;
      }

      if (sampleTimeUs == -1L) {
         helper.inputDone = true;
         if (this.mTimeToSeek >= 0L) {
            helper.outputDone = true;
         }

         if (helper.tag.equals("video") && this.mCallback != null) {
            this.mCallback.onFrameAvailable(-2L, false);
         }

         Log.d(this.TAG, "handleInputMedia: tag " + helper.tag + ", inputDone = " + helper.inputDone + ", outputDone = " + helper.outputDone);
      } else {
         while(true) {
            int inputBufIndex = helper.codec.dequeueInputBuffer(1000L);
            if (advanceSuccess && inputBufIndex == -1) {
               break;
            }

            if (inputBufIndex >= 0) {
               int sampleSize = 0;
               if (advanceSuccess) {
                  sampleSize = helper.extractor.readSampleData(helper.inputBuffers[inputBufIndex], 0);
               }

               if (sampleSize > 0) {
                  helper.codec.queueInputBuffer(inputBufIndex, 0, sampleSize, sampleTimeUs, 0);
                  advanceSuccess = helper.extractor.advance();
               } else {
                  helper.codec.queueInputBuffer(inputBufIndex, 0, 0, 0L, 4);
                  if (!advanceSuccess) {
                     advanceSuccess = true;
                     helper.inputDone = true;
                     Log.d(this.TAG, "handleInputMedia: sampleTimeUs = " + sampleTimeUs);
                  }
               }

               if (advanceSuccess) {
                  break;
               }
            }
         }

      }
   }

   private void handleOutputMedia(MediaHelper helper) throws IllegalStateException {
      boolean doRender = false;

      while(true) {
         int decodeStatus = helper.codec.dequeueOutputBuffer(helper.bufferInfo, 1000L);
         switch(decodeStatus) {
         case -3:
            helper.outputBuffers = helper.codec.getOutputBuffers();
            break;
         case -2:
            MediaFormat var4 = helper.codec.getOutputFormat();
            break;
         case -1:
            return;
         default:
            if (decodeStatus < 0) {
               return;
            }

            Log.d(this.TAG, "handleOutputMedia: tag = " + helper.tag);
            if (helper.bufferInfo.size > 0) {
               if (helper.tag.equals("video")) {
                  if (this.mDecodeSomeFrame > 0) {
                     Log.d(this.TAG, "handleOutputMedia: presentationTimeUs = " + helper.bufferInfo.presentationTimeUs);
                     --this.mDecodeSomeFrame;
                     if (this.mDecodeSomeFrame == 0) {
                        doRender = true;
                     }
                  } else {
                     doRender = true;
                  }

                  if (doRender && this.mCallback != null) {
                     this.mCallback.onFrameAvailable(helper.bufferInfo.presentationTimeUs, false);
                  }
               } else {
                  if (this.mState == 2) {
                     this.internal_write_audio(helper.outputBuffers[decodeStatus], 0, helper.bufferInfo.size, helper.bufferInfo.presentationTimeUs);
                  }

                  if (this.mCallback != null) {
                     this.mCallback.onFrameAvailable(helper.bufferInfo.presentationTimeUs, true);
                  }
               }
            }

            if (helper.seekSampleTimeUs >= 0L) {
               helper.seekSampleTimeUs = -1L;
            }

            helper.codec.releaseOutputBuffer(decodeStatus, doRender);
            helper.markOutput();
            if ((helper.bufferInfo.flags & 4) != 0) {
               helper.outputDone = true;
               helper.reset(true);
               Log.d(this.TAG, "handleOutputMedia: output done " + helper.tag);
            }
         }
      }
   }

   private void handleSeek() {
      if (this.mVideoHelper.extractor != null) {
         this.mVideoHelper.inputDone = false;
         this.mVideoHelper.outputDone = false;
         this.mVideoHelper.start();
         this.mVideoHelper.extractor.seekTo(this.mSeekTimeUs, 2);
         this.mVideoHelper.extractor.advance();

         try {
            this.mVideoHelper.codec.flush();
         } catch (CodecException var4) {
            var4.printStackTrace();
         } catch (IllegalStateException var5) {
            var5.printStackTrace();
         }
      }

      if (this.mAudioHelper.extractor != null) {
         this.mAudioHelper.inputDone = false;
         this.mAudioHelper.outputDone = false;
         this.mAudioHelper.start();
         this.mAudioHelper.extractor.seekTo(this.mSeekTimeUs, 2);
         this.mAudioHelper.extractor.advance();
         this.mAudioHelper.codec.flush();
         if (this.mAudioInputThread != null) {
            try {
               this.mAudioInputThread.flush();
            } catch (CodecException var2) {
               var2.printStackTrace();
            } catch (IllegalStateException var3) {
               var3.printStackTrace();
            }
         }
      }

      this.mTimeToSeek = this.mSeekTimeUs;
      this.mSeekTimeUs = -1L;
      this.mNotSyncCount = 0;
   }

   private boolean checkPlayState() {
      synchronized(this.mSync) {
         switch(this.mState) {
         case 0:
            return false;
         case 3:
            if (this.mSeekTimeUs == -1L && this.mDecodeSomeFrame == 0) {
               this.mVideoHelper.reset(true);
               this.mAudioHelper.reset(true);
               Log.d(this.TAG, "checkPlayState: pause audio");
               if (this.mAudioTrack != null) {
                  this.mAudioTrack.stop();
               }

               try {
                  this.mSync.wait();
               } catch (InterruptedException var4) {
               }

               if (this.mState == 0) {
                  return false;
               } else {
                  Log.d(this.TAG, "checkPlayState: play audio");
                  if (this.mAudioTrack != null) {
                     this.mAudioTrack.play();
                  }
               }
            }
         case 1:
         case 2:
         default:
            if (this.mTimeToSeek >= 0L) {
               this.mTimeToSeek = -1L;
            }

            if (this.mSeekTimeUs >= 0L) {
               this.handleSeek();
               if (this.mState == 3) {
                  this.mDecodeSomeFrame = 30;
               }
            }

            return true;
         }
      }
   }

   public void syncMedia() {
      if (this.mVideoHelper.seekSampleTimeUs < 0L && this.mAudioHelper.seekSampleTimeUs < 0L) {
         long diff = Math.abs(this.mVideoHelper.bufferInfo.presentationTimeUs - this.mAudioHelper.bufferInfo.presentationTimeUs);
         if (diff > 180000L) {
            if (this.lastVideoTimeUs >= 0L) {
            }

            ++this.mNotSyncCount;
            if (this.mNotSyncCount > 100) {
               if (this.mVideoHelper.bufferInfo.presentationTimeUs > this.mAudioHelper.bufferInfo.presentationTimeUs) {
                  Log.d(this.TAG, "syncMedia: pause video");
                  this.mVideoHelper.pause();
                  this.mAudioHelper.start();
               } else {
                  Log.d(this.TAG, "syncMedia: pause audio");
                  this.mVideoHelper.start();
                  this.mAudioHelper.pause();
               }
            }
         } else if (diff < 60000L) {
            this.mNotSyncCount = 0;
            if (!this.mVideoHelper.isStart()) {
               Log.d(this.TAG, "syncMedia: resume video");
               this.mVideoHelper.start();
            } else if (!this.mAudioHelper.isStart()) {
               Log.d(this.TAG, "syncMedia: resume audio");
               this.mAudioHelper.start();
            }
         }

      }
   }

   private void preRender() {
      byte waitFlag = 0;
      long waitTimeMs = 0L;
      long waitTimeNs = 0L;
      long[] videoWaitTime = this.mVideoHelper.getWaitTime();
      long[] audioWaitTime = this.mAudioHelper.getWaitTime();
      if (videoWaitTime == null) {
         waitTimeMs = audioWaitTime[0];
         waitTimeNs = audioWaitTime[1];
      } else if (audioWaitTime == null) {
         waitTimeMs = videoWaitTime[0];
         waitTimeNs = videoWaitTime[1];
      } else {
         waitTimeMs = Math.min(videoWaitTime[0], audioWaitTime[0]);
         if (videoWaitTime[0] == audioWaitTime[0]) {
            waitTimeNs = Math.min(videoWaitTime[1], audioWaitTime[1]);
            waitFlag = 3;
         } else if (waitTimeMs == videoWaitTime[0]) {
            waitTimeNs = videoWaitTime[1];
            waitFlag = 1;
         } else {
            waitTimeNs = audioWaitTime[1];
            waitFlag = 2;
         }
      }

      if (waitTimeMs <= 0L && waitTimeNs <= 0L) {
         if (this.mVideoHelper.outputDone && this.mAudioHelper.outputDone) {
            try {
               Thread.sleep(40L);
            } catch (InterruptedException var9) {
               var9.printStackTrace();
            }
         }
      } else {
         Log.d(this.TAG, "preRender: sleep " + waitTimeMs + "ms, " + waitTimeNs + "ns");

         try {
            Thread.sleep(waitTimeMs, (int)waitTimeNs);
         } catch (InterruptedException var10) {
            var10.printStackTrace();
         }

         if ((waitFlag & 3) == 3) {
            this.mVideoHelper.reset(false);
            this.mAudioHelper.reset(false);
         } else if ((waitFlag & 1) == 1) {
            this.mVideoHelper.reset(false);
            this.mAudioHelper.updateMark(waitTimeMs, (int)waitTimeNs);
         } else if ((waitFlag & 2) == 2) {
            this.mVideoHelper.updateMark(waitTimeMs, (int)waitTimeNs);
            this.mAudioHelper.reset(false);
         }
      }

   }

   private final void handlePrepare(String source_file) throws Exception {
      Log.v(this.TAG, "handlePrepare:" + source_file);
      File src = new File(source_file);
      if (!TextUtils.isEmpty(source_file) && src.canRead()) {
         this.mMetadata = new MediaMetadataRetriever();
         this.mMetadata.setDataSource(source_file);
         this.updateMovieInfo();
         this.internal_prepare_video(source_file);
         if (this.mAudioEnabled) {
            this.internal_prepare_audio(source_file);
         }

         if (this.mVideoHelper.trackIndex < 0 && this.mAudioHelper.trackIndex < 0) {
            throw new RuntimeException("No video and audio track found in " + source_file);
         }
      } else {
         throw new FileNotFoundException("Unable to read " + source_file);
      }
   }

   @TargetApi(16)
   protected int internal_prepare_video(String source_path) {
      int trackindex = true;
      this.mVideoHelper.extractor = new MediaExtractor();

      int trackindex;
      try {
         this.mVideoHelper.extractor.setDataSource(source_path);
         trackindex = selectTrack(this.mVideoHelper.extractor, "video/");
         if (trackindex >= 0) {
            this.mVideoHelper.extractor.selectTrack(trackindex);
            MediaFormat format = this.mVideoHelper.extractor.getTrackFormat(trackindex);

            try {
               this.mVideoWidth = format.getInteger("width");
               this.mVideoHeight = format.getInteger("height");
               this.mDuration = format.getLong("durationUs");
               int frameRate = format.getInteger("frame-rate");
               Log.d(this.TAG, "internal_prepare_video: ---->" + frameRate);
            } catch (NullPointerException var5) {
               var5.printStackTrace();
            }

            Log.v(this.TAG, String.format("format:size(%d,%d),duration=%d,bps=%d,framerate=%f,rotation=%d", this.mVideoWidth, this.mVideoHeight, this.mDuration, this.mBitrate, this.mFrameRate, this.mRotation));
         }
      } catch (IOException var6) {
         trackindex = -1;
      }

      this.mVideoHelper.trackIndex = trackindex;
      if (trackindex < 0) {
         this.mVideoHelper.extractor.release();
         this.mVideoHelper.extractor = null;
      }

      return trackindex;
   }

   protected int internal_prepare_audio(String source_file) {
      int trackindex = true;
      this.mAudioHelper.extractor = new MediaExtractor();

      int trackindex;
      try {
         this.mAudioHelper.extractor.setDataSource(source_file);
         trackindex = selectTrack(this.mAudioHelper.extractor, "audio/");
         if (trackindex >= 0) {
            this.mAudioHelper.extractor.selectTrack(trackindex);
            MediaFormat format = this.mAudioHelper.extractor.getTrackFormat(trackindex);
            Log.d(this.TAG, "internal_prepare_audio: format = " + format);
            this.mAudioChannels = format.getInteger("channel-count");
            this.mAudioSampleRate = format.getInteger("sample-rate");
            int min_buf_size = AudioTrack.getMinBufferSize(this.mAudioSampleRate, this.mAudioChannels == 1 ? 4 : 12, 2);
            this.mAudioInputBufSize = min_buf_size;
         }
      } catch (IOException var5) {
         trackindex = -1;
      }

      this.mAudioHelper.trackIndex = trackindex;
      if (trackindex < 0) {
         this.mAudioHelper.extractor.release();
         this.mAudioHelper.extractor = null;
      }

      return trackindex;
   }

   protected void updateMovieInfo() {
      this.mVideoWidth = this.mVideoHeight = this.mRotation = this.mBitrate = 0;
      this.mDuration = 0L;
      this.mFrameRate = 0.0F;
      String value = this.mMetadata.extractMetadata(18);
      if (!TextUtils.isEmpty(value)) {
         this.mVideoWidth = Integer.parseInt(value);
      }

      value = this.mMetadata.extractMetadata(19);
      if (!TextUtils.isEmpty(value)) {
         this.mVideoHeight = Integer.parseInt(value);
      }

      value = this.mMetadata.extractMetadata(24);
      if (!TextUtils.isEmpty(value)) {
         this.mRotation = Integer.parseInt(value);
      }

      value = this.mMetadata.extractMetadata(20);
      if (!TextUtils.isEmpty(value)) {
         this.mBitrate = Integer.parseInt(value);
      }

      value = this.mMetadata.extractMetadata(9);
      if (!TextUtils.isEmpty(value)) {
         this.mDuration = Long.parseLong(value) * 1000L;
      }

   }

   private final void handleStart() {
      Log.v(this.TAG, "handleStart:");
      MediaCodec codec;
      if (this.mVideoHelper.trackIndex >= 0) {
         codec = this.internal_start_video(this.mVideoHelper.extractor, this.mVideoHelper.trackIndex);
         if (codec == null) {
            this.mVideoHelper.extractor.release();
            this.mVideoHelper.extractor = null;
            this.mVideoHelper.trackIndex = -1;
            if (this.mAudioHelper.extractor != null) {
               this.mAudioHelper.extractor.release();
               this.mAudioHelper.extractor = null;
               this.mAudioHelper.trackIndex = -1;
            }

            return;
         }

         this.mVideoHelper.codec = codec;
         this.mVideoHelper.bufferInfo = new BufferInfo();
         this.mVideoHelper.inputBuffers = codec.getInputBuffers();
         this.mVideoHelper.outputBuffers = codec.getOutputBuffers();
         this.mVideoHelper.inputDone = false;
         this.mVideoHelper.outputDone = false;
         this.mVideoHelper.start();
      }

      if (this.mAudioHelper.trackIndex >= 0) {
         codec = this.internal_start_audio(this.mAudioHelper.extractor, this.mAudioHelper.trackIndex);
         if (codec != null) {
            this.mAudioHelper.codec = codec;
            this.mAudioHelper.bufferInfo = new BufferInfo();
            this.mAudioHelper.inputBuffers = codec.getInputBuffers();
            this.mAudioHelper.outputBuffers = codec.getOutputBuffers();
            this.mAudioHelper.inputDone = false;
            this.mAudioHelper.outputDone = false;
            this.mAudioHelper.start();
         } else {
            this.mAudioHelper.extractor.release();
            this.mAudioHelper.extractor = null;
            this.mAudioHelper.trackIndex = -1;
         }
      }

      if (this.mVideoHelper.trackIndex >= 0 || this.mAudioHelper.trackIndex >= 0) {
         (new Thread(this.mPlayTask)).start();
      }

   }

   @TargetApi(21)
   protected MediaCodec internal_start_video(MediaExtractor media_extractor, int trackIndex) {
      Log.v(this.TAG, "internal_start_video:");
      MediaCodec codec = null;
      if (trackIndex >= 0) {
         MediaFormat format = media_extractor.getTrackFormat(trackIndex);
         String mime = format.getString("mime");

         try {
            codec = MediaCodec.createDecoderByType(mime);
            codec.configure(format, this.mOutputSurface, (MediaCrypto)null, 0);
            codec.start();
            Log.v(this.TAG, "internal_start_video:codec started");
         } catch (IOException var7) {
            var7.printStackTrace();
            codec = null;
         } catch (CodecException var8) {
            if (this.mCallback != null) {
               this.mCallback.onFrameAvailable(-1L, false);
            }

            codec = null;
         }
      }

      return codec;
   }

   protected MediaCodec internal_start_audio(MediaExtractor media_extractor, int trackIndex) {
      Log.v(this.TAG, "internal_start_audio:");
      MediaCodec codec = null;
      if (trackIndex >= 0) {
         MediaFormat format = media_extractor.getTrackFormat(trackIndex);
         String mime = format.getString("mime");
         long duration = -1L;

         try {
            duration = format.getLong("durationUs");
         } catch (NullPointerException var11) {
         }

         if (duration == -1L || duration > 0L) {
            try {
               codec = MediaCodec.createDecoderByType(mime);
               codec.configure(format, (Surface)null, (MediaCrypto)null, 0);
               codec.start();
               Log.v(this.TAG, "internal_start_audio:codec started");
               ByteBuffer[] buffers = codec.getOutputBuffers();
               int sz = buffers[0].capacity();
               if (sz <= 0) {
                  sz = this.mAudioInputBufSize;
               }

               Log.v(this.TAG, "AudioOutputBufSize:" + sz);
            } catch (IOException var10) {
               var10.printStackTrace();
               codec = null;
            }
         }
      }

      return codec;
   }

   protected boolean internal_write_audio(ByteBuffer buffer, int offset, int size, long presentationTimeUs) {
      Log.d(this.TAG, "checkPlayState: write audio");
      if (this.mPlayAudio) {
         byte[] outputBuf = new byte[size];
         buffer.position(offset);
         buffer.get(outputBuf, 0, size);
         buffer.clear();
         if (this.mAudioInputThread != null) {
            this.mAudioInputThread.add(outputBuf, size);
         } else if (this.mCallback != null) {
            this.mCallback.OnAudioData(outputBuf);
         }
      }

      return true;
   }

   protected long adjustPresentationTime(Object sync, long startTime, long presentationTimeUs) {
      if (startTime <= 0L) {
         return System.nanoTime() / 1000L;
      } else {
         for(long t = presentationTimeUs - (System.nanoTime() / 1000L - startTime); t > 0L; t = presentationTimeUs - (System.nanoTime() / 1000L - startTime)) {
            synchronized(sync) {
               try {
                  sync.wait(t / 1000L, (int)(t % 1000L * 1000L));
               } catch (InterruptedException var11) {
               }

               if (this.mState == 4 || this.mState == 9) {
                  break;
               }
            }
         }

         return startTime;
      }
   }

   private final void handleStop() {
      Log.v(this.TAG, "handleStop:");
      this.internal_stop_video();
      this.internal_stop_audio();
      if (this.mMetadata != null) {
         this.mMetadata.release();
         this.mMetadata = null;
      }

   }

   protected void internal_stop_video() {
      Log.v(this.TAG, "internal_stop_video:");
   }

   protected void internal_stop_audio() {
      Log.v(this.TAG, "internal_stop_audio:");
      if (this.mAudioInputThread != null) {
         this.mAudioInputThread.release();
      }

      if (this.mAudioTrack != null) {
         if (this.mAudioTrack.getState() != 0) {
            this.mAudioTrack.stop();
         }

         this.mAudioTrack.release();
         this.mAudioTrack = null;
      }

   }

   private static class InputThread extends Thread {
      private boolean isRunning = true;
      private AudioTrack audioTrack;
      private MediaBufferQueue bufferQueue;

      public InputThread(@NonNull AudioTrack audioTrack) {
         this.audioTrack = audioTrack;
         this.bufferQueue = new MediaBufferQueue();
      }

      public void release() {
         synchronized(this.bufferQueue) {
            this.isRunning = false;
            this.bufferQueue.flush();
            this.interrupt();
         }
      }

      public void add(byte[] data, int dateLen) {
         FrameBuffer tmpBuffer = new FrameBuffer();
         tmpBuffer.setData(data);
         tmpBuffer.setDataLength(dateLen);
         synchronized(this.bufferQueue) {
            this.bufferQueue.add(tmpBuffer);
            this.bufferQueue.notifyAll();
         }
      }

      public void flush() {
         this.audioTrack.flush();
         synchronized(this.bufferQueue) {
            this.bufferQueue.flush();
         }
      }

      public void run() {
         super.run();
         FrameBuffer tmpBuffer = null;

         while(this.isRunning) {
            synchronized(this.bufferQueue) {
               if (this.bufferQueue.isEmpty()) {
                  try {
                     this.bufferQueue.wait();
                  } catch (InterruptedException var5) {
                     var5.printStackTrace();
                  }
                  continue;
               }

               tmpBuffer = this.bufferQueue.poll();
            }

            if (tmpBuffer != null) {
               this.audioTrack.write(tmpBuffer.getData(), 0, tmpBuffer.getDataLength());
               tmpBuffer = null;
            }
         }

      }
   }
}
