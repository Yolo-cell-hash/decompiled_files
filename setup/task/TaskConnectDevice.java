package com.eseeiot.setup.task;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.dispatcher.DeviceEventCallback;
import com.eseeiot.basemodule.util.NetworkUtil;
import com.eseeiot.device.DeviceBuilder;
import com.eseeiot.setup.pojo.ConnectInfo;
import com.eseeiot.setup.pojo.DeviceSetupInfo;
import com.eseeiot.setup.task.base.BaseTask;
import com.eseeiot.setup.task.tag.TaskTag;

public class TaskConnectDevice extends BaseTask {
   private static final String TAG = "MyTaskConnectDevice";
   private DeviceSetupInfo mSetupInfo;
   private boolean mIsConnectOnAP;
   private int mAuthFailedCount = 4;
   private boolean mAuthFailedIfTimeout;
   private boolean mNotifyFailedOrTimeoutStatus;
   private ConnectInfo mEseeConnectInfo;
   private final Runnable DelayRunnable = new Runnable() {
      public void run() {
         if (TaskConnectDevice.this.mIsRunning) {
            boolean success = TaskConnectDevice.this.mEseeConnectInfo != null && TaskConnectDevice.this.mEseeConnectInfo.getStatus() == 6;
            if (success) {
               TaskConnectDevice.this.printLog("run: connect success");
               TaskConnectDevice.this.mSetupInfo.setPreConnected(true);
               TaskConnectDevice.this.requestComplete(TaskConnectDevice.this.mSetupInfo, true);
            } else {
               TaskConnectDevice.this.printLog("run: disconnect, waiting timeout.");
            }

         }
      }
   };
   private int mNoNetworkCount;
   private Handler mHandler = new Handler();
   private MonitorDevice mMonitorDevice;
   private DeviceEventCallback mEventCallback;

   public TaskConnectDevice(Context context, TaskTag taskTag, int timeout) {
      super(context, taskTag, timeout);
   }

   public void release() {
      super.release();
      if (this.mEventCallback != null && this.mMonitorDevice != null) {
         this.mMonitorDevice.unregisterEventCallback(this.mEventCallback);
         this.mMonitorDevice = null;
         this.mEventCallback = null;
      }

      if (this.mHandler != null) {
         this.mHandler.removeCallbacksAndMessages((Object)null);
         this.mHandler = null;
      }

   }

