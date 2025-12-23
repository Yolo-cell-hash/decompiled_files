package com.eseeiot.setup.task;

import android.content.Context;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.option.OptionSessionCallback;
import com.eseeiot.device.DeviceBuilder;
import com.eseeiot.setup.pojo.DeviceSetupInfo;
import com.eseeiot.setup.task.base.BaseTask;
import com.eseeiot.setup.task.tag.TaskTag;
import java.util.TimeZone;

public class TaskSetTimezone extends BaseTask {
   private DeviceSetupInfo mSetupInfo;
   private MonitorDevice mMonitorDevice;
   private OptionSessionCallback mSessionCallback;

   public TaskSetTimezone(Context context, TaskTag taskTag, int timeout) {
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
         this.sendTimeZoneToDevice();
      }

   }

   protected void onTaskStop() {
      this.requestTimeout(this.mSetupInfo, false);
   }

   protected void onTaskTimeout() {
      this.requestTimeout(this.mSetupInfo, true);
   }

   public void release() {
      this.mSessionCallback = null;
      this.mMonitorDevice = null;
      super.release();
   }

   private void sendTimeZoneToDevice() {
      if (this.mSessionCallback == null) {
         this.mSessionCallback = (device, resultCode, errorCode, requestCode) -> {
            if (this.mIsRunning) {
               if (resultCode == 0) {
                  this.requestComplete(this.mSetupInfo, true);
               } else {
                  if (resultCode == 2) {
                     this.mMonitorDevice = null;
                  }

                  this.requestError(this.mSetupInfo);
               }

            }
         };
      }

      int timezone = TimeZone.getDefault().getRawOffset() / '負';
      float tempTimezone = (float)timezone * 1.0F / 100.0F;
      int hour = (int)tempTimezone;
      int minute = (int)((tempTimezone - (float)hour) * 60.0F);
      timezone = hour * 100 + minute;
      int utcTime = (int)(System.currentTimeMillis() / 1000L);
      if (this.mSetupInfo.getVconInfo() != null && this.mSetupInfo.getVconInfo().getIPCam() != null && this.mSetupInfo.getVconInfo().getIPCam().getSystemOperation() != null && this.mSetupInfo.getVconInfo().getIPCam().getSystemOperation().getTimeSync() != null) {
         this.mSetupInfo.getVconInfo().getIPCam().getSystemOperation().getTimeSync().setTimeZone(timezone);
      }

      this.mMonitorDevice.getOptions().newSetSession().usePassword().closeAfterFinish().synchronisedTime(utcTime).setTimezone(timezone).addListener(this.mSessionCallback).commit();
      this.printLog("sendTimeZoneToDevice: --> set timezone = " + timezone);
   }
}
