package com.eseeiot.basemodule.device.dispatcher;

import com.eseeiot.basemodule.device.base.MonitorDevice;

public abstract class DeviceEventCallback implements DeviceEventListener {
   public void onConnectChanged(MonitorDevice device, int status, int channel) {
   }

   public boolean onDisconnected(MonitorDevice device, int status, int channel) {
      return false;
   }

   public void onOpenChanged(MonitorDevice device, int status, int channel) {
   }

   public void onOOBParamAvailable(int installMode, int scene) {
   }

   public void onFishParamAvailable(float centerX, float centerY, float radius, float angleX, float angleY, float angleZ, byte[] angleData, int angleDataLen, int index) {
   }

   public void onGSensorParamAvailable(long timestamp, double x, double y, double z) {
   }

   public void onPlaybackOSDAvailable(int time, int index) {
   }

   public void onCaptureResult(int success, int index, int requestCode) {
   }

   public void onRecordDuration(long duration, int index) {
   }

   public void onDownloadProgress(String connectKey, int total, int progress) {
   }

   public void onPTZSelfCheckBack(String connectKey, int ret, int channel) {
   }
}
