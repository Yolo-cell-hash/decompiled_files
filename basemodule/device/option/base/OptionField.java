package com.eseeiot.basemodule.device.option.base;

public enum OptionField {
   CAPABILITY_SET("CapabilitySet"),
   DEVICE_INFO("DeviceInfo"),
   MODE("ModeSetting"),
   FISHEYE("FisheyeSetting", true),
   ALARM("AlarmSetting"),
   SYSTEM_OPERATION("SystemOperation"),
   PROMPT_SOUND("PromptSounds", true),
   RECORD_MANAGER("RecordManager", true),
   TFCARD("TfcardManager"),
   LED("ledPwm", true),
   CHANNEL_MANAGER("ChannelManager"),
   RECORD_CTRL("RecordCtrl"),
   CHANNEL_INFO("ChannelInfo", true),
   CHANNEL_STATUS("ChannelStatus", true),
   ALARM_MAMAGER("AlarmManager"),
   WIRELESS_MANAGER("WirelessManager"),
   WIRELESS_CHECK("WirelessCheck", true),
   WIRELESS_STATION("WirelessStation"),
   DOORBELL_MANAGER("DoorbellManager"),
   CLOUD("OsscloudSetting", true),
   VIDEO_MANAGER("videoManager"),
   PTZ("ptzManager", true),
   RECORD_INFO("recordInfo"),
   COVER("devCoverSetting", true),
   FEATURE("Feature", true),
   LTE("Lte", true),
   LTE_MODULE("LteModuleDetail", true),
   ALEXA("Alexa", true),
   WORKMODE("WorkMode"),
   CHN_CAPABILITY_SET("ChnCapabilitySet"),
   CHN_CAPABILITY_SET_V2_REQ("ChnCapabilitySetV2Req"),
   CHN_CAPABILITY_SET_V2_RSP("ChnCapabilitySetV2Rsp"),
   FREQUENCY_MODE("powerLineFrequencyMode", true),
   RECORD("Record", true),
   SHUT_DOWN("AutoShutdown", true),
   OSD("Osd", true),
   NETWORK_MODE("NetworkMode", true),
   V2_STATUS("Stat", true),
   V2_R_LIGHT_MAN_CTRL("R/LightManCtrl", true),
   V2_R_SOUND_MAN_CTRL("R/SoundManCtrl", true),
   V2_R_LINKAGE_MAN_CTRL("R/LensLinkageCtrl", true),
   V2_ALARM("Alarm", true),
   V2_RECORD("Record", true),
   V2_LENS_CTRL("LensCtrl", true);

   private String name;
   private boolean maybeNotReturn;

   private OptionField(String name) {
      this(name, false);
   }

   private OptionField(String name, boolean maybeNotReturn) {
      this.name = name;
      this.maybeNotReturn = maybeNotReturn;
   }

   public String getFieldName() {
      return this.name;
   }

   public boolean isMaybeNotReturn() {
      return this.maybeNotReturn;
   }
}
