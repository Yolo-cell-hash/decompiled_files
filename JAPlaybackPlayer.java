package com.eseeiot.event;

import androidx.annotation.NonNull;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.dispatcher.DeviceEventCallback;
import com.eseeiot.basemodule.listener.CaptureCallback;
import com.eseeiot.basemodule.player.APlay;
import com.eseeiot.basemodule.util.TimeoutManager;
import com.eseeiot.core.player.JAPlayer;
import java.util.Iterator;
import java.util.Map;

public class JAPlaybackPlayer extends JAPlayer implements APlay, TimeoutManager.TimeoutCallback {
   private final DeviceEventCallback mDeviceEventCallback = new DeviceEventCallback() {
      public int onRegisterParamGet() {
         return 3;
      }

      public void onConnectChanged(final MonitorDevice device, int status, final int channel) {
         if (!JAPlaybackPlayer.this.mIsStopped) {
            if (status != 17 && status >= 13) {
               JAPlaybackPlayer.this.mPlayStatus.put(channel, status);
            }

            if (status != 6) {
               if (status == 15) {
                  if (JAPlaybackPlayer.this.mRenderHelper != null) {
                     JAPlaybackPlayer.this.mRenderHelper.dismissLoading(channel);
                  }

                  if (JAPlaybackPlayer.this.mJAGLSurfaceView != null) {
                     JAPlaybackPlayer.this.mJAGLSurfaceView.post(new Runnable() {
                        public void run() {
                           if (JAPlaybackPlayer.this.mOnRenderedFirstFrameListener != null && device != null) {
                              JAPlaybackPlayer.this.mOnRenderedFirstFrameListener.onRenderedFirstFrame(device, channel);
                           }

                        }
                     });
                  }
               } else if (status == 10) {
                  if (JAPlaybackPlayer.this.mRenderHelper != null) {
                     JAPlaybackPlayer.this.mRenderHelper.dismissLoading(channel);
                  }

                  if (JAPlaybackPlayer.this.mJAGLSurfaceView != null) {
                     JAPlaybackPlayer.this.mJAGLSurfaceView.post(new Runnable() {
                        public void run() {
                           if (JAPlaybackPlayer.this.mOnPlayErrorListener != null) {
                              JAPlaybackPlayer.this.mOnPlayErrorListener.onPlayError(JAPlaybackPlayer.this.mMonitorDevice, -20, channel);
                           }

                        }
                     });
                  }
               } else if (status == 2 || status == 11 || status == 8 || status == 12 || status == 16 || status == 17) {
                  if (JAPlaybackPlayer.this.mRenderHelper != null) {
                     JAPlaybackPlayer.this.mRenderHelper.dismissLoading(channel);
                  }

                  final int errCode = -10;
                  if (status == 11) {
                     errCode = -110;
                  } else if (status == 12) {
                     errCode = -120;
                  } else if (status == 17) {
                     errCode = -150;
                  }

                  if (JAPlaybackPlayer.this.mJAGLSurfaceView != null) {
                     JAPlaybackPlayer.this.mJAGLSurfaceView.post(new Runnable() {
                        public void run() {
                           if (JAPlaybackPlayer.this.mOnPlayErrorListener != null && JAPlaybackPlayer.this.mMonitorDevice != null) {
                              JAPlaybackPlayer.this.mOnPlayErrorListener.onPlayError(JAPlaybackPlayer.this.mMonitorDevice, errCode, channel);
                           }

                        }
                     });
                  }
               }
            }

         }
      }

      public boolean onDisconnected(MonitorDevice device, int status, final int channel) {
         if (JAPlaybackPlayer.this.mIsStopped) {
            return false;
         } else {
            JAPlaybackPlayer.this.mRenderHelper.dismissLoading(channel);
            if (JAPlaybackPlayer.this.mJAGLSurfaceView != null) {
               JAPlaybackPlayer.this.mJAGLSurfaceView.post(new Runnable() {
                  public void run() {
                     if (JAPlaybackPlayer.this.mOnPlayErrorListener != null) {
                        JAPlaybackPlayer.this.mOnPlayErrorListener.onPlayError(JAPlaybackPlayer.this.mMonitorDevice, -50, channel);
                     }

                  }
               });
            }

            return false;
         }
      }

      public void onPlaybackOSDAvailable(final int time, final int index) {
         if (!JAPlaybackPlayer.this.mIsStopped) {
            if (JAPlaybackPlayer.this.mJAGLSurfaceView != null) {
               JAPlaybackPlayer.this.mJAGLSurfaceView.post(new Runnable() {
                  public void run() {
                     if (JAPlaybackPlayer.this.mFrameResultListener != null) {
                        JAPlaybackPlayer.this.mFrameResultListener.onFrame((long)time * 1000L, 0, index);
                     }

                  }
               });
            }

         }
      }

      public void onCaptureResult(int success, int index, int requestCode) {
         if (!JAPlaybackPlayer.this.mIsStopped) {
            if (JAPlaybackPlayer.this.mCaptureCallbackList != null && JAPlaybackPlayer.this.mCaptureCallbackList.size() > 0) {
               Iterator var4 = JAPlaybackPlayer.this.mCaptureCallbackList.iterator();

               while(var4.hasNext()) {
                  CaptureCallback captureCallback = (CaptureCallback)var4.next();
                  captureCallback.onCapture(success == 1, index, requestCode);
               }
            }

         }
      }

      public void onRecordDuration(long duration, int index) {
         if (!JAPlaybackPlayer.this.mIsStopped) {
            int totalDuration = JAPlaybackPlayer.this.mTotalRecordDurations.get(index, -1);
            if (totalDuration != -1) {
               totalDuration = (int)((long)totalDuration + duration);
               JAPlaybackPlayer.this.mTotalRecordDurations.put(index, totalDuration);
               if (JAPlaybackPlayer.this.mRecordCallback != null) {
                  JAPlaybackPlayer.this.mRecordCallback.onRecording(totalDuration, index);
               }

            }
         }
      }
   };

