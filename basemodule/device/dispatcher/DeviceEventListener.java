package com.eseeiot.basemodule.device.dispatcher;

import com.eseeiot.basemodule.device.base.MonitorDevice;

public interface DeviceEventListener {
   int onRegisterParamGet();

   void onConnectChanged(MonitorDevice var1, int var2, int var3);

   boolean onDisconnected(MonitorDevice var1, int var2, int var3);

   void onOpenChanged(MonitorDevice var1, int var2, int var3);

   void onOOBParamAvailable(int var1, int var2);

   void onFishParamAvailable(float var1, float var2, float var3, float var4, float var5, float var6, byte[] var7, int var8, int var9);

   void onGSensorParamAvailable(long var1, double var3, double var5, double var7);

   void onPlaybackOSDAvailable(int var1, int var2);

   void onCaptureResult(int var1, int var2, int var3);

   void onRecordDuration(long var1, int var3);

   void onDownloadProgress(String var1, int var2, int var3);

   void onPTZSelfCheckBack(String var1, int var2, int var3);
}
