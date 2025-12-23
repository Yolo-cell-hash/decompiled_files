package com.eseeiot.setup.step;

import android.os.Build.VERSION;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.eseeiot.basemodule.util.NetworkUtil;
import com.eseeiot.device.pojo.DeviceInfo;
import com.eseeiot.setup.task.controller.APController;
import com.eseeiot.setup.task.controller.TaskController;
import com.eseeiot.setup.task.tag.TaskTag;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class APStep {
   private static final String TAG = "APStep";
   public static final int ERR_LOCATION_PERMISSION_REQUEST = 17;
   public static final int ERR_WIFI_PERMISSION_REQUEST = 18;
   public static final int ERR_GPS_DISABLE = 19;
   public static final int ERR_WIFI_DISABLE = 20;
   public static final int ERR_TARGET_DEVICE_NOT_FOUND = 21;
   public static final int ERR_CONFIG_WIFI_NOT_SET = 22;
   public static final int ERR_CONNECT_DEVICE_TIMEOUT = 23;
   public static final int ERR_NETWORK_BAD = 24;
   public static final int ERR_CONNECT_ON_DEVICE_AP = 25;
   public static final int ERR_CONNECT_DEVICE_FAILED = 32;
   public static final int ERR_CONNECT_PASSWORD_ERROR = 33;
   public static final int ERR_SET_DEVICE_TIMEZONE_FAILED = 34;
   private static final CopyOnWriteArrayList<TaskController.Callback> sCallbacks = new CopyOnWriteArrayList();
   private static APController sController;
   private static boolean sLogPrint;

   private APStep() {
   }

   public static boolean start() {
      if (sController != null) {
         if (sLogPrint) {
            Log.d("APStep", "Already start.");
         }

         return false;
      } else {
         Iterator var0;
         TaskController.Callback callback;
         if (!checkLocationPermission()) {
            var0 = sCallbacks.iterator();

            while(var0.hasNext()) {
               callback = (TaskController.Callback)var0.next();
               callback.receivedErrMsg(TaskTag.PREPARE_CONFIG, 17, "Request [android.permission.ACCESS_COARSE_LOCATION] permission before.");
            }

            return false;
         } else if (!checkWiFiPermission()) {
            var0 = sCallbacks.iterator();

            while(var0.hasNext()) {
               callback = (TaskController.Callback)var0.next();
               callback.receivedErrMsg(TaskTag.PREPARE_CONFIG, 18, "Request [android.permission.CHANGE_WIFI_STATE] permission before.");
            }

            return false;
         } else if (!checkWifiStatus()) {
            var0 = sCallbacks.iterator();

            while(var0.hasNext()) {
               callback = (TaskController.Callback)var0.next();
               callback.receivedErrMsg(TaskTag.PREPARE_CONFIG, 20, "Please turn on WiFi toggle.");
            }

            return false;
         } else if (checkGpsStatus()) {
            sController = new APController();
            sController.addCallback(new TaskController.Callback() {
               public void receivedErrMsg(@NonNull TaskTag taskTag, int errCode, @NonNull String errMsg) {
                  Iterator var4 = APStep.sCallbacks.iterator();

                  while(var4.hasNext()) {
                     TaskController.Callback callback = (TaskController.Callback)var4.next();
                     callback.receivedErrMsg(taskTag, errCode, errMsg);
                  }

                  if (errCode == 33) {
                     APStep.sController.pauseTask();
                  }

               }

               public void progressValueChange(int progress) {
                  Iterator var2 = APStep.sCallbacks.iterator();

                  while(var2.hasNext()) {
                     TaskController.Callback callback = (TaskController.Callback)var2.next();
                     callback.progressValueChange(progress);
                  }

               }

               public void onStepChange(@NonNull TaskTag taskTag, @Nullable String msg) {
                  Iterator var3 = APStep.sCallbacks.iterator();

                  while(var3.hasNext()) {
                     TaskController.Callback callback = (TaskController.Callback)var3.next();
                     callback.onStepChange(taskTag, msg);
                  }

                  if (APStep.sLogPrint && TaskTag.CONNECT_DEVICE_AP.equals(taskTag)) {
                     Log.d("APStep", "You should connect a wifi when ssid is start with [IPC]");
                  }

               }

               public void onConfigResult(boolean success, @Nullable DeviceInfo deviceInfo) {
                  Iterator var3 = APStep.sCallbacks.iterator();

                  while(var3.hasNext()) {
                     TaskController.Callback callback = (TaskController.Callback)var3.next();
                     callback.onConfigResult(success, deviceInfo);
                  }

                  APStep.sController.removeCallback(this);
                  APStep.stop();
               }
            });
            sController.setDebugMode(sLogPrint);
            return true;
         } else {
            var0 = sCallbacks.iterator();

            while(var0.hasNext()) {
               callback = (TaskController.Callback)var0.next();
               callback.receivedErrMsg(TaskTag.PREPARE_CONFIG, 19, "Please turn on GPS toggle.");
            }

            return false;
         }
      }
   }

   public static void stop() {
      if (sController != null) {
         sController.stopTask();
         sController = null;
      } else if (sLogPrint) {
         Log.d("APStep", "Already stop.");
      }

   }

   public static boolean addCallback(@NonNull TaskController.Callback callback) {
      return sCallbacks.add(callback);
   }

   public static boolean removeCallback(@NonNull TaskController.Callback callback) {
      return sCallbacks.remove(callback);
   }

   public static void setSetupWifiInfo(@NonNull String ssid, @NonNull String pwd) {
      if (sController != null) {
         sController.setSetupWifiInfo(ssid, pwd);
      } else if (sLogPrint) {
         Log.d("APStep", "You should call start() before.");
      }

   }

   public static void updateDevicePassword(@NonNull String pwd) {
      if (sController != null) {
         sController.updateDevicePassword(pwd);
      } else if (sLogPrint) {
         Log.d("APStep", "You should call start() before.");
      }

   }

   public static void setTimeoutWhenPreConnect(long timeout) {
      if (sController != null) {
         sController.setTimeoutWhenPreConnect(timeout);
      } else if (sLogPrint) {
         Log.d("APStep", "You should call start() before.");
      }

   }

   public static void setDebugMode(boolean enable) {
      sLogPrint = enable;
      if (sController != null) {
         sController.setDebugMode(sLogPrint);
      }

   }

   @Nullable
   public static TaskController getProcessController() {
      if (sController == null && sLogPrint) {
         Log.d("APStep", "You should call start() before.");
      }

      return sController;
   }

   private static boolean checkLocationPermission() {
      if (VERSION.SDK_INT >= 23) {
         return ActivityCompat.checkSelfPermission(ContextProvider.getApplicationContext(), "android.permission.ACCESS_COARSE_LOCATION") == 0;
      } else {
         return true;
      }
   }

   private static boolean checkWiFiPermission() {
      if (VERSION.SDK_INT >= 23) {
         return ActivityCompat.checkSelfPermission(ContextProvider.getApplicationContext(), "android.permission.CHANGE_WIFI_STATE") == 0;
      } else {
         return true;
      }
   }

   private static boolean checkGpsStatus() {
      return NetworkUtil.isGpsEnable(ContextProvider.getApplicationContext());
   }

   private static boolean checkWifiStatus() {
      return NetworkUtil.isWifiEnable(ContextProvider.getApplicationContext());
   }
}
