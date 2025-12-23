package com.eseeiot.core.networkCallback;

public interface OnVconDataListener {
   void OnVconData(int var1, byte[] var2, String var3);

   void OnVconResendData(int var1, int var2, int var3, int var4, int var5, int var6);
}
