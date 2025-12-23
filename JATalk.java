package com.eseeiot.live;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import com.eseeiot.basemodule.connector.Connector;
import com.eseeiot.basemodule.device.base.MonitorCamera;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.option.Options;
import com.eseeiot.basemodule.device.talk.Talk;
import com.eseeiot.basemodule.device.talk.TalkSession;
import com.eseeiot.basemodule.device.talk.TalkSessionCallback;
import com.eseeiot.basemodule.util.ThreadPool;
import com.eseeiot.device.JADevProperty;
import com.eseeiot.live.audio.VoiceRecorder;
import java.io.IOException;
import java.io.InputStream;

public class JATalk implements Talk {
   private static final String TAG = "JATalkV2";
   private static final int STATE_EMPTY = 0;
   private static final int STATE_CALLING = 1;
   private static final int STATE_CONNECTED = 2;
   private static final int STATE_TALKING = 3;
   private MonitorCamera mCamera;
   private Connector mConnector;
   private TalkSession mSession;
   private int mSampleRate = -1;

   public void bindCamera(MonitorCamera camera) {
      this.mCamera = camera;
   }

   public void bindConnector(Connector connector) {
      this.mConnector = connector;
   }

   public TalkSession getSession(Context context) {
      if (this.mSession == null) {
         this.mSession = new JATalk.TalkingSession();
      }

      return this.mSession;
   }

   private class TalkingSession implements TalkSession {
      private VoiceRecorder mRecorder;
      private int mState;
      private volatile boolean mIsCanceled;
      private volatile boolean mIsSendingAudio;
      private volatile boolean mIsHangup;

      private TalkingSession() {
         this.mState = 0;
         this.mIsCanceled = false;
         this.mIsSendingAudio = false;
         this.mIsHangup = false;
      }

      private void callProcess(final TalkSessionCallback callback) {
         if (this.mState != 0) {
            if (this.mIsCanceled) {
               this.mIsCanceled = false;
            }

            int state = this.mState == 1 ? 2 : 0;
            callback.onSessionListener(JATalk.this.mCamera, state);
         } else {
            this.mState = 1;
            ThreadPool.initialize();
            ThreadPool.execute(new Runnable() {
               public void run() {
                  int res = TalkingSession.this.doCall();
                  Log.d("JATalkV2", "run: call ret = " + res);
                  if (res == 0) {
                     if (TalkingSession.this.mIsCanceled) {
                        TalkingSession.this.mIsCanceled = false;
                        TalkingSession.this.doHangup();
                        return;
                     }

                     TalkingSession.this.mState = 2;
                     TalkingSession.this.initSampleRate();
                  } else {
                     TalkingSession.this.mState = 0;
                  }

                  if (callback != null) {
                     callback.onSessionListener(JATalk.this.mCamera, res == 0 ? 0 : 1);
                  }

               }
            });
         }
      }

      public void call(final TalkSessionCallback callback) {
         this.callProcess(new TalkSessionCallback() {
            public void onSessionListener(MonitorCamera camera, int state) {
               if (state == 0) {
                  TalkingSession.this.startSendAudio();
               }

               if (callback != null) {
                  callback.onSessionListener(camera, state);
               }

            }
         });
      }

      public void hangup() {
         if (this.mState != 0) {
            if (this.mState == 1) {
               this.mIsCanceled = true;
            } else {
               this.doHangup();
            }
         }
      }

      public int talk(boolean echoEnable) {
         if (this.mRecorder != null) {
            this.mRecorder.talk(echoEnable);
            return 0;
         } else {
            return 3;
         }
      }

      public int releaseTalk() {
         if (this.mRecorder != null) {
            this.mRecorder.releaseTalk();
            return 0;
         } else {
            return 3;
         }
      }

      public boolean isBusy() {
         return this.mState > 0;
      }

      public boolean isCalling() {
         return this.mState == 1;
      }

      public boolean isConnected() {
         return this.mState >= 2;
      }

      public boolean isTalking() {
         return this.mState == 3;
      }

      private int doCall() {
         this.mIsHangup = false;
         JATalk.this.mConnector.hangup(JATalk.this.mCamera.getConnectKey(), JATalk.this.mCamera.getChannel());
         return JATalk.this.mConnector.call(JATalk.this.mCamera.getConnectKey(), JATalk.this.mCamera.getChannel());
      }

