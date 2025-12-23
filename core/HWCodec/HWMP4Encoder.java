package com.eseeiot.core.HWCodec;

import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import com.eseeiot.basemodule.listener.RecordCallback;
import com.eseeiot.core.HWCodec.gles.EglCore;
import com.eseeiot.core.HWCodec.gles.WindowSurface;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class HWMP4Encoder {
   private static final String TAG = "HWMP4Encoder";
   private final float[] mIdentityMatrix;
   private boolean mRecordingEnabled = false;
   public int mWidth = 0;
   public int mHeight = 0;
   private WindowSurface mInputWindowSurface = null;
   private TextureMovieEncoder2 mVideoEncoder = null;
   private EglCore mEglCore = null;
   private EGLDisplay mDisplay = null;
   private EGLContext mContext = null;
   private EGLSurface mSurface = null;
   private FullFrameRect mFullScreen = null;
   private VideoEncoderCore encoderCore;
   private long mTimeStampNanos = 0L;
   private ByteBuffer mAudioBuffer = ByteBuffer.allocate(2048);
   private int mAudioPosition = 0;
   private File mOutputFile = null;
   private int mChannel;
   private long mStartTime;
   private RecordCallback mCallBack;

   public HWMP4Encoder() {
      this.mDisplay = EGL14.eglGetDisplay(0);
      this.mContext = EGL14.eglGetCurrentContext();
      this.mSurface = EGL14.eglGetCurrentSurface(12378);
      this.mIdentityMatrix = new float[16];
      Matrix.setIdentityM(this.mIdentityMatrix, 0);
   }

   public void setCallBack(RecordCallback callback) {
      this.mCallBack = callback;
   }

   public boolean isRecording() {
      return this.mRecordingEnabled;
   }

   public void startRecord(int w, int h, String filename, int channel, EglCore core) {
      this.startRecord(w, h, filename, channel, core, true);
   }

   public void startRecord(int w, int h, String filename, int channel, EglCore core, boolean align) {
      this.mChannel = channel;
      this.mEglCore = core;
      if (align) {
         h = (h / 16 + 1) * 16;
         w = (w / 16 + 1) * 16;
      }

      this.mOutputFile = new File(filename);

      try {
         Log.d("HWMP4Encoder", "StartRecord: ----->" + h + "-----" + w);
         this.encoderCore = new VideoEncoderCore(w, h, 2048000, this.mOutputFile);
      } catch (IOException var9) {
         throw new RuntimeException(var9);
      }

      this.mInputWindowSurface = new WindowSurface(this.mEglCore, this.encoderCore.getInputSurface(), true);
      int height = this.mInputWindowSurface.getHeight();
      int width = this.mInputWindowSurface.getWidth();
      if (width > 0 && height > 0) {
         this.mWidth = width;
         this.mHeight = height;
      } else {
         this.mWidth = w;
         this.mHeight = h;
      }

      this.mVideoEncoder = new TextureMovieEncoder2(this.encoderCore);
      this.mTimeStampNanos = System.nanoTime();
      this.mInputWindowSurface.makeCurrent();
      this.mFullScreen = new FullFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D));
      this.encoderCore.startRecording();
      this.mRecordingEnabled = true;
      this.mStartTime = System.currentTimeMillis();
      if (this.mCallBack != null) {
         this.mCallBack.onRecordStart();
      }

   }

   public void writeFrame(int texid, HWMP4Encoder.FrameDrawListener listener) {
      if (this.mInputWindowSurface != null && listener != null) {
         this.mVideoEncoder.frameAvailableSoon();
         this.mInputWindowSurface.makeCurrent();
         GLES20.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
         GLES20.glClear(16384);
         GLES20.glEnable(3042);
         GLES20.glBlendFunc(770, 771);
         GLES20.glViewport(0, 0, this.mWidth, this.mHeight);
         listener.onDrawFrame();
         this.mInputWindowSurface.setPresentationTime(this.mTimeStampNanos);
         this.mInputWindowSurface.swapBuffers();
         this.mTimeStampNanos = System.nanoTime();
         int recordDurationInMillis = (int)(System.currentTimeMillis() - this.mStartTime);
         if (this.mCallBack != null) {
            this.mCallBack.onRecording(recordDurationInMillis, this.mChannel);
         }

      }
   }

   public void stopRecord(boolean snapshot) {
      this.encoderCore.stopRecording();
      if (this.mVideoEncoder != null) {
         this.mVideoEncoder.stopRecording();
         this.mVideoEncoder = null;
      }

      if (this.mInputWindowSurface != null) {
         this.mInputWindowSurface.release();
         this.mInputWindowSurface = null;
      }

      this.mRecordingEnabled = false;
      if (this.mEglCore != null) {
      }

      if (this.mCallBack != null && this.mOutputFile != null) {
         this.mCallBack.onRecordStop(this.mOutputFile.getAbsolutePath(), snapshot);
      }

   }

   public void writeAudioData(byte[] buffer) {
      if (this.encoderCore != null) {
         if (buffer.length > 2048) {
            int count;
            for(count = 0; count < buffer.length / 2048; ++count) {
               this.mAudioBuffer.position(0);
               this.mAudioBuffer.put(buffer, count * 2048, 2048);
               this.encoderCore.sendAudioToEncoder(this.mAudioBuffer.array());
               this.encoderCore.P_drainEncoder(false, false);
            }

            if (buffer.length > buffer.length / 2048 * 2048) {
               count = buffer.length / 2048;
               this.mAudioBuffer.put(buffer, (count - 1) * 2048, buffer.length - (count - 1) * 2048);
               this.mAudioPosition = buffer.length - (count - 1) * 2048;
               int var3 = buffer.length;
            }
         } else if (buffer.length + this.mAudioPosition > 2048) {
            this.mAudioBuffer.put(buffer, 0, 2048 - this.mAudioPosition);
            this.encoderCore.sendAudioToEncoder(this.mAudioBuffer.array());
            this.encoderCore.P_drainEncoder(false, false);
            this.mAudioBuffer.position(0);
            this.mAudioBuffer.put(buffer, 2048 - this.mAudioPosition, buffer.length + this.mAudioPosition - 2048);
            this.mAudioPosition = buffer.length + this.mAudioPosition - 2048;
         } else {
            this.mAudioBuffer.put(buffer);
            this.mAudioPosition += buffer.length;
         }
      }

   }

   public interface FrameDrawListener {
      void onDrawFrame();
   }
}
