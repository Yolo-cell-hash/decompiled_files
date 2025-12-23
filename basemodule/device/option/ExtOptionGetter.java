package com.eseeiot.basemodule.device.option;

import androidx.annotation.Nullable;
import com.eseeiot.basemodule.pojo.Schedule;
import java.util.List;

public interface ExtOptionGetter {
   boolean isSupportCoverSetting();

   boolean isSupportSetWifi();

   boolean isSupportWirelessCheck();

   boolean isSupportChannelSetting();

   boolean isSupportReboot();

   boolean isSupportResetDefault();

   boolean supportTwoWayTalk();

   String getDeviceId();

   Boolean isPromptEnabled();

   String getPromptLanguage();

   List<String> getPromptLanguages();

   Boolean isAudioEnabled();

   Boolean isLEDEnabled();

   Integer getAudioSample();

   Boolean isMotionEnabled();

   Boolean isMotionAlarmEnabled();

   boolean isSupportRotate180Degrees();

   boolean isRotate180DegreesEnabled();

   void updateMotionEnabled(boolean var1);

   Boolean isMotionRingEnabled();

   Boolean isGatewayMotionRingEnabled();

   Boolean isMotionRingCustom();

   Boolean isGatewayMotionRingCustom();

   Boolean isMotionRecordEnabled();

   String getMotionType();

   String getMotionLevel();

   String getHumanDetectionLevel();

   String getFaceDetectionLevel();

   Integer getMotionRecordDuration();

   Integer getCoolRecordDuration();

   Boolean isMotionTrackEnabled();

   Integer[] getVideoResolution();

   Integer[] getMotionAreaRect();

   String getMotionAreaLine();

   Integer getMotionAreaMaxLine();

   List<Long> getMotionAreaGrid();

   Boolean isDoorbellRingEnabled();

   Boolean isDoorbellRingCustom();

   Boolean isPIREnabled();

   void updatePIREnabled(boolean var1, int var2);

   Integer getPIRPushTime();

   List<Integer> getPIRSchedule();

   Integer getPIRDelayTime();

   Boolean isPushEnabled();

   List<Integer> getPushSchedule();

   Boolean isTimeRecordEnabled();

   OptionResult.RecordMode getIPCRecordMode();

   List<Schedule> getRecordSchedules();

   String getRecordStream();

   String getTFCardStatus();

   String getTFCardTotalSpace();

   String getTFCardLeaveSpace();

   String getRecordDateInfo();

   String getRecordMode();

   String getConvenientSetting();

   Integer getTimezone();

   Integer getUTCTime();

   Boolean isDaylightSavingTimeEnabled();

   String getDaylightSavingCountry();

   String getDaylightSavingTime();

   Integer getPowerLineFrequency();

   String getIRCutMode();

   List<String> getIRCutModes();

   Integer getLightControl();

   Boolean isSupportMultiRecType();

   Integer getInputVolume();

   Integer getOutputVolume();

   Boolean getAlexa();

   Boolean supportAudioInput();

   Boolean supportAudioOutput();

   String getImageDefinition();

   String getImageStyle();

   Boolean isVideoFlipEnabled();

   Boolean isSupportOSDSet();

   Boolean isVideoMirrorEnabled();

   String getVideoCoverAreas();

   String getOSDTextStr();

   double getOSDTextX();

   double getOSDTextY();

   String getFixMode();

   String getFixParams();

   String getOSDFormat();

   Boolean isLiveFeatureEnabled();

   Boolean isPlaybackFeatureEnabled();

   void updateFeatureEnabled(boolean var1);

   Boolean isHumanDetectionEnabled();

   Boolean isDrawHumanRegionEnabled();

   Boolean isFaceDetectionEnabled();

   Boolean isDrawFaceRegionEnabled();

   String getChannelInfo();

   Boolean isChannelEnabled(int var1);

   String getChannelDevType(int var1);

   String getChannelModel(int var1);

   String getChannelFWVersion(int var1);

   String getChannelODMNum(int var1);

   String getChannelSerialNum(int var1);

   String getChannelFWMagic(int var1);

   String getChannelRecordMode(int var1);

   Integer getChannelSignal(int var1);

   Integer getChannelBattery(int var1);

   String getChannelBatteryStatus(int var1);

   Integer getChannelStatus(int var1);

   Integer getChannelPIRStatus(int var1);

