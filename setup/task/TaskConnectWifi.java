package com.eseeiot.setup.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.NetworkRequest.Builder;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.eseeiot.basemodule.util.DeviceTool;
import com.eseeiot.basemodule.util.NetworkUtil;
import com.eseeiot.setup.pojo.DeviceSetupInfo;
import com.eseeiot.setup.task.base.BaseTask;
import com.eseeiot.setup.task.tag.TaskTag;

public class TaskConnectWifi extends BaseTask {
   private NetworkCallback mNetworkCallback;
   private Handler mHandler = new Handler();
   private DeviceSetupInfo mSetupInfo;

   public TaskConnectWifi(Context context, TaskTag taskTag, int timeout) {
      super(context, taskTag, timeout);
   }

   public void release() {
      super.release();
      this.releaseNetworkCallback();
      if (this.mHandler != null) {
         this.mHandler.removeCallbacksAndMessages((Object)null);
         this.mHandler = null;
      }

   }

   protected boolean onTaskInit(Object... object) {
      try {
         this.mSetupInfo = (DeviceSetupInfo)object[0];
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return this.mSetupInfo != null;
   }

   protected void onTaskStart() {
      ConnectivityManager connectivityManager = (ConnectivityManager)this.mContext.getSystemService("connectivity");
      if (this.mNetworkCallback == null) {
         NetworkRequest request = (new Builder()).addTransportType(1).build();
         this.mNetworkCallback = new NetworkCallback() {
            private String mLastNetId;

            public void onAvailable(@NonNull Network network) {
               super.onAvailable(network);
               if (TaskConnectWifi.this.isRunning()) {
                  String netId = network.toString();
                  TaskConnectWifi.this.printLog("NetworkCallback::onAvailable: " + netId);
                  if (!netId.equals(this.mLastNetId)) {
                     this.mLastNetId = netId;
                     if (TaskConnectWifi.this.mHandler != null) {
                        TaskConnectWifi.this.mHandler.post(() -> {
                           if (TaskConnectWifi.this.isRunning()) {
                              TaskConnectWifi.this.handleWifiConnected();
                           }
                        });
                     }

                  }
               }
            }
         };
         connectivityManager.registerNetworkCallback(request, this.mNetworkCallback);
      }

      this.handleWifiConnected();
   }

   protected void onTaskStop() {
      this.requestTimeout((Object)null, false);
      this.mHandler.removeCallbacksAndMessages((Object)null);
      this.releaseNetworkCallback();
   }

   protected void onTaskTimeout() {
      super.onTaskTimeout();
      this.handleWifiConnected();
   }

   private void handleWifiConnected() {
      if (!NetworkUtil.isWifiEnable(this.mContext)) {
         this.printLog("Should turn on WiFi toggle.");
         this.requestError(-22);
         this.releaseNetworkCallback();
      } else if (!NetworkUtil.isGpsEnable(this.mContext)) {
         this.printLog("Should turn on GPS toggle.");
         this.requestError(-3);
         this.releaseNetworkCallback();
      } else {
         if (DeviceTool.isConnectOnIPC(this.mContext)) {
            this.printLog("Connected on device's hotspot, try to get eseeid ....");
            Object[] objects = NetworkUtil.getCurrentConnectWifi(this.mContext);
            if (objects != null) {
               String SSID = (String)objects[0];
               if (SSID.startsWith("IPC")) {
                  String eseeid = DeviceTool.getEseeIdFromSSID(SSID);
                  if (eseeid != null) {
                     this.printLog("Got eseeid from ssid: " + eseeid);
                     this.mSetupInfo.setEseeId(eseeid);
                     this.releaseNetworkCallback();
                     this.requestComplete(this.mSetupInfo, true);
                  }
               }
            }
         } else if (NetworkUtil.getNetWorkType(this.mContext) != 1) {
            this.printLog("Connected on another network type.");
         } else {
            this.printLog("Connected on NOT device's hotspot.");
         }

      }
   }

   private void releaseNetworkCallback() {
      if (this.mNetworkCallback != null) {
         ConnectivityManager connectivityManager = (ConnectivityManager)this.mContext.getSystemService("connectivity");
         if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
         }

         this.mNetworkCallback = null;
      }

   }
}
