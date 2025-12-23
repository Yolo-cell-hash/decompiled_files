package com.eseeiot.basemodule.device.dispatcher;

public interface DeviceEventDispatchEntry {
   boolean isActive();

   void dispatchConnectEvent(String var1, int var2, int var3);

   boolean dispatchDisconnectEvent(String var1, int var2, int var3);

   void dispatchOpenEvent(int var1, int var2);

   void dispatchOOBEvent(int var1, int var2);

   void dispatchFishParamEvent(float var1, float var2, float var3, float var4, float var5, float var6, byte[] var7, int var8, int var9);

   void dispatchGSensorParamEvent(long var1, double var3, double var5, double var7);

   void dispatchCaptureEvent(String var1, int var2, int var3, int var4);

   void dispatchPlaybackOSD(String var1, int var2, int var3);

   void dispatchRecordDuration(String var1, long var2, int var4);

   void dispatchDownloadProgress(String var1, int var2, int var3);

   void dispatchPTZSelfCheckBack(String var1, int var2, int var3);
}