   public JAPlaybackPlayer(@NonNull MonitorDevice device) {
      super(device);
      this.mPropertyValue.put("PROP_CHANNEL", 0);
      this.mPropertyValue.put("PROP_PLAYBACK_TIME", 0);
      this.mPropertyValue.put("PROP_PLAYBACK_STATE", false);
      device.registerEventCallback(this.mDeviceEventCallback);
   }

   public void release() {
      if (this.shouldRelease()) {
         if (this.mMonitorDevice != null) {
            this.mMonitorDevice.unregisterEventCallback(this.mDeviceEventCallback);
         }

         super.release();
      }
   }

   public boolean resume() {
      boolean res = super.resume();
      if (res) {
         int playChannel = (Integer)this.mPropertyValue.get("PROP_CHANNEL");
         this.mMonitorDevice.resumePlayback(playChannel);
      }

      return res;
   }

   public boolean pause() {
      boolean pause = super.pause();
      if (pause) {
         int playChannel = (Integer)this.mPropertyValue.get("PROP_CHANNEL");
         this.mMonitorDevice.pausePlayback(playChannel);
         return true;
      } else {
         return false;
      }
   }

   public final int getType() {
      return 1;
   }

   public boolean stop() {
      if (super.stop()) {
         boolean playState = (Boolean)this.mPropertyValue.get("PROP_PLAYBACK_STATE");
         int playChannel = (Integer)this.mPropertyValue.get("PROP_CHANNEL");
         if (playState) {
            this.mPropertyValue.put("PROP_PLAYBACK_STATE", false);
            this.mMonitorDevice.stopPlayback(playChannel);
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean handleChangePlaybackChannel(Map<String, Object> map) {
      Object val = map.get("PROP_CHANNEL");
      if (val == null) {
         return false;
      } else {
         try {
            int playChannel = (Integer)val;
            if (playChannel < 0) {
               throw new IllegalArgumentException("value of PROP_PLAYBACK_TIME is small than 0");
            } else {
               int srcChannel = (Integer)this.mPropertyValue.get("PROP_CHANNEL");
               boolean playState = (Boolean)this.mPropertyValue.get("PROP_PLAYBACK_STATE");
               if (srcChannel != playChannel) {
                  if (playState) {
                     this.mMonitorDevice.stopPlayback(srcChannel);
                  }

                  this.mPropertyValue.put("PROP_CHANNEL", playChannel);
               }

               return true;
            }
         } catch (ClassCastException var6) {
            throw new IllegalArgumentException("value of PROP_CHANNEL is illegal");
         }
      }
   }

   private boolean handleChangePlaybackTime(Map<String, Object> map) {
      Object val = map.get("PROP_PLAYBACK_TIME");
      if (val == null) {
         return false;
      } else {
         try {
            int playbackTime = (Integer)val;
            if (playbackTime <= 0) {
               throw new IllegalArgumentException("value of PROP_PLAYBACK_TIME is small than 0");
            } else {
               this.mPropertyValue.put("PROP_PLAYBACK_TIME", playbackTime);
               return true;
            }
         } catch (ClassCastException var4) {
            throw new IllegalArgumentException("value of PROP_PLAYBACK_TIME is illegal");
         }
      }
   }

   private boolean handleChangePlaybackState(Map<String, Object> map) {
      Object val = map.get("PROP_PLAYBACK_STATE");
      if (val == null) {
         return false;
      } else {
         try {
            boolean newPlayState = (Boolean)val;
            boolean playState = (Boolean)this.mPropertyValue.get("PROP_PLAYBACK_STATE");
            if (newPlayState != playState) {
               int playTime = (Integer)this.mPropertyValue.get("PROP_PLAYBACK_TIME");
               int playChannel = (Integer)this.mPropertyValue.get("PROP_CHANNEL");
               if (this.mIsStopped || playChannel < 0) {
                  return true;
               }

               if (newPlayState) {
                  if (playTime <= 0) {
                     throw new IllegalStateException("Play time has not been set!");
                  }

                  if (this.mRenderHelper != null) {
                     this.mRenderHelper.showLoading(playChannel);
                  }

                  this.mMonitorDevice.startPlayback(playTime, playChannel);
               } else {
                  this.mMonitorDevice.stopPlayback(playChannel);
                  if (this.mRenderHelper != null) {
                     this.mRenderHelper.dismissLoading(playChannel);
                  }
               }
            }

            return true;
         } catch (ClassCastException var7) {
            throw new IllegalArgumentException("value of PROP_PLAYBACK_STATE is illegal");
         }
      }
   }

   protected void onHardwareEnabled(int supportWidth, int supportHeight) {
      super.onHardwareEnabled(supportWidth, supportHeight);
      this.mMonitorDevice.enableHardwareDecoder(true, supportWidth, supportHeight, 0);
   }

   protected void onHardwareDisabled() {
      super.onHardwareDisabled();
      this.mMonitorDevice.enableHardwareDecoder(false, 0, 0, 0);
   }

   public void onPropertyChanged(Map<String, Object> map) {
      this.handleChangePlaybackChannel(map);
      this.handleChangePlaybackTime(map);
      this.handleChangePlaybackState(map);
      super.onPropertyChanged(map);
   }

   protected Object getProperty(String propName, int channel) {
      byte var4 = -1;
      switch(propName.hashCode()) {
      case -1271384057:
         if (propName.equals("PROP_CHANNEL")) {
            var4 = 1;
         }
         break;
      case 146732457:
         if (propName.equals("PROP_PLAYBACK_STATE")) {
            var4 = 0;
         }
      }

      switch(var4) {
      case 0:
      case 1:
         return this.mPropertyValue.get(propName);
      default:
         return super.getProperty(propName, channel);
      }
   }

   public void onOSDTextureAvailable2(String osd, long utcTime) {
   }
}
