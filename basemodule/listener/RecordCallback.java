package com.eseeiot.basemodule.listener;

public interface RecordCallback {
   void onRecordStart();

   void onRecording(int var1, int var2);

   void onRecordStop(String var1, boolean var2);
}
