package com.eseeiot.setup.step;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.eseeiot.device.pojo.DeviceInfo;
import com.eseeiot.setup.task.controller.QRController;
import com.eseeiot.setup.task.controller.TaskController;
import com.eseeiot.setup.task.tag.TaskTag;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class QRStep {
   private static final String TAG = "QRStep";
   public static final int ERR_CONNECT_PASSWORD_ERROR = 48;
   public static final int ERR_CONFIG_WIFI_NOT_SET = 49;
   public static final int ERR_CONFIG_INVALID_ID = 50;
   public static final int ERR_CONNECT_ON_DEVICE_AP = 51;
   protected static final CopyOnWriteArrayList<TaskController.Callback> sCallbacks = new CopyOnWriteArrayList();
   protected static QRController sController;
   protected static boolean sLogPrint;

   protected QRStep() {
   }

   public static boolean start(String devId) {
      if (sController == null) {
         sController = new QRController(devId);
         sController.addCallback(new TaskController.Callback() {
            public void receivedErrMsg(@NonNull TaskTag taskTag, int errCode, @NonNull String errMsg) {
               Iterator var4 = QRStep.sCallbacks.iterator();

               while(var4.hasNext()) {
                  TaskController.Callback callback = (TaskController.Callback)var4.next();
                  callback.receivedErrMsg(taskTag, errCode, errMsg);
               }

               if (errCode == 48) {
                  QRStep.sController.pauseTask();
               }

            }

            public void progressValueChange(int progress) {
               Iterator var2 = QRStep.sCallbacks.iterator();

               while(var2.hasNext()) {
                  TaskController.Callback callback = (TaskController.Callback)var2.next();
                  callback.progressValueChange(progress);
               }

            }

            public void onStepChange(@NonNull TaskTag taskTag, @Nullable String msg) {
               Iterator var3 = QRStep.sCallbacks.iterator();

               while(var3.hasNext()) {
                  TaskController.Callback callback = (TaskController.Callback)var3.next();
                  callback.onStepChange(taskTag, msg);
               }

            }

            public void onConfigResult(boolean success, @Nullable DeviceInfo deviceInfo) {
               Iterator var3 = QRStep.sCallbacks.iterator();

               while(var3.hasNext()) {
                  TaskController.Callback callback = (TaskController.Callback)var3.next();
                  callback.onConfigResult(success, deviceInfo);
               }

               QRStep.sController.removeCallback(this);
               QRStep.stop();
            }
         });
         sController.setDebugMode(sLogPrint);
         return true;
      } else {
         if (sLogPrint) {
            Log.d("QRStep", "Already start.");
         }

         return false;
      }
   }

   public static void stop() {
      if (sController != null) {
         sController.stopTask();
         sController = null;
      } else if (sLogPrint) {
         Log.d("QRStep", "Already stop.");
      }

   }

   @Nullable
   public static TaskController getProcessController() {
      if (sController == null && sLogPrint) {
         Log.d("QRStep", "You should call start() before.");
      }

      return sController;
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
         Log.d("QRStep", "You should call start() before.");
      }

   }

   public static String getConfigQrCode() {
      if (sController != null) {
         return sController.getConfigQrCode();
      } else {
         if (sLogPrint) {
            Log.d("QRStep", "You should call start() before.");
         }

         return null;
      }
   }

   public static void updateDevicePassword(@NonNull String pwd) {
      if (sController != null) {
         sController.updateDevicePassword(pwd);
      } else if (sLogPrint) {
         Log.d("QRStep", "You should call start() before.");
      }

   }

   public static void setDebugMode(boolean enable) {
      sLogPrint = enable;
      if (sController != null) {
         sController.setDebugMode(sLogPrint);
      }

   }
}
