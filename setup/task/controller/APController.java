package com.eseeiot.setup.task.controller;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.eseeiot.basemodule.util.NetworkUtil;
import com.eseeiot.device.pojo.DeviceInfo;
import com.eseeiot.setup.pojo.DeviceSetupInfo;
import com.eseeiot.setup.step.ContextProvider;
import com.eseeiot.setup.task.DeviceSetupType;
import com.eseeiot.setup.task.DeviceTaskManager;
import com.eseeiot.setup.task.TaskExecParam;
import com.eseeiot.setup.task.TimerHandler;
import com.eseeiot.setup.task.listener.OnTaskChangedListener;
import com.eseeiot.setup.task.tag.TaskTag;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class APController implements TaskController, OnTaskChangedListener {
   private static final String TAG = "APController";
   private static final int STATE_NONE = 0;
   private static final int STATE_START = 1;
   private static final int STATE_PAUSE = 2;
   private static final long TIMEOUT_WAIT_FOR_CONNECT_WIFI = 30000L;
   private static long TIMEOUT_PRE_CONNECT_DEVICE = 36000L;
   private CopyOnWriteArrayList<TaskController.Callback> mCallbacks = new CopyOnWriteArrayList();
   private DeviceTaskManager mTaskManager = new DeviceTaskManager(ContextProvider.getApplicationContext());
   private DeviceSetupInfo mSetupInfo;
   private boolean mLogPrint;
   private TimerHandler mTimerHandler;
   private Handler mHandler;
   private boolean mLookAsSucceed;
   private int mCurrentProgress;
   private int mCurrentState = 0;

   public APController() {
      this.mTaskManager.setCallback(this);
      this.mTaskManager.setType(DeviceSetupType.AP);
      this.mTimerHandler = new TimerHandler();
      this.mTimerHandler.setOnTimerChangedListener(new TimerHandler.OnTimerChangedListener() {
         public boolean onTimeout(TimerHandler handler) {
            return APController.this.handleTimeout(handler);
         }

         public int onValueChanged(TimerHandler handler, int value) {
            return APController.this.handleValueChanged(handler, value);
         }
      });
      this.mSetupInfo = new DeviceSetupInfo();
      this.mHandler = new Handler(Looper.getMainLooper());
      this.mCurrentProgress = -1;
   }

   public void setDebugMode(boolean enable) {
      this.mLogPrint = enable;
      if (this.mTaskManager != null) {
         this.mTaskManager.setDebugMode(this.mLogPrint);
      }

   }

   public void setSetupWifiInfo(@NonNull String ssid, @NonNull String pwd) {
      if (this.mSetupInfo != null) {
         String capabilities = "[ESS]";
         if (pwd.length() >= 1) {
            capabilities = capabilities + "[WPA-PSK-CCMP][WPA2-PSK-CCMP]";
         }

         this.mSetupInfo.getUserWifi().setSSID(ssid);
         this.mSetupInfo.getUserWifi().setPassword(pwd);
         this.mSetupInfo.getUserWifi().setCapabilities(capabilities);
      } else if (this.mLogPrint) {
         Log.d("APController", "Controller was release.");
      }

   }

   public void setTimeoutWhenPreConnect(long timeout) {
      if (timeout > 0L) {
         TIMEOUT_PRE_CONNECT_DEVICE = timeout;
      }

   }

   public void updateDevicePassword(@NonNull String pwd) {
      if (this.mSetupInfo != null) {
         this.mSetupInfo.setDevicePassword(pwd);
      } else if (this.mLogPrint) {
         Log.d("APController", "Controller was release.");
      }

   }

   public void doTask() {
      if (this.mSetupInfo != null) {
         if (this.mCurrentState == 2) {
            if (this.mTaskManager != null && this.doTask(this.mTaskManager.getTaskTag()) >= 0) {
               if (this.mTimerHandler != null) {
                  this.mTimerHandler.start(0);
               }

               this.mCurrentState = 1;
            }
         } else {
            if (TextUtils.isEmpty(this.mSetupInfo.getUserWifi().getSSID())) {
               Iterator var1 = this.mCallbacks.iterator();

               while(var1.hasNext()) {
                  TaskController.Callback callback = (TaskController.Callback)var1.next();
                  callback.receivedErrMsg(TaskTag.PREPARE_CONFIG, 22, "Please call setSetupWiFiInfo() before, and give a NOT EMPTY SSID wifi info.");
               }

               return;
            }

            if (this.mTimerHandler != null) {
               this.mTimerHandler.start(0);
            }

            if (this.doNextTask()) {
               this.mCurrentState = 1;
            }
         }
      } else if (this.mLogPrint) {
         Log.d("APController", "Task is already release ...");
      }

   }

   public void pauseTask() {
      if (this.mCurrentState == 1) {
         if (this.mTaskManager.stopTask()) {
            if (this.mTimerHandler != null) {
               this.mTimerHandler.pause();
            }

            this.mCurrentState = 2;
         }
      } else if (this.mLogPrint) {
         Log.d("APController", "Tasks NOT EXECUTING");
      }

   }

   public void stopTask() {
      if (this.mTaskManager != null) {
         this.mTaskManager.setCallback((OnTaskChangedListener)null);
         if (this.mTaskManager.isRunning()) {
            this.mTaskManager.stopTask();
         }

         this.mTaskManager.clearTask();
         this.mTaskManager = null;
      }

      this.mSetupInfo = null;
      if (this.mTimerHandler != null) {
         this.mTimerHandler.stop();
         this.mTimerHandler.setOnTimerChangedListener((TimerHandler.OnTimerChangedListener)null);
         this.mTimerHandler = null;
      }

      if (this.mHandler != null) {
         this.mHandler.removeCallbacksAndMessages((Object)null);
         this.mHandler = null;
      }

      if (this.mCallbacks != null) {
         this.mCallbacks.clear();
         this.mCallbacks = null;
      }

      this.mCurrentState = 0;
   }

   public void addCallback(@NonNull TaskController.Callback callback) {
      if (this.mCallbacks != null) {
         this.mCallbacks.add(callback);
      }

   }

   public void removeCallback(@NonNull TaskController.Callback callback) {
      if (this.mCallbacks != null) {
         this.mCallbacks.remove(callback);
      }

   }

   private boolean handleTimeout(@NonNull TimerHandler handler) {
      if (this.mTaskManager == null) {
         return true;
      } else {
         this.mTaskManager.stopTask();
         if (this.mLookAsSucceed) {
            Log.d("APController", "run: Configure timeout, main step was success, it look as success.");
         } else {
            handler.stop();
            if (this.mHandler != null) {
               this.mHandler.postDelayed(() -> {
                  Log.d("APController", "run: Configure timeout, failed.");
                  this.notifyConfigFailed((TaskTag)null, -1, (String)null);
                  this.stopTask();
               }, 1000L);
            }
         }

         return !this.mLookAsSucceed;
      }
   }

   private int handleValueChanged(@NonNull TimerHandler handler, int value) {
      if (this.mTaskManager != null) {
         if (value == -1) {
            this.mCurrentProgress = 0;
         } else {
            if (value <= this.mCurrentProgress) {
               return this.mCurrentProgress;
            }

            this.mCurrentProgress = value;
            if (this.mHandler != null) {
               this.mHandler.post(() -> {
                  Iterator var1 = this.mCallbacks.iterator();

                  while(var1.hasNext()) {
                     TaskController.Callback callback = (TaskController.Callback)var1.next();
                     callback.progressValueChange(this.mCurrentProgress);
                  }

               });
            }
         }

         TaskTag taskTag = this.mTaskManager.getTaskTag();
         if (taskTag == TaskTag.CONNECT_DEVICE) {
            if (value > 85) {
               handler.setBaseTimeRoot(5000);
               handler.setRandomTimeRoot(300);
            } else if (value > 70) {
               handler.setBaseTimeRoot(2000);
            }
         }

         if (value >= 100) {
            Log.d("APController", "onValueChanged: All configure step is end. ");
            if (this.mHandler != null) {
               this.mHandler.postDelayed(() -> {
                  Iterator var1 = this.mCallbacks.iterator();

                  while(var1.hasNext()) {
                     TaskController.Callback callback = (TaskController.Callback)var1.next();
                     callback.onConfigResult(true, this.genDeviceInfo());
                  }

                  this.stopTask();
               }, 1500L);
            }

            this.mTaskManager.stopTask();
         }
      }

      return 0;
   }

   private boolean doNextTask() {
      if (this.mTaskManager == null) {
         return false;
      } else {
         if (this.mTimerHandler != null) {
            this.mTimerHandler.setValue(this.mTaskManager.getProgress());
         }

         boolean run = true;
         TaskTag taskTag = this.mTaskManager.nextTask();
         if (taskTag == null) {
            if (this.mLogPrint) {
               Log.d("APController", "doNextTask: All task execute finish.");
            }

            if (this.mHandler != null) {
               this.mHandler.postDelayed(() -> {
                  Iterator var1 = this.mCallbacks.iterator();

                  while(var1.hasNext()) {
                     TaskController.Callback callback = (TaskController.Callback)var1.next();
                     callback.onConfigResult(true, this.genDeviceInfo());
                  }

                  this.stopTask();
               }, 150L);
            }
         } else {
            int ret = this.doTask(taskTag);
            if (ret >= 0 && this.mHandler != null) {
               this.mHandler.post(() -> {
                  String msg = null;
                  if (taskTag.equals(TaskTag.SEARCH_DEVICE_ON_AP)) {
                     msg = "Target device's id is: " + this.mSetupInfo.getEseeId();
                  } else if (taskTag.equals(TaskTag.CONNECT_DEVICE_AP)) {
                     msg = "Please connect a wifi start with [IPC]";
                  }

                  Iterator var3 = this.mCallbacks.iterator();

                  while(var3.hasNext()) {
                     TaskController.Callback callback = (TaskController.Callback)var3.next();
                     callback.onStepChange(taskTag, msg);
                  }

               });
            }

            if (ret == 1) {
               this.doNextTask();
            } else {
               run = ret != -1;
            }
         }

         if (!run) {
            if (this.mTimerHandler != null) {
               this.mTimerHandler.stop();
            }

            if (this.mHandler != null) {
               this.mHandler.post(() -> {
                  this.notifyConfigFailed((TaskTag)null, -1, (String)null);
                  this.stopTask();
               });
            }
         }

         return run;
      }
   }

   private int doTask(@NonNull TaskTag taskTag) {
      TaskExecParam param = new TaskExecParam();
      this.getTaskParam(taskTag, param);
      if (param.skip) {
         return 1;
      } else {
         if (!param.bbreak) {
            if (!this.mTaskManager.doTask(taskTag, param.delayTime, param.objects)) {
               return -1;
            }

            if (!param.sync) {
               return 1;
            }
         }

         return 0;
      }
   }

   private void getTaskParam(@NonNull TaskTag taskTag, @NonNull TaskExecParam execParam) {
      switch(taskTag) {
      case CONNECT_DEVICE_AP:
         if (this.mTimerHandler != null) {
            this.mTimerHandler.pause();
         }
      case SEND_WIFI_INFO_TO_DEVICE:
      case WAIT_FOR_CONNECT_WIFI_AUTO:
         execParam.objects = new Object[1];
         execParam.objects[0] = this.mSetupInfo;
         break;
      case SEARCH_DEVICE_ON_AP:
         execParam.objects = new Object[4];
         execParam.objects[0] = this.mSetupInfo;
         execParam.objects[1] = true;
         execParam.objects[2] = false;
         execParam.objects[3] = false;
         break;
      case CONNECT_DEVICE:
         execParam.objects = new Object[4];
         execParam.objects[0] = this.mSetupInfo;
         execParam.objects[1] = false;
         execParam.objects[2] = true;
         execParam.objects[3] = 2;
         if (!this.mLookAsSucceed) {
            execParam.delayTime = 4000L;
         }
         break;
      case GET_DEVICE_SETUP_INFO:
         if (this.mSetupInfo.getVconInfo() != null) {
            if (this.mLogPrint) {
               Log.d("APController", "Got target device remote info, skip exec " + taskTag);
            }

            execParam.skip = true;
         } else if (!this.mSetupInfo.isPreConnected()) {
            if (this.mLogPrint) {
               Log.d("APController", "Connect device's AP failed, skip exec " + taskTag);
            }

            execParam.skip = true;
         } else {
            execParam.objects = new Object[1];
            execParam.objects[0] = this.mSetupInfo;
            execParam.delayTime = 1000L;
         }
         break;
      case SET_TIMEZONE_FOR_DEVICE:
         if (!this.mSetupInfo.isPreConnected()) {
            if (this.mLogPrint) {
               Log.d("APController", "Connect device's AP failed, skip exec " + taskTag);
            }

            execParam.skip = true;
         } else {
            execParam.objects = new Object[1];
            execParam.objects[0] = this.mSetupInfo;
         }
      }

   }

   public boolean onTaskTimeout(@NonNull TaskTag taskTag, @Nullable Object object, long costTime) {
      switch(taskTag) {
      case CONNECT_DEVICE_AP:
      default:
         break;
      case SEND_WIFI_INFO_TO_DEVICE:
         if (this.mLogPrint) {
            Log.d("APController", "Send wifi information timeout, do next task.");
         }

         if (this.mTimerHandler != null) {
            this.mTimerHandler.postDelayed(this::doNextTask, 10L);
         }

         return true;
      case WAIT_FOR_CONNECT_WIFI_AUTO:
         if (costTime >= 30000L) {
            if (this.mLogPrint) {
               Log.d("APController", "Waiting wifi connect timeout, do next task.");
            }

            if (this.mTimerHandler != null) {
               this.mTimerHandler.postDelayed(this::doNextTask, 10L);
            }

            return true;
         }
         break;
      case SEARCH_DEVICE_ON_AP:
         this.notifyConfigFailed(taskTag, 21, "Can not search target device [" + this.mSetupInfo.getEseeId() + "].");
         break;
      case CONNECT_DEVICE:
         if (costTime >= TIMEOUT_PRE_CONNECT_DEVICE) {
            if (this.mLookAsSucceed) {
               if (this.mTimerHandler != null) {
                  this.mTimerHandler.setValue(100);
               }
            } else {
               this.notifyConfigFailed(taskTag, 23, "Connect target device timeout, cost time: " + costTime + " ms.");
            }

            return true;
         }
         break;
      case GET_DEVICE_SETUP_INFO:
      case SET_TIMEZONE_FOR_DEVICE:
         long timeout = this.mTaskManager.getTimeoutDuration();
         if (timeout == 0L || costTime >= timeout * 2L) {
            if (this.mTimerHandler != null) {
               this.mTimerHandler.postDelayed(() -> {
                  if (this.mLogPrint) {
                     Log.d("APController", "handleTimeout: P2P channel " + taskTag + " timeout, retry to connect.");
                  }

                  if (this.doTask(TaskTag.CONNECT_DEVICE) == -1 && this.doTask(TaskTag.CONNECT_DEVICE) == -1 && this.mHandler != null) {
                     this.mHandler.post(() -> {
                        this.notifyConfigFailed(taskTag, 32, "Connect target device failed.");
                     });
                  }

               }, 10L);
            }

            return true;
         }

         if (this.mLogPrint) {
            Log.d("APController", "handleTimeout: P2P channel " + taskTag + "timeout, retry.");
         }
      }

      return false;
   }

   public void onTaskChanged(@NonNull TaskTag taskTag, @Nullable Object object, boolean finish) {
      switch(taskTag) {
      case CONNECT_DEVICE_AP:
         if (this.mTimerHandler != null) {
            this.mTimerHandler.start(0);
         }

         this.mSetupInfo = (DeviceSetupInfo)object;
      case SEND_WIFI_INFO_TO_DEVICE:
      case WAIT_FOR_CONNECT_WIFI_AUTO:
      default:
         break;
      case CONNECT_DEVICE:
         this.mLookAsSucceed = true;
      case SEARCH_DEVICE_ON_AP:
      case GET_DEVICE_SETUP_INFO:
      case SET_TIMEZONE_FOR_DEVICE:
         if (object instanceof DeviceSetupInfo) {
            this.mSetupInfo = (DeviceSetupInfo)object;
         }
      }

      if (finish) {
         this.doNextTask();
      }

   }

   public void onTaskError(@NonNull TaskTag taskTag, @Nullable Object object) {
      int errCode;
      switch(taskTag) {
      case SEND_WIFI_INFO_TO_DEVICE:
         if (object instanceof Integer && -30 == (Integer)object) {
            if (this.mLogPrint) {
               Object[] objects = NetworkUtil.getCurrentConnectWifi(ContextProvider.getApplicationContext());
               String ssid = objects == null ? "unknown ssid" : (String)objects[0];
               Log.d("APController", "Current connect wifi [" + ssid + "] is NOT DEVICE HOTSPOT, ignore, do next task.");
            }

            this.doNextTask();
         }
         break;
      case WAIT_FOR_CONNECT_WIFI_AUTO:
         if (object instanceof Integer && -27 == (Integer)object) {
            if (this.mTimerHandler != null) {
               this.mTimerHandler.postDelayed(this::doNextTask, 10L);
            }

            if (this.mLogPrint) {
               Log.d("APController", "Not wifi connecting for long time.");
            }

            return;
         }
      case CONNECT_DEVICE_AP:
      case SEARCH_DEVICE_ON_AP:
         if (object instanceof Integer) {
            errCode = (Integer)object;
            if (errCode == -22) {
               this.notifyConfigFailed(taskTag, 20, "Please turn on WiFi toggle.");
            } else if (errCode == -3) {
               this.notifyConfigFailed(taskTag, 19, "Please turn on GPS toggle.");
            }
         }
         break;
      case CONNECT_DEVICE:
         if (object instanceof Integer) {
            errCode = (Integer)object;
            switch(errCode) {
            case -26:
               if (this.mTimerHandler != null) {
                  this.mTimerHandler.postDelayed(() -> {
                     if (this.doTask(TaskTag.WAIT_FOR_CONNECT_WIFI_AUTO) == -1 && this.mHandler != null) {
                        this.mHandler.post(() -> {
                           this.notifyConfigFailed(taskTag, 25, "You should disconnect device's AP");
                        });
                     }

                  }, 10L);
               }
               break;
            case -25:
               if (this.mLookAsSucceed) {
                  this.mTimerHandler.setValue(100);
               } else {
                  if (this.mLogPrint) {
                     Log.d("APController", "run: configure failed, network is bad!");
                  }

                  this.notifyConfigFailed(taskTag, 24, "Network is bad.");
               }
               break;
            case -24:
               Iterator var8 = this.mCallbacks.iterator();

               while(var8.hasNext()) {
                  TaskController.Callback callback = (TaskController.Callback)var8.next();
                  callback.receivedErrMsg(taskTag, 33, "Device's password NOT EMPTY, please update password and retry.");
               }
            }
         }
         break;
      case GET_DEVICE_SETUP_INFO:
         Iterator var3 = this.mCallbacks.iterator();

         while(var3.hasNext()) {
            TaskController.Callback callback = (TaskController.Callback)var3.next();
            callback.receivedErrMsg(taskTag, 33, "Detach device has password, please update the password.");
         }

         return;
      case SET_TIMEZONE_FOR_DEVICE:
         this.notifyConfigFailed(taskTag, 34, "Set timezone to device failed, please retry again.");
      }

   }

   private void notifyConfigFailed(@Nullable TaskTag taskTag, int errCode, @Nullable String errMsg) {
      this.mCurrentState = 0;

      TaskController.Callback callback;
      for(Iterator var4 = this.mCallbacks.iterator(); var4.hasNext(); callback.onConfigResult(false, (DeviceInfo)null)) {
         callback = (TaskController.Callback)var4.next();
         if (taskTag != null && errMsg != null) {
            callback.receivedErrMsg(taskTag, errCode, errMsg);
         }
      }

   }

   @Nullable
   private DeviceInfo genDeviceInfo() {
      if (this.mSetupInfo != null) {
         DeviceInfo deviceInfo = new DeviceInfo();
         deviceInfo.setDeviceId(this.mSetupInfo.getEseeId());
         deviceInfo.setChannelCount(this.mSetupInfo.getChannelCount());
         deviceInfo.setUsername(this.mSetupInfo.getDeviceUser());
         deviceInfo.setPwd(this.mSetupInfo.getDevicePassword());
         return deviceInfo;
      } else {
         return null;
      }
   }
}
