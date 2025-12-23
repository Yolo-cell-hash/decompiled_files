package com.eseeiot.setup.task;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import com.eseeiot.basemodule.util.DeviceTool;
import com.eseeiot.basemodule.util.NetworkUtil;
import com.eseeiot.setup.pojo.DeviceSetupInfo;
import com.eseeiot.setup.receiver.WifiEventReceiver;
import com.eseeiot.setup.receiver.WifiReceiverListenerImpl;
import com.eseeiot.setup.task.base.BaseTask;
import com.eseeiot.setup.task.tag.TaskTag;

public class TaskWait4ConnectAuto extends BaseTask {
   private TaskWait4ConnectAuto.WifiConnectionResult mConnectResult;
   private WifiEventReceiver mReceiver;
   private DeviceSetupInfo mSetupInfo;
   private int mCountOfConnectAPAgain;
   private int mTimeoutCount;

   public TaskWait4ConnectAuto(Context context, TaskTag taskTag, int timeout) {
      super(context, taskTag, timeout);
   }

   private void init() {
      if (this.mConnectResult == null) {
         this.mConnectResult = new TaskWait4ConnectAuto.WifiConnectionResult();
      }

      if (this.mReceiver == null) {
         this.mReceiver = new WifiEventReceiver();
         this.mReceiver.setListener(this.mConnectResult);
         IntentFilter filter = new IntentFilter();
         filter.addAction("android.net.wifi.STATE_CHANGE");
         filter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
         this.mContext.registerReceiver(this.mReceiver, filter);
      }

      this.mCountOfConnectAPAgain = 0;
      this.mTimeoutCount = 0;
   }

   public void release() {
      super.release();
      if (this.mReceiver != null) {
         this.mContext.unregisterReceiver(this.mReceiver);
         this.mReceiver = null;
      }

   }

