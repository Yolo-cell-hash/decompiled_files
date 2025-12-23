package com.eseeiot.basemodule.device.option.base;

import android.text.TextUtils;
import android.util.Log;
import com.eseeiot.basemodule.device.option.OptionSessionCallback;
import com.eseeiot.basemodule.device.option.SetOptionSession;
import com.eseeiot.basemodule.pojo.JSONArrayInfo;
import com.eseeiot.basemodule.pojo.JSONObjectInfo;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseSettingSession extends BaseOptionSession implements SetOptionSession {
   private static final String TAG = "BaseSettingV21";
   protected JSONObject mSettingOptionObj;
   private JSONObjectInfo mPreviousSetObject = null;
   private JSONArrayInfo mPreviousSetArray = null;
   private boolean mIsCombineEnabled = false;

   protected BaseSettingSession(CommonOption option) {
      super(option);
      this.get = false;
      this.mSettingOptionObj = new JSONObject();
   }

   public void save() {
      this.mOption.mTempSession = this;
   }

   public void discard() {
      if (this.equals(this.mOption.mTempSession)) {
         this.mOption.mTempSession = null;
      }

   }

   public boolean hasSomethingChanged() {
      try {
         JSONObject object = this.mergeBaseJSONObjectFromGetting(true, true);
         Log.d("BaseSettingV21", "hasSomethingChanged: " + object.toString());
         return object.length() > 0;
      } catch (JSONException var2) {
         return false;
      }
   }

   public void close() {
      this.closeSession();
   }

   public int commit() {
      this.uploadLog();
      return this.performCommit();
   }

   public SetOptionSession closeAfterFinish() {
      this.toBeClosed = true;
      return this;
   }

   public SetOptionSession addListener(OptionSessionCallback callback) {
      this.callback = callback;
      return this;
   }

   public SetOptionSession useVerify() {
      this.useVerify = true;
      return this;
   }

   public SetOptionSession usePassword() {
      this.useVerify = false;
      return this;
   }

   public SetOptionSession setTimeout(int timeoutInMillis) {
      this.timeout(timeoutInMillis);
      return this;
   }

   public SetOptionSession enableCombine(boolean enable) {
      this.mIsCombineEnabled = enable;
      return this;
   }

   public SetOptionSession skipMatchExistsGettingField() {
      this.mSkipMatchExistsGettingField = true;
      return this;
   }

   private JSONArray setObject(boolean optArray, Object val, String... paths) {
      return this.setObject(this.mSettingOptionObj, true, optArray, val, paths);
   }

   private JSONArray setObject(JSONObject optionObj, boolean enableCache, boolean optArray, Object val, String... paths) {
      if (paths.length == 0) {
         return null;
      } else {
         int i = 0;
         JSONObject previousObject = optionObj;
         if (enableCache && this.mPreviousSetObject != null && paths.length > 1 && paths[paths.length - 2].equals(this.mPreviousSetObject.keyName)) {
            i = paths.length - 1;
            previousObject = this.mPreviousSetObject.object;
         }

         try {
            for(; i < paths.length; ++i) {
               if (i == paths.length - 1) {
                  if (optArray) {
                     JSONArray array = previousObject.optJSONArray(paths[i]);
                     if (array == null) {
                        array = new JSONArray();
                        previousObject.put(paths[i], array);
                     }

                     return array;
                  }

                  previousObject.put(paths[i], val);
               } else {
                  JSONObject object = previousObject.optJSONObject(paths[i]);
                  if (object == null) {
                     object = new JSONObject();
                     previousObject.put(paths[i], object);
                  }

                  previousObject = object;
                  if (enableCache && i == paths.length - 2) {
                     if (this.mPreviousSetObject == null) {
                        this.mPreviousSetObject = new JSONObjectInfo();
                     }

                     this.mPreviousSetObject.keyName = paths[paths.length - 2];
                     this.mPreviousSetObject.object = object;
                  }
               }
            }
         } catch (JSONException var9) {
            Log.d("BaseSettingV21", "setObject: ex:" + var9);
         }

         return null;
      }
   }

   private void setChannelObject(String channelKey, int channel, String key, Object val, String... paths) {
      this.setChannelObject(this.mSettingOptionObj, true, channelKey, channel, key, val, paths);
   }

   private void setChannelObject(JSONObject optionObj, boolean enableCache, String channelKey, int channel, String key, Object val, String... paths) {
      if (paths.length > 1) {
         JSONArray array;
         if (enableCache && this.mPreviousSetArray != null && this.mPreviousSetArray.keyName.equals(paths[paths.length - 1])) {
            Log.d("BaseSettingV21", "setChannelObject: set " + paths[paths.length - 1] + " from cache");
            array = this.mPreviousSetArray.array;
         } else {
            array = this.setObject(optionObj, enableCache, true, (Object)null, paths);
         }

         if (array != null) {
            try {
               JSONObject object = null;

               for(int i = 0; i < array.length(); ++i) {
                  JSONObject tmpObject = array.getJSONObject(i);
                  Integer value = (Integer)tmpObject.opt(channelKey);
                  if (value != null && value == channel) {
                     object = tmpObject;
                     break;
                  }
               }

               if (object == null) {
                  object = new JSONObject();
                  array.put(object);
               }

               object.put(key, val);
            } catch (JSONException var13) {
            }

            if (enableCache) {
               if (this.mPreviousSetArray == null) {
                  this.mPreviousSetArray = new JSONArrayInfo();
               }

               this.mPreviousSetArray.keyName = paths[paths.length - 1];
               this.mPreviousSetArray.array = array;
            }

         }
      }
   }

   public SetOptionSession enableAudio(boolean enable) {
      this.setObject(false, enable, "IPCam", "ModeSetting", "AudioEnabled");
      return this;
   }

   public SetOptionSession enableLED(boolean enable) {
      this.setObject(false, enable, "IPCam", "ModeSetting", "LedEnabled");
      return this;
   }

   public SetOptionSession enableAlexa(boolean enable) {
      this.setObject(false, enable, "IPCam", "Alexa", "Enable");
      return this;
   }

   public SetOptionSession setInputVolume(int val) {
      this.setObject(false, val, "IPCam", "ModeSetting", "AudioVolume", "AudioInputVolume");
      return this;
   }

   public SetOptionSession setOutputVolume(int val) {
      this.setObject(false, val, "IPCam", "ModeSetting", "AudioVolume", "AudioOutputVolume");
      return this;
   }

   public SetOptionSession enablePromptSound(boolean enable) {
      this.setObject(false, enable, "IPCam", "PromptSounds", "Enabled");
      return this;
   }

   public SetOptionSession setPromptSoundLanguage(String type) {
      this.setObject(false, type, "IPCam", "PromptSounds", "Type");
      return this;
   }

   public SetOptionSession enableMotionDetection(boolean enable) {
      this.setObject(false, enable, "IPCam", "AlarmSetting", "MotionDetection", "Enabled");
      return this;
   }

   public SetOptionSession enableMotionPush(boolean enable) {
      this.setObject(false, enable, "IPCam", "AlarmSetting", "MessagePushEnabled");
      return this;
   }

   public SetOptionSession setPushSchedule(String data) {
      try {
         this.setObject(false, new JSONArray(data), "IPCam", "AlarmSetting", "MessagePushBitSchedule");
      } catch (JSONException var3) {
      }

      return this;
   }

   public SetOptionSession enableMotionRecord(boolean enable) {
      this.setObject(false, enable, "IPCam", "AlarmSetting", "MotionDetection", "MotionRecord");
      return this;
   }

   public SetOptionSession setMotionLevel(String level) {
      this.setObject(false, level, "IPCam", "AlarmSetting", "MotionDetection", "SensitivityLevel");
      return this;
   }

   public SetOptionSession setHumanoidLevel(String level) {
      this.setObject(false, level, "IPCam", "AlarmSetting", "HumanoidDetection", "sensitivityStep");
      return this;
   }

   public SetOptionSession setFaceLevel(String level) {
      this.setObject(false, level, "IPCam", "AlarmSetting", "FaceDetection", "sensitivityStep");
      return this;
   }

   public SetOptionSession setMotionType(String type) {
      this.setObject(false, type, "IPCam", "AlarmSetting", "MotionDetection", "type");
      return this;
   }

   public SetOptionSession setMotionRecordDuration(int duration) {
      this.setObject(false, duration, "IPCam", "AlarmSetting", "MotionDetection", "MdRecDuration");
      return this;
   }

   public SetOptionSession enableMotionTrack(boolean enable) {
      this.setObject(false, enable, "IPCam", "AlarmSetting", "MotionDetection", "motionTrackEnabled");
      return this;
   }

   public SetOptionSession setMotionAreaGrid(String data) {
      try {
         this.setObject(false, new JSONArray(data), "IPCam", "AlarmSetting", "MotionDetection", "grid");
      } catch (JSONException var3) {
      }

      return this;
   }

   public SetOptionSession setMotionAreaLine(String data) {
      try {
         this.setObject(false, new JSONArray(data), "IPCam", "AlarmSetting", "MotionDetection", "line");
      } catch (JSONException var3) {
      }

      return this;
   }

   public SetOptionSession enableMotionRing(boolean enable, boolean isGateway) {
      if (isGateway) {
         this.setObject(false, enable, "IPCam", "PromptSounds", "MotionWarningTone");
      } else {
         this.setObject(false, enable, "IPCam", "AlarmSetting", "MotionDetection", "MotionWarningTone");
      }

      return this;
   }

   public SetOptionSession customMotionRing(boolean custom, boolean isGateway) {
      if (isGateway) {
         this.setObject(false, custom ? "custom" : "default", "IPCam", "PromptSounds", "WarningToneType");
      } else {
         this.setObject(false, custom ? "custom" : "default", "IPCam", "AlarmSetting", "MotionDetection", "WarningToneType");
      }

      return this;
   }

   public SetOptionSession setCoolRecordDuration(int durationInSec) {
      this.setObject(false, durationInSec, "IPCam", "AlarmSetting", "MotionDetection", "CoolOffTimeDuration");
      return this;
   }

   public SetOptionSession enableHumanDetection(boolean enable) {
      this.setObject(false, enable, "IPCam", "AlarmSetting", "HumanoidDetection", "enable");
      return this;
   }

   public SetOptionSession enableHumanDrawRegion(boolean enable) {
      this.setObject(false, enable, "IPCam", "AlarmSetting", "HumanoidDetection", "drawRegion");
      return this;
   }

   public SetOptionSession enableFaceDetection(boolean enable) {
      this.setObject(false, enable, "IPCam", "AlarmSetting", "FaceDetection", "enable");
      return this;
   }

   public SetOptionSession enableFaceDrawRegion(boolean enable) {
      this.setObject(false, enable, "IPCam", "AlarmSetting", "FaceDetection", "drawRegion");
      return this;
   }

   public SetOptionSession enablePIR(boolean enable) {
      this.setObject(false, enable, "IPCam", "AlarmSetting", "PIRSetting", "Enabled");
      return this;
   }

   public SetOptionSession setPIRMediaPushTime(int time) {
      this.setObject(false, time, "IPCam", "AlarmSetting", "PIRSetting", "MediaPushTime");
      return this;
   }

   public SetOptionSession setPIRDelayTime(int time) {
      this.setObject(false, time, "IPCam", "AlarmSetting", "PIRSetting", "DelayTime");
      return this;
   }

   public SetOptionSession setPIRSchedule(String scheduleData) {
      try {
         this.setObject(false, new JSONArray(scheduleData), "IPCam", "AlarmSetting", "PIRSetting", "Schedule");
      } catch (JSONException var3) {
      }

      return this;
   }

   public SetOptionSession enableTimeRecord(boolean enable) {
      this.setObject(false, enable, "IPCam", "TfcardManager", "TimeRecordEnabled");
      return this;
   }

   public SetOptionSession setRecordSchedule(String schedules) {
      try {
         this.setObject(false, new JSONArray(schedules), "IPCam", "TfcardManager", "TFcard_recordSchedule");
      } catch (JSONException var3) {
      }

      return this;
   }

   public SetOptionSession setRecordMode(String mode) {
      this.setObject(false, mode, "IPCam", "RecordManager", "Mode");
      return this;
   }

   public SetOptionSession synchronisedTime(int time) {
      this.setObject(false, "" + time, "IPCam", "SystemOperation", "TimeSync", "LocalTime");
      this.setObject(false, "" + time, "IPCam", "SystemOperation", "TimeSync", "UTCTime");
      return this;
   }

   public SetOptionSession setTimezone(int timezone) {
      this.setObject(false, timezone, "IPCam", "SystemOperation", "TimeSync", "TimeZone");
      return this;
   }

   public SetOptionSession enableDaylightSavingTime(boolean enable) {
      this.setObject(false, enable, "IPCam", "SystemOperation", "DaylightSavingTime", "Enabled");
      return this;
   }

   public SetOptionSession setDaylightSavingTime(String dstData) {
      try {
         this.setObject(false, new JSONObject(dstData), "IPCam", "SystemOperation", "DaylightSavingTime");
      } catch (JSONException var3) {
      }

      return this;
   }

   public SetOptionSession setPowerLineFrequency(int frequency) {
      this.setObject(false, frequency, "IPCam", "powerLineFrequencyMode");
      return this;
   }

   public SetOptionSession setFixMode(String mode) {
      this.setObject(false, mode, "IPCam", "FisheyeSetting", "FixMode");
      return this;
   }

   public SetOptionSession setFixParam(String param) {
      try {
         this.setObject(false, new JSONArray(param), "IPCam", "FisheyeSetting", "FixParam");
      } catch (JSONException var3) {
      }

      return this;
   }

   public SetOptionSession setOSDFormat(String format) {
      this.setObject(false, format, "IPCam", "SystemOperation", "dateFormat");
      return this;
   }

   public SetOptionSession enableLiveFeature(boolean enable) {
      this.setObject(false, enable, "IPCam", "Feature", "Live");
      return this;
   }

   public SetOptionSession enablePlaybackFeature(boolean enable) {
      this.setObject(false, enable, "IPCam", "Feature", "Record");
      return this;
   }

   public SetOptionSession setDefinition(String definition) {
      this.setObject(false, definition, "IPCam", "ModeSetting", "Definition");
      return this;
   }

   public SetOptionSession setIRCutMode(String irCutMode) {
      this.setObject(false, irCutMode, "IPCam", "ModeSetting", "IRCutFilterMode");
      return this;
   }

   public SetOptionSession setImageStyle(String imageStyle) {
      this.setObject(false, imageStyle, "IPCam", "ModeSetting", "imageStyle");
      return this;
   }

   public SetOptionSession enableVideoFlip(boolean enable) {
      this.setObject(false, enable, "IPCam", "videoManager", "flipEnabled");
      return this;
   }

   public SetOptionSession enableVideoMirror(boolean enable) {
      this.setObject(false, enable, "IPCam", "videoManager", "mirrorEnabled");
      return this;
   }

   public SetOptionSession setVideoCoverAreas(String data) {
      try {
         JSONArray array = new JSONArray(data);
         this.setObject(false, array, "IPCam", "devCoverSetting");
      } catch (JSONException var3) {
      }

      return this;
   }

   public SetOptionSession setOSDSettingTextStr(String data) {
      this.setObject(false, data, "IPCam", "Osd", "Title", "Str");
      return this;
   }

   public SetOptionSession setOSDSettingTextX(double data) {
      this.setObject(false, data, "IPCam", "Osd", "Title", "X");
      return this;
   }

   public SetOptionSession setOSDSettingTextY(double data) {
      this.setObject(false, data, "IPCam", "Osd", "Title", "Y");
      return this;
   }

   public SetOptionSession setWorkMode(String mode) {
      this.setObject(false, mode, "IPCam", "WorkMode", "Mode");
      return this;
   }

   public SetOptionSession enableDoorbellRing(boolean enable) {
      this.setObject(false, enable, "IPCam", "PromptSounds", "DoorbellRingEnabled");
      return this;
   }

   public SetOptionSession customDoorbellRing(boolean custom) {
      this.setObject(false, custom, "IPCam", "PromptSounds", "DoorbellRingCustom");
      return this;
   }

   public SetOptionSession setWirelessChannel(int channel) {
      this.setObject(false, channel, "IPCam", "WirelessManager", "Channel");
      return this;
   }

   public SetOptionSession enableLamp(boolean enable) {
      this.setObject(false, enable ? 1 : 0, "IPCam", "ledPwm", "switch");
      return this;
   }

   public SetOptionSession enableInfraredLamp(boolean enable) {
      this.setObject(false, enable ? 1 : 0, "IPCam", "ledPwm", "infraredLampSwitch");
      return this;
   }

   public SetOptionSession enableNewSwitch(boolean enable) {
      this.setObject(false, enable ? 1 : 0, "IPCam", "ledPwm", "newSwitch");
      return this;
   }

   public SetOptionSession setLedProduct(String product) {
      this.setObject(false, product, "IPCam", "ledPwm", "product");
      return this;
   }

   public SetOptionSession setLedChannelValue(int type, int value) {
      this.setChannelObject("type", type, "type", type, "IPCam", "ledPwm", "channelInfo");
      this.setChannelObject("type", type, "num", value, "IPCam", "ledPwm", "channelInfo");
      this.setChannelObject("type", type, "channel", 1, "IPCam", "ledPwm", "channelInfo");
      return this;
   }

   public SetOptionSession enableChannelSetting(int channel) {
      this.setObject(false, "" + channel, "IPCam", "ChannelManager", "ChannelList");
      this.setObject(false, "SetChannel", "IPCam", "ChannelManager", "Operation");
      return this;
   }

   public SetOptionSession enableChannelCloudUpload(boolean enable, int channel, int type) {
      this.setObject(false, true, "IPCam", "OsscloudSetting", "IsBound");
      int chn = channel + 1;
      this.setChannelObject("ID", chn, "ID", chn, "IPCam", "OsscloudSetting", "Upload");
      this.setChannelObject("ID", chn, "Enabled", enable, "IPCam", "OsscloudSetting", "Upload");
      if (type >= 0) {
         this.setChannelObject("ID", chn, "Type", type, "IPCam", "OsscloudSetting", "Upload");
      }

      return this;
   }

   public SetOptionSession modifyWifi(String ssid, String password, String appBound) {
      this.setObject(false, ssid, "IPCam", "WirelessStation", "ssid");
      this.setObject(false, password, "IPCam", "WirelessStation", "psk");
      if (!appBound.isEmpty()) {
         this.setObject(false, appBound, "AppSet", "AppBound");
      }

      return this;
   }

   public SetOptionSession enableWifi(boolean enable, String appBound) {
      this.setObject(false, enable, "IPCam", "WirelessStation", "enable");
      if (!appBound.isEmpty()) {
         this.setObject(false, appBound, "AppSet", "AppBound");
      }

      return this;
   }

   public SetOptionSession removeChannel(int channel) {
      this.setObject(false, "" + channel, "IPCam", "ChannelManager", "ChannelList");
      this.setObject(false, "DelChannel", "IPCam", "ChannelManager", "Operation");
      return this;
   }

   public SetOptionSession formatTFCard() {
      this.setObject(false, new JSONObject(), "IPCam");
      this.setObject(false, "format", "IPCam", "TfcardManager", "Operation");
      return this;
   }

   /** @deprecated */
   @Deprecated
   public SetOptionSession repairTFCard() {
      this.setObject(false, new JSONObject(), "IPCam");
      this.setObject(false, "repair", "IPCam", "TfcardManager", "Operation");
      return this;
   }

   public SetOptionSession setConvenientSetting(String data) {
      this.setObject(false, new JSONObject(), "IPCam");
      this.setObject(false, data, "IPCam", "ModeSetting", "ConvenientSetting");
      return this;
   }

   public SetOptionSession upgradeFirmware() {
      this.setObject(false, new JSONObject(), "IPCam");
      String localTime = "" + (int)(System.currentTimeMillis() / 1000L);
      this.setObject(false, localTime, "IPCam", "SystemOperation", "TimeSync", "LocalTime");
      this.setObject(false, true, "IPCam", "SystemOperation", "Upgrade", "Enabled");
      return this;
   }

   public SetOptionSession upgradeFirmware(int channel) {
      this.setObject(false, new JSONObject(), "IPCam");
      this.setObject(false, true, "IPCam", "SystemOperation", "Upgrade", "Enabled");
      this.setObject(false, "SetChannel", "IPCam", "ChannelManager", "Operation");
      this.setObject(false, "" + channel, "IPCam", "ChannelManager", "ChannelList");
      return this;
   }

   public SetOptionSession upgradeFirmware(boolean upgradeChannel) {
      this.setObject(false, true, "IPCam", "SystemOperation", "Upgrade", "Enabled");
      this.setObject(false, upgradeChannel, "IPCam", "SystemOperation", "Upgrade", "EnabledUpgradeChannel");
      return this;
   }

   public SetOptionSession modifyPassword(String newUser, String newPassword) {
      this.setObject(false, "modify", "UserManager", "Method");
      this.setObject(false, "", "UserManager", "Verify");
      this.setObject(false, newUser, "UserManager", "username");
      this.setObject(false, newPassword, "UserManager", "password");
      this.setObject(false, new JSONObject(), "IPCam");
      return this;
   }

   public SetOptionSession modifyMonopolyPassword(String newUser, String newPassword) {
      this.modifyPassword(newUser, newPassword);
      this.setObject(false, "modify", "Monopoly", "Method");
      this.setObject(false, "", "Monopoly", "Verify");
      this.setObject(false, newUser, "Monopoly", "username");
      this.setObject(false, newPassword, "Monopoly", "password");
      return this;
   }

   public SetOptionSession reboot() {
      this.setObject(false, "{}", "IPCam");
      this.setObject(false, true, "IPCam", "SystemOperation", "Reboot");
      return this;
   }

   public SetOptionSession resetDefault() {
      this.setObject(false, "{}", "IPCam");
      this.setObject(false, true, "IPCam", "SystemOperation", "ResetDefault");
      return this;
   }

   public SetOptionSession ptzCtrlSpeed(int speed) {
      this.setObject(false, new JSONObject(), "IPCam");
      this.setObject(false, speed, "IPCam", "ptzManager", "ptzCtrlSpeed");
      return this;
   }

   public SetOptionSession enableLightAlarm(boolean enable) {
      this.setObject(false, enable, "IPCam", "AlarmSetting", "MotionDetection", "LightAlarm", "Enabled");
      return this;
   }

   public SetOptionSession setLightAlarmDuration(int sec) {
      this.setObject(false, sec, "IPCam", "AlarmSetting", "MotionDetection", "LightAlarm", "DurationSec");
      return this;
   }

   public SetOptionSession setLightAlarmMode(String mode) {
      this.setObject(false, mode, "IPCam", "AlarmSetting", "MotionDetection", "LightAlarm", "Mode");
      return this;
   }

   public SetOptionSession enableWhiteAlarmLightV2(boolean enable) {
      this.setObject(false, enable, "IPCam", "V2", "Alarm", "AlarmWhiteLight", "Enabled");
      return this;
   }

   public SetOptionSession setWhiteAlarmLightV2Duration(int sec) {
      this.setObject(false, sec, "IPCam", "V2", "Alarm", "AlarmWhiteLight", "DurSec");
      return this;
   }

   public SetOptionSession setWhiteAlarmLightV2Mode(String mode) {
      this.setObject(false, mode, "IPCam", "V2", "Alarm", "AlarmWhiteLight", "Mode");
      return this;
   }

   public SetOptionSession ptzCruiseMode(String mode) {
      if (TextUtils.isEmpty(mode)) {
         mode = "none";
      }

      this.setObject(false, new JSONObject(), "IPCam");
      this.setObject(false, mode, "IPCam", "ptzManager", "ptzCruiseMode");
      return this;
   }

   public SetOptionSession setLinkageEnable(boolean enable) {
      this.setObject(false, enable, "IPCam", "V2", "LensCtrl", "LensLinkageCtrl", "LinkageEnabled");
      return this;
   }

   public SetOptionSession setLensLinkageAdjust() {
      this.setObject(false, "ON", "IPCam", "V2", "R/LensLinkageAdjust", "Operate");
      return this;
   }

   public SetOptionSession setLinkageManCtrl(float x, float y) {
      this.setObject(false, "ON", "IPCam", "V2", "R/LensLinkageCtrl", "Operate");
      this.setObject(false, x, "IPCam", "V2", "R/LensLinkageCtrl", "CurCoordinates", "X");
      this.setObject(false, y, "IPCam", "V2", "R/LensLinkageCtrl", "CurCoordinates", "Y");
      return this;
   }

   private JSONObject mergeBaseJSONObjectFromGetting(boolean removeIPCamIfEmpty, boolean mergeOnNew) throws JSONException {
      if (this.mOption.mGettingOptionObj != null && this.mSettingOptionObj.length() != 0) {
         JSONObject srcIPCamObject = this.mOption.mGettingOptionObj.optJSONObject("IPCam");
         JSONObject res;
         JSONObject dstIPCamObject;
         if (mergeOnNew) {
            JSONObject tmpObject = new JSONObject(this.mSettingOptionObj.toString());
            dstIPCamObject = tmpObject.optJSONObject("IPCam");
            res = tmpObject;
         } else {
            dstIPCamObject = this.mSettingOptionObj.optJSONObject("IPCam");
            res = this.mSettingOptionObj;
         }

         if (srcIPCamObject != null && dstIPCamObject != null && OptionHelper.mergeJSONObject("IPCam", srcIPCamObject, dstIPCamObject, (List)null) && removeIPCamIfEmpty) {
            res.remove("IPCam");
         }

         return res;
      } else {
         return this.mSettingOptionObj;
      }
   }

   protected void createSubJSON(JSONObject optionJSON) throws JSONException {
      if (this.mSettingOptionObj.length() == 0) {
         throw new IllegalArgumentException("Without setting option!");
      } else {
         boolean removeIPCamIfEmpty = !this.mSettingOptionObj.has("UserManager");
         if (!this.mSkipMatchExistsGettingField) {
            this.mergeBaseJSONObjectFromGetting(removeIPCamIfEmpty, false);
         }

         if (this.mSettingOptionObj.length() == 0) {
            throw new IllegalArgumentException("Without setting option!");
         } else {
            Iterator addKeys = this.mSettingOptionObj.keys();

            while(addKeys.hasNext()) {
               String key = (String)addKeys.next();
               Object value = this.mSettingOptionObj.get(key);
               optionJSON.put(key, value);
            }

         }
      }
   }

   protected void handleResult(String result, JSONObject jsonResult) {
      String option = (String)jsonResult.opt("option");
      if (option != null) {
         if (option.equals("Authorization failed")) {
            this.performVconResult(2, 0);
         } else if (!option.equals("failed") && !option.equals("fail")) {
            if (option.equals("success")) {
               if (this.mIsCombineEnabled) {
                  try {
                     if (this.mSettingOptionObj != null) {
                        JSONObject ipcam = this.mSettingOptionObj.optJSONObject("IPCam");
                        if (ipcam != null) {
                           JSONObject channelManager = ipcam.optJSONObject("ChannelManager");
                           if (channelManager != null) {
                              channelManager.remove("ChannelList");
                              channelManager.remove("Operation");
                              ipcam.put("ChannelManager", channelManager);
                           }

                           JSONObject v2 = ipcam.optJSONObject("V2");
                           if (v2 != null) {
                              JSONObject v2Status = null;
                              JSONObject manCtrl = v2.optJSONObject("R/LightManCtrl");
                              String operate;
                              if (manCtrl != null) {
                                 operate = manCtrl.optString("Operate");
                                 if (!TextUtils.isEmpty(operate)) {
                                    v2Status = new JSONObject();
                                    v2Status.put("Light", operate);
                                 }
                              }

                              manCtrl = v2.optJSONObject("R/SoundManCtrl");
                              if (manCtrl != null) {
                                 operate = manCtrl.optString("Operate");
                                 if (!TextUtils.isEmpty(operate)) {
                                    if (v2Status == null) {
                                       v2Status = new JSONObject();
                                    }

                                    v2Status.put("Sound", operate);
                                 }
                              }

                              manCtrl = v2.optJSONObject("R/LensLinkageCtrl");
                              if (manCtrl != null) {
                                 operate = manCtrl.optString("Operate");
                                 if (!TextUtils.isEmpty(operate)) {
                                    if (v2Status == null) {
                                       v2Status = new JSONObject();
                                    }

                                    v2Status.put("CurCoordinates", operate);
                                 }
                              }

                              if (v2Status != null) {
                                 v2.put("Stat", v2Status);
                                 ipcam.put("V2", v2);
                              }
                           }

                           this.mSettingOptionObj.put("IPCam", ipcam);
                        }
                     }

                     OptionHelper.combineSubJson(this.mOption.mGettingOptionObj, this.mSettingOptionObj);
                     this.mOption.mPreviousGetObj = null;
                  } catch (JSONException var10) {
                  }
               }

               this.performVconResult(0, 0);
            }
         } else {
            this.performVconResult(1, 0);
         }
      }

   }

   public SetOptionSession setUsageScenario(String mode) {
      this.setObject(false, mode, "IPCam", "ModeSetting", "usageScenario");
      return this;
   }

   public SetOptionSession setTalkMode(String mode) {
      this.setObject(false, mode, "IPCam", "ModeSetting", "TalkMode");
      return this;
   }

   public SetOptionSession setTimerSchedule(String scheduleData, int channel) {
      return this.setGWChannelRecordSchedule("Timer", scheduleData, channel);
   }

   public SetOptionSession setMotionSchedule(String scheduleData, int channel) {
      return this.setGWChannelRecordSchedule("Motion", scheduleData, channel);
   }

   public SetOptionSession setAlarmSchedule(String scheduleData, int channel) {
      return this.setGWChannelRecordSchedule("Alarm", scheduleData, channel);
   }

   public SetOptionSession enableShutDown(boolean enable) {
      this.setObject(false, enable, "IPCam", "AutoShutdown", "Enabled");
      return this;
   }

   public SetOptionSession setShutDownSch(String Sch) {
      try {
         this.setObject(false, new JSONArray(Sch), "IPCam", "AutoShutdown", "Sch");
      } catch (JSONException var3) {
      }

      return this;
   }

   public SetOptionSession setWTSchEnable(boolean enable, int index) {
      String setKey = null;
      switch(index) {
      case 0:
         setKey = "MotionWarningTone";
         break;
      case 1:
         setKey = "WT2ndEnabled";
         break;
      case 2:
         setKey = "WT3rdEnabled";
      }

      if (setKey != null) {
         this.setObject(false, enable, "IPCam", "AlarmSetting", "MotionDetection", setKey);
      }

      return this;
   }

   public SetOptionSession setWTSch(String schListStr, int index) {
      String setKey = null;
      switch(index) {
      case 0:
         setKey = "WTSch";
         break;
      case 1:
         setKey = "WTSch2nd";
         break;
      case 2:
         setKey = "WTSch3rd";
      }

      if (setKey != null) {
         try {
            this.setObject(false, new JSONArray(schListStr), "IPCam", "AlarmSetting", "MotionDetection", setKey);
         } catch (JSONException var5) {
         }
      }

      return this;
   }

   private SetOptionSession setGWChannelRecordSchedule(String recordKey, String scheduleData, int channel) {
      try {
         JSONArray bitSchedule = this.setObject(true, (Object)null, "IPCam", "Record", "BitSchedule");
         if (bitSchedule != null) {
            if (bitSchedule.length() == 0) {
               this.setObject(false, new JSONArray("[{\"Channel\":" + channel + ",\"" + recordKey + "\":" + scheduleData + "}]"), "IPCam", "Record", "BitSchedule");
            } else {
               boolean findChannel = false;

               for(int i = 0; i < bitSchedule.length(); ++i) {
                  JSONObject jsonObject = bitSchedule.optJSONObject(i);
                  if (channel == jsonObject.optInt("Channel")) {
                     JSONArray alarm = new JSONArray(scheduleData);
                     jsonObject.putOpt(recordKey, alarm);
                     findChannel = true;
                     break;
                  }
               }

               if (!findChannel) {
                  JSONObject object = new JSONObject("{\"Channel\":" + channel + ",\"" + recordKey + "\":" + scheduleData + "}");
                  bitSchedule.put(object);
               }
            }
         }
      } catch (JSONException var9) {
      }

      return this;
   }

   public SetOptionSession enableGuardPosition(boolean guard) {
      this.setObject(false, guard, "IPCam", "ptzManager", "guardPos", "Enable");
      return this;
   }

   public SetOptionSession setGuardIndex(int index) {
      this.setObject(false, index, "IPCam", "ptzManager", "guardPos", "Index");
      return this;
   }

   public SetOptionSession setGuardStay(int stay) {
      this.setObject(false, stay, "IPCam", "ptzManager", "guardPos", "Stay");
      return this;
   }

   public SetOptionSession setGuardSchedule(int[] sch) {
      JSONArray array = new JSONArray();

      for(int i = 0; i < sch.length; ++i) {
         try {
            array.put(i, sch[i]);
         } catch (JSONException var5) {
            var5.printStackTrace();
         }
      }

      this.setObject(false, array, "IPCam", "ptzManager", "guardPos", "Sch");
      return this;
   }

   public SetOptionSession autoConnect(boolean enable) {
      this.mAutoConnect = enable;
      return this;
   }

   public SetOptionSession enableAlarmV2(boolean enable) {
      this.setObject(false, enable, "IPCam", "V2", "Alarm", "Enabled");
      return this;
   }

   public SetOptionSession enableAlarmPushV2(boolean enable) {
      this.setObject(false, enable, "IPCam", "V2", "Alarm", "MsgToApp", "Enabled");
      return this;
   }

   public SetOptionSession setAlarmPushEventV2(String[] event) {
      JSONArray jsonArray = new JSONArray();
      String[] var3 = event;
      int var4 = event.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String s = var3[var5];
         jsonArray.put(s);
      }

      this.setObject(false, jsonArray, "IPCam", "V2", "Alarm", "MsgToApp", "Event");
      return this;
   }

   public SetOptionSession setAlarmPushScheduleV2(String json) {
      try {
         this.setObject(false, new JSONArray(json), "IPCam", "V2", "Alarm", "MsgToApp", "Sch");
      } catch (JSONException var3) {
         var3.printStackTrace();
      }

      return this;
   }

   public SetOptionSession setAlarmAlarmPushIntervalV2(int intervalSec) {
      this.setObject(false, intervalSec, "IPCam", "V2", "Alarm", "MsgToApp", "Interval");
      return this;
   }

   public SetOptionSession appendLightManCtrl(boolean on, int durSec) {
      if (durSec < 0) {
         durSec = 1;
      } else if (durSec > 65536) {
         durSec = 65536;
      }

      String status = "OFF";
      if (on) {
         status = "ON";
      }

      this.setObject(false, status, "IPCam", "V2", "R/LightManCtrl", "Operate");
      this.setObject(false, durSec, "IPCam", "V2", "R/LightManCtrl", "DurSec");
      return this;
   }

   public SetOptionSession setAlarmVolume(int val) {
      this.setObject(false, val, "IPCam", "ModeSetting", "AudioVolume", "AlarmVolume");
      return this;
   }

   public SetOptionSession setSoundManCtrl(boolean enable, Integer val) {
      this.setObject(false, enable ? "ON" : "OFF", "IPCam", "V2", "R/SoundManCtrl", "Operate");
      if (val != null) {
         if (val >= 30) {
            val = 30;
         } else if (val == 0) {
            val = 1;
         }

         this.setObject(false, val, "IPCam", "V2", "R/SoundManCtrl", "DurSec");
      }

      return this;
   }

   public SetOptionSession enableRedBlueLightAlarm(boolean enable) {
      this.setObject(false, enable, "IPCam", "V2", "Alarm", "AlarmLight", "Enabled");
      return this;
   }

   public SetOptionSession setRedBlueLightDuration(int sec) {
      this.setObject(false, sec, "IPCam", "V2", "Alarm", "AlarmLight", "DurSec");
      return this;
   }

   public SetOptionSession toggleRedBlueLight(boolean isOpen, int sec) {
      this.setObject(false, isOpen ? "ON" : "OFF", "IPCam", "V2", "R/AlarmLightManCtrl", "Operate");
      this.setObject(false, sec, "IPCam", "V2", "R/AlarmLightManCtrl", "DurSec");
      return this;
   }

   public SetOptionSession switchRecordStream(String stream) {
      this.setObject(false, stream, "IPCam", "V2", "Record", "Stream");
      return this;
   }

   private void uploadLog() {
   }
}
