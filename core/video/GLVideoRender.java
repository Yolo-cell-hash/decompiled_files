package com.eseeiot.core.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import com.eseeiot.basemodule.listener.CaptureCallback;
import com.eseeiot.basemodule.listener.RecordCallback;
import com.eseeiot.basemodule.listener.RenderListener;
import com.eseeiot.basemodule.player.listener.SurfaceCallback;
import com.eseeiot.core.HWCodec.FullFrameRect;
import com.eseeiot.core.HWCodec.GlUtil;
import com.eseeiot.core.HWCodec.HWMP4Encoder;
import com.eseeiot.core.HWCodec.HWPlayer;
import com.eseeiot.core.HWCodec.Texture2dProgram;
import com.eseeiot.core.HWCodec.gles.EglCore;
import com.eseeiot.core.listener.AnimationCallback;
import com.eseeiot.core.listener.AudioDataListener;
import com.eseeiot.core.listener.CaptureCloudListener;
import com.eseeiot.core.listener.ConnectStatusListener;
import com.eseeiot.core.listener.DestoryListener;
import com.eseeiot.core.listener.GLVideoSurfaceCreateListener;
import com.eseeiot.core.listener.OnGSenserDataListener;
import com.eseeiot.core.listener.OnRecordVideoBackListener;
import com.eseeiot.core.listener.PlayfileProgress;
import com.eseeiot.core.networkCallback.OnDirectTextureFrameUpdataListener;
import com.eseeiot.core.networkCallback.OnPlayedFirstFrameListener;
import com.eseeiot.core.pojo.SensorInfo;
import com.eseeiot.core.util.Memory;
import com.eseeiot.core.view.GLSurfaceViewOrg;
import com.eseeiot.core.view.JAGLSurfaceView;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLVideoRender implements GLSurfaceViewOrg.Renderer, OnDirectTextureFrameUpdataListener, OnPlayedFirstFrameListener, HWPlayer.PresentAudioData, OnGSenserDataListener {
   private static final String TAG = "GLVideoRender";
   public static final String LOGO_FILE_NAME = "gles_loading.png";
   public long mParametricManager = 0L;
   public float mAspect = 1.0F;
   private boolean[] runend = new boolean[36];
   private boolean[] runendOSD = new boolean[1];
   public int mWidth = 0;
   public int mHeight = 0;
   private int mRecordWidth = 0;
   private int mRecordHeight = 0;
   private JAGLSurfaceView mView;
   private int recordStatuTexId_1 = 0;
   private int recordStatuTexId_2 = 0;
   private HWPlayer hwplayer = null;
   private HWPlayer.PresentTimeCallback mCallback = null;
   private FullFrameRect mFullScreen;
   private final float[] mIdentityMatrix;
   public static long conn;
   private int[] mFileTexture = null;
   private int[] mFileWidth = null;
   private int[] mFileHeight = null;
   private long nowTime;
   private long lastTime = 0L;
   public boolean isRotateScreen = false;
   public boolean isPauseDraw = false;
   private AtomicBoolean isDestory = new AtomicBoolean();
   private AtomicBoolean isDestoryed = new AtomicBoolean();
   public int mScreenMode = 0;
   private boolean[] mEnableKeepAspect = new boolean[36];
   private boolean[] mShowLoading = new boolean[36];
   private int mInputTexture = 0;
   private List<SensorInfo> mSensorList;
   private List<Long> mConnectCtxs;
   private int[] mFrameWidth = new int[36];
   private int[] mFrameHeight = new int[36];
   private int mForceInstallMode = -1;
   private boolean mShowHemisphereTimestamp = true;
   private int mSampleRate = 8000;
   private boolean mIsYUV;
   private final WeakReference<Context> mContext;
   private int mRcvFrameCount = 0;
   public AudioDataListener mAudioDataListener;
   public ConnectStatusListener mConnectStatusListener;
   public GLVideoSurfaceCreateListener mGLVideoSurfaceCreateListener;
   public DestoryListener mDestoryListener;
   public PlayfileProgress mProgress;
   public OnPlayedFirstFrameListener mFirstFrameListener;
   public CaptureCloudListener mCaptureCloudListener;
   public OnRecordVideoBackListener mRecordVideoBackLinstener;
   private String mCaptureFileName = null;
   private int mCaptureRequest = 0;
   private boolean mRecordScreen = false;
   private String mRecordFileName = null;
   private int mRecordChannel;
   private int mCaptureChannel;
   private boolean mSnapShotEnabled = false;
   private HWMP4Encoder mHWEncoder = null;
   private final HWMP4Encoder.FrameDrawListener mDrawFrameListener = new HWMP4Encoder.FrameDrawListener() {
      public void onDrawFrame() {
         GLVideoRender.this.DrawParametric(GLVideoRender.this.mParametricManager, true);
      }
   };
   private RenderListener mRenderListener;
   private SurfaceCallback mSurfaceCallback;
   private AnimationCallback mAnimationCallback;
   private CaptureCallback mCaptureCallback;
   private RecordCallback mRecordCallback;

   public int getInstallMode(int index) {
      return this.mForceInstallMode != -1 ? this.mForceInstallMode : 0;
   }

   public void setAudioSample(int sampleRate) {
      this.mSampleRate = sampleRate;
   }

   public native void resetRenderObject();

   public native void SetMode(long var1, int var3);

   public native long StartAnimation(long var1, float[] var3, int var4, int var5, boolean var6, int var7, boolean var8, int var9, boolean var10, int var11);

   public native void StopAnimation(long var1, long var3);

   private native long nativeInitRenderManager(float var1, int var2, int var3, boolean var4, String var5);

   public native long nativeConvertYUV2RGBA(int var1, int var2, byte[] var3, int var4);

   private native void nativeLoadTexture(long var1, int var3, int var4, byte[] var5, int var6, long var7, boolean var9, int var10, long var11);

   private native void nativeLoadYUVTexture(long var1, int var3, int var4, byte[] var5, byte[] var6, int var7, long var8);

   private native void nativeSetVerticalOffset(long var1, float var3, int var4);

   private native void nativeSetObjectVisibility(long var1, boolean var3, int var4);

   public native void OSDTextureAvaible(long var1, long var3, int var5, int var6, int var7);

   public native void OSDTextureAvaible2(long var1, String var3);

   public native boolean DestroyManager(long var1);

   public native void DrawParametric(long var1, boolean var3);

   public native void TransformObject(long var1, float[] var3, int var4, boolean var5, int var6);

   public native boolean Playfile(long var1, String var3, boolean var4, boolean var5, int var6, int var7, int var8);

   public native boolean PlayfileCapture(long var1, String var3, int var4);

   public native boolean PlayfileRecord(long var1, String var3);

   public native boolean PlayfileRecordStop(long var1);

   public native void PlayfileHardwareDecoder(long var1, boolean var3, int var4, int var5);

   public native int GetCloudRecPlatTraffic();

   public native void PauseFile(long var1);

   public native boolean IsPauseFile(long var1);

   public native void ResumeFile(long var1);

   public native void SeekFile(long var1, int var3);

   public static native long GetNativeMediaPlayer(Object var0);

   public static native boolean CheckNativeMediaPlayerBusy(long var0);

   public static native boolean NativeMediaPlayerDownload(long var0, String var2, String var3, int var4);

   public static native void StopNativeMediaPlayerDownload(long var0);

   public static native void ReleaseNativeMediaPlayer(long var0);

   public native void StopPlay(long var1);

   public native float[] GetObjectPosition(long var1, int var3, boolean var4, int var5);

   public native void SetSplit(long var1, int var3);

   public native void SetScreenPage(long var1, int var3);

   public native void SetSelected(long var1, int var3, int var4, int var5, int var6);

   public native void SetSelectedByIndex(long var1, int var3);

   public native void CancelSelectedByIndex(long var1, int var3);

   public native int GetPageIndex(long var1);

   public native int GetScreenCount(long var1);

   public native int GetAllPage(long var1);

   public native int GetVideoIndex(long var1);

   public native int GetMode(long var1);

   public native void SetSingVideo(long var1, int var3, boolean var4);

   public native int GetSplitMode(long var1);

   public native void ClearAnimation(long var1);

   public native void StopAnimationOperator(long var1);

   public native float[] GetScale(long var1, boolean var3, int var4);

   public native void ResetPosition(long var1, boolean var3, int var4);

   public native boolean GetVisibility(long var1, int var3);

   public native void DoStatus(long var1, String var3, int var4, int var5);

   public native void ReSizeSplite(long var1, float var3, int var4, int var5);

   public native void VRSensor(long var1, float[] var3, float[] var4, int var5, int var6);

   public native void LoadRecordStatuTexture(long var1, int var3);

   public native void SetWindowWidthHeight(long var1, int var3, int var4);

   public native void DoTapOrMouseDown(long var1, int var3, int var4);

   public native void DoTapOrMouseMove(long var1, int var3, int var4, int var5, int var6);

   public native void DoTapOrMouseUp(long var1, int var3, int var4, int var5);

   public native void DoTapOrMouseWheel(long var1, float var3, int var4, int var5, int var6, int var7, int var8);

   public native void DoDoubleTap(long var1, int var3, int var4, int var5, int var6, int var7);

   public native void UpdateAspect(long var1, float var3);

   public native void SetAllPage(long var1, int var3);

   public native void SetX35Display(long var1, boolean var3);

   public native void CylinderWind(long var1);

   public native void CylinderUnwind(long var1);

   public native void HemisphereWind(long var1);

   public native void HemisphereUnwind(long var1);

   public native void TransformVertex(long var1, int var3, float[] var4, boolean var5, int var6, int var7);

   private native void DoDirectTextureFrameUpdata(long var1, long var3, int var5, int var6, long var7, int var9);

   private native void DoDirectTextureOSDFrameUpdata(long var1, long var3, int var5, int var6, long var7, int var9);

   private native void PlayAudioData(long var1, byte[] var3, int var4, int var5);

   public native void EnableGrid(long var1, boolean var3);

   public native void LoadLogo(long var1, String var3);

   private native void ShowVideoLoading(long var1, int var3);

   private native void HideVideoLoading(long var1, int var3);

   private native boolean IsVideoLoadingShowing(long var1, int var3);

   public native void StartMotionTracking(long var1, int var3);

   public native void StopMotionTracking(long var1, int var3);

   public native void AdjustActionExperience(long var1, int var3, int var4, float var5);

   private native void SetBorderColor(long var1, int var3, int var4);

   public native void SetViewAngle(long var1, float var3);

   public native boolean GetDualMode(long var1);

   public native int GetDualTexture(long var1);

   public native void CleanTexture(long var1);

   public native void SetHardwareTexture(long var1, int var3, int var4, int var5, long var6, int var8);

   public native void CleanFishEyeParameterFlag(long var1, long var3, boolean var5);

   public native boolean GetHaveCropParmeter(long var1);

   public native int[] GetCropParameter(long var1, int var3, int var4);

   public native int[] SetCropInfo(long var1, long var3, int var5, int var6);

   public native void SetFBOTexture(long var1, int var3);

   public native void RenderFBO(long var1);

   public native boolean GetIsHEVC(long var1, long var3);

   private native void SetFishEyeParameters(long var1, int var3, int var4, float var5, float var6, float var7, float var8, float var9, float var10, int var11, byte[] var12, int var13, boolean var14);

   private native void SetDistortion(long var1, float[] var3, float[] var4, int var5);

   private native void SetSurfaceReady(long var1, boolean var3);

   private native void ShowRecordState(long var1, boolean var3, int var4);

   public native void SetKeepAspect(long var1, float var3, int var4);

   public void SetFishEyeParameters(int w, int h, float centerX, float centerY, float radius, float angleX, float angleY, float angleZ, int index, byte[] angleData, int angleDataLength, boolean is720) {
      this.SetFishEyeParameters(this.mParametricManager, w, h, centerX, centerY, radius, angleX, angleY, angleZ, index, angleData, angleDataLength, is720);
   }

   public void SetDistortion(float[] distortion, float[] angle, int dist_len) {
      this.SetDistortion(this.mParametricManager, distortion, angle, dist_len);
   }

   public native void CloseInfo(long var1);

   public native void SetScaleMax(long var1, int var3);

   public void OnAnimationEnd(int msgid) {
      if (this.mAnimationCallback != null) {
         this.mAnimationCallback.onAnimationEnd(msgid);
      }

   }

   public boolean isImageLoaded() {
      return this.mFileTexture != null;
   }

   public void loadImageTexture(@NonNull Bitmap bmp, int channelCount, int index) {
      if (this.mFileTexture == null && channelCount > 0) {
         this.mFileTexture = new int[channelCount];
         this.mFileHeight = new int[channelCount];
         this.mFileWidth = new int[channelCount];
      }

      try {
         this.mFileWidth[index] = bmp.getWidth();
         this.mFileHeight[index] = bmp.getHeight();
         this.mFileTexture[index] = GlUtil.loadTextureFromBitmap(bmp);
         this.mFrameWidth[index] = bmp.getWidth();
         this.mFrameHeight[index] = bmp.getHeight();
         if (this.mEnableKeepAspect[index]) {
            float aspect = (float)this.mFileWidth[index] * 1.0F / (float)this.mFileHeight[index];
            this.SetKeepAspect(this.mParametricManager, aspect, index);
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void removeImageTexture(int channel) {
      if (this.mFileTexture != null) {
         if (channel < 0 || channel >= this.mFileTexture.length || this.mFileTexture[channel] == 0) {
            return;
         }

         this.SetHardwareTexture(this.mParametricManager, 0, 0, 0, conn, channel);
         GLES20.glDeleteTextures(1, this.mFileTexture, channel);
         this.mFileTexture[channel] = 0;
         boolean hasRemoveIndex = false;

         for(int i = 0; i < this.mFileTexture.length; ++i) {
            if (this.mFileTexture[i] != 0) {
               hasRemoveIndex = true;
            }
         }

         if (!hasRemoveIndex) {
            this.mFileTexture = null;
            this.mFileWidth = null;
            this.mFileHeight = null;
         }
      }

   }

   public void CleanFishEyeParameterFlag(boolean flag) {
      this.CleanFishEyeParameterFlag(flag, conn);
   }

   public void CleanFishEyeParameterFlag(boolean flag, long context) {
      this.CleanFishEyeParameterFlag(this.mParametricManager, context, flag);
   }

   public boolean IsVideoLoadingShowing(int index) {
      return this.IsVideoLoadingShowing(this.mParametricManager, index);
   }

   public int getFrameWidth(int index) {
      return this.mFrameWidth[index];
   }

   public int getFrameHeight(int index) {
      return this.mFrameHeight[index];
   }

   public void doOSDTexture(int width, int height, long frameHandle, int frameLength) {
      if (!this.isDestory.get()) {
         this.OSDTextureAvaible(this.mParametricManager, frameHandle, frameLength, width, height);
      }
   }

   public void doOSDTexture2(String osd) {
      if (!this.isDestory.get()) {
         this.OSDTextureAvaible2(this.mParametricManager, osd);
      }
   }

   public void doTexture(int width, int height, byte[] buffer, long bufferHandle, int bufferSize, int frameType, boolean yuv2rgb, int channel, int renderIndex, long connectCtx) {
      if (!this.isDestory.get()) {
         if (renderIndex < this.mEnableKeepAspect.length && renderIndex >= 0) {
            this.mFrameWidth[renderIndex] = width;
            this.mFrameHeight[renderIndex] = height;
            if (this.mRcvFrameCount > 15) {
               this.dismissLoading(renderIndex);
            } else {
               ++this.mRcvFrameCount;
            }

            if (this.mInputTexture > 10) {
               this.mInputTexture = 0;
            }

            if (this.mInputTexture <= 0) {
               this.removeImageTexture(renderIndex);
               this.SetSurfaceReady(connectCtx, false);
               this.CloseInfo(this.mParametricManager);
               if (this.mIsYUV) {
                  byte[] yPixels = new byte[bufferSize * 2 / 3];
                  byte[] cbcrPixels = new byte[bufferSize - yPixels.length];
                  System.arraycopy(buffer, 0, yPixels, 0, yPixels.length);
                  System.arraycopy(buffer, yPixels.length, cbcrPixels, 0, cbcrPixels.length);
                  this.nativeLoadYUVTexture(this.mParametricManager, width, height, yPixels, cbcrPixels, renderIndex, connectCtx);
               } else {
                  if (!yuv2rgb) {
                     int rgbSize = width * height * 4;
                     if (bufferSize < rgbSize) {
                        return;
                     }
                  }

                  this.nativeLoadTexture(this.mParametricManager, width, height, buffer, bufferSize, bufferHandle, yuv2rgb, renderIndex, connectCtx);
               }

               this.SetSurfaceReady(connectCtx, true);
               this.setKeepAspect(width, height, renderIndex);
            }
         }
      }
   }

   public GLVideoRender(Context context, boolean isYUV) {
      this.mContext = new WeakReference(context);
      this.mIsYUV = isYUV;
      this.mIdentityMatrix = new float[16];
      Matrix.setIdentityM(this.mIdentityMatrix, 0);
      this.mSensorList = new ArrayList();
      SensorInfo.FIRST_FLAG = true;
   }

   public void PlayFile(String fn) {
      this.hwplayer.PlayFile(fn);
   }

   public void StopPlay() {
      if (this.hwplayer.mIsPlaying) {
         this.hwplayer.Stop();
      }

   }

   public void onSurfaceCreated(GL10 unused, EGLConfig config) {
      this.isDestoryed.set(false);
      this.mHWEncoder = new HWMP4Encoder();
      this.mHWEncoder.setCallBack(this.mRecordCallback);
      this.mFullScreen = new FullFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D));
   }

   public void onDrawFrame(GL10 unused) {
      if (!this.isDestory.get() && !this.isDestoryed.get()) {
         if (this.mRenderListener != null) {
            this.mRenderListener.beforeDraw();
         }

         if (!this.isRotateScreen && !this.isPauseDraw) {
            this.nowTime = SystemClock.uptimeMillis();
            if (this.lastTime == 0L) {
               this.lastTime = this.nowTime;
            } else if (this.nowTime - this.lastTime > 200L) {
               this.LoadRecordStatuTexture(this.mParametricManager, this.recordStatuTexId_2);
            }

            if (this.mFileTexture != null && this.mFileTexture.length > 0) {
               for(int i = 0; i < this.mFileTexture.length; ++i) {
                  if (this.mFileTexture[i] != 0) {
                     this.SetHardwareTexture(this.mParametricManager, this.mFileTexture[i], this.mFileWidth[i], this.mFileHeight[i], conn, i);
                  }
               }
            }

            GLES20.glClear(16384);
            GLES20.glEnable(3042);
            GLES20.glBlendFunc(770, 771);
            GLES20.glViewport(0, 0, this.mWidth, this.mHeight);
            this.DrawParametric(this.mParametricManager, false);
            this.performCapture();
            this.performRecord();
         }
      }
   }

   public void DirectDestroy() {
      this.isDestoryed.set(true);
      conn = 0L;
      this.mSensorList.clear();
      this.mSensorList = null;
      if (this.hwplayer != null && this.hwplayer.mIsPlaying) {
         this.hwplayer.Stop();
      }

      (new Handler()).postDelayed(new Runnable() {
         public void run() {
            GLVideoRender.this.DestroyManager(GLVideoRender.this.mParametricManager);
            GLVideoRender.this.mParametricManager = 0L;
         }
      }, 200L);
      this.mAudioDataListener = null;
   }

   public void onSurfaceChanged(GL10 unused, int width, int height) {
      GLES20.glViewport(0, 0, width, height);
      this.isRotateScreen = false;
      this.mWidth = width;
      this.mHeight = height;
      this.mAspect = (float)width / (float)height;
      if (this.mParametricManager != 0L) {
         this.ReSizeSplite(this.mParametricManager, this.mAspect, this.mWidth, this.mHeight);
         this.UpdateAspect(this.mParametricManager, this.mAspect);
         if (this.mSurfaceCallback != null) {
            this.mSurfaceCallback.onSurfaceChanged(width, height);
         }
      } else {
         String path = "";
         Context context = (Context)this.mContext.get();
         if (context != null) {
            path = context.getFilesDir().getPath();
         }

         this.mParametricManager = this.nativeInitRenderManager(this.mAspect, this.mWidth, this.mHeight, this.mIsYUV, path);
         this.ReSizeSplite(this.mParametricManager, this.mAspect, this.mWidth, this.mHeight);
         this.UpdateAspect(this.mParametricManager, this.mAspect);
         if (this.mSurfaceCallback != null) {
            this.mSurfaceCallback.onSurfaceCreated(width, height);
         }
      }

   }

   public void enableHarWareDecoder(boolean enable, boolean cleanTexture, int index) {
      if (cleanTexture) {
         this.CleanTexture(this.mParametricManager);
      }

   }

   public boolean playFile(String fileName, boolean isImage, boolean isFishEye, boolean hardware, int duration, int index) {
      this.enableHarWareDecoder(hardware, false, 0);
      this.showLoading(index);
      return this.Playfile(this.mParametricManager, fileName, isImage, isFishEye, duration, this.mSampleRate, index);
   }

   public boolean isZh() {
      Context context = (Context)this.mContext.get();
      if (context == null) {
         return false;
      } else {
         Locale locale = context.getResources().getConfiguration().locale;
         String language = locale.getLanguage();
         return language.endsWith("zh");
      }
   }

   public void destroy() {
      this.mAnimationCallback = null;
      this.mRenderListener = null;
      this.mSurfaceCallback = null;
      this.mCaptureCallback = null;
      this.mRecordCallback = null;
      this.isDestory.set(true);
      this.StopAnimationOperator(this.mParametricManager);
   }

   public boolean isDestroy() {
      return this.isDestory.get();
   }

   public void OnPlayfileProgress(int time, int duration, boolean wallMode) {
      if (this.mProgress != null) {
         this.mProgress.OnProgress(time, duration);
      }

   }

   public void OnFilePlayCapture(int success, int requestCode) {
      if (this.mCaptureCloudListener != null) {
         this.mCaptureCloudListener.OnCaptureImage(success, requestCode);
      }

   }

   public void OnRecordVideoBack(long recordTime, int channel) {
      if (this.mRecordVideoBackLinstener != null) {
         this.mRecordVideoBackLinstener.OnRecordBack(recordTime, channel);
      }

   }

   public void OnGSensorData(long timeStamp, double x, double y, double z) {
      if (this.mSensorList != null) {
         this.mSensorList.add(new SensorInfo(timeStamp, x, y, z));
      }

   }

   public void OnDirectTextureFrameUpdata(int width, int height, long directBuffer, final int index) {
      this.runend[index] = false;
      this.mView.queueEvent(new Runnable() {
         public void run() {
            GLVideoRender.this.runend[index] = true;
         }
      });

      while(!this.runend[index]) {
         try {
            Thread.sleep(0L);
         } catch (InterruptedException var7) {
            var7.printStackTrace();
         }
      }

   }

   public void setConnectCtxs(long[] ctxs) {
      List<Long> tmpList = new ArrayList();

      for(int i = 0; i < ctxs.length; ++i) {
         tmpList.add(ctxs[i]);
      }

      this.setConnectCtxs((List)tmpList);
   }

   public void setConnectCtxs(List<Long> ctxs) {
      if (ctxs != null) {
         if (this.mConnectCtxs == null) {
            this.mConnectCtxs = new ArrayList();
         } else {
            this.mConnectCtxs.clear();
         }

         this.mConnectCtxs.addAll(ctxs);
      }

   }

   public void showRecordState(boolean show, int index) {
      this.ShowRecordState(this.mParametricManager, show, index);
   }

   public void OnPlayedFirstFrame(int is180, int index) {
      Log.i("Lee", "GLVideoRender.OnPlayedFirstFrame  is180 = " + is180 + ", index = " + index);
      if (is180 == 0) {
         this.SetViewAngle(this.mParametricManager, 90.0F);
      } else {
         this.SetViewAngle(this.mParametricManager, 60.0F);
      }

      if (this.mFirstFrameListener != null) {
         this.mFirstFrameListener.OnPlayedFirstFrame(is180, index);
      }

   }

   public void OnAudioData(byte[] data) {
      if (!this.isDestory.get()) {
         this.PlayAudioData(this.mParametricManager, data, data.length, this.mSampleRate);
         if (this.mRecordScreen) {
            this.mHWEncoder.writeAudioData(data);
         }

      }
   }

   public void setShowHemisphereTimestamp(boolean show) {
      this.mShowHemisphereTimestamp = show;
   }

   public boolean isRecording() {
      return this.mRecordScreen;
   }

   public void startRecord(String fileName, int channel) {
      this.mRecordFileName = fileName;
      this.mRecordScreen = true;
      this.mRecordChannel = channel;
   }

   public void startRecord(String fileName, int channel, int width, int height) {
      this.startRecord(fileName, channel);
      this.mRecordWidth = width;
      this.mRecordHeight = height;
   }

   public void stopRecord(boolean snapshot) {
      this.mRecordScreen = false;
      this.mSnapShotEnabled = snapshot;
      this.mRecordWidth = 0;
      this.mRecordHeight = 0;
   }

   private void performRecord() {
      if (this.mRecordScreen) {
         if (!this.mHWEncoder.isRecording()) {
            int width = this.mWidth;
            int height = this.mHeight;
            if (width > height) {
               --width;
            }

            boolean align = true;
            if (this.mRecordWidth > 0 && this.mRecordHeight > 0) {
               width = this.mRecordWidth;
               height = this.mRecordHeight;
               align = false;
            }

            this.mHWEncoder.startRecord(width, height, this.mRecordFileName, this.mRecordChannel, new EglCore(EGL14.eglGetCurrentContext(), 3), align);
         }

         if (this.mFileTexture == null || this.mFileTexture.length == 0) {
            this.mHWEncoder.writeFrame(0, this.mDrawFrameListener);
         }
      } else if (this.mHWEncoder.isRecording()) {
         this.mHWEncoder.stopRecord(this.mSnapShotEnabled);
      }

   }

   public void writeAudioFrame(byte[] buffer) {
      if (this.mHWEncoder != null && this.mHWEncoder.isRecording()) {
         this.mHWEncoder.writeAudioData(buffer);
      }

   }

   public void capture(String fileName, int requestCode, int channel) {
      this.mCaptureFileName = fileName;
      this.mCaptureRequest = requestCode;
      this.mCaptureChannel = channel;
   }

   private void performCapture() {
      if (!TextUtils.isEmpty(this.mCaptureFileName)) {
         int bufferSize = this.mWidth * this.mHeight * 4;
         if (!Memory.hasEnoughMemory(bufferSize)) {
            this.handleCaptureResult();
         } else {
            GLES20.glFlush();
            ByteBuffer buf = ByteBuffer.allocateDirect(bufferSize);
            buf.order(ByteOrder.BIG_ENDIAN);
            GLES20.glReadPixels(0, 0, this.mWidth, this.mHeight, 6408, 5121, buf);
            GlUtil.checkGlError("glReadPixels");
            buf.rewind();
            if (!Memory.hasEnoughMemory(bufferSize)) {
               this.handleCaptureResult();
            } else {
               BufferedOutputStream bos = null;

               try {
                  Bitmap bmp = Bitmap.createBitmap(this.mWidth, this.mHeight, Config.ARGB_8888);
                  bmp.copyPixelsFromBuffer(buf);

                  try {
                     bos = new BufferedOutputStream(new FileOutputStream(this.mCaptureFileName));
                     if (Memory.hasEnoughMemory(bufferSize)) {
                        android.graphics.Matrix matrix = new android.graphics.Matrix();
                        matrix.postScale(1.0F, -1.0F);
                        Bitmap bmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                        bmp2.compress(CompressFormat.JPEG, 90, bos);
                        bmp2.recycle();
                     }
                  } catch (IOException var14) {
                     var14.printStackTrace();
                  }

                  bmp.recycle();
               } finally {
                  if (bos != null) {
                     try {
                        bos.close();
                     } catch (IOException var13) {
                        var13.printStackTrace();
                     }
                  }

               }

               this.handleCaptureResult();
            }
         }
      }
   }

   private void handleCaptureResult() {
      if (this.mCaptureCallback != null) {
         File tempFile = new File(this.mCaptureFileName);
         this.mCaptureCallback.onCapture(tempFile.exists(), this.mCaptureChannel, this.mCaptureRequest);
      }

      this.mCaptureFileName = null;
   }

   public void showLoading(int index) {
      if (!this.isDestory.get()) {
         this.ShowVideoLoading(this.mParametricManager, index);
         this.mShowLoading[index] = true;
         this.mRcvFrameCount = 0;
      }
   }

   public void dismissLoading(int index) {
      if (!this.isDestory.get()) {
         if (this.mShowLoading[index]) {
            this.HideVideoLoading(this.mParametricManager, index);
            this.mShowLoading[index] = false;
         }
      }
   }

   public boolean isLoadingShow(int index) {
      return this.mShowLoading[index];
   }

   public void setRenderListener(RenderListener listener) {
      this.mRenderListener = listener;
   }

   public void setSurfaceCallback(SurfaceCallback callback) {
      this.mSurfaceCallback = callback;
   }

   public void setAnimationCallback(AnimationCallback callback) {
      this.mAnimationCallback = callback;
   }

   public void setCaptureCallback(CaptureCallback callback) {
      this.mCaptureCallback = callback;
   }

   public void setRecordCallback(RecordCallback callback) {
      if (this.mHWEncoder != null) {
         this.mHWEncoder.setCallBack(callback);
      } else {
         this.mRecordCallback = callback;
      }

   }

   public void cleanTexture() {
      this.CleanTexture(this.mParametricManager);
   }

   public void setHardwareTexture(int width, int height, int textureId) {
      if (!this.isImageLoaded()) {
         this.SetHardwareTexture(this.mParametricManager, textureId, width, height, conn, 0);
      }
   }

   public void enabledKeepAspect(boolean enable, int index) {
      if (index >= 0 && index <= 35) {
         this.mEnableKeepAspect[index] = enable;
      }
   }

   public boolean isKeepAspectEnabled(int index) {
      return index >= 0 && index <= 35 ? this.mEnableKeepAspect[index] : false;
   }

   public void setKeepAspect(int width, int height, int index) {
      if (this.mEnableKeepAspect[index]) {
         if (width > 0 && height > 0) {
            float aspect = (float)width / (float)height;
            if (aspect == 1.0F) {
               aspect += 1.0E-4F;
            }

            this.SetKeepAspect(this.mParametricManager, aspect, index);
         }
      } else {
         this.SetKeepAspect(this.mParametricManager, 1.0F, index);
      }

   }

   public void setTexutureVerticalOffset(float offset, int index) {
      this.nativeSetVerticalOffset(this.mParametricManager, offset, index);
   }

   public void setWindowSize(int width, int height) {
      this.SetWindowWidthHeight(this.mParametricManager, width, height);
   }

   public void setBorderColor(int color) {
      this.SetBorderColor(this.mParametricManager, color, -1);
   }

   public void setBorderColor(int selectColor, int UnSelectColor) {
      this.SetBorderColor(this.mParametricManager, selectColor, UnSelectColor);
   }

   public void setWindowSplitMode(int mode) {
      this.SetSplit(this.mParametricManager, mode);
   }

   public int getWindowSplitMode() {
      return this.GetSplitMode(this.mParametricManager);
   }

   public int getPageCount() {
      return this.GetAllPage(this.mParametricManager);
   }

   public int getPage() {
      return this.GetPageIndex(this.mParametricManager);
   }

   public void setPage(int index) {
      this.SetScreenPage(this.mParametricManager, index);
   }

   public int getScreenCount() {
      return this.GetScreenCount(this.mParametricManager);
   }

   public void setScreenCount(int count) {
      this.SetAllPage(this.mParametricManager, count);
   }

   public void setIsX35Display(boolean isX35Display) {
      this.SetX35Display(this.mParametricManager, isX35Display);
   }

   public int getSelectScreenIndex() {
      return this.GetVideoIndex(this.mParametricManager);
   }

   public void selectScreen(int index) {
      this.SetSelectedByIndex(this.mParametricManager, index);
   }

   public int getDisplayMode() {
      return this.GetMode(this.mParametricManager);
   }

   public void setDisplayMode(int mode) {
      this.SetMode(this.mParametricManager, mode);
   }

   public void doTapOrMouseDown(int x, int y) {
      this.DoTapOrMouseDown(this.mParametricManager, x, y);
   }

   public void doTapOrMouseUp(int x, int y, boolean wallMode) {
      this.DoTapOrMouseUp(this.mParametricManager, x, y, wallMode ? 1 : 0);
   }

   public void doTapOrMouseMove(int x, int y, boolean wallMode, int index) {
      this.DoTapOrMouseMove(this.mParametricManager, x, y, wallMode ? 1 : 0, index);
   }

   public void doTapOrMouseWheel(float scaleValue, int intra, int focusX, int focusY, int index, boolean wallMode) {
      this.DoTapOrMouseWheel(this.mParametricManager, scaleValue, intra, focusX, focusY, index, wallMode ? 1 : 0);
   }

   public void doDoubleTap(int wallMode, int scene, int scrnFourIndex, int index, int endMsg) {
      this.DoDoubleTap(this.mParametricManager, wallMode, scene, scrnFourIndex, index, endMsg);
   }

   public void startAnimation(float[] pend, int step, int duration, boolean isloop, int type, boolean texture, int index, boolean inertia, int endmsg) {
      this.StartAnimation(this.mParametricManager, pend, step, duration, isloop, type, texture, index, inertia, endmsg);
   }

   public void stopAnimation() {
      this.ClearAnimation(this.mParametricManager);
   }

   public long rotate(float angleX, float angleY, float angleZ, int step, int duration, boolean isloop, boolean isTexture, int index, boolean inertia, int msg) {
      float[] pend = new float[]{angleX, angleY, angleZ};
      return this.StartAnimation(this.mParametricManager, pend, step, duration, isloop, 2, isTexture, index, inertia, msg);
   }

   public long scale(float[] pend, int step, int duration, boolean isloop, boolean isTexture, int index, boolean inertia, int msg) {
      return this.StartAnimation(this.mParametricManager, pend, step, duration, isloop, 1, isTexture, index, inertia, msg);
   }

   public long scaleTo(float scale, float toPointX, float toPointY, int step, int duration, boolean isloop, boolean isTexture, int index, boolean inertia, int msg) {
      float[] pend = new float[]{toPointX, toPointY, scale};
      return this.StartAnimation(this.mParametricManager, pend, step, duration, isloop, 3, isTexture, index, inertia, msg);
   }

   public long position(float x, float y, float z, int step, int duration, boolean isloop, boolean isTexture, int index, boolean inertia, int msg) {
      float[] pend = new float[]{x, y, z};
      return this.StartAnimation(this.mParametricManager, pend, step, duration, isloop, 0, isTexture, index, inertia, msg);
   }

   public void setObjectVisibility(boolean visible, int index) {
      this.nativeSetObjectVisibility(this.mParametricManager, visible, index);
   }

   public void transformObject(float[] pos, int type, boolean texture, int index) {
      this.TransformObject(this.mParametricManager, pos, type, texture, index);
   }

   public void resetPosition(boolean texture, int index) {
      this.ResetPosition(this.mParametricManager, texture, index);
   }

   public float[] getObjectPosition(int type, boolean texture, int index) {
      return this.GetObjectPosition(this.mParametricManager, type, texture, index);
   }

   public float[] getObjectScale(boolean texture, int index) {
      return this.GetScale(this.mParametricManager, texture, index);
   }

   public void setScaleMax(int max) {
      this.SetScaleMax(this.mParametricManager, max);
   }

   static {
      System.loadLibrary("avutil");
      System.loadLibrary("swresample");
      System.loadLibrary("swscale");
      System.loadLibrary("avcodec");
      System.loadLibrary("avformat");
      System.loadLibrary("kdp");
      System.loadLibrary("IOTCAPIs");
      System.loadLibrary("RDTAPIs");
      System.loadLibrary("jnnat");
      System.loadLibrary("JAVideo");
      conn = 0L;
   }
}
