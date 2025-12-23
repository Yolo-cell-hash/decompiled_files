package com.eseeiot.setup.task.controller;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.eseeiot.basemodule.util.DeviceTool;
import com.eseeiot.basemodule.util.EncryptionUtil;
import com.eseeiot.device.pojo.DeviceInfo;
import com.eseeiot.setup.pojo.DeviceSetupInfo;
import com.eseeiot.setup.pojo.LanguageComparison;
import com.eseeiot.setup.step.ContextProvider;
import com.eseeiot.setup.task.DeviceSetupType;
import com.eseeiot.setup.task.DeviceTaskManager;
import com.eseeiot.setup.task.TaskExecParam;
import com.eseeiot.setup.task.TimerHandler;
import com.eseeiot.setup.task.listener.OnTaskChangedListener;
import com.eseeiot.setup.task.tag.TaskTag;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

public class QRController implements TaskController, OnTaskChangedListener {
   private static final String TAG = "QRController";
   protected static final int STATE_NONE = 0;
   protected static final int STATE_START = 1;
   protected static final int STATE_PAUSE = 2;
   private CopyOnWriteArrayList<TaskController.Callback> mCallbacks = new CopyOnWriteArrayList();
   protected DeviceTaskManager mTaskManager = new DeviceTaskManager(ContextProvider.getApplicationContext());
   private DeviceSetupInfo mSetupInfo;
   private boolean mLogPrint;
   private TimerHandler mTimerHandler;
   private Handler mHandler;
   private int mCurrentProgress;
   protected int mCurrentState = 0;
   private boolean mConfigSuccess;

