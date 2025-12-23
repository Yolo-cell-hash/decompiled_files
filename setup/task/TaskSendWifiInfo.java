package com.eseeiot.setup.task;

import android.content.Context;
import android.text.TextUtils;
import com.eseeiot.basemodule.helper.MulticastHelper;
import com.eseeiot.basemodule.util.DeviceTool;
import com.eseeiot.basemodule.util.EncryptionUtil;
import com.eseeiot.basemodule.util.NetworkUtil;
import com.eseeiot.setup.pojo.DeviceSetupInfo;
import com.eseeiot.setup.pojo.MultiCastResponseInfo;
import com.eseeiot.setup.task.base.BaseTask;
import com.eseeiot.setup.task.tag.TaskTag;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;

public class TaskSendWifiInfo extends BaseTask implements MulticastHelper.OnMultiCastCallbackListener {
   private static final String TAG = "MyTaskSendWifiInfo";
   private static final int REQUEST_SEND_WIFI = 134489351;
   private MulticastHelper mHelper;
   private DeviceSetupInfo mSetupInfo;
   private final Gson mGson = new Gson();

   public TaskSendWifiInfo(Context context, TaskTag taskTag, int timeout) {
      super(context, taskTag, timeout);
   }

   public void release() {
      super.release();
      if (this.mHelper != null) {
         this.mHelper.release();
         this.mHelper = null;
      }

   }

   protected boolean onTaskInit(Object... object) {
      if (this.mHelper == null) {
         this.mHelper = new MulticastHelper(this.mContext.getApplicationContext());
         this.mHelper.setLogPrint(this.mLogPrint);
      }

      try {
         this.mSetupInfo = (DeviceSetupInfo)object[0];
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return this.mSetupInfo != null;
   }

   protected void onTaskStart() {
      this.sendWifiInfoToDevice();
   }

   protected void onTaskStop() {
      if (this.mHelper != null) {
         this.mHelper.release();
         this.mHelper = null;
      }

   }

   protected void onTaskTimeout() {
      super.onTaskTimeout();
      this.requestTimeout(this.mSetupInfo, true);
   }

   private void sendWifiInfoToDevice() {
      if (!this.mHelper.isReceiverRegistered()) {
         this.mHelper.registerReceiveListener(this, false, true);
      }

      if (!DeviceTool.isConnectOnIPC(this.mContext)) {
         Object[] objects = NetworkUtil.getCurrentConnectWifi(this.mContext);
         this.printLog("Current connect wifi is NOT Device's AP HOTSPOT --> " + Arrays.toString(objects));
         this.requestError(-30);
      } else {
         String wifiSSID = this.mSetupInfo.isSSIDNeedEncrypt() ? EncryptionUtil.encodeBase64(this.mSetupInfo.getUserWifi().getSSID()) : this.mSetupInfo.getUserWifi().getSSID();
         String wifiPassword = EncryptionUtil.encodeBase64(this.mSetupInfo.getUserWifi().getPassword());
         String data = "{\n    \"fromApp\":true,\n    \"requestID\":134489351,\n    \"deviceID\":\"" + this.mSetupInfo.getDeviceId() + "\",\n    \"tokenID\":" + 754276439 + ",\n    \"command\":\"setup\",\n    \"Content\":{\n        \"Ethernet\":{\n            \"dhcp\":true,\n            \"Wireless\":{\n                \"securityMode\":\"WEP\",\n                \"ssid\":\"" + wifiSSID + "\",\n                \"password\":\"" + wifiPassword + "\"\n            }\n        }\n    }\n}";
         this.printLog("sendWifiInfoToDevice: --> msg = " + data);
         this.mThread = new Thread(() -> {
            while(true) {
               if (this.mIsRunning) {
                  this.printLog("sendWifiInfoToDevice: --> sending wifi password... ");
                  this.mHelper.postData(data, 12306);

                  try {
                     Thread.sleep(3000L);
                     continue;
                  } catch (InterruptedException var3) {
                  }
               }

               return;
            }
         });
         this.mThread.start();
      }
   }

   public boolean onMultiCastCallback(String value) {
      if (!this.mIsRunning) {
         return true;
      } else {
         this.printLog("OnMultiCastCallBack: ");
         MultiCastResponseInfo responseInfo = null;

         try {
            responseInfo = (MultiCastResponseInfo)this.mGson.fromJson(value, MultiCastResponseInfo.class);
         } catch (JsonSyntaxException var6) {
            var6.printStackTrace();
         }

         if (responseInfo != null && !responseInfo.isFromApp() && !TextUtils.isEmpty(responseInfo.getDeviceID()) && !TextUtils.isEmpty(this.mSetupInfo.getDeviceId()) && responseInfo.getDeviceID().equals(this.mSetupInfo.getDeviceId())) {
            this.printLog("OnMultiCastCallBack --> received response value = [" + value + "]");
            if (responseInfo.getRequestID() == 134489351) {
               synchronized(this) {
                  if (this.mIsRunning) {
                     this.printLog("OnMultiCastCallBack: --> device received wifi password");
                     this.requestComplete(this.mSetupInfo, true);
                  }

                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }
}
