package com.eseeiot.basemodule.device.dispatcher;

public interface VconEventDispatchEntry {
   void dispatchVconEvent(String var1, int var2);

   void dispatchVconResendEvent(int var1, int var2, int var3, int var4, int var5, int var6, byte[] var7);
}
