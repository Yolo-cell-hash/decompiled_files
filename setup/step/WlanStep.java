package com.eseeiot.setup.step;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.eseeiot.basemodule.util.NetworkUtil;
import com.eseeiot.setup.pojo.LanDeviceInfo;
import com.eseeiot.setup.task.TaskScanLanDevice;
import com.eseeiot.setup.task.base.BaseTask;
import com.eseeiot.setup.task.listener.OnTaskChangedListener;
import com.eseeiot.setup.task.tag.TaskTag;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class WlanStep {
   private static final String TAG = "WlanStep";
   private static boolean sLogPrint;
   public static final int ERR_WIFI_PERMISSION_REQUEST = 0;
   public static final int ERR_NETWORK_DISCONNECTED = 1;
   public static final int ERR_WIFI_DISABLE = 2;
   private static final CopyOnWriteArrayList<WlanStep.Callback> sCallback = new CopyOnWriteArrayList();
   @SuppressLint({"StaticFieldLeak"})
   private static BaseTask sLanTask;

   private WlanStep() {
   }

   public static void startDeviceDetecting() {
      if (!isDeviceDetecting()) {
         if (sLanTask != null) {
            sLanTask.release();
            sLanTask = null;
         }

         Iterator var0;
         WlanStep.Callback callback;
         if (!checkPermission()) {
            var0 = sCallback.iterator();

            while(var0.hasNext()) {
               callback = (WlanStep.Callback)var0.next();
               callback.receivedErrMsg(0, "Please configure [android.permission.ACCESS_WIFI_STATE] permission in AndroidManifest.xml.");
            }

            return;
         }

         if (!checkWifiStatus()) {
            var0 = sCallback.iterator();

            while(var0.hasNext()) {
               callback = (WlanStep.Callback)var0.next();
               if (!NetworkUtil.isNetworkConnected(ContextProvider.getApplicationContext())) {
                  callback.receivedErrMsg(1, "Network is bad!");
               } else {
                  callback.receivedErrMsg(2, "Please turn on WiFi toggle before.");
               }
            }

            return;
         }

         sLanTask = new TaskScanLanDevice(ContextProvider.getApplicationContext(), TaskTag.SEARCH_DEVICE, 0);
         sLanTask.switchLogPrint(sLogPrint);
         sLanTask.setCallback(new OnTaskChangedListener() {
            public boolean onTaskTimeout(@NonNull TaskTag taskTag, @Nullable Object object, long costTime) {
               return false;
            }

            public void onTaskChanged(@NonNull TaskTag taskTag, @Nullable Object object, boolean finish) {
               if (object instanceof LanDeviceInfo) {
                  Iterator var4 = WlanStep.sCallback.iterator();

                  while(var4.hasNext()) {
                     WlanStep.Callback callback = (WlanStep.Callback)var4.next();
                     callback.receivedDevice((LanDeviceInfo)object);
                  }
               }

            }

            public void onTaskError(@NonNull TaskTag taskTag, @Nullable Object object) {
               if (object instanceof Integer && -22 == (Integer)object) {
                  Iterator var3 = WlanStep.sCallback.iterator();

                  while(var3.hasNext()) {
                     WlanStep.Callback callback = (WlanStep.Callback)var3.next();
                     callback.receivedErrMsg(2, "Please turn on WiFi toggle before.");
                  }
               }

            }
         });
         sLanTask.exec(10L, null, true, true, true);
      } else if (sLogPrint) {
         Log.d("WlanStep", "WlanStep is searching ...");
      }

   }

   public static boolean isDeviceDetecting() {
      return sLanTask != null && sLanTask.isRunning();
   }

   public static void stopDeviceDetecting() {
      if (isDeviceDetecting()) {
         sLanTask.requestStop();
         sLanTask.release();
         sLanTask = null;
      }

   }

   public static boolean addCallback(@NonNull WlanStep.Callback callback) {
      return sCallback.add(callback);
   }

   public static boolean removeCallback(@NonNull WlanStep.Callback callback) {
      return sCallback.remove(callback);
   }

   public static void setDebugMode(boolean enable) {
      sLogPrint = enable;
      if (sLanTask != null) {
         sLanTask.switchLogPrint(sLogPrint);
      }

   }

   private static boolean checkPermission() {
      if (VERSION.SDK_INT >= 23) {
         return ActivityCompat.checkSelfPermission(ContextProvider.getApplicationContext(), "android.permission.ACCESS_WIFI_STATE") == 0;
      } else {
         return true;
      }
   }

   private static boolean checkWifiStatus() {
      return NetworkUtil.isNetworkConnected(ContextProvider.getApplicationContext()) ? NetworkUtil.isWifiEnable(ContextProvider.getApplicationContext()) : false;
   }

   public interface Callback {
      void receivedErrMsg(int var1, @NonNull String var2);

      void receivedDevice(@NonNull LanDeviceInfo var1);
   }
}
