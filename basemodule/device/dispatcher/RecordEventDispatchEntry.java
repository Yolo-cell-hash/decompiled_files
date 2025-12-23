package com.eseeiot.basemodule.device.dispatcher;

public interface RecordEventDispatchEntry {
   boolean onRecordAvailable(int var1, int var2, int var3, int var4, int var5, boolean var6);

   boolean onRecordAvailable(int var1, int var2, String var3, String var4, int var5, String var6, int var7, int var8, boolean var9);
}