      private void doHangup() {
         this.mIsSendingAudio = false;
         this.mIsHangup = true;
         ThreadPool.execute(new Runnable() {
            public void run() {
               for(int i = 0; i < 2; ++i) {
                  int ret = JATalk.this.mConnector.hangup(JATalk.this.mCamera.getConnectKey(), JATalk.this.mCamera.getChannel());
                  if (ret == 0) {
                     TalkingSession.this.mState = 0;
                     break;
                  }
               }

            }
         });
      }

      private void initSampleRate() {
         if (JATalk.this.mSampleRate == -1) {
            MonitorDevice device = JATalk.this.mCamera.getParentDevice();
            Options option = device.getOptions();
            Integer sampleRate = option.getAudioSample();
            JATalk.this.mSampleRate = sampleRate == null ? 8000 : sampleRate;
            JADevProperty var4 = (JADevProperty)device.getProperty();
         }

      }

      private void startSendAudio() {
         if (!this.mIsSendingAudio) {
            this.mIsSendingAudio = true;
            this.mRecorder = VoiceRecorder.getDefault(JATalk.this.mSampleRate);
            ThreadPool.execute(new Runnable() {
               public void run() {
                  if (TalkingSession.this.mRecorder != null) {
                     TalkingSession.this.mRecorder.clearBuffer();

                     while(TalkingSession.this.mIsSendingAudio) {
                        byte[] buffer = TalkingSession.this.mRecorder.readBuffer();
                        if (buffer != null) {
                           JATalk.this.mConnector.sendAudioPacket(JATalk.this.mCamera.getConnectKey(), buffer, buffer.length, 20L, "G711A", JATalk.this.mSampleRate, 16, 1, 2.0F, JATalk.this.mCamera.getChannel());
                        } else {
                           SystemClock.sleep(20L);
                        }
                     }

                  }
               }
            });
         }
      }

      public void sendAudioFile(final InputStream is, final int loop, final TalkSessionCallback callback, final TalkSessionCallback sendCallback) {
         this.callProcess(new TalkSessionCallback() {
            public void onSessionListener(MonitorCamera camera, int state) {
               if (callback != null) {
                  callback.onSessionListener(camera, state);
               }

               if (state == 0) {
                  try {
                     for(int i = 0; i < loop; ++i) {
                        is.reset();
                        int result = TalkingSession.this.sendAudioBuff(is);
                        if (TalkingSession.this.mIsHangup) {
                           break;
                        }
                     }

                     TalkingSession.this.doHangup();
                  } catch (IOException var5) {
                     if (sendCallback != null) {
                        sendCallback.onSessionListener(camera, 1);
                     }
                  }

                  if (sendCallback != null) {
                     sendCallback.onSessionListener(camera, 0);
                  }
               }

            }
         });
      }

      private int sendAudioBuff(InputStream is) throws IOException {
         if (this.mIsSendingAudio) {
            return 2;
         } else {
            this.mIsSendingAudio = true;
            int lenx = false;
            byte[] buffer = new byte[160];
            short[] shortsData = null;
            long sleepTime = (long)(Math.abs(1000 / (JATalk.this.mSampleRate / 1000 * 1024 / buffer.length)) - 1);

            int len;
            while((len = is.read(buffer)) != -1 && len == buffer.length && !this.mIsHangup) {
               JATalk.this.mConnector.sendAudioPacket(JATalk.this.mCamera.getConnectKey(), buffer, buffer.length, 20L, "G711A", JATalk.this.mSampleRate, 16, 1, 2.0F, JATalk.this.mCamera.getChannel());
               long start = SystemClock.uptimeMillis() + sleepTime;

               while(true) {
                  int var10 = 100;

                  while(var10-- > 0 && !this.mIsHangup) {
                  }

                  if (this.mIsHangup || SystemClock.uptimeMillis() - start > 0L) {
                     break;
                  }
               }
            }

            this.mIsSendingAudio = false;
            return 0;
         }
      }

      // $FF: synthetic method
      TalkingSession(Object x1) {
         this();
      }
   }
}
