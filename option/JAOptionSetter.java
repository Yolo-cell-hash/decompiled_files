package com.eseeiot.option;

import android.text.TextUtils;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.option.ExtOptionSetter;
import com.eseeiot.basemodule.device.option.OptionResult;
import com.eseeiot.basemodule.device.option.Options;
import com.eseeiot.basemodule.device.option.RemoteSnapshotCallback;
import com.eseeiot.basemodule.device.option.SetOptionSession;
import com.eseeiot.basemodule.device.option.SettingResultCallback;
import com.eseeiot.basemodule.pojo.Schedule;
import com.eseeiot.core.audio.VirtualChannel;
import com.eseeiot.option.component.remotesnapshot.RemoteSnapshotVirtualChannel;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JAOptionSetter implements ExtOptionSetter {
   private static final Integer AUDITION_DURATION_SECOND = 5;
   protected Options mOptions;
   private VirtualChannel mVirtualChannel;
   private RemoteSnapshotVirtualChannel mRemoteSnapshotVcon;

   protected JAOptionSetter(Options options) {
      this.mOptions = options;
   }

   public void enableAudio(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().enableAudio(enable);
      this.baseSessionAction(session, callback);
   }

   public void enableMotionDetection(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession();
      if (this.mOptions.getAlarmEnableV2() != null) {
         session.enableAlarmV2(enable);
      } else {
         session.enableMotionDetection(enable);
      }

      this.baseSessionAction(session, callback);
   }

   public void enableHumanDetection(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().enableHumanDetection(enable);
      this.baseSessionAction(session, callback);
   }

   public void enableMotionTrack(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().enableMotionTrack(enable);
      this.baseSessionAction(session, callback);
   }

   public void enableLightAlarm(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().enableLightAlarm(enable);
      this.baseSessionAction(session, callback);
   }

   public void enableWhiteLightAlarm(boolean enable, SettingResultCallback callback) {
      SetOptionSession session;
      if (this.mOptions.isWhiteAlarmLightV2Enable(false) != null) {
         session = this.mOptions.newSetSession().enableWhiteAlarmLightV2(enable);
         this.baseSessionAction(session, callback);
      } else if (this.mOptions.isLightAlarmEnable(false) != null) {
         session = this.mOptions.newSetSession().enableLightAlarm(enable);
         this.baseSessionAction(session, callback);
      }

   }

   public void toggleWhiteLight(boolean isOpen, int durationSec, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().skipMatchExistsGettingField().appendLightManCtrl(isOpen, durationSec);
      this.baseSessionAction(session, callback);
   }

   public void enableRedBlueLightAlarm(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().enableRedBlueLightAlarm(enable);
      this.baseSessionAction(session, callback);
   }

   public void toggleRedBlueLight(boolean isOPen, int sec, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().toggleRedBlueLight(isOPen, sec);
      this.baseSessionAction(session, callback);
   }

   public void setCordonData(String type, String areaData, String lineData, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setMotionAreaGrid(areaData).setMotionAreaLine(lineData).setMotionType(type);
      this.baseSessionAction(session, callback);
   }

   public void enableMotionRing(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().enableMotionRing(enable, false);
      this.baseSessionAction(session, callback);
   }

   public void setSoundManCtrl(SettingResultCallback callback) {
      this.mOptions.newSetSession().setSoundManCtrl(true, AUDITION_DURATION_SECOND).setTimeout(10).addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void setAlarmVolume(int val, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setAlarmVolume(val);
      this.baseSessionAction(session, callback);
   }

   public void setOutputVolume(int val, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setOutputVolume(val);
      this.baseSessionAction(session, callback);
   }

   public void setInputVolume(int val, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setInputVolume(val);
      this.baseSessionAction(session, callback);
   }

   public void enableLED(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().enableLED(enable);
      this.baseSessionAction(session, callback);
   }

   public void enableRotate180Degrees(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().enableVideoFlip(enable).enableVideoMirror(enable);
      this.baseSessionAction(session, callback);
   }

   public void enableHumanDrawRegion(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().enableHumanDrawRegion(enable);
      this.baseSessionAction(session, callback);
   }

   public void setVideoCoverAreas(String data, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setVideoCoverAreas(data);
      this.baseSessionAction(session, callback);
   }

   public void setVideoCoverAreas(int channel, String data, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setVideoCoverAreas(data).enableCombine(true).enableChannelSetting(channel);
      this.baseSessionAction(session, callback);
   }

   public void setMotionLevel(String level, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setMotionLevel(level);
      this.baseSessionAction(session, callback);
   }

   public void setMotionLevel(int channel, String level, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setMotionLevel(level).enableCombine(true).enableChannelSetting(channel);
      this.baseSessionAction(session, callback);
   }

   public void setIPCRecordMode(OptionResult.RecordMode mode, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().enableTimeRecord(mode == OptionResult.RecordMode.RECORD_WITH_TIME).enableMotionRecord(true);
      this.baseSessionAction(session, callback);
   }

   public void setRecordSchedule(List<Schedule> schedules, SettingResultCallback callback) {
      String schStr = "[]";
      if (schedules != null && !schedules.isEmpty()) {
         DecimalFormat df = new DecimalFormat("00");
         JSONArray scheduleArray = new JSONArray();

         for(int j = 0; j < schedules.size(); ++j) {
            Schedule schedule = (Schedule)schedules.get(j);
            if (!schedule.isValid()) {
               throw new IllegalArgumentException("Invalid schedule, index = " + j);
            }

            String weekDay = "";

            for(int i = 0; i < 7; ++i) {
               if (schedule.isDayEnableOfWeek(1 + i)) {
                  if (weekDay.length() > 0) {
                     weekDay = weekDay + ",";
                  }

                  weekDay = weekDay + i;
               }
            }

            if (!TextUtils.isEmpty(weekDay)) {
               String beginTime = df.format((long)schedule.getStartTime(10)) + ":" + df.format((long)schedule.getStartTime(12)) + ":" + df.format((long)schedule.getStartTime(13));
               String endTime = df.format((long)schedule.getEndTime(10)) + ":" + df.format((long)schedule.getEndTime(12)) + ":" + df.format((long)schedule.getEndTime(13));

               try {
                  JSONObject scheduleObj = new JSONObject();
                  scheduleObj.put("BeginTime", beginTime);
                  scheduleObj.put("EndTime", endTime);
                  scheduleObj.put("Weekday", weekDay);
                  scheduleObj.put("ID", j);
                  scheduleArray.put(scheduleObj);
               } catch (JSONException var12) {
               }
            }
         }

         schStr = scheduleArray.toString();
      }

      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword().setRecordSchedule(schStr);
      this.baseSessionAction(session, callback);
   }

   public void upgradeFirmware(SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword().upgradeFirmware();
      this.baseSessionAction(session, callback);
   }

   public void upgradeFirmware(boolean upgradeChannel, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword().upgradeFirmware(upgradeChannel);
      this.baseSessionAction(session, callback);
   }

   public void modifyPassword(String newUser, String newPassword, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword().modifyPassword(newUser, newPassword);
      this.baseSessionActionNoCombine(session, callback);
   }

   public void setPushSchedule(List<Integer> pushSchedule, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setPushSchedule(ScheduleHelper.toJson(pushSchedule));
      this.baseSessionAction(session, callback);
   }

   public void enablePromptSound(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().enablePromptSound(enable);
      this.baseSessionAction(session, callback);
   }

   public void setPromptSoundLanguage(String prompt, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().enablePromptSound(true).setPromptSoundLanguage(prompt);
      this.baseSessionAction(session, callback);
   }

   public void synchronisedTime(SettingResultCallback callback) {
      this.mOptions.newSetSession().usePassword().closeAfterFinish().synchronisedTime((int)(System.currentTimeMillis() / 1000L)).addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void setTimezone(int timeZoom, SettingResultCallback callback) {
      this.mOptions.newSetSession().usePassword().closeAfterFinish().enableCombine(true).setTimezone(timeZoom).addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void enableDaylightSavingTime(boolean enable, SettingResultCallback callback) {
      this.mOptions.newSetSession().usePassword().closeAfterFinish().enableCombine(true).enableDaylightSavingTime(enable).addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void setDaylightSavingTime(String dst, SettingResultCallback callback) {
      String dstJson = this.getDSTJson(dst);
      this.mOptions.newSetSession().usePassword().closeAfterFinish().enableCombine(true).enableDaylightSavingTime(true).setDaylightSavingTime(dstJson).addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void reboot(SettingResultCallback callback) {
      this.mOptions.newSetSession().closeAfterFinish().usePassword().reboot().addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void resetDefault(SettingResultCallback callback) {
      this.mOptions.newSetSession().closeAfterFinish().usePassword().resetDefault().addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void modifyWifi(String ssid, String pwd, SettingResultCallback callback) {
      this.mOptions.newSetSession().usePassword().closeAfterFinish().addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).modifyWifi(ssid, pwd, "").commit();
   }

   private String getDSTJson(String key) {
      String temp = null;
      Calendar cal = Calendar.getInstance();
      int year = cal.get(1);
      byte var6 = -1;
      switch(key.hashCode()) {
      case -2095341728:
         if (key.equals("Israel")) {
            var6 = 5;
         }
         break;
      case -1898810230:
         if (key.equals("Poland")) {
            var6 = 3;
         }
         break;
      case -928898448:
         if (key.equals("Netherlands")) {
            var6 = 2;
         }
         break;
      case -223328434:
         if (key.equals("Greenland")) {
            var6 = 6;
         }
         break;
      case -59446962:
         if (key.equals("Canberra")) {
            var6 = 8;
         }
         break;
      case 2287414:
         if (key.equals("Iran")) {
            var6 = 4;
         }
         break;
      case 335430064:
         if (key.equals("Washington")) {
            var6 = 7;
         }
         break;
      case 1544803905:
         if (key.equals("default")) {
            var6 = 0;
         }
         break;
      case 1588421523:
         if (key.equals("Germany")) {
            var6 = 1;
         }
      }

      switch(var6) {
      case 0:
      case 1:
         temp = DaylightSavingTimeUtil.getString(101, year);
         break;
      case 2:
         temp = DaylightSavingTimeUtil.getString(102, year);
         break;
      case 3:
         temp = DaylightSavingTimeUtil.getString(103, year);
         break;
      case 4:
         temp = DaylightSavingTimeUtil.getString(104, year);
         break;
      case 5:
         temp = DaylightSavingTimeUtil.getString(105, year);
         break;
      case 6:
         temp = DaylightSavingTimeUtil.getString(106, year);
         break;
      case 7:
         temp = DaylightSavingTimeUtil.getString(107, year);
         break;
      case 8:
         temp = DaylightSavingTimeUtil.getString(108, year);
         break;
      default:
         temp = DaylightSavingTimeUtil.getString(106, year);
      }

      return temp;
   }

   public void sendAudioDataToDevice(MonitorDevice device, String path, SettingResultCallback callback) {
      int fileType = 5;
      boolean isNewAlarm = this.mOptions.isWTSchEnable(false, 1) != null;
      boolean useAAC = "AAC".equals(this.mOptions.getWTType());
      if (!useAAC) {
         path = path.replace(".aac", ".pcm");
      }

      if (!isNewAlarm) {
         fileType = 1;
      }

      if (this.mVirtualChannel != null) {
         this.mVirtualChannel.release();
      }

      this.mVirtualChannel = (new VirtualChannel.Builder(device, path, 1024)).setFileType(fileType).build();
      this.mVirtualChannel.init();
      this.mVirtualChannel.setCallback(() -> {
         if (callback != null) {
            callback.onResult(true);
         }

      });
      this.mVirtualChannel.send();
   }

   public void releaseAudioDataChannel() {
      if (this.mVirtualChannel != null) {
         this.mVirtualChannel.release();
      }

   }

   public void customMotionRing(boolean enable, SettingResultCallback callback) {
      this.mOptions.newSetSession().closeAfterFinish().usePassword().enableCombine(true).customMotionRing(enable, false).addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void setIRCutMode(String key, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setIRCutMode(key);
      this.baseSessionAction(session, callback);
   }

   public void setIRCutMode(int channel, String key, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setIRCutMode(key).enableCombine(true);
      if (channel != -1) {
         session.enableChannelSetting(channel);
      }

      this.baseSessionAction(session, callback);
   }

   public void setSpeed(int speed, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().ptzCtrlSpeed(speed);
      this.baseSessionAction(session, callback);
   }

   public void formatTFCard(SettingResultCallback callback) {
      this.mOptions.newSetSession().closeAfterFinish().usePassword().formatTFCard().addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void enableMotionPush(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession();
      if (this.mOptions.getAlarmPushEnableV2() != null) {
         session.enableAlarmPushV2(enable);
      } else {
         session.enableMotionPush(enable);
      }

      this.baseSessionAction(session, callback);
   }

   public void enableMotionPush(int channel, boolean enable, SettingResultCallback callback) {
      this.mOptions.newSetSession().closeAfterFinish().usePassword().enableCombine(true).enableChannelSetting(channel).enableMotionPush(enable).addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void setWirelessChannel(int channel, SettingResultCallback callback) {
      this.mOptions.newSetSession().usePassword().closeAfterFinish().enableCombine(true).setWirelessChannel(channel).addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void enableChannelCloudUpload(boolean enable, int channel, int type, SettingResultCallback callback) {
      this.mOptions.newSetSession().usePassword().closeAfterFinish().enableCombine(true).enableChannelCloudUpload(enable, channel, type).addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void switchRecordStream(String stream, SettingResultCallback callback) {
      this.mOptions.newSetSession().usePassword().closeAfterFinish().enableCombine(true).switchRecordStream(stream).addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   public void setWorkMode(String mode, boolean skipMatchExistsGettingField, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword().setWorkMode(mode);
      if (skipMatchExistsGettingField) {
         session.skipMatchExistsGettingField();
      }

      this.baseSessionAction(session, callback);
   }

   public void setMotionRecordDuration(int duration, boolean skipMatchExistsGettingField, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword().setMotionRecordDuration(duration);
      if (skipMatchExistsGettingField) {
         session.skipMatchExistsGettingField();
      }

      this.baseSessionAction(session, callback);
   }

   public void setWorkModeAndDuration(String mode, Integer duration, boolean skipMatchExistsGettingField, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword();
      if (!TextUtils.isEmpty(mode)) {
         session.setWorkMode(mode);
      }

      if (duration != null) {
         session.setMotionRecordDuration(duration);
      }

      if (skipMatchExistsGettingField) {
         session.skipMatchExistsGettingField();
      }

      this.baseSessionAction(session, callback);
   }

   public void setCoolRecordDuration(int durationInSec, boolean skipMatchExistsGettingField, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword().setCoolRecordDuration(durationInSec);
      if (skipMatchExistsGettingField) {
         session.skipMatchExistsGettingField();
      }

      this.baseSessionAction(session, callback);
   }

   public void enableMotionRing(boolean enable, boolean skipMatchExistsGettingField, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword().enableMotionRing(enable, false);
      if (skipMatchExistsGettingField) {
         session.skipMatchExistsGettingField();
      }

      this.baseSessionAction(session, callback);
   }

   public void setAlarmVolume(int val, boolean skipMatchExistsGettingField, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword().setAlarmVolume(val);
      if (skipMatchExistsGettingField) {
         session.skipMatchExistsGettingField();
      }

      this.baseSessionAction(session, callback);
   }

   public void customMotionRing(boolean enable, boolean skipMatchExistsGettingField, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword().customMotionRing(enable, false);
      if (skipMatchExistsGettingField) {
         session.skipMatchExistsGettingField();
      }

      this.baseSessionAction(session, callback);
   }

   public void setOutputVolume(int val, boolean skipMatchExistsGettingField, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword().setOutputVolume(val);
      if (skipMatchExistsGettingField) {
         session.skipMatchExistsGettingField();
      }

      this.baseSessionAction(session, callback);
   }

   public void setInputVolume(int val, boolean skipMatchExistsGettingField, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().closeAfterFinish().usePassword().setInputVolume(val);
      if (skipMatchExistsGettingField) {
         session.skipMatchExistsGettingField();
      }

      this.baseSessionAction(session, callback);
   }

   public void setLinkageEnable(boolean enable, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setLinkageEnable(enable);
      this.baseSessionAction(session, callback);
   }

   public void setLinkageManCtrl(float x, float y, SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setLinkageManCtrl(x, y);
      this.baseSessionAction(session, callback);
   }

   public void setLensLinkageAdjust(SettingResultCallback callback) {
      SetOptionSession session = this.mOptions.newSetSession().setLensLinkageAdjust();
      this.baseSessionAction(session, callback);
   }

   public void sendRemoteSnapshotReqToDevice(MonitorDevice device, String filePath, SettingResultCallback callback, RemoteSnapshotCallback snapshotCallback) {
      boolean isNewAlarm = this.mOptions.isWTSchEnable(false, 1) != null;
      if (this.mRemoteSnapshotVcon != null) {
         this.mRemoteSnapshotVcon.release();
      }

      this.mRemoteSnapshotVcon = (new RemoteSnapshotVirtualChannel.Builder(device, 2, filePath, snapshotCallback)).build();
      this.mRemoteSnapshotVcon.init();
      this.mRemoteSnapshotVcon.setCallback(() -> {
         if (callback != null) {
            callback.onResult(true);
         }

      });
      this.mRemoteSnapshotVcon.send();
   }

   public void releaseRemoteSnanshot() {
      if (this.mRemoteSnapshotVcon != null) {
         this.mRemoteSnapshotVcon.release();
      }

   }

   private void baseSessionAction(SetOptionSession session, SettingResultCallback callback) {
      session.setTimeout(10).enableCombine(true).addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }

   private void baseSessionActionNoCombine(SetOptionSession session, SettingResultCallback callback) {
      session.setTimeout(10).addListener((device, resultCode, errorCode, requestCode) -> {
         if (callback != null) {
            callback.onResult(0 == resultCode);
         }

      }).commit();
   }
}
