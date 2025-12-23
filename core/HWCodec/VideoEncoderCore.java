package com.eseeiot.core.HWCodec;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CodecException;
import android.util.Log;
import android.view.Surface;
import androidx.annotation.RequiresApi;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoEncoderCore {
   public static final int SAMPLE_RATE = 8000;
   public static final int SAMPLES_PER_FRAME = 1024;
   public static final int CHANNEL_CONFIG = 16;
   public static final int AUDIO_FORMAT = 2;
   private static final String TAG = "honglee_0704";
   private static final boolean VERBOSE = true;
   private static final String MIME_TYPE = "video/avc";
   private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
   private static final int FRAME_RATE = 25;
   private static final int IFRAME_INTERVAL = 2;
   boolean firstFrameReady = false;
   boolean audioEosRequested = false;
   long startWhen;
   boolean eosSentToAudioEncoder = false;
   boolean haveAudioData = false;
   int frameCount = 0;
   boolean eosSentToVideoEncoder = false;
   boolean firstRun = true;
   int audioInputLength;
   long audioAbsolutePtsUs;
   long startPTS = 0L;
   long totalSamplesNum = 0L;
   private Surface mInputSurface;
   private VideoEncoderCore.MediaMuxerWrapper mMuxerWrapper;
   private BufferInfo mVideoBufferInfo;
   private BufferInfo mAudioBufferInfo;
   private VideoEncoderCore.TrackInfo mVideoTrackInfo;
   private VideoEncoderCore.TrackInfo mAudioTrackInfo;
   private MediaCodec mVideoEncoder;
   private MediaCodec mAudioEncoder;
   private MediaFormat mVideoFormat;
   private MediaFormat mAudioFormat;
   private MediaFormat mVideoOutputFormat;
   private MediaFormat mAudioOutputFormat;
   private boolean fullStopReceived = false;
   private long lastEncodedAudioTimeStamp = 0L;

   @RequiresApi(
      api = 21
   )
   public VideoEncoderCore(int width, int height, int bitRate, File outputFile) throws IOException {
      this.eosSentToVideoEncoder = false;
      this.mVideoBufferInfo = new BufferInfo();
      this.mVideoTrackInfo = new VideoEncoderCore.TrackInfo();
      Log.d("honglee_0704", "createVideoFormat: width = " + width + ", height = " + height);
      this.mVideoEncoder = MediaCodec.createEncoderByType("video/avc");
      this.createVideoFormat(width, height, bitRate, width / 10, height / 10);
      this.mInputSurface = this.mVideoEncoder.createInputSurface();
      this.mVideoEncoder.start();
      this.mAudioBufferInfo = new BufferInfo();
      this.mAudioTrackInfo = new VideoEncoderCore.TrackInfo();
      this.mAudioFormat = new MediaFormat();
      this.mAudioFormat.setString("mime", "audio/mp4a-latm");
      this.mAudioFormat.setInteger("aac-profile", 2);
      this.mAudioFormat.setInteger("sample-rate", 8000);
      this.mAudioFormat.setInteger("channel-count", 1);
      this.mAudioFormat.setInteger("bitrate", 64000);
      this.mAudioFormat.setInteger("max-input-size", 16384);
      this.mAudioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
      this.mAudioEncoder.configure(this.mAudioFormat, (Surface)null, (MediaCrypto)null, 1);
      this.mAudioEncoder.start();
      this.mMuxerWrapper = new VideoEncoderCore.MediaMuxerWrapper(outputFile, 0);
      this.mVideoTrackInfo.index = -1;
      this.mVideoTrackInfo.muxerWrapper = this.mMuxerWrapper;
      this.mAudioTrackInfo.index = -1;
      this.mAudioTrackInfo.muxerWrapper = this.mMuxerWrapper;
   }

   private void createVideoFormat(int width, int height, int bitRate, int widthRoot, int heightRoot) {
      this.mVideoFormat = MediaFormat.createVideoFormat("video/avc", width, height);
      this.mVideoFormat.setInteger("color-format", 2130708361);
      this.mVideoFormat.setInteger("bitrate", bitRate);
      this.mVideoFormat.setInteger("frame-rate", 25);
      this.mVideoFormat.setInteger("i-frame-interval", 2);
      Log.d("honglee_0704", "format: " + this.mVideoFormat);

      try {
         this.mVideoEncoder.configure(this.mVideoFormat, (Surface)null, (MediaCrypto)null, 1);
         Log.d("honglee_0704", "createVideoFormat: width = " + width + ", height = " + height);
      } catch (CodecException var7) {
         this.createVideoFormat(width - widthRoot, height - heightRoot, bitRate, widthRoot, heightRoot);
      }

   }

   public Surface getInputSurface() {
      return this.mInputSurface;
   }

   public void stopRecording() {
      this.fullStopReceived = true;
      this.drainEncoder(this.mVideoEncoder, this.mVideoBufferInfo, this.mVideoTrackInfo, this.fullStopReceived);
      ByteBuffer buf = ByteBuffer.allocate(2048);
      this.sendAudioToEncoder(buf.array());
      this.drainEncoder(this.mAudioEncoder, this.mAudioBufferInfo, this.mAudioTrackInfo, this.fullStopReceived);
   }

   public void startRecording() {
      this.fullStopReceived = false;
      this.haveAudioData = false;
      if (this.firstRun) {
         this.setupAudioRecord();
         this.startAudioRecord();
         this.firstFrameReady = true;
         this.startWhen = System.nanoTime();
         this.firstRun = false;
      }

      try {
         this.drainEncoder(this.mVideoEncoder, this.mVideoBufferInfo, this.mVideoTrackInfo, this.fullStopReceived);
      } catch (NullPointerException var2) {
         var2.printStackTrace();
      }

   }

   private void setupAudioRecord() {
   }

   public void P_drainEncoder(boolean video, boolean endstream) {
      if (video) {
         this.drainEncoder(this.mVideoEncoder, this.mVideoBufferInfo, this.mVideoTrackInfo, endstream);
      } else {
         if (!this.firstFrameReady) {
            return;
         }

         synchronized(this.mAudioTrackInfo.muxerWrapper.sync) {
            this.drainEncoder(this.mAudioEncoder, this.mAudioBufferInfo, this.mAudioTrackInfo, this.fullStopReceived);
         }
      }

   }

   private void startAudioRecord() {
      this.drainEncoder(this.mAudioEncoder, this.mAudioBufferInfo, this.mAudioTrackInfo, false);
   }

   public void sendAudioToEncoder(byte[] buffer) {
      boolean endOfStream = this.fullStopReceived;
      this.haveAudioData = true;

      try {
         ByteBuffer[] inputBuffers = this.mAudioEncoder.getInputBuffers();
         int inputBufferIndex = this.mAudioEncoder.dequeueInputBuffer(-1L);
         if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.position(0);
            inputBuffer.put(buffer);
            this.audioInputLength = buffer.length;
            this.audioAbsolutePtsUs = System.nanoTime() / 1000L;
            this.audioAbsolutePtsUs = this.getJitterFreePTS(this.audioAbsolutePtsUs, (long)(this.audioInputLength / 2));
            if (this.audioInputLength == -3) {
               Log.e("honglee_0704", "Audio read error: invalid operation");
            }

            if (this.audioInputLength == -2) {
               Log.e("honglee_0704", "Audio read error: bad value");
            }

            if (endOfStream) {
               Log.i("honglee_0704", "EOS received in sendAudioToEncoder");
               this.mAudioEncoder.queueInputBuffer(inputBufferIndex, 0, this.audioInputLength, this.audioAbsolutePtsUs, 4);
               this.eosSentToAudioEncoder = true;
            } else {
               this.mAudioEncoder.queueInputBuffer(inputBufferIndex, 0, this.audioInputLength, this.audioAbsolutePtsUs, 0);
            }
         }
      } catch (Throwable var6) {
         Log.e("honglee_0704", "_offerAudioEncoder exception");
         var6.printStackTrace();
      }

   }

   private long getJitterFreePTS(long bufferPts, long bufferSamplesNum) {
      long correctedPts = 0L;
      long bufferDuration = 1000000L * bufferSamplesNum / 8000L;
      bufferPts -= bufferDuration;
      if (this.totalSamplesNum == 0L) {
         this.startPTS = bufferPts;
         this.totalSamplesNum = 0L;
      }

      correctedPts = this.startPTS + 1000000L * this.totalSamplesNum / 8000L;
      if (bufferPts - correctedPts >= 2L * bufferDuration) {
         this.startPTS = bufferPts;
         this.totalSamplesNum = 0L;
         correctedPts = this.startPTS;
      }

      this.totalSamplesNum += bufferSamplesNum;
      return correctedPts;
   }

   public void release() {
      Log.d("honglee_0704", "releasing encoder objects");
      this.mMuxerWrapper.stop();
   }

   private void drainEncoder(MediaCodec encoder, BufferInfo bufferInfo, VideoEncoderCore.TrackInfo trackInfo, boolean endOfStream) {
      int TIMEOUT_USEC = true;
      if (encoder != null) {
         VideoEncoderCore.MediaMuxerWrapper muxerWrapper = trackInfo.muxerWrapper;
         Log.d("honglee_0704", "drain" + (encoder == this.mVideoEncoder ? "Video" : "Audio") + "Encoder(" + endOfStream + ")");
         if (endOfStream && encoder == this.mVideoEncoder) {
            Log.d("honglee_0704", "sending EOS to " + (encoder == this.mVideoEncoder ? "video" : "audio") + " encoder");
            if (encoder != null) {
               encoder.signalEndOfInputStream();
            }

            this.eosSentToVideoEncoder = true;
         }

         ByteBuffer[] encoderOutputBuffers = null;

         try {
            encoderOutputBuffers = encoder.getOutputBuffers();
         } catch (Exception var12) {
            var12.printStackTrace();
         }

         while(true) {
            boolean var8 = true;

            int encoderStatus;
            try {
               encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, 100L);
            } catch (Exception var14) {
               var14.printStackTrace();
               return;
            }

            if (encoderStatus == -1) {
               if (!endOfStream) {
                  Log.d("honglee_0704", "no output available. aborting drain");
               } else {
                  if (encoder != this.mVideoEncoder) {
                     continue;
                  }

                  muxerWrapper.finishTrack();
                  Log.d("honglee_0704", "end of " + (encoder == this.mVideoEncoder ? " video" : " audio") + " stream reached. ");
                  if (!this.fullStopReceived) {
                     continue;
                  }

                  if (encoder == this.mVideoEncoder) {
                     Log.i("honglee_0704", "Stopping and releasing video encoder");
                     this.stopAndReleaseVideoEncoder();
                  } else if (encoder == this.mAudioEncoder) {
                     Log.i("honglee_0704", "Stopping and releasing audio encoder");
                     this.stopAndReleaseAudioEncoder();
                  }
               }
            } else {
               if (encoderStatus == -3) {
                  try {
                     encoderOutputBuffers = encoder.getOutputBuffers();
                  } catch (Exception var11) {
                     var11.printStackTrace();
                  }
                  continue;
               }

               if (encoderStatus == -2) {
                  if (muxerWrapper.started) {
                     continue;
                  }

                  MediaFormat newFormat = encoder.getOutputFormat();
                  if (encoder == this.mVideoEncoder) {
                     this.mVideoOutputFormat = newFormat;
                  } else if (encoder == this.mAudioEncoder) {
                     this.mAudioOutputFormat = newFormat;
                  }

                  trackInfo.index = muxerWrapper.addTrack(newFormat);
                  if (muxerWrapper.allTracksAdded()) {
                     continue;
                  }
               } else {
                  if (encoderStatus < 0) {
                     Log.w("honglee_0704", "unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
                     continue;
                  }

                  if (encoderOutputBuffers == null) {
                     continue;
                  }

                  ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                  if (encodedData == null) {
                     throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                  }

                  if ((bufferInfo.flags & 2) != 0) {
                     Log.d("honglee_0704", "ignoring BUFFER_FLAG_CODEC_CONFIG");
                     bufferInfo.size = 0;
                  }

                  if (bufferInfo.size != 0) {
                     if (!trackInfo.muxerWrapper.started) {
                        Log.e("honglee_0704", "Muxer not started. dropping " + (encoder == this.mVideoEncoder ? " video" : " audio") + " frames");
                     } else {
                        encodedData.position(bufferInfo.offset);
                        encodedData.limit(bufferInfo.offset + bufferInfo.size);
                        if (encoder == this.mAudioEncoder) {
                           if (bufferInfo.presentationTimeUs < this.lastEncodedAudioTimeStamp) {
                              bufferInfo.presentationTimeUs = this.lastEncodedAudioTimeStamp += 23219L;
                           }

                           this.lastEncodedAudioTimeStamp = bufferInfo.presentationTimeUs;
                        }

                        if (bufferInfo.presentationTimeUs < 0L) {
                           bufferInfo.presentationTimeUs = 0L;
                        }

                        Log.d("honglee_0704", "trackInfo.index:" + trackInfo.index + "-----" + bufferInfo);
                        muxerWrapper.muxer.writeSampleData(trackInfo.index, encodedData, bufferInfo);
                        Log.d("honglee_0704", "sent " + bufferInfo.size + (encoder == this.mVideoEncoder ? " video" : " audio") + " bytes to muxer with pts " + bufferInfo.presentationTimeUs);
                     }
                  }

                  try {
                     encoder.releaseOutputBuffer(encoderStatus, false);
                  } catch (Exception var13) {
                     var13.printStackTrace();
                     continue;
                  }

                  if ((bufferInfo.flags & 4) == 0) {
                     continue;
                  }

                  if (!endOfStream) {
                     Log.w("honglee_0704", "reached end of stream unexpectedly");
                  } else {
                     Log.d("honglee_0704", "end of " + (encoder == this.mVideoEncoder ? " video" : " audio") + " stream reached. ");
                     if (this.fullStopReceived) {
                        muxerWrapper.finishTrack();
                        if (encoder == this.mVideoEncoder) {
                           Log.i("honglee_0704", "Stopping and releasing video encoder");
                           this.stopAndReleaseVideoEncoder();
                        } else if (encoder == this.mAudioEncoder) {
                           Log.i("honglee_0704", "Stopping and releasing audio encoder");
                           this.stopAndReleaseAudioEncoder();
                        }
                     }
                  }
               }
            }

            return;
         }
      }
   }

   private void stopAndReleaseVideoEncoder() {
      this.eosSentToVideoEncoder = false;
      this.frameCount = 0;
      if (this.mVideoEncoder != null) {
         try {
            this.mVideoEncoder.stop();
         } catch (IllegalStateException var2) {
            var2.printStackTrace();
         }

         this.mVideoEncoder.release();
         this.mVideoEncoder = null;
      }

   }

   private void stopAndReleaseAudioEncoder() {
      this.lastEncodedAudioTimeStamp = 0L;
      this.eosSentToAudioEncoder = false;
      if (this.mAudioEncoder != null) {
         this.mAudioEncoder.stop();
         this.mAudioEncoder.release();
      }

   }

   class MediaMuxerWrapper {
      final int TOTAL_NUM_TRACKS = 2;
      MediaMuxer muxer;
      boolean started = false;
      int numTracksAdded = 0;
      int numTracksFinished = 0;
      Object sync = new Object();

      public MediaMuxerWrapper(File outputFile, int format) {
         this.restart(outputFile, format);
      }

      public int TrackCount() {
         return this.numTracksAdded;
      }

      public int addTrack(MediaFormat format) {
         ++this.numTracksAdded;
         int trackIndex = this.muxer.addTrack(format);
         if (this.numTracksAdded == 2) {
            Log.i("honglee_0704", "All tracks added, starting " + (this == VideoEncoderCore.this.mMuxerWrapper ? "muxer1" : "muxer2") + "!");
            this.muxer.start();
            this.started = true;
         }

         return trackIndex;
      }

      public void finishTrack() {
         ++this.numTracksFinished;
         if (this.numTracksFinished == 2) {
            Log.i("honglee_0704", "All tracks finished, stopping " + (this == VideoEncoderCore.this.mMuxerWrapper ? "muxer1" : "muxer2") + "!");
            this.stop();
         }

      }

      public boolean allTracksAdded() {
         return this.numTracksAdded == 2;
      }

      public boolean allTracksFinished() {
         return this.numTracksFinished == 2;
      }

      public void stop() {
         if (this.muxer != null) {
            try {
               if (!this.allTracksFinished()) {
                  Log.e("honglee_0704", "Stopping Muxer before all tracks added!");
               }

               if (!this.started) {
                  Log.e("honglee_0704", "Stopping Muxer before it was started");
               }

               this.muxer.stop();
               this.muxer.release();
               this.muxer = null;
               this.started = false;
               this.numTracksAdded = 0;
               this.numTracksFinished = 0;
            } catch (Exception var2) {
               var2.printStackTrace();
            }
         }

      }

      private void restart(File outputFile, int format) {
         this.stop();

         try {
            this.muxer = new MediaMuxer(outputFile.toString(), format);
         } catch (IOException var4) {
            throw new RuntimeException("MediaMuxer creation failed", var4);
         }
      }
   }

   class TrackInfo {
      int index = 0;
      VideoEncoderCore.MediaMuxerWrapper muxerWrapper;
   }
}
