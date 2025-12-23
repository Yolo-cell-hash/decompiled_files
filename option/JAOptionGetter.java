package com.eseeiot.option;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.eseeiot.basemodule.device.option.ExtOptionGetter;
import com.eseeiot.basemodule.device.option.OptionResult;
import com.eseeiot.basemodule.device.option.Options;
import com.eseeiot.basemodule.pojo.Schedule;
import com.eseeiot.basemodule.util.EncryptionUtil;
import com.eseeiot.option.pojo.APsInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class JAOptionGetter implements ExtOptionGetter {
   protected Options mOptions;

   protected JAOptionGetter(Options options) {
      this.mOptions = options;
   }

   public boolean isSupportCoverSetting() {
      return this.mOptions.isSupportCoverSetting();
   }

   public boolean isSupportSetWifi() {
      return this.mOptions.isSupportSetWifi();
   }

   public boolean isSupportWirelessCheck() {
      return false;
   }

   public boolean isSupportChannelSetting() {
      return this.mOptions.isSupportChannelSetting();
   }

   public boolean isSupportReboot() {
      return this.mOptions.isSupportReboot();
   }

   public boolean isSupportResetDefault() {
      return this.mOptions.isSupportResetDefault();
   }

   public boolean supportTwoWayTalk() {
      return this.mOptions.supportTwoWayTalk();
   }

   public String getDeviceId() {
      return this.mOptions.getDeviceId();
   }

   public Boolean isPromptEnabled() {
      return this.mOptions.isPromptEnabled(false);
   }

   public String getPromptLanguage() {
      return this.mOptions.getPromptLanguage(false);
   }

   public List<String> getPromptLanguages() {
      return this.mOptions.getPromptLanguages();
   }

   public Boolean isAudioEnabled() {
      return this.mOptions.isAudioEnabled(false);
   }

   public Boolean isLEDEnabled() {
      return this.mOptions.isLEDEnabled(false);
   }

   public Integer getAudioSample() {
      return this.mOptions.getAudioSample();
   }

   public Boolean isMotionEnabled() {
      return this.mOptions.isMotionEnabled(false);
   }

   public Boolean isMotionAlarmEnabled() {
      boolean enable;
      if (this.mOptions.getAlarmEnableV2() != null) {
         enable = this.mOptions.getAlarmEnableV2() != null && this.mOptions.getAlarmEnableV2();
      } else {
         enable = this.mOptions.isMotionEnabled(false) != null && this.mOptions.isMotionEnabled(false);
      }

      return enable;
   }

   public boolean isSupportRotate180Degrees() {
      return this.mOptions.isVideoFlipEnabled(false) != null || this.mOptions.isVideoMirrorEnabled(false) != null;
   }

   public boolean isRotate180DegreesEnabled() {
      return this.isSupportRotate180Degrees() && this.mOptions.isVideoFlipEnabled(true) && this.mOptions.isVideoMirrorEnabled(true);
   }

   public void updateMotionEnabled(boolean enable) {
   }

   public Boolean isMotionRingEnabled() {
      return this.mOptions.isMotionRingEnabled(true);
   }

   public Boolean isGatewayMotionRingEnabled() {
      return null;
   }

   public Boolean isMotionRingCustom() {
      return this.mOptions.isMotionRingCustom(true);
   }

   public Boolean isGatewayMotionRingCustom() {
      return null;
   }

   public Boolean isMotionRecordEnabled() {
      return this.mOptions.isMotionRecordEnabled(false);
   }

   public String getMotionType() {
      return this.mOptions.getMotionType(true);
   }

   public String getMotionLevel() {
      return this.mOptions.getMotionLevel(true);
   }

   public String getHumanDetectionLevel() {
      return this.mOptions.getHumanDetectionLevel(false);
   }

   public String getFaceDetectionLevel() {
      return this.mOptions.getFaceDetectionLevel(false);
   }

   public Integer getMotionRecordDuration() {
      return this.mOptions.getMotionRecordDuration(false);
   }

   public Integer getCoolRecordDuration() {
      return this.mOptions.getCoolRecordDuration(false);
   }

   public Boolean isMotionTrackEnabled() {
      return this.mOptions.isMotionTrackEnabled(true);
   }

   public Integer[] getVideoResolution() {
      return this.mOptions.getVideoResolution();
   }

   public Integer[] getMotionAreaRect() {
      return this.mOptions.getMotionAreaRect();
   }

   public String getMotionAreaLine() {
      return this.mOptions.getMotionAreaLine(false);
   }

   public Integer getMotionAreaMaxLine() {
      return this.mOptions.getMotionAreaMaxLine();
   }

   public List<Long> getMotionAreaGrid() {
      return this.mOptions.getMotionAreaGrid(false);
   }

   public Boolean isDoorbellRingEnabled() {
      return null;
   }

   public Boolean isDoorbellRingCustom() {
      return null;
   }

   public Boolean isPIREnabled() {
      return null;
   }

   public void updatePIREnabled(boolean enable, int channel) {
   }

   public Integer getPIRPushTime() {
      return null;
   }

   public List<Integer> getPIRSchedule() {
      return null;
   }

   public Integer getPIRDelayTime() {
      return null;
   }

   public Boolean isPushEnabled() {
      boolean enable;
      if (this.mOptions.getAlarmPushEnableV2() != null) {
         enable = this.mOptions.getAlarmPushEnableV2() != null && this.mOptions.getAlarmPushEnableV2();
      } else {
         enable = this.mOptions.isPushEnabled(false) != null && this.mOptions.isPushEnabled(false);
      }

      return enable;
   }

   public List<Integer> getPushSchedule() {
      return this.mOptions.getPushSchedule(true);
   }

   public Boolean isTimeRecordEnabled() {
      return null;
   }

   public OptionResult.RecordMode getIPCRecordMode() {
      Boolean isTimeRecordEnabled = this.mOptions.isTimeRecordEnabled(false);
      Boolean isMotionRecordEnabled = this.mOptions.isMotionRecordEnabled(false);
      if (isTimeRecordEnabled != null || isMotionRecordEnabled != null) {
         if (Objects.equals(isTimeRecordEnabled, true)) {
            return OptionResult.RecordMode.RECORD_WITH_TIME;
         }

         if (Objects.equals(isMotionRecordEnabled, true)) {
            return OptionResult.RecordMode.RECORD_WITH_EVENT;
         }
      }

      return OptionResult.RecordMode.NONE;
   }

   public List<Schedule> getRecordSchedules() {
      JSONArray array = this.mOptions.getTimeRecordSchedule(false);
      if (array == null) {
         return new ArrayList();
      } else {
         List<Schedule> schedules = new ArrayList(array.length());
         if (array.length() == 0) {
            return schedules;
         } else {
            for(int i = 0; i < array.length(); ++i) {
               Schedule schedule = new Schedule();
               JSONObject sch = array.optJSONObject(i);
               String beginTimeStr = sch.optString("BeginTime");
               String endTimeStr = sch.optString("EndTime");
               String weekDayStr = sch.optString("Weekday");
               String ID = sch.optString("ID");
               String[] weekDayString = weekDayStr.split(",");
               String[] time = weekDayString;
               int hour = weekDayString.length;

               int minute;
               for(minute = 0; minute < hour; ++minute) {
                  String weekDay = time[minute];
                  int weekDayIndex = Integer.parseInt(weekDay);
                  schedule.enableWeedDay(1 + weekDayIndex);
               }

               time = beginTimeStr.split(":");
               hour = Integer.parseInt(time[0]);
               minute = Integer.parseInt(time[1]);
               int second = 0;

               try {
                  second = Integer.parseInt(time[2]);
               } catch (ArrayIndexOutOfBoundsException var17) {
               }

               schedule.setStartTime(10, hour);
               schedule.setStartTime(12, minute);
               schedule.setStartTime(13, second);
               time = endTimeStr.split(":");
               hour = Integer.parseInt(time[0]);
               minute = Integer.parseInt(time[1]);
               second = 0;

               try {
                  second = Integer.parseInt(time[2]);
               } catch (ArrayIndexOutOfBoundsException var16) {
               }

               schedule.setEndTime(10, hour);
               schedule.setEndTime(12, minute);
               schedule.setEndTime(13, second);
               schedules.add(schedule);
            }

            return schedules;
         }
      }
   }

   public String getRecordStream() {
      return this.mOptions.getRecordStreamV2(false);
   }

   public String getTFCardStatus() {
      return this.mOptions.getTFCardStatus();
   }

   public String getTFCardTotalSpace() {
      return this.mOptions.getTFCardTotalSpace();
   }

   public String getTFCardLeaveSpace() {
      return this.mOptions.getTFCardLeaveSpace();
   }

   public String getRecordDateInfo() {
      return null;
   }

   public String getRecordMode() {
      return null;
   }

   public String getConvenientSetting() {
      return null;
   }

   public Integer getTimezone() {
      return this.mOptions.getTimezone(true);
   }

   public Integer getUTCTime() {
      return null;
   }

   public Boolean isDaylightSavingTimeEnabled() {
      return this.mOptions.isDaylightSavingTimeEnabled(false);
   }

   public String getDaylightSavingCountry() {
      return this.mOptions.getDaylightSavingCountry(false);
   }

   public String getDaylightSavingTime() {
      return null;
   }

   public Integer getPowerLineFrequency() {
      return null;
   }

   public String getIRCutMode() {
      return this.mOptions.getIRCutMode(false);
   }

   public List<String> getIRCutModes() {
      return this.mOptions.getIRCutModes();
   }

   public Integer getLightControl() {
      return this.mOptions.getLightControl();
   }

   public Boolean isSupportMultiRecType() {
      return null;
   }

   public Integer getInputVolume() {
      return this.mOptions.getInputVolume(false);
   }

   public Integer getOutputVolume() {
      return this.mOptions.getOutputVolume(false);
   }

   public Boolean getAlexa() {
      return null;
   }

   public Boolean supportAudioInput() {
      return null;
   }

   public Boolean supportAudioOutput() {
      return null;
   }

   public String getImageDefinition() {
      return null;
   }

   public String getImageStyle() {
      return null;
   }

   public Boolean isVideoFlipEnabled() {
      return null;
   }

   public Boolean isSupportOSDSet() {
      return null;
   }

   public Boolean isVideoMirrorEnabled() {
      return null;
   }

   public String getVideoCoverAreas() {
      return this.mOptions.getVideoCoverAreas(true);
   }

   public String getOSDTextStr() {
      return null;
   }

   public double getOSDTextX() {
      return 0.0D;
   }

   public double getOSDTextY() {
      return 0.0D;
   }

   public String getFixMode() {
      return null;
   }

   public String getFixParams() {
      return null;
   }

   public String getOSDFormat() {
      return null;
   }

   public Boolean isLiveFeatureEnabled() {
      return null;
   }

   public Boolean isPlaybackFeatureEnabled() {
      return null;
   }

   public void updateFeatureEnabled(boolean enable) {
   }

   public Boolean isHumanDetectionEnabled() {
      return this.mOptions.isHumanDetectionEnabled(true);
   }

   public Boolean isDrawHumanRegionEnabled() {
      return this.mOptions.isDrawHumanRegionEnabled(false);
   }

   public Boolean isFaceDetectionEnabled() {
      return null;
   }

   public Boolean isDrawFaceRegionEnabled() {
      return null;
   }

   public String getChannelInfo() {
      return this.mOptions.getChannelInfo(false);
   }

   public Boolean isChannelEnabled(int channel) {
      return this.mOptions.isChannelEnabled(channel);
   }

   public String getChannelDevType(int channel) {
      return this.mOptions.getChannelDevType(channel);
   }

   public String getChannelModel(int channel) {
      return this.mOptions.getChannelModel(channel);
   }

   public String getChannelFWVersion(int channel) {
      return this.mOptions.getChannelFWVersion(channel);
   }

   public String getChannelODMNum(int channel) {
      return this.mOptions.getChannelODMNum(channel);
   }

   public String getChannelSerialNum(int channel) {
      return this.mOptions.getChannelSerialNum(channel);
   }

   public String getChannelFWMagic(int channel) {
      return this.mOptions.getChannelFWMagic(channel);
   }

   public String getChannelRecordMode(int channel) {
      return this.mOptions.getChannelRecordMode(channel);
   }

   public Integer getChannelSignal(int channel) {
      return this.mOptions.getChannelSignal(channel);
   }

   public Integer getChannelBattery(int channel) {
      return this.mOptions.getChannelBattery(channel);
   }

   public String getChannelBatteryStatus(int channel) {
      String batteryStatus = this.mOptions.getChannelBatteryStatus(channel);
      if (batteryStatus != null) {
         batteryStatus = batteryStatus.toLowerCase();
      }

      String display = "";
      if (!TextUtils.isEmpty(batteryStatus) && !TextUtils.equals(batteryStatus, "none")) {
         int batteryValue = this.mOptions.getChannelBattery(channel);
         if (batteryValue >= 0 && batteryValue <= 100) {
            display = batteryValue + "%";
         }
      }

      return display;
   }

   public Integer getChannelStatus(int channel) {
      return this.mOptions.getChannelStatus(channel);
   }

   public Integer getChannelPIRStatus(int channel) {
      return this.mOptions.getChannelPIRStatus(channel);
   }

   public Boolean isChannelOnCharging(int channel) {
      return this.mOptions.isChannelOnCharging(channel);
   }

   public Boolean isChannelVersionLastest(int channel) {
      return this.mOptions.isChannelVersionLastest(channel);
   }

   public Boolean supportPTZ() {
      return this.mOptions.supportPTZ();
   }

   public int getPTZCapability() {
      return this.mOptions.getPTZCapability();
   }

   public Integer getPTZSpeed() {
      return this.mOptions.getPTZSpeed();
   }

   public Boolean supportAF() {
      return this.mOptions.supportAF();
   }

   public Boolean supportIrControl() {
      return this.mOptions.supportIrControl();
   }

   public String getPTZCruiseMode() {
      return this.mOptions.getPTZCruiseMode(false);
   }

   public Boolean isLinkageEnable() {
      return this.mOptions.isLinkageEnable(false);
   }

   public Double getLinkageCurCoordinatesX() {
      return this.mOptions.getLinkageCurCoordinatesX(false);
   }

   public Double getLinkageCurCoordinatesY() {
      return this.mOptions.getLinkageCurCoordinatesY(false);
   }

   public String getLedData() {
      return this.mOptions.getLedData(false);
   }

   public boolean isLedEnabled() {
      return this.mOptions.isLedEnabled();
   }

   public String getLedProduct() {
      return null;
   }

   public Integer getLedProject() {
      return null;
   }

   public Integer getLedChannelCount() {
      return this.mOptions.getLedChannelCount();
   }

   public Integer getLedChannelType(int channel) {
      return this.mOptions.getLedChannelType(channel);
   }

   public Integer getLedChannelValue(int channel) {
      return this.mOptions.getLedChannelValue(channel);
   }

   public String getWirelessSSID() {
      String ssid = this.mOptions.getWirelessSSID();
      if (isBase64(ssid)) {
         ssid = EncryptionUtil.decodeBase64(ssid);
      }

      return ssid;
   }

   public String getIpAddress() {
      return this.mOptions.getIpAddress();
   }

   public String getMacAddress() {
      return this.mOptions.getMacAddress();
   }

   public Boolean getWirelessEnable() {
      return this.mOptions.getWirelessEnable();
   }

   public String getWirelessState() {
      return this.mOptions.getWirelessState();
   }

   public String getWirelessAPs() {
      return this.mOptions.getWirelessAPs();
   }

   public List<String> getWirelessAPsList() {
      List<String> tempList = new ArrayList();
      if (this.getWirelessSSID() != null) {
         List<APsInfo> apsList = (List)(new Gson()).fromJson(this.getWirelessAPs(), (new TypeToken<List<APsInfo>>() {
         }).getType());

         for(int i = 0; i < apsList.size(); ++i) {
            String ssid = ((APsInfo)apsList.get(i)).getSsid();
            if (!TextUtils.isEmpty(ssid)) {
               tempList.add(EncryptionUtil.decodeBase64(ssid));
            }
         }
      }

      return tempList;
   }

   public Boolean isCloudBound() {
      return this.mOptions.isCloudBound();
   }

   public Boolean isChannelCloudEnabled(int channel) {
      return this.mOptions.isChannelCloudEnabled(channel);
   }

   public int getCloudChannelCount() {
      return this.mOptions.getCloudChannelCount();
   }

   public Integer getChannelCloudType(int channel) {
      return this.mOptions.getChannelCloudType(channel);
   }

   public String getWorkMode() {
      return this.mOptions.getWorkMode(false);
   }

   public List<String> getWorkModes() {
      return this.mOptions.getWorkModes();
   }

   public String getLTEOperator() {
      return this.mOptions.getLTEOperator();
   }

   public String getLTEPhone() {
      return this.mOptions.getLTEPhone();
   }

   public String getLTEICCID() {
      return this.mOptions.getLTEICCID();
   }

   public String getLTESimType() {
      return this.mOptions.getLTESimType();
   }

   public Integer getLTESignal() {
      return this.mOptions.getLTESignal();
   }

   public String getLteModuleIMEI() {
      return this.mOptions.getLteModuleIMEI();
   }

   public Integer getMaxChannel() {
      return this.mOptions.getMaxChannel();
   }

   public Integer getWirelessChannel() {
      return this.mOptions.getWirelessChannel(false);
   }

   public Integer getWirelessSignal() {
      return this.mOptions.getWirelessSignal();
   }

   public Integer getWirelessThroughput() {
      return null;
   }

   public String getUpgradeStatus() {
      return this.mOptions.getUpgradeStatus();
   }

   public String getUpgradeErrDescription() {
      return this.mOptions.getUpgradeErrDescription();
   }

   public Integer getUpgradeIndex() {
      return this.mOptions.getUpgradeIndex();
   }

   public Integer getUpgradeProgress() {
      return this.mOptions.getUpgradeProgress();
   }

   public String getIPCam() {
      return this.mOptions.getIPCam();
   }

   public String getCapabilitySet() {
      return this.mOptions.getCapabilitySet();
   }

   public Boolean isLightAlarmEnable() {
      return this.mOptions.isLightAlarmEnable(false);
   }

   public Integer getLightAlarmDuration() {
      return this.mOptions.getLightAlarmDuration(false);
   }

   public String getLightAlarmMode() {
      return this.mOptions.getLightAlarmMode(false);
   }

   public Boolean isWhiteLightAlarmEnable() {
      Boolean lightEnableV2 = this.mOptions.isWhiteAlarmLightV2Enable(false);
      Boolean lightEnable = this.mOptions.isLightAlarmEnable(false);
      return lightEnableV2 != null ? lightEnableV2 : lightEnable;
   }

   public Boolean getWhiteLightEnable() {
      String devType = this.mOptions.getChannelDevType(-1);
      return !"BATTERY_IPC".equals(devType) && !"BATTERY_DOOR_BELL".equals(devType) ? this.mOptions.getLightManCtrl() : this.mOptions.getLightEnableV2();
   }

   public Boolean getRedBlueLightEnable() {
      String devType = this.mOptions.getChannelDevType(-1);
      return !"BATTERY_IPC".equals(devType) && !"BATTERY_DOOR_BELL".equals(devType) ? this.mOptions.getAlarmLightManCtrl() : this.mOptions.getAlarmLightEnableV2();
   }

   public Boolean isRedBlueLightAlarmEnable() {
      return this.mOptions.isAlarmRedBlueLightEnable(false);
   }

   public void updateTalkMode(String mode) {
   }

   public void updateTimezone(int timezone) {
   }

   public void updateOSDFormat(int formatInt) {
   }

   public Options disableMatchExistsGettingObj() {
      return null;
   }

   public void restoreExistsGettingObj() {
   }

   public Boolean isSupportLightControl(int channel) {
      return this.mOptions.isSupportLightControl(channel);
   }

   public Integer getChannelManagerChannel() {
      return null;
   }

   public String getUsageScenario() {
      return null;
   }

   public String getTalkMode() {
      return null;
   }

   public List<Integer> getTimerSchedule(int channel) {
      return null;
   }

   public List<Integer> getMotionSchedule(int channel) {
      return null;
   }

   public List<Integer> getAlarmSchedule(int channel) {
      return null;
   }

   public List<Integer> getShutDownSchedule() {
      return null;
   }

   public Boolean isShutDownEnabled() {
      return null;
   }

   public String getWTType() {
      return this.mOptions.getWTType();
   }

   public Integer getWTSample() {
      return this.mOptions.getWTSample();
   }

   public List<Integer> getWTSch(int index) {
      return this.mOptions.getWTSch(false, index);
   }

   public Boolean isWTSchEnable(int index) {
      return this.mOptions.isWTSchEnable(false, index);
   }

   public String getNetworkStatus() {
      return this.mOptions.getNetworkStatus(false);
   }

   public Boolean getGuardEnable() {
      return null;
   }

   public Integer getGuardIndex() {
      return null;
   }

   public Integer getGuardStay() {
      return null;
   }

   public Integer[] getGuardSchedule() {
      return new Integer[0];
   }

   public boolean supportAddChannel() {
      return false;
   }

   public String getAddChannelStatus() {
      return null;
   }

   public Integer getAddChannelLeftTimeout() {
      return null;
   }

   public List<String> getAddChannelErrorCode() {
      return null;
   }

   public void clearAllOptions() {
   }

   @Nullable
   public Integer getBatteryDevMinPowerLimit() {
      return null;
   }

   @Nullable
   public Integer getBatterDevMaxPowerLimit() {
      return null;
   }

   public String getVersion() {
      return this.mOptions.getVersion();
   }

   public boolean isSupportChannelV2() {
      return false;
   }

   public boolean isChannelV2Got() {
      return false;
   }

   public boolean isChannelSupportPtzV2(int channel) {
      return false;
   }

   @Nullable
   public Boolean getChannelPtzV2(int channel) {
      return null;
   }

   @Nullable
   public Boolean getChannelPtzHorizontalV2(int channel) {
      return null;
   }

   @Nullable
   public Boolean getChannelPtzVerticalV2(int channel) {
      return null;
   }

   public Boolean isChannelSupportAfV2(int channel) {
      return null;
   }

   public Boolean isChannelSupportIrControlV2(int channel) {
      return null;
   }

   public boolean isChannelSupportPowerBatteryV2(int channel) {
      return false;
   }

   public boolean isChannelSupportLightControlV2(int channel) {
      return false;
   }

   @Nullable
   public Integer getChannelLightControlV2(int channel) {
      return null;
   }

   @Nullable
   public Integer getChannelSpFisheyeV2(int channel) {
      return null;
   }

   public Boolean getAlarmEnableV2() {
      return null;
   }

   public Boolean getAlarmPushEnableV2() {
      return null;
   }

   public List<String> getAlarmPushEventV2() {
      return null;
   }

   public List<String> getAlarmPushEventOptV2() {
      return null;
   }

   public Integer getAlarmPushIntervalV2() {
      return null;
   }

   public String getAlarmPushScheduleV2() {
      return null;
   }

   public Boolean getLightEnableV2() {
      return this.mOptions.getLightEnableV2();
   }

   public boolean isSupportSoundEnableV2() {
      return this.mOptions.isSupportSoundEnableV2(false);
   }

   public Integer getAlarmVolume() {
      return this.mOptions.getAlarmVolume(true);
   }

   public Boolean getOTA() {
      return this.mOptions.getOTA();
   }

   public boolean isBatteryDev() {
      String serialId = this.getDeviceId();
      return !TextUtils.isEmpty(serialId) ? serialId.startsWith("B") : false;
   }

   private static boolean isBase64(String str) {
      if (TextUtils.isEmpty(str)) {
         return false;
      } else {
         String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
         return Pattern.matches(base64Pattern, str);
      }
   }
}
