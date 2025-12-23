package com.eseeiot.setup.task.tag;

import android.text.TextUtils;
import androidx.annotation.NonNull;

public enum TaskTag {
   PREPARE_CONFIG("Prepare_Config_Task"),
   SEARCH_DEVICE("Scan_Device_Task"),
   SEARCH_DEVICE_ON_AP("Scan_Device_On_AP_Task"),
   SEND_WIFI_INFO_TO_DEVICE("Send_WiFi_To_Device_Task"),
   WAIT_FOR_CONNECT_WIFI_AUTO("Waiting_For_Auto_Connect_WiFi_Task"),
   CONNECT_DEVICE_AP("Connect_Dev_AP_Task"),
   CONNECT_DEVICE("Pre-connect_Device_Task"),
   GET_DEVICE_SETUP_INFO("Get_Device_Info_With_P2P_Task"),
   SET_TIMEZONE_FOR_DEVICE("Set_Timezone_To_Device_Task");

   private String name;

   private TaskTag() {
   }

   private TaskTag(String name) {
      this.name = name;
   }

   @NonNull
   public String toString() {
      return TextUtils.isEmpty(this.name) ? super.toString() : this.name;
   }
}