   protected boolean onTaskInit(Object... object) {
      DeviceSetupInfo tempInfo = this.mSetupInfo;

      try {
         this.mSetupInfo = (DeviceSetupInfo)object[0];
         if (object.length > 1) {
            this.mIsConnectOnAP = (Boolean)object[1];
         }

         if (object.length > 2) {
            this.mAuthFailedIfTimeout = (Boolean)object[2];
         }

         if (object.length > 3) {
            this.mAuthFailedCount = (Integer)object[3];
         }

         if (object.length > 4) {
            this.mNotifyFailedOrTimeoutStatus = (Boolean)object[4];
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      if (tempInfo != null && this.mSetupInfo != null && !tempInfo.getEseeId().equals(this.mSetupInfo.getEseeId()) && this.mEseeConnectInfo != null) {
         this.mEseeConnectInfo.setStatus(-1);
      }

      if (this.mEseeConnectInfo != null) {
         this.mEseeConnectInfo.reset();
      }

      return this.mSetupInfo != null && !TextUtils.isEmpty(this.mSetupInfo.getEseeId());
   }

   protected void onTaskStart() {
      if (this.mMonitorDevice == null) {
         this.mMonitorDevice = (new DeviceBuilder()).setIotId(this.mSetupInfo.getEseeId()).setUsername(this.mSetupInfo.getDeviceUser()).setPassword(this.mSetupInfo.getDevicePassword()).setChannelCount(this.mSetupInfo.getChannelCount()).build();
      }

      if (this.mMonitorDevice != null) {
         this.connectDevice();
      }

   }

   protected void onTaskStop() {
      this.mIsRunning = false;
      this.requestTimeout(this.mSetupInfo, false);
   }

   protected void onTaskTimeout() {
      if (this.mIsRunning) {
         this.printLog("onTaskTimeout: detach timeout");
         if (this.mAuthFailedIfTimeout) {
            ConnectInfo currentConnectInfo = this.mEseeConnectInfo;
            if (currentConnectInfo != null && currentConnectInfo.getAuthFailedCount() > 0) {
               this.printLog("onTaskTimeout: pre-connect timeout, received wrong password message.");
               this.requestError(-24);
               this.mMonitorDevice = null;
               return;
            }
         }

         this.requestTimeout(this.mSetupInfo, true);
      }
   }

   private void connectDevice() {
      if (this.checkNetworkState()) {
         if (this.checkWifiState()) {
            if (this.mEseeConnectInfo == null) {
               this.mEseeConnectInfo = new ConnectInfo();
            }

            if (!this.checkConnectState(this.mEseeConnectInfo)) {
               this.connect(this.mEseeConnectInfo);
            }

         }
      }
   }

   private boolean checkNetworkState() {
      if (NetworkUtil.isNetworkConnected(this.mContext)) {
         this.mNoNetworkCount = 0;
      } else {
         ++this.mNoNetworkCount;
         if (this.mNoNetworkCount >= 3) {
            this.requestError(-25);
            return false;
         }
      }

      return true;
   }

   private boolean checkConnectState(ConnectInfo connectInfo) {
      this.mSetupInfo.setPreConnected(false);
      if (connectInfo.getStatus() == 6) {
         this.printLog("connectDevice: pre-connected");
         this.mSetupInfo.setPreConnected(true);
         this.requestComplete(this.mSetupInfo, true);
         return true;
      } else if (connectInfo.getStatus() == 0) {
         this.printLog("connectDevice: handle pre-connect status, waiting callback ...");
         return true;
      } else {
         return false;
      }
   }

   private boolean checkWifiState() {
      if (!this.mIsConnectOnAP) {
         Object[] objects = NetworkUtil.getCurrentConnectWifi(this.mContext);
         if (objects != null) {
            String SSID = (String)objects[0];
            if (SSID.startsWith("IPC")) {
               this.requestError(-26);
               return false;
            }
         }
      }

      return true;
   }

   private void connect(ConnectInfo connectInfo) {
      connectInfo.setHasReceiveConnectingStatus(false);
      if (connectInfo.isNotConnectYet()) {
         this.printLog("disconnectDevice first!!");
         connectInfo.setNotConnectYet(false);
         if (this.mMonitorDevice != null) {
            this.mMonitorDevice.disconnect();
         }
      }

      if (this.mEventCallback == null && this.mMonitorDevice != null) {
         this.mEventCallback = new DeviceEventCallback() {
            public int onRegisterParamGet() {
               return 1;
            }

            public void onConnectChanged(MonitorDevice device, int status, int channel) {
               super.onConnectChanged(device, status, channel);
               TaskConnectDevice.this.printLog("onReceive: 【key = " + TaskConnectDevice.this.mSetupInfo.getEseeId() + ", status = " + status + "】");
               if (status < 13) {
                  ConnectInfo currentConnectInfo = null;
                  if (TaskConnectDevice.this.mEseeConnectInfo != null) {
                     currentConnectInfo = TaskConnectDevice.this.mEseeConnectInfo;
                  }

                  if (currentConnectInfo != null) {
                     currentConnectInfo.setStatus(status);
                     if (status == 0) {
                        currentConnectInfo.setHasReceiveConnectingStatus(true);
                     } else if (!currentConnectInfo.hasReceiveConnectingStatus()) {
                        return;
                     }

                     if (TaskConnectDevice.this.mIsRunning) {
                        switch(status) {
                        case 2:
                        case 11:
                           currentConnectInfo.setAuthFailedCount(0);
                           if (TaskConnectDevice.this.mNotifyFailedOrTimeoutStatus) {
                              TaskConnectDevice.this.requestTimeout(status, false);
                           }
                        case 3:
                        case 4:
                        case 5:
                        case 7:
                        case 8:
                        case 9:
                        case 12:
                        default:
                           break;
                        case 6:
                           TaskConnectDevice.this.printLog("run: pre-connect success, please waiting ...");
                           currentConnectInfo.setAuthFailedCount(0);
                           if (TaskConnectDevice.this.mHandler != null) {
                              TaskConnectDevice.this.mHandler.removeCallbacks(TaskConnectDevice.this.DelayRunnable);
                              TaskConnectDevice.this.mHandler.postDelayed(TaskConnectDevice.this.DelayRunnable, 500L);
                           }
                           break;
                        case 10:
                           currentConnectInfo.addAuthFailedCount();
                           if (currentConnectInfo.getAuthFailedCount() >= TaskConnectDevice.this.mAuthFailedCount) {
                              TaskConnectDevice.this.printLog("run: pre-connect failed, password was wrong " + TaskConnectDevice.this.mAuthFailedCount);
                              TaskConnectDevice.this.requestError(-24);
                              TaskConnectDevice.this.mMonitorDevice = null;
                           }
                        }

                     }
                  }
               }
            }
         };
         this.mMonitorDevice.registerEventCallback(this.mEventCallback);
      }

      this.printLog("connect: 【id = " + this.mSetupInfo.getEseeId() + ", password = " + this.mSetupInfo.getDevicePassword() + ", user = " + this.mSetupInfo.getDeviceUser() + "】");
      if (this.mMonitorDevice != null) {
         this.mMonitorDevice.connect();
      }

   }

   public void setAuthFailedCount(int authFailedCount) {
      this.mAuthFailedCount = authFailedCount;
   }
}
