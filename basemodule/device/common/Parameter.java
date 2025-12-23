package com.eseeiot.basemodule.device.common;

public class Parameter {
   public static final int SCRN_NORMAL = 0;
   public static final int SCRN_HEMISPHERE = 1;
   public static final int SCRN_CYLINDER = 2;
   public static final int SCRN_EXPAND = 3;
   public static final int SCRN_UPDOWN = 4;
   public static final int SCRN_FOUR = 5;
   public static final int SCRN_VR = 6;
   public static final int SCRN_SPHERE = 7;
   public static final int SCRN_PANORAMA = 11;
   public static final int SCRN_PLANET = 14;
   public static final int SCRN_VR_SPHERE = 17;
   public static final int SCRN_STITCH2 = 18;
   public static final int SCRN_ONESCREEN = 257;
   public static final int SCRN_ONENORMAL = 258;
   public static final int SCRN_HEMISPHERE_VRSensor = -1;
   public static final int SCRN_SPLIT_ONE = 0;
   public static final int SCRN_SPLIT_FOUR = 1;
   public static final int SCRN_SPLIT_SIX = 2;
   public static final int SCRN_SPLIT_EIGHT = 3;
   public static final int SCRN_SPLIT_NINE = 4;
   public static final int SCRN_SPLIT_THIRTEEN = 5;
   public static final int SCRN_SPLIT_SIXTEEN = 6;
   public static final int SCREEN_SINGLE_2_SINGLE = 0;
   public static final int SCREEN_SINGLE_2_MULTI = 1;
   public static final int SCREEN_MULTI_2_SINGLE = 2;
   public static final int SCREEN_MULTI_2_MULTI = 3;
   public static final int DIRECTION_NONE = 0;
   public static final int DIRECTION_LEFT = 1;
   public static final int DIRECTION_RIGHT = 2;
   public static final int DIRECTION_UP = 3;
   public static final int DIRECTION_DOWN = 4;
   public static final int NET_STATUS_CONNECTING = 0;
   public static final int NET_STATUS_CONNECTED = 1;
   public static final int NET_STATUS_CONNECTFAIL = 2;
   public static final int NET_STATUS_LOGINING = 3;
   public static final int NET_STATUS_LOGINED = 4;
   public static final int NET_STATUS_LOGINFAIL = 5;
   public static final int NET_STATUS_OPENING = 6;
   public static final int NET_STATUS_OPENFAIL = 7;
   public static final int NET_STATUS_DISCONNECTED = 8;
   public static final int NET_STATUS_CLOSED = 9;
   public static final int NET_STATUS_AUTHOFAIL = 10;
   public static final int NET_STATUS_TIMEOUT = 11;
   public static final int NET_STATUS_ACTIVECLOSED = 12;
   public static final int NET_STATUS_OPENCHANNEL = 13;
   public static final int NET_STATUS_CLOSECHANNEL = 14;
   public static final int NET_STATUS_FIRST_FRAME = 15;
   public static final int NET_STATUS_CONNECTOR_FULL = 16;
   public static final int NET_STATUS_P2P_LOGIN_FULL = 17;
   public static final int HLS_VIDEO_PARAMS_FLAG_AUDIO_CODEC = 100;
   public static final int HLS_VIDEO_PARAMS_FLAG_VIDEO_CODEC = 101;
   public static final int BACKUP_STATUS_STOP = 1014;

   public static class ScaleOrder {
      public static final int SCALE_OUT = 0;
      public static final int SCALE_IN = 1;
      public static final int SCALE_STOP = -1;
   }

   public static class PtzOrder {
      public static final int PTZ_UP = 0;
      public static final int PTZ_DOWN = 1;
      public static final int PTZ_LEFT = 2;
      public static final int PTZ_RIGHT = 3;
      public static final int PTZ_AUTOPAN = 8;
      public static final int PTZ_IRIS_OPEN = 9;
      public static final int PTZ_IRIS_CLOSE = 10;
      public static final int PTZ_ZOOM_IN = 11;
      public static final int PTZ_ZOOM_OUT = 12;
      public static final int PTZ_FOCUS_FAR = 13;
      public static final int PTZ_FOCUS_NEAR = 14;
      public static final int PTZ_STOP = 15;
      public static final int PTZ_PRESET_SET = 28;
      public static final int PTZ_PRESET_CLEAR = 29;
      public static final int PTZ_PRESET_GOTO = 30;
      public static final int PTZ_SELF_CHECK = 116;
      public static final int PTZ_STEP_UP = 117;
      public static final int PTZ_STEP_DOWN = 118;
      public static final int PTZ_STEP_LEFT = 119;
      public static final int PTZ_STEP_RIGHT = 120;
      public static final int PTZ_SELF_CHECK_LENS_LINKAGE = 121;
   }
}
