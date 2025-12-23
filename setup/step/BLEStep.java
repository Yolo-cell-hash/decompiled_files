package com.eseeiot.setup.step;

import android.bluetooth.BluetoothManager;
import android.os.Build.VERSION;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.eseeiot.device.pojo.DeviceInfo;
import com.eseeiot.setup.task.controller.BLEController;
import com.eseeiot.setup.task.controller.TaskController;
import com.eseeiot.setup.task.tag.TaskTag;
import java.util.Iterator;

public class BLEStep extends QRStep {
   private static final String TAG = "QRStep";
   public static final int ERR_BLE_DISABLE_OR_FAULT = 64;
   public static final int ERR_PERMISSION_NEARBY_MISS = 65;

   private BLEStep() {
   }

   public static boolean start(String devId) {
      if (sController != null) {
         if (sLogPrint) {
            Log.d("QRStep", "Already start.");
         }

         return false;
      } else {
         Iterator var1;
         TaskController.Callback callback;
         if (!checkBLEStatus()) {
            var1 = sCallbacks.iterator();

            while(var1.hasNext()) {
               callback = (TaskController.Callback)var1.next();
               callback.receivedErrMsg(TaskTag.PREPARE_CONFIG, 64, "You should open [BLUETOOTH] before.");
            }

            return false;
         } else if (VERSION.SDK_INT >= 31 && !checkNearByPermission()) {
            var1 = sCallbacks.iterator();

            while(var1.hasNext()) {
               callback = (TaskController.Callback)var1.next();
               callback.receivedErrMsg(TaskTag.PREPARE_CONFIG, 65, "Request [android.permission.BLUETOOTH_ADVERTISE] permission before.");
            }

            return false;
         } else {
            sController = new BLEController(devId);
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
                  BLEStep.stop();
               }
            });
            sController.setDebugMode(sLogPrint);
            return true;
         }
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

   public static String getBLEPeripheralMsg() {
      if (sController != null) {
         return sController.getConfigQrCode();
      } else {
         if (sLogPrint) {
            Log.d("QRStep", "You should call start() before.");
         }

         return null;
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

   private static boolean checkBLEStatus() {
      BluetoothManager manager = (BluetoothManager)ContextProvider.getApplicationContext().getSystemService("bluetooth");
      if (manager == null) {
         return false;
      } else {
         return manager.getAdapter() != null && manager.getAdapter().isEnabled();
      }
   }

   private static boolean checkNearByPermission() {
      if (VERSION.SDK_INT >= 31) {
         return ActivityCompat.checkSelfPermission(ContextProvider.getApplicationContext(), "android.permission.BLUETOOTH_ADVERTISE") == 0;
      } else {
         return true;
      }
   }
}