   Boolean isChannelOnCharging(int var1);

   Boolean isChannelVersionLastest(int var1);

   Boolean supportPTZ();

   int getPTZCapability();

   Integer getPTZSpeed();

   Boolean supportAF();

   Boolean supportIrControl();

   String getPTZCruiseMode();

   Boolean isLinkageEnable();

   Double getLinkageCurCoordinatesX();

   Double getLinkageCurCoordinatesY();

   String getLedData();

   boolean isLedEnabled();

   String getLedProduct();

   Integer getLedProject();

   Integer getLedChannelCount();

   Integer getLedChannelType(int var1);

   Integer getLedChannelValue(int var1);

   String getWirelessSSID();

   String getIpAddress();

   String getMacAddress();

   Boolean getWirelessEnable();

   String getWirelessState();

   String getWirelessAPs();

   List<String> getWirelessAPsList();

   Boolean isCloudBound();

   Boolean isChannelCloudEnabled(int var1);

   int getCloudChannelCount();

   Integer getChannelCloudType(int var1);

   String getWorkMode();

   List<String> getWorkModes();

   String getLTEOperator();

   String getLTEPhone();

   String getLTEICCID();

   String getLTESimType();

   Integer getLTESignal();

   String getLteModuleIMEI();

   Integer getMaxChannel();

   Integer getWirelessChannel();

   Integer getWirelessSignal();

   Integer getWirelessThroughput();

   String getUpgradeStatus();

   String getUpgradeErrDescription();

   Integer getUpgradeIndex();

   Integer getUpgradeProgress();

   String getIPCam();

   String getCapabilitySet();

   Boolean isLightAlarmEnable();

   Integer getLightAlarmDuration();

   String getLightAlarmMode();

   Boolean isWhiteLightAlarmEnable();

   Boolean getWhiteLightEnable();

   Boolean getRedBlueLightEnable();

   Boolean isRedBlueLightAlarmEnable();

   void updateTalkMode(String var1);

   void updateTimezone(int var1);

   void updateOSDFormat(int var1);

   Options disableMatchExistsGettingObj();

   void restoreExistsGettingObj();

   Boolean isSupportLightControl(int var1);

   Integer getChannelManagerChannel();

   String getUsageScenario();

   String getTalkMode();

   List<Integer> getTimerSchedule(int var1);

   List<Integer> getMotionSchedule(int var1);

   List<Integer> getAlarmSchedule(int var1);

   List<Integer> getShutDownSchedule();

   Boolean isShutDownEnabled();

   String getWTType();

   Integer getWTSample();

   List<Integer> getWTSch(int var1);

   Boolean isWTSchEnable(int var1);

   String getNetworkStatus();

   Boolean getGuardEnable();

   Integer getGuardIndex();

   Integer getGuardStay();

   Integer[] getGuardSchedule();

   boolean supportAddChannel();

   String getAddChannelStatus();

   Integer getAddChannelLeftTimeout();

   List<String> getAddChannelErrorCode();

   void clearAllOptions();

   @Nullable
   Integer getBatteryDevMinPowerLimit();

   @Nullable
   Integer getBatterDevMaxPowerLimit();

   String getVersion();

   boolean isSupportChannelV2();

   boolean isChannelV2Got();

   boolean isChannelSupportPtzV2(int var1);

   @Nullable
   Boolean getChannelPtzV2(int var1);

   @Nullable
   Boolean getChannelPtzHorizontalV2(int var1);

   @Nullable
   Boolean getChannelPtzVerticalV2(int var1);

   Boolean isChannelSupportAfV2(int var1);

   Boolean isChannelSupportIrControlV2(int var1);

   boolean isChannelSupportPowerBatteryV2(int var1);

   boolean isChannelSupportLightControlV2(int var1);

   @Nullable
   Integer getChannelLightControlV2(int var1);

   @Nullable
   Integer getChannelSpFisheyeV2(int var1);

   Boolean getAlarmEnableV2();

   Boolean getAlarmPushEnableV2();

   List<String> getAlarmPushEventV2();

   List<String> getAlarmPushEventOptV2();

   Integer getAlarmPushIntervalV2();

   String getAlarmPushScheduleV2();

   Boolean getLightEnableV2();

   boolean isSupportSoundEnableV2();

   Integer getAlarmVolume();

   Boolean getOTA();

   boolean isBatteryDev();
}
