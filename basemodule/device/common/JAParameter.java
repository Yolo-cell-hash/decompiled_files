package com.eseeiot.basemodule.device.common;

public class JAParameter extends Parameter {
   public static final int VIDEO_BITRATE_HD = 0;
   public static final int VIDEO_BITRATE_SD = 1;
   public static final int VIDEO_BITRATE_AT = 2;
   public static final int VIDEO_BITRATE_DOOR_BELL_HD = 100;
   public static final int VIDEO_BITRATE_DOOR_BELL_SD = 101;
   public static final int VIDEO_ONLY_AUDIO = 102;
   public static final String ACTION_REQUEST_WIFI_CONNECT_Q = "action_request_wifi_connect_q";

   public static class PIR_STATUS {
      public static final int NONE = 0;
      public static final int CLOSE = 1;
      public static final int OPEN = 2;
   }

   public static class FW_VERSION {
      public static final int RELEASE = 1;
      public static final int TESTING = 0;
   }

   public static class VIDEO_SERVICE {
      public static final int DEFAULT = 0;
      public static final int OPEN = 1;
      public static final int CLOSE = -1;
   }
}