   public QRController(String devId) {
      this.mTaskManager.setCallback(this);
      this.mTaskManager.setType(DeviceSetupType.QR);
      this.mTimerHandler = new TimerHandler();
      this.mTimerHandler.setOnTimerChangedListener(new TimerHandler.OnTimerChangedListener() {
         public boolean onTimeout(TimerHandler handler) {
            if (QRController.this.mTaskManager != null) {
               long costTime = QRController.this.mTaskManager.getCostTime();
               if (costTime >= 180000L) {
                  handler.stop();
                  if (QRController.this.mHandler != null) {
                     QRController.this.mHandler.postDelayed(() -> {
                        Log.d(QRController.this.getTag(), "run: Configure timeout, failed.");
                        QRController.this.notifyConfigFailed((TaskTag)null, -1, (String)null);
                        QRController.this.stopTask();
                     }, 1000L);
                  }

                  return true;
               }
            }

            return false;
         }

         public int onValueChanged(TimerHandler handler, int value) {
            return QRController.this.handleValueChanged(handler, value);
         }
      });
      this.mSetupInfo = new DeviceSetupInfo();
      this.mSetupInfo.setEseeId(devId);
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
         Log.d(this.getTag(), "Controller was release.");
      }

   }

   public String getConfigQrCode() {
      if (this.mSetupInfo != null && !TextUtils.isEmpty(this.mSetupInfo.getEseeId())) {
         String ssid = this.mSetupInfo.getUserWifi().getSSID();
         String password = this.mSetupInfo.getUserWifi().getPassword();
         if (TextUtils.isEmpty(ssid)) {
            if (this.mLogPrint) {
               Log.d(this.getTag(), "Please call [setSetupWifiInfo] fun first!");
            }

            return null;
         } else {
            String languageStr = this.getAssetFileToString(ContextProvider.getApplicationContext(), "language_comparison_table.json");
            List<LanguageComparison> list = null;
            if (!TextUtils.isEmpty(languageStr)) {
               list = (List)(new Gson()).fromJson(languageStr, (new TypeToken<List<LanguageComparison>>() {
               }).getType());
            }

            if (list != null && !list.isEmpty()) {
               String result = "V=5&E=" + EncryptionUtil.encodeBase64(ssid) + "&P=" + EncryptionUtil.encodeBase64(password) + "&T=" + this.getTimezoneStr() + "&L=" + this.getLanguageCountry(list);
               if (this.shouldAppendIDSuffixInQrCode()) {
                  String eseeId = this.mSetupInfo.getEseeId();
                  if (!TextUtils.isEmpty(eseeId) && eseeId.length() >= 4) {
                     result = result + "&I=" + eseeId.substring(eseeId.length() - 4);
                  }
               }

               if (this.mLogPrint) {
                  Log.d(this.getTag(), "getConfigQrCode: [ssid] - " + ssid + ", [password] - " + password + ", [result] - " + result);
               }

               return result;
            } else {
               if (this.mLogPrint) {
                  Log.d(this.getTag(), "Make sure [language_comparison_table.json] in the project assets.");
               }

               return null;
            }
         }
      } else {
         if (this.mLogPrint) {
            Log.d(this.getTag(), "Device's id is NULL or Empty.");
         }

         return null;
      }
   }

   public void updateDevicePassword(@NonNull String pwd) {
      if (this.mSetupInfo != null) {
         this.mSetupInfo.setDevicePassword(pwd);
      } else if (this.mLogPrint) {
         Log.d(this.getTag(), "Controller was release.");
      }

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

   public void doTask() {
      if (this.mSetupInfo != null) {
         this.mConfigSuccess = false;
         if (this.mCurrentState == 2) {
            if (this.mTaskManager != null && this.doTask(this.mTaskManager.getTaskTag()) >= 0) {
               if (this.mTimerHandler != null) {
                  this.mTimerHandler.start(0);
               }

               this.mCurrentState = 1;
            }
         } else {
            Iterator var1;
            TaskController.Callback callback;
            if (TextUtils.isEmpty(this.mSetupInfo.getEseeId()) || !DeviceTool.isCommonEseeId(this.mSetupInfo.getEseeId())) {
               var1 = this.mCallbacks.iterator();

               while(var1.hasNext()) {
                  callback = (TaskController.Callback)var1.next();
                  callback.receivedErrMsg(TaskTag.PREPARE_CONFIG, 50, "Please provider a valid device's id.");
               }

               return;
            }

            if (TextUtils.isEmpty(this.mSetupInfo.getUserWifi().getSSID())) {
               var1 = this.mCallbacks.iterator();

               while(var1.hasNext()) {
                  callback = (TaskController.Callback)var1.next();
                  callback.receivedErrMsg(TaskTag.PREPARE_CONFIG, 49, "Please call setSetupWiFiInfo() before, and give a NOT EMPTY SSID wifi info.");
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
         Log.d(this.getTag(), "Task is already release ...");
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
         Log.d(this.getTag(), "Tasks NOT EXECUTING");
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

   public boolean onTaskTimeout(@NonNull TaskTag taskTag, @Nullable Object object, long costTime) {
      return false;
   }

   public void onTaskChanged(@NonNull TaskTag taskTag, @Nullable Object object, boolean finish) {
      switch(taskTag) {
      case CONNECT_DEVICE:
         this.mConfigSuccess = true;
         if (this.mTimerHandler != null) {
            this.mTimerHandler.start(0);
         }

         this.mSetupInfo = (DeviceSetupInfo)object;
      default:
         if (finish) {
            this.doNextTask();
         }

      }
   }

   public void onTaskError(@NonNull TaskTag taskTag, @Nullable Object object) {
      switch(taskTag) {
      case CONNECT_DEVICE:
         if (object instanceof Integer) {
            int errCode = (Integer)object;
            switch(errCode) {
            case -26:
               if (this.mTimerHandler != null) {
                  this.mTimerHandler.postDelayed(() -> {
                     this.mHandler.post(() -> {
                        this.notifyConfigFailed(taskTag, 51, "You should disconnect device's AP");
                     });
                  }, 10L);
               }
               break;
            case -25:
               if (this.mLogPrint) {
                  Log.d(this.getTag(), "run: configure failed, network is bad!");
               }

               this.notifyConfigFailed(taskTag, 24, "Network is bad.");
               break;
            case -24:
               Iterator var4 = this.mCallbacks.iterator();

               while(var4.hasNext()) {
                  TaskController.Callback callback = (TaskController.Callback)var4.next();
                  callback.receivedErrMsg(taskTag, 48, "Device's password NOT EMPTY, please update password and retry.");
               }
            }
         }
      default:
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
      if (taskTag == TaskTag.CONNECT_DEVICE) {
         execParam.objects = new Object[4];
         execParam.objects[0] = this.mSetupInfo;
         execParam.objects[1] = false;
         execParam.objects[2] = true;
         execParam.objects[3] = 2;
         execParam.delayTime = this.getPreConnectTaskDelayTime();
      }

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
               Log.d(this.getTag(), "doNextTask: All task execute finish.");
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
                  Iterator var2 = this.mCallbacks.iterator();

                  while(var2.hasNext()) {
                     TaskController.Callback callback = (TaskController.Callback)var2.next();
                     callback.onStepChange(taskTag, (String)null);
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

   protected void notifyConfigFailed(@Nullable TaskTag taskTag, int errCode, @Nullable String errMsg) {
      this.mCurrentState = 0;

      TaskController.Callback callback;
      for(Iterator var4 = this.mCallbacks.iterator(); var4.hasNext(); callback.onConfigResult(false, (DeviceInfo)null)) {
         callback = (TaskController.Callback)var4.next();
         if (taskTag != null && errMsg != null) {
            callback.receivedErrMsg(taskTag, errCode, errMsg);
         }
      }

   }

   protected long getPreConnectTaskDelayTime() {
      return 0L;
   }

   protected boolean shouldAppendIDSuffixInQrCode() {
      return false;
   }

   protected String getTag() {
      return "QRController";
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

         if (!this.mConfigSuccess && value >= 85) {
            if (this.mLogPrint) {
               Log.d(this.getTag(), "handleValueChanged: " + value);
            }

            if (this.mHandler != null) {
               this.mHandler.post(() -> {
                  this.mTimerHandler.stop();
                  Log.d(this.getTag(), "run: Configure timeout, failed.");
                  this.notifyConfigFailed((TaskTag)null, -1, (String)null);
                  this.stopTask();
               });
            }

            return 0;
         }

         if (value >= 100) {
            Log.d(this.getTag(), "onValueChanged: All configure step is end. ");
            if (this.mHandler != null) {
               this.mHandler.postDelayed(() -> {
                  if (this.mConfigSuccess) {
                     Iterator var1 = this.mCallbacks.iterator();

                     while(var1.hasNext()) {
                        TaskController.Callback callback = (TaskController.Callback)var1.next();
                        callback.onConfigResult(true, this.genDeviceInfo());
                     }
                  } else {
                     this.notifyConfigFailed((TaskTag)null, -1, (String)null);
                  }

                  this.stopTask();
               }, 1500L);
            }

            this.mTaskManager.stopTask();
         }

         if (this.mLogPrint) {
            Log.d(this.getTag(), "handleValueChanged: " + value);
         }
      }

      return 0;
   }

   private String getTimezoneStr() {
      int rawOffset = TimeZone.getDefault().getRawOffset();
      int timeZone = rawOffset / '負';
      float tempTimezone = (float)timeZone * 1.0F / 100.0F;
      int hour = (int)tempTimezone;
      int minute = (int)((tempTimezone - (float)hour) * 60.0F);
      timeZone = hour * 100 + minute;
      DecimalFormat decimalFormat = new DecimalFormat("0000");
      String result = decimalFormat.format((long)timeZone);
      return timeZone >= 0 ? "+" + result : result;
   }

   private String getLanguageCountry(List<LanguageComparison> languageList) {
      String language = Locale.getDefault().getLanguage();
      String regLanguage = Locale.getDefault().toString();
      String result = "EN";
      if (languageList != null && !languageList.isEmpty()) {
         boolean found = false;
         Iterator var6;
         LanguageComparison comparison;
         if (!TextUtils.isEmpty(regLanguage)) {
            var6 = languageList.iterator();

            while(var6.hasNext()) {
               comparison = (LanguageComparison)var6.next();
               if (regLanguage.startsWith(comparison.getCountry())) {
                  result = comparison.getAbbreviation();
                  found = true;
                  break;
               }
            }
         }

         if (!found && !TextUtils.isEmpty(language)) {
            var6 = languageList.iterator();

            while(var6.hasNext()) {
               comparison = (LanguageComparison)var6.next();
               if (comparison.getCountry().startsWith(language + "_")) {
                  result = comparison.getAbbreviation();
                  break;
               }
            }
         }
      }

      return result;
   }

   private String getAssetFileToString(Context context, String fileName) {
      AssetManager assetManager = context.getAssets();
      InputStream inputStream = null;

      try {
         inputStream = assetManager.open(fileName);
         if (inputStream != null) {
            int size = inputStream.available();
            int len = true;
            byte[] bytes = new byte[size];
            inputStream.read(bytes);
            inputStream.close();
            return new String(bytes);
         }
      } catch (IOException var8) {
         var8.printStackTrace();
      }

      return null;
   }
}
