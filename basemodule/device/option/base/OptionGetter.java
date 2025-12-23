package com.eseeiot.basemodule.device.option.base;

import androidx.annotation.Nullable;
import com.eseeiot.basemodule.device.option.Options;
import java.util.List;
import org.json.JSONArray;

public interface OptionGetter {
   boolean isSupportCoverSetting();

   boolean isSupportSetWifi();

   boolean isSupportWirelessCheck();

   boolean isSupportChannelSetting();

   boolean isSupportReboot();

   boolean isSupportResetDefault();

   boolean supportTwoWayTalk();

   String getDeviceId();

   Boolean isPromptEnabled(boolean var1);

   String getPromptLanguage(boolean var1);

   List<String> getPromptLanguages();

   Boolean isAudioEnabled(boolean var1);

   Boolean isLEDEnabled(boolean var1);

   Integer getAudioSample();

   Boolean isMotionEnabled(boolean var1);

   Boolean isSupportLv();

   void updateMotionEnabled(boolean var1);

   Boolean isMotionRingEnabled(boolean var1);

   Boolean isGatewayMotionRingEnabled(boolean var1);

   Boolean isMotionRingCustom(boolean var1);

   Boolean isGatewayMotionRingCustom(boolean var1);

   Boolean isMotionRecordEnabled(boolean var1);

   String getMotionType(boolean var1);

   String getMotionLevel(boolean var1);

   String getHumanDetectionLevel(boolean var1);

   String getFaceDetectionLevel(boolean var1);

   Integer getMotionRecordDuration(boolean var1);

   Integer getCoolRecordDuration(boolean var1);

   Boolean isMotionTrackEnabled(boolean var1);

   Integer[] getVideoResolution();

   Integer[] getMotionAreaRect();

   String getMotionAreaLine(boolean var1);

   Integer getMotionAreaMaxLine();

   List<Long> getMotionAreaGrid(boolean var1);

   Boolean isDoorbellRingEnabled(boolean var1);

   Boolean isDoorbellRingCustom(boolean var1);

   Boolean isPIREnabled(boolean var1);

   void updatePIREnabled(boolean var1, int var2);

   Integer getPIRPushTime(boolean var1);

   List<Integer> getPIRSchedule(boolean var1);

   Integer getPIRDelayTime(boolean var1);

   Boolean isPushEnabled(boolean var1);

   List<Integer> getPushSchedule(boolean var1);

   Boolean isTimeRecordEnabled(boolean var1);

   JSONArray getTimeRecordSchedule(boolean var1);

   String getTFCardStatus();

   String getTFCardTotalSpace();

   String getTFCardLeaveSpace();

   String getRecordDateInfo();

   String getRecordMode(boolean var1);

   String getConvenientSetting();

   Integer getTimezone(boolean var1);

   Integer getUTCTime(boolean var1);

   Boolean isDaylightSavingTimeEnabled(boolean var1);

   String getDaylightSavingCountry(boolean var1);

   String getDaylightSavingTime(boolean var1);

   Integer getPowerLineFrequency(boolean var1);

   String getIRCutMode(boolean var1);

   List<String> getIRCutModes();

   Integer getLightControl();

   Boolean isSupportMultiRecType();

   Integer getInputVolume(boolean var1);

   Integer getOutputVolume(boolean var1);

   Boolean getAlexa();

   Boolean supportAudioInput(boolean var1);

   Boolean supportAudioOutput(boolean var1);

   String getImageDefinition(boolean var1);

   String getImageStyle(boolean var1);

   Boolean isVideoFlipEnabled(boolean var1);

   Boolean isSupportOSDSet();

   Boolean isVideoMirrorEnabled(boolean var1);

   String getVideoCoverAreas(boolean var1);

   String getOSDTextStr(boolean var1);

   double getOSDTextX(boolean var1);

   double getOSDTextY(boolean var1);

   String getFixMode(boolean var1);

   String getFixParams(boolean var1);

   String getOSDFormat(boolean var1);

   Boolean isLiveFeatureEnabled(boolean var1);

   Boolean isPlaybackFeatureEnabled(boolean var1);

   void updateFeatureEnabled(boolean var1);

   Boolean isHumanDetectionEnabled(boolean var1);

   Boolean isDrawHumanRegionEnabled(boolean var1);

   Boolean isFaceDetectionEnabled(boolean var1);

   Boolean isDrawFaceRegionEnabled(boolean var1);

   String getChannelInfo(boolean var1);

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

   String getPTZCruiseMode(boolean var1);

   Boolean isLinkageEnable(boolean var1);

   Double getLinkageCurCoordinatesX(boolean var1);

   Double getLinkageCurCoordinatesY(boolean var1);

   String getLedData(boolean var1);

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

   Boolean isCloudBound();

   Boolean isChannelCloudEnabled(int var1);

   int getCloudChannelCount();

   Integer getChannelCloudType(int var1);

   String getWorkMode(boolean var1);

   List<String> getWorkModes();

   String getLTEOperator();

   String getLTEPhone();

   String getLTEICCID();

   String getLTESimType();

   Integer getLTESignal();

   String getLteModuleIMEI();

   Integer getMaxChannel();

   Integer getWirelessChannel(boolean var1);

   Integer getWirelessSignal();

   Integer getWirelessThroughput();

   String getUpgradeStatus();

   String getUpgradeErrDescription();

   Integer getUpgradeIndex();

   Integer getUpgradeProgress();

   String getIPCam();

   String getCapabilitySet();

   Boolean isLightAlarmEnable(boolean var1);

   Integer getLightAlarmDuration(boolean var1);

   String getLightAlarmMode(boolean var1);

   Boolean isWhiteAlarmLightV2Enable(boolean var1);

   Integer getWhiteAlarmLightV2Duration(boolean var1);

   String getWhiteAlarmLightV2Mode(boolean var1);

   Boolean getLightManCtrl();

   Boolean getAlarmLightManCtrl();

   Boolean isAlarmRedBlueLightEnable(boolean var1);

   Integer getRedBlueLightAlarmDuration(boolean var1);

   void updateTalkMode(String var1);

   void updateTimezone(int var1);

   void updateOSDFormat(int var1);

   Options disableMatchExistsGettingObj();

   void restoreExistsGettingObj();

   Boolean isSupportLightControl(int var1);

   Integer getChannelManagerChannel();

   String getUsageScenario(boolean var1);

   String getTalkMode(boolean var1);

   List<Integer> getTimerSchedule(boolean var1, int var2);

   List<Integer> getMotionSchedule(boolean var1, int var2);

   List<Integer> getAlarmSchedule(boolean var1, int var2);

   List<Integer> getShutDownSchedule(boolean var1);

   Boolean isShutDownEnabled(boolean var1);

   String getWTType();

   Integer getWTSample();

   List<Integer> getWTSch(boolean var1, int var2);

   Boolean isWTSchEnable(boolean var1, int var2);

   String getNetworkStatus(boolean var1);

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

   Boolean getAlarmLightEnableV2();

   boolean isSupportSoundEnableV2(boolean var1);

   Integer getAlarmVolume(boolean var1);

   Boolean getOTA();

   String getRecordStreamV2(boolean var1);
}
