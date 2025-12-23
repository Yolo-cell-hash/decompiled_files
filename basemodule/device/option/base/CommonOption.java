package com.eseeiot.basemodule.device.option.base;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.option.GetOptionSession;
import com.eseeiot.basemodule.device.option.Options;
import com.eseeiot.basemodule.device.option.SetOptionSession;
import com.eseeiot.basemodule.pojo.JSONArrayInfo;
import com.eseeiot.basemodule.pojo.JSONObjectInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class CommonOption implements Options {
   private static final String TAG = "CommonOptionV21";
   private static final String SUPPORT_CRUISE_MODE_VERSION = "03052800";
   private static final String SUPPORT_CHANNEL_MORE_INFO_VERSION = "1.2.1";
   protected MonitorDevice mDevice;
   protected int mChannel = 0;
   protected boolean mIsGot = false;
   protected long mGotTimeInMillis = -1L;
   protected volatile JSONObject mGettingOptionObj;
   protected BaseSettingSession mTempSession = null;
   protected List<BaseOptionSession> mGettingSessions = new CopyOnWriteArrayList();
   protected JSONObjectInfo mPreviousGetObj = null;
   private JSONArrayInfo mPreviousGetArray = null;
   private final Object mLock = new Object();
   protected boolean mSkipExistsGettingObj;
   protected JSONObject mExistsGettingOptionObj;

   private JSONObject getOptionObj(boolean optFromSetting) {
      return optFromSetting && this.mTempSession != null ? this.mTempSession.mSettingOptionObj : this.mGettingOptionObj;
   }

   public Object optObject(String... paths) {
      return this.optObject(false, paths);
   }

   public JSONObject getGettingOptionObj() {
      return this.mGettingOptionObj;
   }

   public synchronized void setGettingOptionObj(JSONObject gettingOptionObj) {
      this.mGettingOptionObj = gettingOptionObj;
   }

   private Object optObject(boolean attemptOptFromSetting, String... paths) {
      JSONObject optionObj = this.getOptionObj(attemptOptFromSetting);
      if (optionObj == null) {
         if (attemptOptFromSetting) {
            attemptOptFromSetting = false;
            optionObj = this.getOptionObj(attemptOptFromSetting);
         }

         if (optionObj == null) {
            return null;
         }
      }

      return this.optObject(optionObj, attemptOptFromSetting, paths);
   }

   private Object optObject(JSONObject optionObj, boolean attemptOptFromSetting, String... paths) {
      if (paths.length == 0) {
         return null;
      } else {
         JSONObject tmpObject = null;
         String tmpKeyName = null;
         if (!attemptOptFromSetting && this.mPreviousGetObj != null && this.mPreviousGetObj.object != null) {
            tmpObject = this.mPreviousGetObj.object;
            tmpKeyName = this.mPreviousGetObj.keyName;
         }

         if (tmpObject != null && paths.length > 1 && paths[paths.length - 2].equals(tmpKeyName)) {
            return tmpObject.opt(paths[paths.length - 1]);
         } else {
            try {
               JSONObject previousObject = optionObj;

               for(int i = 0; i < paths.length; ++i) {
                  if (i == paths.length - 1) {
                     return previousObject.get(paths[i]);
                  }

                  previousObject = previousObject.getJSONObject(paths[i]);
                  if (!attemptOptFromSetting && i == paths.length - 2) {
                     if (this.mPreviousGetObj == null) {
                        this.mPreviousGetObj = new JSONObjectInfo();
                     }

                     this.mPreviousGetObj.object = previousObject;
                     this.mPreviousGetObj.keyName = paths[i];
                  }
               }
            } catch (JSONException var8) {
            }

            return null;
         }
      }
   }

   private Object optChannelObject(String channelKey, int channel, String keyName, String... paths) {
      JSONArray array;
      if (this.mPreviousGetArray != null && paths.length > 1 && paths[paths.length - 1].equals(this.mPreviousGetArray.keyName)) {
         try {
            array = this.mPreviousGetArray.array;
         } catch (Exception var9) {
            array = (JSONArray)this.optObject(paths);
         }
      } else {
         array = (JSONArray)this.optObject(paths);
      }

      if (array == null) {
         return null;
      } else {
         this.cacheGetArray(array, paths[paths.length - 1]);

         for(int i = 0; i < array.length(); ++i) {
            JSONObject object = (JSONObject)array.opt(i);
            Integer val = (Integer)object.opt(channelKey);
            if (val != null && val == channel) {
               return object.opt(keyName);
            }
         }

         return null;
      }
   }

   protected boolean attemptSetObject(Object val, String... paths) {
      if (paths.length == 0) {
         return false;
      } else {
         try {
            JSONObject previousObject = this.mGettingOptionObj;

            for(int i = 0; i < paths.length; ++i) {
               if (i == paths.length - 1) {
                  previousObject.put(paths[i], val);
                  return true;
               }

               previousObject = previousObject.getJSONObject(paths[i]);
            }
         } catch (JSONException var5) {
         }

         return false;
      }
   }

   public void bindDevice(MonitorDevice device) {
      this.mDevice = device;
   }

   public void setChannel(int channel) {
      this.mChannel = channel;
   }

   public SetOptionSession newSetSession() {
      if (this.mDevice == null) {
         throw new IllegalStateException("option not bind to device!");
      } else {
         return null;
      }
   }

   public SetOptionSession restoreSession() {
      return this.mTempSession;
   }

   public GetOptionSession newGetSession() {
      if (this.mDevice == null) {
         throw new IllegalStateException("option not bind to device!");
      } else {
         return null;
      }
   }

   public boolean isGot() {
      return this.mIsGot;
   }

   public boolean isGetting() {
      return !this.mGettingSessions.isEmpty();
   }

   public int gotTimeFromNowInSec() {
      return this.mGotTimeInMillis > 0L ? (int)((System.currentTimeMillis() - this.mGotTimeInMillis) / 1000L) : (int)this.mGotTimeInMillis;
   }

   public boolean isSupportCoverSetting() {
      JSONArray array = (JSONArray)this.optObject("IPCam", "devCoverSetting");
      return array != null && array.length() > 0;
   }

   public boolean isSupportSetWifi() {
      Boolean enabled = (Boolean)this.optObject("CapabilitySet", "wifiStationCanSet");
      return enabled != null ? enabled : false;
   }

   public Boolean isSupportLv() {
      Integer type = (Integer)this.optObject("CapabilitySet", "spP2pType");
      return type != null && (type.byteValue() & 2) == 2;
   }

   public boolean isSupportWirelessCheck() {
      Boolean enabled = (Boolean)this.optObject("IPCam", "WirelessCheck", "Support");
      return enabled != null ? enabled : false;
   }

   public boolean isSupportChannelSetting() {
      JSONArray array = (JSONArray)this.optObject("IPCam", "ChannelInfo");
      return array != null && array.length() > 1;
   }

   public boolean isSupportReboot() {
      Boolean support = (Boolean)this.optObject("IPCam", "SystemOperation", "Reboot");
      return support != null;
   }

   public boolean isSupportResetDefault() {
      Boolean support = (Boolean)this.optObject("IPCam", "SystemOperation", "ResetDefault");
      return support != null;
   }

   public boolean supportTwoWayTalk() {
      Boolean support = (Boolean)this.optObject("CapabilitySet", "twowayTalk");
      return support != null && support;
   }

   public String getDeviceId() {
      return (String)this.optObject("IPCam", "DeviceInfo", "ID");
   }

   public Boolean isPromptEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "PromptSounds", "Enabled");
      return fromSet && obj == null ? this.isPromptEnabled(false) : obj;
   }

   public String getPromptLanguage(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "PromptSounds", "Type");
      return fromSet && obj == null ? this.getPromptLanguage(false) : obj;
   }

   public List<String> getPromptLanguages() {
      JSONArray array = (JSONArray)this.optObject("IPCam", "PromptSounds", "TypeOption");
      if (array != null && array.length() > 0) {
         List<String> languages = new ArrayList();

         for(int i = 0; i < array.length(); ++i) {
            languages.add(array.optString(i));
         }

         return languages;
      } else {
         return null;
      }
   }

   public Boolean isAudioEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "ModeSetting", "AudioEnabled");
      return fromSet && obj == null ? this.isAudioEnabled(false) : obj;
   }

   public Boolean isLEDEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "ModeSetting", "LedEnabled");
      return fromSet && obj == null ? this.isLEDEnabled(false) : obj;
   }

   public Integer getAudioSample() {
      return (Integer)this.optObject(false, "IPCam", "ModeSetting", "AudioSample");
   }

   public Boolean isMotionEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "Enabled");
      return fromSet && obj == null ? this.isMotionEnabled(false) : obj;
   }

   public void updateMotionEnabled(boolean enable) {
      if (this.mGettingOptionObj != null) {
         JSONObject ipcam = this.mGettingOptionObj.optJSONObject("IPCam");
         if (ipcam != null) {
            JSONObject alarmSetting = ipcam.optJSONObject("AlarmSetting");
            if (alarmSetting != null) {
               JSONObject motionDetection = alarmSetting.optJSONObject("MotionDetection");
               if (motionDetection != null) {
                  try {
                     motionDetection.put("Enabled", enable);
                  } catch (JSONException var6) {
                     var6.printStackTrace();
                  }
               }
            }
         }
      }

   }

   public Boolean isMotionRingEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "MotionWarningTone");
      if (obj == null) {
         obj = (Boolean)this.optObject(fromSet, "IPCam", "PromptSounds", "MotionWarningTone");
      }

      return fromSet && obj == null ? this.isMotionRingEnabled(false) : obj;
   }

   public Boolean isGatewayMotionRingEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "PromptSounds", "MotionWarningTone");
      return fromSet && obj == null ? this.isGatewayMotionRingEnabled(false) : obj;
   }

   public Boolean isMotionRingCustom(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "WarningToneType");
      if (obj == null) {
         obj = (String)this.optObject(fromSet, "IPCam", "PromptSounds", "WarningToneType");
      }

      if (fromSet && obj == null) {
         return this.isMotionRingCustom(false);
      } else {
         return obj != null ? obj.equals("custom") : null;
      }
   }

   public Boolean isGatewayMotionRingCustom(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "PromptSounds", "WarningToneType");
      if (fromSet && obj == null) {
         return this.isGatewayMotionRingCustom(false);
      } else {
         return obj != null ? obj.equals("custom") : null;
      }
   }

   public Boolean isMotionRecordEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "MotionRecord");
      return fromSet && obj == null ? this.isMotionRecordEnabled(false) : obj;
   }

   public String getMotionType(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "type");
      return fromSet && obj == null ? this.getMotionType(false) : obj;
   }

   public String getMotionLevel(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "SensitivityLevel");
      return fromSet && obj == null ? this.getMotionLevel(false) : obj;
   }

   public String getHumanDetectionLevel(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "AlarmSetting", "HumanoidDetection", "sensitivityStep");
      return fromSet && obj == null ? this.getHumanDetectionLevel(false) : obj;
   }

   public String getFaceDetectionLevel(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "AlarmSetting", "FaceDetection", "sensitivityStep");
      return fromSet && obj == null ? this.getHumanDetectionLevel(false) : obj;
   }

   public Integer getMotionRecordDuration(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "MdRecDuration");
      return fromSet && obj == null ? this.getMotionRecordDuration(false) : obj;
   }

   public Integer getCoolRecordDuration(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "CoolOffTimeDuration");
      return fromSet && obj == null ? this.getMotionRecordDuration(false) : obj;
   }

   public Boolean isMotionTrackEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "motionTrackEnabled");
      return fromSet && obj == null ? this.isMotionTrackEnabled(false) : obj;
   }

   public Integer[] getVideoResolution() {
      Integer w = (Integer)this.optObject("IPCam", "AlarmSetting", "MotionDetection", "width");
      Integer h = (Integer)this.optObject("IPCam", "AlarmSetting", "MotionDetection", "height");
      return w != null && h != null ? new Integer[]{w, h} : null;
   }

   public Integer[] getMotionAreaRect() {
      Integer row = (Integer)this.optObject("IPCam", "AlarmSetting", "MotionDetection", "maxRows");
      Integer column = (Integer)this.optObject("IPCam", "AlarmSetting", "MotionDetection", "maxColumns");
      return row != null && column != null ? new Integer[]{row, column} : null;
   }

   public String getMotionAreaLine(boolean fromSet) {
      JSONArray array = (JSONArray)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "line");
      if (fromSet && array == null) {
         return this.getMotionAreaLine(false);
      } else {
         return array != null ? array.toString() : null;
      }
   }

   public Integer getMotionAreaMaxLine() {
      return (Integer)this.optObject("IPCam", "AlarmSetting", "MotionDetection", "maxlines");
   }

   public List<Long> getMotionAreaGrid(boolean fromSet) {
      JSONArray array = (JSONArray)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "grid");
      if (!fromSet || array != null && array.length() != 0) {
         if (array != null && array.length() > 0) {
            List<Long> list = new ArrayList();

            for(int i = 0; i < array.length(); ++i) {
               list.add(array.optLong(i));
            }

            return list;
         } else {
            return null;
         }
      } else {
         return this.getMotionAreaGrid(false);
      }
   }

   public Boolean isDoorbellRingEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "PromptSounds", "DoorbellRingEnabled");
      return fromSet && obj == null ? this.isDoorbellRingEnabled(false) : obj;
   }

   public Boolean isDoorbellRingCustom(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "PromptSounds", "DoorbellRingCustom");
      return fromSet && obj == null ? this.isDoorbellRingCustom(false) : obj;
   }

   public Boolean isPIREnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AlarmSetting", "PIRSetting", "Enabled");
      return fromSet && obj == null ? this.isPIREnabled(false) : obj;
   }

   public Boolean isShutDownEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AutoShutdown", "Enabled");
      return fromSet && obj == null ? this.isShutDownEnabled(false) : obj;
   }

   public void updatePIREnabled(boolean enable, int channel) {
      this.updateCachePirStatus(this.mGettingOptionObj, enable, channel);
      this.updateCachePirStatus(this.mExistsGettingOptionObj, enable, channel);
   }

   public Integer getPIRPushTime(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "AlarmSetting", "PIRSetting", "MediaPushTime");
      return fromSet && obj == null ? this.getPIRPushTime(false) : obj;
   }

   public List<Integer> getPIRSchedule(boolean fromSet) {
      JSONArray array = (JSONArray)this.optObject(fromSet, "IPCam", "AlarmSetting", "PIRSetting", "Schedule");
      if (fromSet && array == null) {
         return this.getPIRSchedule(false);
      } else if (array == null) {
         return null;
      } else {
         List<Integer> schedule = new ArrayList();

         for(int i = 0; i < array.length(); ++i) {
            schedule.add(array.optInt(i));
         }

         return schedule;
      }
   }

   public List<Integer> getShutDownSchedule(boolean fromSet) {
      JSONArray array = (JSONArray)this.optObject(fromSet, "IPCam", "AutoShutdown", "Sch");
      if (fromSet && array == null) {
         return this.getShutDownSchedule(false);
      } else if (array == null) {
         return null;
      } else {
         List<Integer> schedule = new ArrayList();

         for(int i = 0; i < array.length(); ++i) {
            schedule.add(array.optInt(i));
         }

         return schedule;
      }
   }

   public Integer getPIRDelayTime(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "AlarmSetting", "PIRSetting", "DelayTime");
      return fromSet && obj == null ? this.getPIRDelayTime(false) : obj;
   }

   public Boolean isPushEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AlarmSetting", "MessagePushEnabled");
      return fromSet && obj == null ? this.isPushEnabled(false) : obj;
   }

   public List<Integer> getPushSchedule(boolean fromSet) {
      JSONArray array = (JSONArray)this.optObject(fromSet, "IPCam", "AlarmSetting", "MessagePushBitSchedule");
      if (fromSet && array == null) {
         return this.getPushSchedule(false);
      } else if (array == null) {
         return null;
      } else {
         List<Integer> schedule = new ArrayList();

         for(int i = 0; i < array.length(); ++i) {
            schedule.add(array.optInt(i));
         }

         return schedule;
      }
   }

   public Boolean isTimeRecordEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "TfcardManager", "TimeRecordEnabled");
      return fromSet && obj == null ? this.isTimeRecordEnabled(false) : obj;
   }

   public JSONArray getTimeRecordSchedule(boolean fromSet) {
      JSONArray array = (JSONArray)this.optObject(fromSet, "IPCam", "TfcardManager", "TFcard_recordSchedule");
      return fromSet && array == null ? this.getTimeRecordSchedule(false) : array;
   }

   public String getTFCardStatus() {
      return (String)this.optObject("IPCam", "TfcardManager", "Status");
   }

   public String getTFCardTotalSpace() {
      return (String)this.optObject("IPCam", "TfcardManager", "TotalSpacesize");
   }

   public String getTFCardLeaveSpace() {
      return (String)this.optObject("IPCam", "TfcardManager", "LeaveSpacesize");
   }

   public String getRecordDateInfo() {
      JSONArray array = (JSONArray)this.optObject("IPCam", "recordInfo", "recordScheduleDateInfo");
      return array != null ? array.toString() : null;
   }

   public String getRecordMode(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "RecordManager", "Mode");
      return fromSet && obj == null ? this.getRecordMode(false) : obj;
   }

   public String getConvenientSetting() {
      return (String)this.optObject("IPCam", "ModeSetting", "ConvenientSetting");
   }

   public Integer getTimezone(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "SystemOperation", "TimeSync", "TimeZone");
      return fromSet && obj == null ? this.getTimezone(false) : obj;
   }

   public Integer getUTCTime(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "SystemOperation", "TimeSync", "UTCTime");
      return fromSet && obj == null ? this.getUTCTime(false) : obj;
   }

   public Boolean isDaylightSavingTimeEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "SystemOperation", "DaylightSavingTime", "Enabled");
      return fromSet && obj == null ? this.isDaylightSavingTimeEnabled(false) : obj;
   }

   public String getDaylightSavingCountry(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "SystemOperation", "DaylightSavingTime", "Country");
      return fromSet && obj == null ? this.getDaylightSavingCountry(false) : obj;
   }

   public String getDaylightSavingTime(boolean fromSet) {
      JSONObject obj = (JSONObject)this.optObject(fromSet, "IPCam", "SystemOperation", "DaylightSavingTime");
      if (fromSet && obj == null) {
         return this.getDaylightSavingTime(false);
      } else {
         return obj != null ? obj.toString() : null;
      }
   }

   public Integer getPowerLineFrequency(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "powerLineFrequencyMode");
      return fromSet && obj == null ? this.getPowerLineFrequency(false) : obj;
   }

   public String getIRCutMode(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "ModeSetting", "IRCutFilterMode");
      return fromSet && obj == null ? this.getIRCutMode(false) : obj;
   }

   public List<String> getIRCutModes() {
      JSONArray array = (JSONArray)this.optObject("IPCam", "ModeSetting", "IRCutFilterModeProperty", "opt");
      if (array != null && array.length() > 0) {
         List<String> opts = new ArrayList();

         for(int i = 0; i < array.length(); ++i) {
            opts.add(array.optString(i));
         }

         return opts;
      } else {
         return null;
      }
   }

   public Integer getLightControl() {
      return (Integer)this.optObject(false, "CapabilitySet", "lightControl");
   }

   public Boolean isSupportMultiRecType() {
      return (Boolean)this.optObject(false, "CapabilitySet", "multiRecType");
   }

   public Integer getInputVolume(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "ModeSetting", "AudioVolume", "AudioInputVolume");
      return fromSet && obj == null ? this.getInputVolume(false) : obj;
   }

   public Integer getOutputVolume(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "ModeSetting", "AudioVolume", "AudioOutputVolume");
      return fromSet && obj == null ? this.getOutputVolume(false) : obj;
   }

   public Boolean getAlexa() {
      Boolean obj = (Boolean)this.optObject("IPCam", "Alexa", "Enable");
      return obj;
   }

   public Boolean supportAudioInput(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "CapabilitySet", "audioInput");
      return fromSet && obj == null ? this.supportAudioInput(false) : obj;
   }

   public Boolean supportAudioOutput(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "CapabilitySet", "audioOutput");
      return fromSet && obj == null ? this.supportAudioOutput(false) : obj;
   }

   public String getImageDefinition(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "ModeSetting", "Definition");
      return fromSet && obj == null ? this.getImageDefinition(false) : obj;
   }

   public String getImageStyle(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "ModeSetting", "imageStyle");
      return fromSet && obj == null ? this.getImageStyle(false) : obj;
   }

   public Boolean isVideoFlipEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "videoManager", "flipEnabled");
      return fromSet && obj == null ? this.isVideoFlipEnabled(false) : obj;
   }

   public Boolean isSupportOSDSet() {
      JSONObject obj = (JSONObject)this.optObject(false, "IPCam", "Osd");
      return obj != null;
   }

   public Boolean isVideoMirrorEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "videoManager", "mirrorEnabled");
      return fromSet && obj == null ? this.isVideoMirrorEnabled(false) : obj;
   }

   public String getVideoCoverAreas(boolean fromSet) {
      JSONArray array = (JSONArray)this.optObject(fromSet, "IPCam", "devCoverSetting");
      if (fromSet && array == null) {
         return this.getVideoCoverAreas(false);
      } else {
         return array != null ? array.toString() : null;
      }
   }

   public String getOSDTextStr(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "Osd", "Title", "Str");
      return fromSet && obj == null ? this.getOSDTextStr(false) : obj;
   }

   public double getOSDTextX(boolean fromSet) {
      Double obj = (Double)this.optObject(fromSet, "IPCam", "Osd", "Title", "X");
      return fromSet && obj == null ? this.getOSDTextX(false) : obj;
   }

   public double getOSDTextY(boolean fromSet) {
      Double obj = (Double)this.optObject(fromSet, "IPCam", "Osd", "Title", "Y");
      return fromSet && obj == null ? this.getOSDTextY(false) : obj;
   }

   public String getFixMode(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "FisheyeSetting", "FixMode");
      return fromSet && obj == null ? this.getFixMode(false) : obj;
   }

   public String getFixParams(boolean fromSet) {
      JSONArray array = (JSONArray)this.optObject(fromSet, "IPCam", "FisheyeSetting", "FixParam");
      if (fromSet && array == null) {
         return this.getFixParams(false);
      } else {
         return array != null ? array.toString() : null;
      }
   }

   public String getOSDFormat(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "SystemOperation", "dateFormat");
      return fromSet && obj == null ? this.getOSDFormat(false) : obj;
   }

   public Boolean isLiveFeatureEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "Feature", "Live");
      return fromSet && obj == null ? this.isLiveFeatureEnabled(false) : obj;
   }

   public Boolean isPlaybackFeatureEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "Feature", "Record");
      return fromSet && obj == null ? this.isPlaybackFeatureEnabled(false) : obj;
   }

   public void updateFeatureEnabled(boolean enable) {
      if (this.mGettingOptionObj != null) {
         JSONObject ipcam = this.mGettingOptionObj.optJSONObject("IPCam");
         if (ipcam != null) {
            JSONObject feature = ipcam.optJSONObject("Feature");
            if (feature != null) {
               try {
                  feature.put("Live", enable);
                  feature.put("Record", enable);
               } catch (JSONException var5) {
                  var5.printStackTrace();
               }
            }
         }
      }

   }

   public Boolean isHumanDetectionEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AlarmSetting", "HumanoidDetection", "enable");
      return fromSet && obj == null ? this.isHumanDetectionEnabled(false) : obj;
   }

   public Boolean isDrawHumanRegionEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AlarmSetting", "HumanoidDetection", "drawRegion");
      return fromSet && obj == null ? this.isDrawHumanRegionEnabled(false) : obj;
   }

   public Boolean isFaceDetectionEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AlarmSetting", "FaceDetection", "enable");
      return fromSet && obj == null ? this.isFaceDetectionEnabled(false) : obj;
   }

   public Boolean isDrawFaceRegionEnabled(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AlarmSetting", "FaceDetection", "drawRegion");
      return fromSet && obj == null ? this.isDrawFaceRegionEnabled(false) : obj;
   }

   public String getChannelInfo(boolean fromSet) {
      JSONArray obj = (JSONArray)this.optObject(fromSet, "IPCam", "ChannelInfo");
      if (fromSet && obj == null) {
         return this.getChannelInfo(false);
      } else {
         return obj == null ? null : obj.toString();
      }
   }

   public Boolean isChannelEnabled(int channel) {
      return (Boolean)this.optChannelObject("Channel", channel, "Enabled", "IPCam", "ChannelInfo");
   }

   public String getChannelDevType(int channel) {
      return channel == -1 ? (String)this.optObject("IPCam", "DeviceInfo", "DeviceType") : (String)this.optChannelObject("Channel", channel, "DeviceType", "IPCam", "ChannelInfo");
   }

   public String getChannelModel(int channel) {
      return channel == -1 ? (String)this.optObject("IPCam", "DeviceInfo", "Model") : (String)this.optChannelObject("Channel", channel, "Model", "IPCam", "ChannelInfo");
   }

   public String getChannelFWVersion(int channel) {
      return channel == -1 ? (String)this.optObject("IPCam", "DeviceInfo", "FWVersion") : (String)this.optChannelObject("Channel", channel, "Version", "IPCam", "ChannelInfo");
   }

   public String getChannelODMNum(int channel) {
      return channel == -1 ? (String)this.optObject("IPCam", "DeviceInfo", "OEMNumber") : (String)this.optChannelObject("Channel", channel, "OdmNum", "IPCam", "ChannelInfo");
   }

   public String getChannelSerialNum(int channel) {
      return (String)this.optChannelObject("Channel", channel, "SerialNum", "IPCam", "ChannelInfo");
   }

   public String getChannelFWMagic(int channel) {
      return channel == -1 ? (String)this.optObject("IPCam", "DeviceInfo", "FWMagic") : (String)this.optChannelObject("Channel", channel, "FWMagic", "IPCam", "ChannelInfo");
   }

   public String getChannelRecordMode(int channel) {
      return (String)this.optChannelObject("Channel", channel, "RecordStatus", "IPCam", "ChannelStatus");
   }

   public Integer getChannelSignal(int channel) {
      return (Integer)this.optChannelObject("Channel", channel, "Signal", "IPCam", "ChannelStatus");
   }

   public Integer getChannelBattery(int channel) {
      return (Integer)this.optChannelObject("Channel", channel, "Battery", "IPCam", "ChannelStatus");
   }

   public String getChannelBatteryStatus(int channel) {
      return (String)this.optChannelObject("Channel", channel, "BatteryStatus", "IPCam", "ChannelStatus");
   }

   public Integer getChannelStatus(int channel) {
      return (Integer)this.optChannelObject("Channel", channel, "Status", "IPCam", "ChannelStatus");
   }

   public Integer getChannelPIRStatus(int channel) {
      return (Integer)this.optChannelObject("Channel", channel, "PIRStatus", "IPCam", "ChannelStatus");
   }

   public Boolean isChannelOnCharging(int channel) {
      return (Boolean)this.optChannelObject("Channel", channel, "PowerCablePlugIn", "IPCam", "ChannelStatus");
   }

   public Boolean isChannelVersionLastest(int channel) {
      return (Boolean)this.optChannelObject("Channel", channel, "IsLastest", "IPCam", "ChannelStatus");
   }

   public Boolean supportPTZ() {
      Boolean ptz = this.getPTZ();
      if (ptz != null && ptz) {
         return true;
      } else {
         Boolean ptzh = this.getPTZH();
         if (ptzh != null && ptzh) {
            return true;
         } else {
            Boolean ptzv = this.getPTZV();
            if (ptzv != null && ptzv) {
               return true;
            } else {
               return ptz == null && ptzh == null && ptzv == null ? null : false;
            }
         }
      }
   }

   private Boolean getPTZ() {
      return (Boolean)this.optObject("CapabilitySet", "ptz");
   }

   private Boolean getPTZH() {
      return (Boolean)this.optObject("CapabilitySet", "ptz_h");
   }

   private Boolean getPTZV() {
      return (Boolean)this.optObject("CapabilitySet", "ptz_v");
   }

   public int getPTZCapability() {
      Boolean ptz = this.getPTZ();
      Boolean ptzh = this.getPTZH();
      Boolean ptzv = this.getPTZV();
      if (ptz == null) {
         ptz = false;
      }

      if (ptzh == null) {
         ptzh = false;
      }

      if (ptzv == null) {
         ptzv = false;
      }

      if (ptz && !ptzh && !ptzv || ptz && ptzh && ptzv) {
         return 3;
      } else if ((!ptz || !ptzh) && (ptz || !ptzh || ptzv)) {
         return ptzv ? 1 : 0;
      } else {
         return 2;
      }
   }

   public Integer getPTZSpeed() {
      return (Integer)this.optObject("IPCam", "ptzManager", "ptzCtrlSpeed");
   }

   public Boolean supportAF() {
      return (Boolean)this.optObject("CapabilitySet", "af");
   }

   public Boolean supportIrControl() {
      return (Boolean)this.optObject("CapabilitySet", "irControl");
   }

   public String getPTZCruiseMode(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "ptzManager", "ptzCruiseMode");
      if (fromSet && obj == null) {
         obj = (String)this.optObject("IPCam", "ptzManager", "ptzCruiseMode");
      }

      return obj != null && !this.isGreaterOrEqualVersion("03052800") ? null : obj;
   }

   public Boolean isLinkageEnable(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "V2", "LensCtrl", "LensLinkageCtrl", "LinkageEnabled");
      return fromSet && obj == null ? this.isLinkageEnable(false) : obj;
   }

   public Double getLinkageCurCoordinatesX(boolean fromSet) {
      Object xObject = this.optObject(fromSet, "IPCam", "V2", "R/LensLinkageCtrl", "CurCoordinates", "X");
      if (xObject instanceof Integer) {
         xObject = 1.0D * (double)(Integer)xObject;
      }

      return (Double)xObject;
   }

   public Double getLinkageCurCoordinatesY(boolean fromSet) {
      Object yObject = this.optObject(fromSet, "IPCam", "V2", "R/LensLinkageCtrl", "CurCoordinates", "Y");
      if (yObject instanceof Integer) {
         yObject = 1.0D * (double)(Integer)yObject;
      }

      return (Double)yObject;
   }

   public String getLedData(boolean fromSet) {
      JSONObject object = (JSONObject)this.optObject(fromSet, "IPCam", "ledPwm");
      return object != null ? object.toString() : null;
   }

   public boolean isLedEnabled() {
      Integer value = (Integer)this.optObject("IPCam", "ledPwm", "switch");
      return value != null && value == 1;
   }

   public String getLedProduct() {
      return (String)this.optObject("IPCam", "ledPwm", "product");
   }

   public Integer getLedProject() {
      return (Integer)this.optObject("IPCam", "ledPwm", "project");
   }

   public Integer getLedChannelCount() {
      Integer value = (Integer)this.optObject("IPCam", "ledPwm", "channelCount");
      return value != null ? value : 0;
   }

   public Integer getLedChannelType(int channel) {
      return (Integer)this.optChannelObject("channel", channel, "type", "IPCam", "ledPwm", "channelInfo");
   }

   public Integer getLedChannelValue(int channel) {
      return (Integer)this.optChannelObject("channel", channel, "num", "IPCam", "ledPwm", "channelInfo");
   }

   public String getWirelessSSID() {
      return (String)this.optObject("IPCam", "WirelessStation", "ssid");
   }

   public String getIpAddress() {
      return (String)this.optObject("IPCam", "DeviceInfo", "IP");
   }

   public String getMacAddress() {
      return (String)this.optObject("IPCam", "DeviceInfo", "MAC");
   }

   public Boolean getWirelessEnable() {
      return (Boolean)this.optObject("IPCam", "WirelessStation", "enable");
   }

   public String getWirelessState() {
      return (String)this.optObject("IPCam", "WirelessStation", "status");
   }

   public String getWirelessAPs() {
      JSONArray array = (JSONArray)this.optObject("IPCam", "WirelessStation", "APs");
      return array != null ? array.toString() : null;
   }

   public Boolean isCloudBound() {
      return (Boolean)this.optObject("IPCam", "OsscloudSetting", "IsBound");
   }

   public Boolean isChannelCloudEnabled(int channel) {
      return (Boolean)this.optChannelObject("ID", channel + 1, "Enabled", "IPCam", "OsscloudSetting", "Upload");
   }

   public int getCloudChannelCount() {
      JSONArray array = (JSONArray)this.optObject("IPCam", "OsscloudSetting", "Upload");
      return array != null ? array.length() : 0;
   }

   public Integer getChannelCloudType(int channel) {
      return (Integer)this.optChannelObject("ID", channel + 1, "Type", "IPCam", "OsscloudSetting", "Upload");
   }

   public String getWorkMode(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "WorkMode", "Mode");
      return fromSet && obj == null ? this.getWorkMode(false) : obj;
   }

   public List<String> getWorkModes() {
      JSONArray array = (JSONArray)this.optObject("IPCam", "WorkMode", "ModeProperty", "opt");
      if (array != null && array.length() > 0) {
         List<String> opts = new ArrayList();

         for(int i = 0; i < array.length(); ++i) {
            opts.add(array.optString(i));
         }

         return opts;
      } else {
         return null;
      }
   }

   public String getLTEOperator() {
      return (String)this.optObject("IPCam", "Lte", "OperatorsName");
   }

   public String getLTEPhone() {
      return (String)this.optObject("IPCam", "Lte", "PhoneNumber");
   }

   public String getLTEICCID() {
      return (String)this.optObject("IPCam", "Lte", "ICCID");
   }

   public String getLTESimType() {
      return (String)this.optObject("IPCam", "Lte", "SIMType");
   }

   public Integer getLTESignal() {
      return (Integer)this.optObject("IPCam", "Lte", "Signal");
   }

   public String getLteModuleIMEI() {
      return (String)this.optObject("IPCam", "LteModuleDetail", "IMEI");
   }

   public Integer getMaxChannel() {
      return (Integer)this.optObject("IPCam", "ChannelManager", "maxChannel");
   }

   public Integer getWirelessChannel(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "WirelessManager", "Channel");
      return fromSet && obj == null ? this.getWirelessChannel(false) : obj;
   }

   public Integer getWirelessSignal() {
      return (Integer)this.optObject("IPCam", "WirelessCheck", "Signal");
   }

   public Integer getWirelessThroughput() {
      return (Integer)this.optObject("IPCam", "WirelessCheck", "Throughput");
   }

   public String getUpgradeStatus() {
      return (String)this.optObject("IPCam", "SystemOperation", "UpgradeStatus", "Status");
   }

   public String getUpgradeErrDescription() {
      return (String)this.optObject("IPCam", "SystemOperation", "UpgradeStatus", "error");
   }

   public Integer getUpgradeIndex() {
      return (Integer)this.optObject("IPCam", "SystemOperation", "UpgradeStatus", "DeviceIndex");
   }

   public Integer getUpgradeProgress() {
      return (Integer)this.optObject("IPCam", "SystemOperation", "UpgradeStatus", "Progress");
   }

   public String getIPCam() {
      Object object = this.optObject("IPCam");
      return object != null ? object.toString() : null;
   }

   public String getCapabilitySet() {
      Object object = this.optObject("CapabilitySet");
      return object != null ? object.toString() : null;
   }

   public Boolean isLightAlarmEnable(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "LightAlarm", "Enabled");
      return fromSet && obj == null ? this.isLightAlarmEnable(false) : obj;
   }

   public Integer getLightAlarmDuration(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "LightAlarm", "DurationSec");
      return fromSet && obj == null ? this.getLightAlarmDuration(false) : obj;
   }

   public String getLightAlarmMode(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", "LightAlarm", "Mode");
      return fromSet && obj == null ? this.getLightAlarmMode(false) : obj;
   }

   public Boolean isWhiteAlarmLightV2Enable(boolean fromSet) {
      Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "V2", "Alarm", "AlarmWhiteLight", "Enabled");
      return fromSet && obj == null ? this.isWhiteAlarmLightV2Enable(false) : obj;
   }

   public Integer getWhiteAlarmLightV2Duration(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "V2", "Alarm", "AlarmWhiteLight", "DurSec");
      return fromSet && obj == null ? this.getWhiteAlarmLightV2Duration(false) : obj;
   }

   public String getWhiteAlarmLightV2Mode(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "V2", "Alarm", "AlarmWhiteLight", "Mode");
      return fromSet && obj == null ? this.getWhiteAlarmLightV2Mode(false) : obj;
   }

   public Boolean getLightManCtrl() {
      String status = (String)this.optObject("IPCam", "V2", "R/LightManCtrl", "Operate");
      return status != null ? "ON".equals(status) : null;
   }

   public Boolean getAlarmLightManCtrl() {
      String status = (String)this.optObject("IPCam", "V2", "R/AlarmLightManCtrl", "Operate");
      return status != null ? "ON".equals(status) : null;
   }

   public Boolean isAlarmRedBlueLightEnable(boolean fromSet) {
      Boolean status = (Boolean)this.optObject(fromSet, "IPCam", "V2", "Alarm", "AlarmLight", "Enabled");
      return fromSet && status == null ? this.isLightAlarmEnable(false) : status;
   }

   public Integer getRedBlueLightAlarmDuration(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "V2", "Alarm", "AlarmLight", "DurSec");
      return fromSet && obj == null ? this.getRedBlueLightAlarmDuration(false) : obj;
   }

   public void updateTalkMode(String mode) {
      if (this.mGettingOptionObj != null) {
         JSONObject ipcam = this.mGettingOptionObj.optJSONObject("IPCam");
         if (ipcam != null) {
            JSONObject modeSetting = ipcam.optJSONObject("ModeSetting");
            if (modeSetting != null) {
               try {
                  modeSetting.put("TalkMode", mode);
               } catch (JSONException var5) {
                  var5.printStackTrace();
               }
            }
         }
      }

   }

   public void updateTimezone(int timezone) {
      if (this.mGettingOptionObj != null) {
         JSONObject ipcam = this.mGettingOptionObj.optJSONObject("IPCam");
         if (ipcam != null) {
            JSONObject systemOperation = ipcam.optJSONObject("SystemOperation");
            if (systemOperation != null) {
               JSONObject timeSync = systemOperation.optJSONObject("TimeSync");
               if (timeSync != null) {
                  try {
                     timeSync.put("TimeZone", timezone);
                  } catch (JSONException var6) {
                     var6.printStackTrace();
                  }
               }
            }
         }
      }

   }

   public void updateOSDFormat(int formatInt) {
      if (formatInt > 2) {
         formatInt = 2;
      } else if (formatInt < 0) {
         formatInt = 0;
      }

      String formatStr;
      switch(formatInt) {
      case 1:
         formatStr = "MMDDYYYY";
         break;
      case 2:
         formatStr = "DDMMYYYY";
         break;
      default:
         formatStr = "YYYYMMDD";
      }

      if (this.mGettingOptionObj != null) {
         JSONObject ipcam = this.mGettingOptionObj.optJSONObject("IPCam");
         if (ipcam != null) {
            JSONObject systemOperation = ipcam.optJSONObject("SystemOperation");
            if (systemOperation != null) {
               try {
                  systemOperation.put("dateFormat", formatStr);
               } catch (JSONException var6) {
                  var6.printStackTrace();
               }
            }
         }
      }

   }

   public Options disableMatchExistsGettingObj() {
      this.mSkipExistsGettingObj = true;
      if (this.mIsGot && this.mGettingOptionObj != null) {
         this.mExistsGettingOptionObj = this.mGettingOptionObj;
         this.mGettingOptionObj = null;
         this.mIsGot = false;
      }

      return this;
   }

   public void restoreExistsGettingObj() {
      this.mSkipExistsGettingObj = false;
      if (this.mExistsGettingOptionObj != null) {
         this.mGettingOptionObj = this.mExistsGettingOptionObj;
         this.mExistsGettingOptionObj = null;
         this.mIsGot = true;
      }

   }

   public Boolean isSupportLightControl(int channel) {
      Integer lightControl = (Integer)this.optChannelObject("ID", channel, "lightControl", "ChnCapabilitySet");
      return lightControl != null && lightControl == 1;
   }

   public Integer getChannelManagerChannel() {
      String channel = (String)this.optObject(false, "IPCam", "ChannelManager", "ChannelList");
      return channel != null && TextUtils.isDigitsOnly(channel) ? Integer.parseInt(channel) : null;
   }

   public String getUsageScenario(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "ModeSetting", "usageScenario");
      return fromSet && obj == null ? this.getUsageScenario(false) : obj;
   }

   public String getTalkMode(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "ModeSetting", "TalkMode");
      return fromSet && obj == null ? this.getTalkMode(false) : obj;
   }

   public List<Integer> getTimerSchedule(boolean fromSet, int channel) {
      return this.getGWChannelRecordSchedule("Timer", fromSet, channel);
   }

   public List<Integer> getMotionSchedule(boolean fromSet, int channel) {
      return this.getGWChannelRecordSchedule("Motion", fromSet, channel);
   }

   public List<Integer> getAlarmSchedule(boolean fromSet, int channel) {
      return this.getGWChannelRecordSchedule("Alarm", fromSet, channel);
   }

   public String getWTType() {
      String wtType = (String)this.optObject(false, "IPCam", "AlarmSetting", "MotionDetection", "WTType");
      if (wtType == null) {
         wtType = "PCM";
      }

      return wtType;
   }

   public Integer getWTSample() {
      Integer wtSample = (Integer)this.optObject(false, "IPCam", "AlarmSetting", "MotionDetection", "WTSample");
      if (wtSample == null) {
         wtSample = 8000;
      }

      return wtSample;
   }

   public List<Integer> getWTSch(boolean fromSet, int index) {
      String searchKey = null;
      switch(index) {
      case 0:
         searchKey = "WTSch";
         break;
      case 1:
         searchKey = "WTSch2nd";
         break;
      case 2:
         searchKey = "WTSch3rd";
      }

      if (searchKey == null) {
         return null;
      } else {
         JSONArray array = (JSONArray)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", searchKey);
         if (fromSet && array == null) {
            return this.getWTSch(false, index);
         } else if (array == null) {
            return null;
         } else {
            List<Integer> wtSch = new ArrayList();

            for(int i = 0; i < array.length(); ++i) {
               wtSch.add(array.optInt(i));
            }

            return wtSch;
         }
      }
   }

   public Boolean isWTSchEnable(boolean fromSet, int index) {
      String searchKey = null;
      switch(index) {
      case 0:
         searchKey = "MotionWarningTone";
         break;
      case 1:
         searchKey = "WT2ndEnabled";
         break;
      case 2:
         searchKey = "WT3rdEnabled";
      }

      if (searchKey == null) {
         return null;
      } else {
         Boolean obj = (Boolean)this.optObject(fromSet, "IPCam", "AlarmSetting", "MotionDetection", searchKey);
         return fromSet && obj == null ? this.isWTSchEnable(false, index) : obj;
      }
   }

   public String getNetworkStatus(boolean fromSet) {
      String obj = (String)this.optObject(fromSet, "IPCam", "NetworkMode", "Status");
      return fromSet && obj == null ? this.getNetworkStatus(false) : obj;
   }

   private List<Integer> getGWChannelRecordSchedule(String recordKey, boolean fromSet, int channel) {
      JSONArray array = (JSONArray)this.optObject(fromSet, "IPCam", "Record", "BitSchedule");
      if (fromSet && array == null) {
         return this.getGWChannelRecordSchedule(recordKey, false, channel);
      } else {
         if (array != null) {
            for(int i = 0; i < array.length(); ++i) {
               JSONObject jsonObject = array.optJSONObject(i);
               if (channel == jsonObject.optInt("Channel")) {
                  List<Integer> schedule = new ArrayList();
                  JSONArray timer = jsonObject.optJSONArray(recordKey);

                  for(int j = 0; j < timer.length(); ++j) {
                     schedule.add(timer.optInt(j));
                  }

                  return schedule;
               }
            }
         }

         return null;
      }
   }

   protected void cacheGetArray(JSONArray array, String key) {
      synchronized(this.mLock) {
         if (this.mPreviousGetArray == null) {
            this.mPreviousGetArray = new JSONArrayInfo();
         }

         this.mPreviousGetArray.array = array;
         this.mPreviousGetArray.keyName = key;
      }
   }

   protected void clearCacheGetArray() {
      synchronized(this.mLock) {
         this.mPreviousGetArray = null;
      }
   }

   public Boolean getGuardEnable() {
      return (Boolean)this.optObject("IPCam", "ptzManager", "guardPos", "Enable");
   }

   public Integer getGuardIndex() {
      return (Integer)this.optObject("IPCam", "ptzManager", "guardPos", "Index");
   }

   public Integer getGuardStay() {
      return (Integer)this.optObject("IPCam", "ptzManager", "guardPos", "Stay");
   }

   public Integer[] getGuardSchedule() {
      JSONArray array = (JSONArray)this.optObject("IPCam", "ptzManager", "guardPos", "Sch");
      Integer[] sch = new Integer[]{0, 0, 0, 0, 0, 0, 0};
      if (array != null) {
         for(int i = 0; i < array.length(); ++i) {
            try {
               sch[i] = array.getInt(i);
            } catch (JSONException var5) {
               var5.printStackTrace();
            }
         }
      }

      return sch;
   }

   private void updateCachePirStatus(JSONObject object, boolean enable, int channel) {
      if (object != null) {
         JSONObject ipcam = object.optJSONObject("IPCam");
         if (ipcam != null) {
            JSONObject alarmSetting = ipcam.optJSONObject("AlarmSetting");
            if (alarmSetting != null) {
               JSONObject pirSetting = alarmSetting.optJSONObject("PIRSetting");
               if (pirSetting != null) {
                  try {
                     pirSetting.put("Enabled", enable);
                  } catch (JSONException var11) {
                     var11.printStackTrace();
                  }
               }
            }

            JSONArray channelStatusArr = ipcam.optJSONArray("ChannelStatus");
            if (channelStatusArr != null && channelStatusArr.length() > channel && channel >= 0) {
               try {
                  JSONObject channelStatus = channelStatusArr.getJSONObject(channel);
                  if (channelStatus != null) {
                     channelStatus.put("PIRStatus", enable ? 2 : 1);
                  }
               } catch (JSONException var10) {
                  var10.printStackTrace();
               }
            }

            if (this.mPreviousGetArray != null && "ChannelStatus".equals(this.mPreviousGetArray.keyName)) {
               JSONArray array = this.mPreviousGetArray.array;
               if (array != null && array.length() > channel && channel >= 0) {
                  try {
                     JSONObject channelStatus = array.getJSONObject(channel);
                     if (channelStatus != null) {
                        channelStatus.put("PIRStatus", enable ? 2 : 1);
                     }
                  } catch (JSONException var9) {
                     var9.printStackTrace();
                  }
               }
            }
         }
      }

   }

   private String getFwVersion() {
      String fwVersion = this.getChannelFWVersion(-1);
      if (fwVersion != null) {
         String[] vals = fwVersion.split("\\.");

         try {
            StringBuilder stringBuilder = new StringBuilder();
            String[] var4 = vals;
            int var5 = vals.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String val = var4[var6];
               String hexString = Integer.toHexString(Integer.parseInt(val));
               if (hexString.length() == 1) {
                  stringBuilder.append("0").append(hexString);
               } else {
                  stringBuilder.append(hexString);
               }
            }

            fwVersion = stringBuilder.toString();
         } catch (NumberFormatException var9) {
            var9.printStackTrace();
            fwVersion = "";
         }
      }

      return fwVersion;
   }

   private boolean isGreaterOrEqualVersion(String targetVersion) {
      if (!TextUtils.isEmpty(targetVersion)) {
         String currentVersion = this.getFwVersion();
         if (!TextUtils.isEmpty(currentVersion)) {
            if (currentVersion.length() % 2 != 0) {
               currentVersion = "0" + currentVersion;
            }

            if (targetVersion.length() % 2 != 0) {
               targetVersion = "0" + targetVersion;
            }

            currentVersion = currentVersion.toLowerCase();
            targetVersion = targetVersion.toLowerCase();
            int count;
            StringBuilder targetVersionBuilder;
            int i;
            if (targetVersion.length() > currentVersion.length()) {
               count = targetVersion.length() - currentVersion.length();
               targetVersionBuilder = new StringBuilder(currentVersion);

               for(i = 0; i < count; ++i) {
                  targetVersionBuilder.append("0");
               }

               currentVersion = targetVersionBuilder.toString();
            } else if (targetVersion.length() < currentVersion.length()) {
               count = currentVersion.length() - targetVersion.length();
               targetVersionBuilder = new StringBuilder(targetVersion);

               for(i = 0; i < count; ++i) {
                  targetVersionBuilder.append("0");
               }

               targetVersion = targetVersionBuilder.toString();
            }

            return currentVersion.compareTo(targetVersion) >= 0;
         }
      }

      return false;
   }

   public boolean supportAddChannel() {
      Boolean support = (Boolean)this.optObject("CapabilitySet", "addChannel");
      return support != null && support;
   }

   public String getAddChannelStatus() {
      String status = (String)this.optObject("IPCam", "ChannelManager", "addStatus");
      if (status == null) {
         status = "";
      }

      return status;
   }

   public Integer getAddChannelLeftTimeout() {
      Integer timeout = (Integer)this.optObject("IPCam", "ChannelManager", "addLeftTimeout");
      if (timeout == null) {
         timeout = 0;
      }

      return timeout;
   }

   public List<String> getAddChannelErrorCode() {
      List<String> errorArr = new ArrayList();
      JSONArray error = (JSONArray)this.optObject("IPCam", "ChannelManager", "addErrorCode");
      if (error != null) {
         for(int i = 0; i < error.length(); ++i) {
            String errStr = error.optString(i);
            if (!TextUtils.isEmpty(errStr) && !errorArr.contains(errStr)) {
               errorArr.add(errStr);
            }
         }
      }

      return errorArr;
   }

   public void clearAllOptions() {
      if (this.mIsGot) {
         this.mIsGot = false;
         this.mGettingOptionObj = null;
         this.mPreviousGetArray = null;
         this.mPreviousGetObj = null;
      }

      if (this.mSkipExistsGettingObj) {
         this.mSkipExistsGettingObj = false;
         this.mExistsGettingOptionObj = null;
      }

   }

   public Integer getBatterDevMaxPowerLimit() {
      return (Integer)this.optObject("IPCam", "WorkMode", "CapHighLevel");
   }

   public Integer getBatteryDevMinPowerLimit() {
      return (Integer)this.optObject("IPCam", "WorkMode", "CapLowLevel");
   }

   public String getVersion() {
      return (String)this.optObject("Version");
   }

   public boolean isSupportChannelV2() {
      String version = this.getVersion();
      return "1.2.1".compareTo(version != null ? version : "") <= 0;
   }

   public boolean isChannelV2Got() {
      return this.optObject("ChnCapabilitySetV2Rsp") != null;
   }

   public boolean isChannelSupportPtzV2(int channel) {
      return Objects.equals(this.getChannelPtzV2(channel), true) || Objects.equals(this.getChannelPtzHorizontalV2(channel), true) || Objects.equals(this.getChannelPtzVerticalV2(channel), true);
   }

   @Nullable
   public Boolean getChannelPtzV2(int channel) {
      if (channel != 0 && channel != -1) {
         return (Boolean)this.optChannelObject("ID", channel, "ptz", "ChnCapabilitySetV2Rsp");
      } else {
         return this.getPTZ() != null ? this.getPTZ() : (Boolean)this.optChannelObject("ID", channel, "ptz", "ChnCapabilitySetV2Rsp");
      }
   }

   public Boolean getChannelPtzHorizontalV2(int channel) {
      if (channel != 0 && channel != -1) {
         return (Boolean)this.optChannelObject("ID", channel, "ptz_h", "ChnCapabilitySetV2Rsp");
      } else {
         return this.getPTZH() != null ? this.getPTZH() : (Boolean)this.optChannelObject("ID", channel, "ptz_h", "ChnCapabilitySetV2Rsp");
      }
   }

   public Boolean getChannelPtzVerticalV2(int channel) {
      if (channel != 0 && channel != -1) {
         return (Boolean)this.optChannelObject("ID", channel, "ptz_v", "ChnCapabilitySetV2Rsp");
      } else {
         return this.getPTZV() != null ? this.getPTZV() : (Boolean)this.optChannelObject("ID", channel, "ptz_v", "ChnCapabilitySetV2Rsp");
      }
   }

   public Boolean isChannelSupportAfV2(int channel) {
      if (channel != 0 && channel != -1) {
         return (Boolean)this.optChannelObject("ID", channel, "af", "ChnCapabilitySetV2Rsp");
      } else {
         return this.supportAF() != null ? this.supportAF() : (Boolean)this.optChannelObject("ID", channel, "af", "ChnCapabilitySetV2Rsp");
      }
   }

   public Boolean isChannelSupportIrControlV2(int channel) {
      if (channel == -1) {
         return this.supportIrControl();
      } else {
         return this.isSupportChannelV2() ? (Boolean)this.optChannelObject("ID", channel, "irControl", "ChnCapabilitySetV2Rsp") : this.supportIrControl();
      }
   }

   public boolean isChannelSupportPowerBatteryV2(int channel) {
      Boolean powerBattery = (Boolean)this.optChannelObject("ID", channel, "powerBattery", "ChnCapabilitySetV2Rsp");
      return Objects.equals(powerBattery, true);
   }

   public boolean isChannelSupportLightControlV2(int channel) {
      Integer lightControlV2 = this.getChannelLightControlV2(channel);
      return lightControlV2 != null && lightControlV2 == 1;
   }

   @Nullable
   public Integer getChannelLightControlV2(int channel) {
      if (channel == -1) {
         return this.getLightControl();
      } else {
         return this.isSupportChannelV2() ? (Integer)this.optChannelObject("ID", channel, "lightControl", "ChnCapabilitySetV2Rsp") : (Integer)this.optChannelObject("ID", channel, "lightControl", "ChnCapabilitySet");
      }
   }

   @Nullable
   public Integer getChannelSpFisheyeV2(int channel) {
      return (Integer)this.optChannelObject("ID", channel, "spFisheye", "ChnCapabilitySetV2Rsp");
   }

   public Boolean getAlarmEnableV2() {
      return (Boolean)this.optObject("IPCam", "V2", "Alarm", "Enabled");
   }

   public Boolean getAlarmPushEnableV2() {
      return (Boolean)this.optObject("IPCam", "V2", "Alarm", "MsgToApp", "Enabled");
   }

   public void clearIRCutModes() {
      this.clearIRCutModes(this.mGettingOptionObj);
      this.clearIRCutModes(this.mExistsGettingOptionObj);
   }

   private void clearIRCutModes(JSONObject object) {
      if (object != null) {
         JSONObject ipcam = object.optJSONObject("IPCam");
         if (ipcam != null) {
            JSONObject optionMode = ipcam.optJSONObject("ModeSetting");
            if (optionMode != null) {
               try {
                  optionMode.put("IRCutFilterModeProperty", (Object)null);
               } catch (JSONException var5) {
                  var5.printStackTrace();
               }
            }
         }
      }

   }

   public List<String> getAlarmPushEventV2() {
      JSONArray jsonArray = (JSONArray)this.optObject("IPCam", "V2", "Alarm", "MsgToApp", "Event");
      if (jsonArray == null) {
         return null;
      } else {
         List<String> list = new ArrayList();
         int i = 0;

         for(int len = jsonArray.length(); i < len; ++i) {
            list.add(jsonArray.optString(i));
         }

         return list;
      }
   }

   public List<String> getAlarmPushEventOptV2() {
      JSONArray jsonArray = (JSONArray)this.optObject("IPCam", "V2", "Alarm", "MsgToApp", "EventProperty", "opt");
      if (jsonArray == null) {
         return null;
      } else {
         List<String> list = new ArrayList();
         int i = 0;

         for(int len = jsonArray.length(); i < len; ++i) {
            list.add(jsonArray.optString(i));
         }

         return list;
      }
   }

   public Integer getAlarmPushIntervalV2() {
      return (Integer)this.optObject("IPCam", "V2", "Alarm", "MsgToApp", "Interval");
   }

   public String getAlarmPushScheduleV2() {
      JSONArray jsonArray = (JSONArray)this.optObject("IPCam", "V2", "Alarm", "MsgToApp", "Sch");
      return jsonArray == null ? null : jsonArray.toString();
   }

   public Boolean getLightEnableV2() {
      String status = (String)this.optObject("IPCam", "V2", "Stat", "Light");
      return status != null ? "ON".equals(status) : null;
   }

   public Boolean getAlarmLightEnableV2() {
      String status = (String)this.optObject("IPCam", "V2", "Stat", "AlarmLight");
      return status != null ? "ON".equals(status) : null;
   }

   public Integer getAlarmVolume(boolean fromSet) {
      Integer obj = (Integer)this.optObject(fromSet, "IPCam", "ModeSetting", "AudioVolume", "AlarmVolume");
      return fromSet && obj == null ? this.getAlarmVolume(false) : obj;
   }

   public boolean isSupportSoundEnableV2(boolean fromSet) {
      String status = (String)this.optObject(fromSet, "IPCam", "V2", "Stat", "Sound");
      return status != null;
   }

   public Boolean getOTA() {
      return (Boolean)this.optObject("CapabilitySet", "ota");
   }

   public String getRecordStreamV2(boolean fromSet) {
      return (String)this.optObject(fromSet, "IPCam", "V2", "Record", "Stream");
   }
}