   protected boolean onTaskInit(Object... object) {
      this.init();

      try {
         if (object != null) {
            this.mSetupInfo = (DeviceSetupInfo)object[0];
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return true;
   }

   protected void onTaskStart() {
      if (this.mSetupInfo != null && !NetworkUtil.isWifiEnable(this.mContext)) {
         this.requestError(-22);
      } else {
         TaskWait4ConnectAuto.WifiConnectionInfo info = this.mConnectResult.getWifiConnectionInfo();
         if (info.getState() == 4) {
            this.handleComplete(info);
         } else if (info.getState() == 3 && this.mSetupInfo != null) {
            this.printLog("onWifiConnected()  --> connect on AP hotspot, waiting auto disconnect.");
            NetworkUtil.scanWifi(this.mContext);
         }

      }
   }

   protected void onTaskStop() {
      this.mIsRunning = false;
      this.requestTimeout((Object)null, false);
   }

   protected void onTaskTimeout() {
      if (this.mIsRunning) {
         if (this.mSetupInfo != null && !NetworkUtil.isWifiEnable(this.mContext)) {
            this.requestError(-22);
         } else {
            TaskWait4ConnectAuto.WifiConnectionInfo info = this.mConnectResult.getWifiConnectionInfo();
            switch(info.getState()) {
            case 2:
               this.handleNoConnect();
               break;
            case 3:
               this.handleAPConnect();
               break;
            case 4:
               this.handleComplete(info);
            }

         }
      }
   }

   private void handleNoConnect() {
      ++this.mTimeoutCount;
      this.printLog("handleNoConnect: mTimeoutCount = " + this.mTimeoutCount);
      if (this.mSetupInfo != null) {
         if (this.mTimeoutCount >= 5) {
            this.requestError(-27);
         } else {
            this.printLog("handleNoConnect: wifi network is bad, should try to reconnect.");
            this.requestTimeout((Object)null, true);
         }
      } else {
         this.requestTimeout((Object)null, true);
      }

   }

   private void handleAPConnect() {
      NetworkUtil.scanWifi(this.mContext);
      this.printLog("onWifiConnected()  --> connect on device's AP, try to scan wifi.");
   }

   private void handleComplete(TaskWait4ConnectAuto.WifiConnectionInfo info) {
      this.printLog("onWifiConnected()  --> waiting connect wifi success: " + info.getSSID());
      if (this.mSetupInfo != null) {
         this.requestComplete(info.getSSID(), true);
      } else {
         this.requestComplete(info.getWifiInfo(), false);
      }

   }

   private class WifiConnectionResult extends WifiReceiverListenerImpl {
      public static final int CONNECTED = 0;
      public static final int UNKNOWN = 1;
      public static final int DISCONNECTED = 2;
      public static final int AP_CONNECTED = 3;
      public static final int WIFI_CONNECTED = 4;

      private WifiConnectionResult() {
      }

      private TaskWait4ConnectAuto.WifiConnectionInfo getWifiConnectionInfo() {
         return this.getWifiConnectionInfo(1);
      }

      private TaskWait4ConnectAuto.WifiConnectionInfo getWifiConnectionInfo(int connectState) {
         if (connectState == 2) {
            return new TaskWait4ConnectAuto.WifiConnectionInfo(2);
         } else {
            Object[] objects = NetworkUtil.getCurrentConnectWifi(TaskWait4ConnectAuto.this.mContext);
            if (objects != null) {
               String SSID = (String)objects[0];
               WifiInfo wifiInfo = (WifiInfo)objects[1];
               if (connectState == 0 || NetworkUtil.isWifiConnected(TaskWait4ConnectAuto.this.mContext, SSID) > 0) {
                  if (SSID.startsWith("IPC")) {
                     return new TaskWait4ConnectAuto.WifiConnectionInfo(3, SSID, wifiInfo);
                  } else {
                     return DeviceTool.isConnectOnIPC(TaskWait4ConnectAuto.this.mContext) ? new TaskWait4ConnectAuto.WifiConnectionInfo(3, SSID, wifiInfo) : new TaskWait4ConnectAuto.WifiConnectionInfo(4, SSID, wifiInfo);
                  }
               }
            }

            return new TaskWait4ConnectAuto.WifiConnectionInfo(2);
         }
      }

      public void onWifiConnected(Intent intent, NetworkInfo info) {
         TaskWait4ConnectAuto.this.printLog("onWifiConnected()  --> info = [" + info + "] --> running = " + TaskWait4ConnectAuto.this.mIsRunning);
         if (TaskWait4ConnectAuto.this.mIsRunning && (info.getExtraInfo() == null || !info.getExtraInfo().contains("0X") && !info.getExtraInfo().contains("<unknown ssid>")) && (info.getState() == State.CONNECTED || info.getState() == State.DISCONNECTED)) {
            TaskWait4ConnectAuto.WifiConnectionInfo connectionInfo = this.getWifiConnectionInfo(info.getState().ordinal());
            switch(connectionInfo.getState()) {
            case 2:
               TaskWait4ConnectAuto.this.requestComplete((Object)null, false);
               break;
            case 3:
               TaskWait4ConnectAuto.this.mCountOfConnectAPAgain++;
               if (TaskWait4ConnectAuto.this.mSetupInfo != null) {
                  TaskWait4ConnectAuto.this.printLog("onWifiConnected()  --> repeatConnectAPCount = [" + TaskWait4ConnectAuto.this.mCountOfConnectAPAgain + "]");
                  if (TaskWait4ConnectAuto.this.mCountOfConnectAPAgain > 5) {
                     TaskWait4ConnectAuto.this.requestError(-28);
                     return;
                  }
               }

               NetworkUtil.scanWifi(TaskWait4ConnectAuto.this.mContext);
               break;
            case 4:
               TaskWait4ConnectAuto.this.mCountOfConnectAPAgain = 0;
               TaskWait4ConnectAuto.this.handleComplete(connectionInfo);
            }

         }
      }

      // $FF: synthetic method
      WifiConnectionResult(Object x1) {
         this();
      }
   }

   private static class WifiConnectionInfo {
      private int state;
      private String SSID;
      private WifiInfo wifiInfo;

      public WifiConnectionInfo(int state) {
         this.state = state;
      }

      public WifiConnectionInfo(int state, String SSID, WifiInfo wifiInfo) {
         this.state = state;
         this.SSID = SSID;
         this.wifiInfo = wifiInfo;
      }

      public int getState() {
         return this.state;
      }

      public String getSSID() {
         return this.SSID;
      }

      public WifiInfo getWifiInfo() {
         return this.wifiInfo;
      }
   }
}
