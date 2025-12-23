package com.eseeiot.setup.task;

import android.content.Context;
import android.text.TextUtils;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.option.OptionSessionCallback;
import com.eseeiot.basemodule.device.option.Options;
import com.eseeiot.device.DeviceBuilder;
import com.eseeiot.setup.pojo.DeviceSetupInfo;
import com.eseeiot.setup.pojo.VConInfo;
import com.eseeiot.setup.task.base.BaseTask;
import com.eseeiot.setup.task.tag.TaskTag;
import com.google.gson.Gson;

public class TaskGetDeviceInfo extends BaseTask {
   private DeviceSetupInfo mSetupInfo;
   private final Gson mGson = new Gson();
   private MonitorDevice mMonitorDevice;
   private OptionSessionCallback mSessionCallback;

   public TaskGetDeviceInfo(Context context, TaskTag taskTag, int timeout) {
      super(context, taskTag, timeout);
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
      if (this.mMonitorDevice == null) {
         this.mMonitorDevice = (new DeviceBuilder()).setIotId(this.mSetupInfo.getEseeId()).setUsername(this.mSetupInfo.getDeviceUser()).setPassword(this.mSetupInfo.getDevicePassword()).setChannelCount(this.mSetupInfo.getChannelCount()).build();
      }

      if (this.mMonitorDevice != null) {
         this.getDeviceInfo();
      }

   }

   protected void onTaskStop() {
      this.requestTimeout(this.mSetupInfo, false);
   }

   protected void onTaskTimeout() {
      super.onTaskTimeout();
      this.requestTimeout(this.mSetupInfo, true);
   }

   public void release() {
      this.mSessionCallback = null;
      this.mMonitorDevice = null;
      super.release();
   }

   private void getDeviceInfo() {
      if (this.mSessionCallback == null) {
         this.mSessionCallback = (device, resultCode, errorCode, requestCode) -> {
            if (this.mIsRunning) {
               if (resultCode == 2) {
                  this.requestError(this.mSetupInfo);
                  this.mMonitorDevice = null;
               } else if (resultCode == 0) {
                  VConInfo vConInfo = new VConInfo();
                  Options options = device.getOptions(0);
                  String ipCam = options.getIPCam();
                  this.printLog("onReceive: --> Got remote info success, ipcam: " + ipCam);
                  if (!TextUtils.isEmpty(ipCam)) {
                     VConInfo.IPCamClass ipCamClass = (VConInfo.IPCamClass)this.mGson.fromJson(ipCam, VConInfo.IPCamClass.class);
                     if (ipCamClass != null) {
                        vConInfo.setIPCam(ipCamClass);
                     }
                  }

                  String capabilitySet = options.getCapabilitySet();
                  this.printLog("onReceive: --> Got remote info success, capability_set: " + capabilitySet);
                  if (!TextUtils.isEmpty(capabilitySet)) {
                     VConInfo.CapabilitySetClass capabilitySetClass = (VConInfo.CapabilitySetClass)this.mGson.fromJson(capabilitySet, VConInfo.CapabilitySetClass.class);
                     if (capabilitySetClass != null) {
                        vConInfo.setCapabilitySet(capabilitySetClass);
                     }
                  }

                  if (vConInfo.getIPCam() != null && vConInfo.getIPCam().getDeviceInfo() != null) {
                     this.printLog("onReceive: --> Got remote info success");
                     this.mSetupInfo.setVconInfo(vConInfo);
                     this.requestComplete(this.mSetupInfo, true);
                  }
               }

            }
         };
      }

      this.mMonitorDevice.getOptions().newGetSession().appendDeviceInfo().appendModeSetting().appendAlarmSetting().appendSystemOperation(false).appendLedPwm().usePassword().closeAfterFinish().addListener(this.mSessionCallback).commit();
      this.printLog("getDeviceInfo: --> Start get remote info ...");
   }
}
