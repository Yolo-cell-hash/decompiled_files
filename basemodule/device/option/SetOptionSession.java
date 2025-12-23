package com.eseeiot.basemodule.device.option;

public interface SetOptionSession {
   void close();

   int commit();

   SetOptionSession closeAfterFinish();

   SetOptionSession useVerify();

   SetOptionSession usePassword();

   SetOptionSession setTimeout(int var1);

   SetOptionSession enableCombine(boolean var1);

   SetOptionSession skipMatchExistsGettingField();

   SetOptionSession addListener(OptionSessionCallback var1);

   void save();

   void discard();

   boolean hasSomethingChanged();

   SetOptionSession enableAudio(boolean var1);

   SetOptionSession enableLED(boolean var1);

   SetOptionSession enableAlexa(boolean var1);

   SetOptionSession setInputVolume(int var1);

   SetOptionSession setOutputVolume(int var1);

   SetOptionSession enablePromptSound(boolean var1);

   SetOptionSession setPromptSoundLanguage(String var1);

   SetOptionSession enableMotionDetection(boolean var1);

   SetOptionSession enableMotionPush(boolean var1);

   SetOptionSession setPushSchedule(String var1);

   SetOptionSession enableMotionRecord(boolean var1);

   SetOptionSession setMotionLevel(String var1);

   SetOptionSession setHumanoidLevel(String var1);

   SetOptionSession setFaceLevel(String var1);

   SetOptionSession setMotionType(String var1);

   SetOptionSession setMotionRecordDuration(int var1);

   SetOptionSession enableMotionTrack(boolean var1);

   SetOptionSession setMotionAreaGrid(String var1);

   SetOptionSession setMotionAreaLine(String var1);

   SetOptionSession enableMotionRing(boolean var1, boolean var2);

   SetOptionSession customMotionRing(boolean var1, boolean var2);

   SetOptionSession setCoolRecordDuration(int var1);

   SetOptionSession enableHumanDetection(boolean var1);

   SetOptionSession enableHumanDrawRegion(boolean var1);

   SetOptionSession enableFaceDetection(boolean var1);

   SetOptionSession enableFaceDrawRegion(boolean var1);

   SetOptionSession enablePIR(boolean var1);

   SetOptionSession setPIRMediaPushTime(int var1);

   SetOptionSession setPIRDelayTime(int var1);

   SetOptionSession setPIRSchedule(String var1);

   SetOptionSession enableTimeRecord(boolean var1);

   SetOptionSession setRecordSchedule(String var1);

   SetOptionSession setRecordMode(String var1);

   SetOptionSession synchronisedTime(int var1);

   SetOptionSession setTimezone(int var1);

   SetOptionSession enableDaylightSavingTime(boolean var1);

   SetOptionSession setDaylightSavingTime(String var1);

   SetOptionSession setPowerLineFrequency(int var1);

   SetOptionSession setFixMode(String var1);

   SetOptionSession setFixParam(String var1);

   SetOptionSession setOSDFormat(String var1);

   SetOptionSession enableLiveFeature(boolean var1);

   SetOptionSession enablePlaybackFeature(boolean var1);

   SetOptionSession setDefinition(String var1);

   SetOptionSession setIRCutMode(String var1);

   SetOptionSession setImageStyle(String var1);

   SetOptionSession enableVideoFlip(boolean var1);

   SetOptionSession enableVideoMirror(boolean var1);

   SetOptionSession setVideoCoverAreas(String var1);

   SetOptionSession setOSDSettingTextStr(String var1);

   SetOptionSession setOSDSettingTextX(double var1);

   SetOptionSession setOSDSettingTextY(double var1);

   SetOptionSession setWorkMode(String var1);

   SetOptionSession enableDoorbellRing(boolean var1);

   SetOptionSession customDoorbellRing(boolean var1);

   SetOptionSession setWirelessChannel(int var1);

   SetOptionSession enableLamp(boolean var1);

   SetOptionSession enableInfraredLamp(boolean var1);

   SetOptionSession enableNewSwitch(boolean var1);

   SetOptionSession setLedProduct(String var1);

   SetOptionSession setLedChannelValue(int var1, int var2);

   SetOptionSession enableChannelSetting(int var1);

   SetOptionSession enableChannelCloudUpload(boolean var1, int var2, int var3);

   SetOptionSession modifyWifi(String var1, String var2, String var3);

   SetOptionSession enableWifi(boolean var1, String var2);

   SetOptionSession removeChannel(int var1);

   SetOptionSession formatTFCard();

   SetOptionSession repairTFCard();

   SetOptionSession setConvenientSetting(String var1);

   SetOptionSession upgradeFirmware();

   SetOptionSession upgradeFirmware(int var1);

   SetOptionSession upgradeFirmware(boolean var1);

   SetOptionSession modifyPassword(String var1, String var2);

   SetOptionSession modifyMonopolyPassword(String var1, String var2);

   SetOptionSession reboot();

   SetOptionSession resetDefault();

   SetOptionSession ptzCtrlSpeed(int var1);

   SetOptionSession enableLightAlarm(boolean var1);

   SetOptionSession setLightAlarmDuration(int var1);

   SetOptionSession setLightAlarmMode(String var1);

   SetOptionSession enableWhiteAlarmLightV2(boolean var1);

   SetOptionSession setWhiteAlarmLightV2Duration(int var1);

   SetOptionSession setWhiteAlarmLightV2Mode(String var1);

   SetOptionSession ptzCruiseMode(String var1);

   SetOptionSession setLinkageEnable(boolean var1);

   SetOptionSession setLinkageManCtrl(float var1, float var2);

   SetOptionSession setLensLinkageAdjust();

   SetOptionSession setUsageScenario(String var1);

   SetOptionSession setTalkMode(String var1);

   SetOptionSession setTimerSchedule(String var1, int var2);

   SetOptionSession setMotionSchedule(String var1, int var2);

   SetOptionSession setAlarmSchedule(String var1, int var2);

   SetOptionSession enableShutDown(boolean var1);

   SetOptionSession setShutDownSch(String var1);

   SetOptionSession setWTSchEnable(boolean var1, int var2);

   SetOptionSession setWTSch(String var1, int var2);

   SetOptionSession enableGuardPosition(boolean var1);

   SetOptionSession setGuardIndex(int var1);

   SetOptionSession setGuardStay(int var1);

   SetOptionSession setGuardSchedule(int[] var1);

   SetOptionSession autoConnect(boolean var1);

   SetOptionSession enableAlarmV2(boolean var1);

   SetOptionSession enableAlarmPushV2(boolean var1);

   SetOptionSession setAlarmPushEventV2(String[] var1);

   SetOptionSession setAlarmPushScheduleV2(String var1);

   SetOptionSession setAlarmAlarmPushIntervalV2(int var1);

   SetOptionSession appendLightManCtrl(boolean var1, int var2);

   SetOptionSession setAlarmVolume(int var1);

   SetOptionSession setSoundManCtrl(boolean var1, Integer var2);

   SetOptionSession enableRedBlueLightAlarm(boolean var1);

   SetOptionSession setRedBlueLightDuration(int var1);

   SetOptionSession toggleRedBlueLight(boolean var1, int var2);

   SetOptionSession switchRecordStream(String var1);
}
