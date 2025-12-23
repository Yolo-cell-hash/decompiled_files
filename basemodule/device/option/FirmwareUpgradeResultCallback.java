package com.eseeiot.basemodule.device.option;

public interface FirmwareUpgradeResultCallback {
   void onResult(boolean var1);

   void onUpgradeStatus(boolean var1, int var2, String var3, String var4, int var5);
}
