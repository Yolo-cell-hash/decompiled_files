package com.eseeiot.option.component.remotesnapshot;

import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.option.RemoteSnapshotCallback;
import com.eseeiot.basemodule.device.option.SettingResultCallback;

public class RemoteSnapshot {
   public static void snapshot(MonitorDevice device, String filePath, RemoteSnapshotCallback callback) {
      device.getOptionHelper().setter().sendRemoteSnapshotReqToDevice(device, filePath, (SettingResultCallback)null, callback);
   }

   public static void release(MonitorDevice device) {
      device.getOptionHelper().setter().releaseRemoteSnanshot();
   }
}
