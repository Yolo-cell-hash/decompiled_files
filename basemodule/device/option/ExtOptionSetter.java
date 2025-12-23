package com.eseeiot.basemodule.device.option;

import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.pojo.Schedule;
import java.util.List;

public interface ExtOptionSetter {
   void enableAudio(boolean var1, SettingResultCallback var2);

   void enableMotionDetection(boolean var1, SettingResultCallback var2);

   void enableHumanDetection(boolean var1, SettingResultCallback var2);

   void enableMotionTrack(boolean var1, SettingResultCallback var2);

   void enableLightAlarm(boolean var1, SettingResultCallback var2);

   void enableWhiteLightAlarm(boolean var1, SettingResultCallback var2);

   void toggleWhiteLight(boolean var1, int var2, SettingResultCallback var3);

   void enableRedBlueLightAlarm(boolean var1, SettingResultCallback var2);

   void toggleRedBlueLight(boolean var1, int var2, SettingResultCallback var3);

   void setCordonData(String var1, String var2, String var3, SettingResultCallback var4);

   void enableMotionRing(boolean var1, SettingResultCallback var2);

   void setSoundManCtrl(SettingResultCallback var1);

   void setAlarmVolume(int var1, SettingResultCallback var2);

   void setOutputVolume(int var1, SettingResultCallback var2);

   void setInputVolume(int var1, SettingResultCallback var2);

   void enableLED(boolean var1, SettingResultCallback var2);

   void enableRotate180Degrees(boolean var1, SettingResultCallback var2);

   void enableHumanDrawRegion(boolean var1, SettingResultCallback var2);

   void setVideoCoverAreas(String var1, SettingResultCallback var2);

   void setVideoCoverAreas(int var1, String var2, SettingResultCallback var3);

   void setMotionLevel(String var1, SettingResultCallback var2);

   void setMotionLevel(int var1, String var2, SettingResultCallback var3);

   void setPushSchedule(List<Integer> var1, SettingResultCallback var2);

   void enablePromptSound(boolean var1, SettingResultCallback var2);

   void setPromptSoundLanguage(String var1, SettingResultCallback var2);

   void synchronisedTime(SettingResultCallback var1);

   void setTimezone(int var1, SettingResultCallback var2);

   void enableDaylightSavingTime(boolean var1, SettingResultCallback var2);

   void setDaylightSavingTime(String var1, SettingResultCallback var2);

   void reboot(SettingResultCallback var1);

   void resetDefault(SettingResultCallback var1);

   void modifyWifi(String var1, String var2, SettingResultCallback var3);

   void sendAudioDataToDevice(MonitorDevice var1, String var2, SettingResultCallback var3);

   void releaseAudioDataChannel();

   void customMotionRing(boolean var1, SettingResultCallback var2);

   void setIRCutMode(String var1, SettingResultCallback var2);

   void setIRCutMode(int var1, String var2, SettingResultCallback var3);

   void setSpeed(int var1, SettingResultCallback var2);

   void formatTFCard(SettingResultCallback var1);

   void setIPCRecordMode(OptionResult.RecordMode var1, SettingResultCallback var2);

   void setRecordSchedule(List<Schedule> var1, SettingResultCallback var2);

   void upgradeFirmware(SettingResultCallback var1);

   void upgradeFirmware(boolean var1, SettingResultCallback var2);

   void modifyPassword(String var1, String var2, SettingResultCallback var3);

   void enableMotionPush(boolean var1, SettingResultCallback var2);

   void enableMotionPush(int var1, boolean var2, SettingResultCallback var3);

   void setWirelessChannel(int var1, SettingResultCallback var2);

   void enableChannelCloudUpload(boolean var1, int var2, int var3, SettingResultCallback var4);

   void switchRecordStream(String var1, SettingResultCallback var2);

   void setWorkMode(String var1, boolean var2, SettingResultCallback var3);

   void setMotionRecordDuration(int var1, boolean var2, SettingResultCallback var3);

   void setWorkModeAndDuration(String var1, Integer var2, boolean var3, SettingResultCallback var4);

   void setCoolRecordDuration(int var1, boolean var2, SettingResultCallback var3);

   void enableMotionRing(boolean var1, boolean var2, SettingResultCallback var3);

   void setAlarmVolume(int var1, boolean var2, SettingResultCallback var3);

   void customMotionRing(boolean var1, boolean var2, SettingResultCallback var3);

   void setOutputVolume(int var1, boolean var2, SettingResultCallback var3);

   void setInputVolume(int var1, boolean var2, SettingResultCallback var3);

   void setLinkageEnable(boolean var1, SettingResultCallback var2);

   void setLinkageManCtrl(float var1, float var2, SettingResultCallback var3);

   void setLensLinkageAdjust(SettingResultCallback var1);

   void sendRemoteSnapshotReqToDevice(MonitorDevice var1, String var2, SettingResultCallback var3, RemoteSnapshotCallback var4);

   void releaseRemoteSnanshot();
}
