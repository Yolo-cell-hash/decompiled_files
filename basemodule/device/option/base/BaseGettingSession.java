package com.eseeiot.basemodule.device.option.base;

import android.util.Log;
import com.eseeiot.basemodule.device.option.GetOptionSession;
import com.eseeiot.basemodule.device.option.OptionSessionCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseGettingSession extends BaseOptionSession implements GetOptionSession {
   private static final String TAG = "BaseGettingV21";
   private final boolean[] appendStatus = new boolean[OptionField.values().length];
   private int appendChannel = -1;
   private boolean testChannel = false;
   private boolean addChannel = false;
   private boolean abortAddChannel = false;
   private boolean appendSchedule = false;
   private boolean appendUpgradeStatus = false;
   private int mBeginTimestamp;
   private int mEndTimestamp;
   private boolean isGateway = false;
   private boolean mWirelessStationWithoutAps;
   private boolean isNvrV2ProductWithOdmPtz = false;
   private int appendNvrPtzManager = -1;
   private boolean appendNvrV2LensCtrl = false;
   private int[] reqChannels;

   protected BaseGettingSession(CommonOption option) {
      super(option);
      this.get = true;
      this.mAutoConnect = false;
   }

   public void close() {
      this.closeSession();
   }

   public int commit() {
      this.mOption.mPreviousGetObj = null;
      int ret = this.performCommit();
      if (ret == 0) {
         this.mOption.mGettingSessions.add(this);
      }

      return ret;
   }

   public GetOptionSession closeAfterFinish() {
      this.toBeClosed = true;
      return this;
   }

   public GetOptionSession holdSession() {
      this.toBeClosed = false;
      return this;
   }

   public GetOptionSession addListener(OptionSessionCallback callback) {
      this.callback = callback;
      return this;
   }

   public GetOptionSession useVerify() {
      this.useVerify = true;
      return this;
   }

   public GetOptionSession usePassword() {
      this.useVerify = false;
      return this;
   }

   public GetOptionSession setVersion(String version) {
      this.version = version;
      return this;
   }

   public GetOptionSession setTimeout(int timeoutInMillis) {
      this.timeout(timeoutInMillis);
      return this;
   }

   public GetOptionSession appendCapabilitySet() {
      this.appendStatus[OptionField.CAPABILITY_SET.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendChnCapabilitySet() {
      this.appendStatus[OptionField.CHN_CAPABILITY_SET.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendChnCapabilitySetV2Req(int... channel) {
      this.reqChannels = channel;
      this.appendStatus[OptionField.CHN_CAPABILITY_SET_V2_REQ.ordinal()] = true;
      this.appendStatus[OptionField.CHN_CAPABILITY_SET_V2_RSP.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendDeviceInfo() {
      this.appendStatus[OptionField.DEVICE_INFO.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendModeSetting() {
      this.appendStatus[OptionField.MODE.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendModeSettingWithIRCutMode() {
      this.mModeSettingWithIRCutMode = true;
      this.appendStatus[OptionField.MODE.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendAlarmSetting() {
      this.appendStatus[OptionField.ALARM.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendSystemOperation(boolean enableUpgradeStatus) {
      this.appendUpgradeStatus = enableUpgradeStatus;
      this.appendStatus[OptionField.SYSTEM_OPERATION.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendSystemOperation(boolean enableUpgradeStatus, boolean isGateway) {
      this.appendUpgradeStatus = enableUpgradeStatus;
      this.isGateway = isGateway;
      this.appendStatus[OptionField.SYSTEM_OPERATION.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendPromptSounds() {
      this.appendStatus[OptionField.PROMPT_SOUND.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendChannelManager(int... channel) {
      this.appendStatus[OptionField.CHANNEL_MANAGER.ordinal()] = true;
      if (channel.length > 0) {
         this.appendChannel = channel[0];
      }

      this.testChannel = false;
      return this;
   }

   public GetOptionSession testChannelManager(int channel) {
      this.appendStatus[OptionField.CHANNEL_MANAGER.ordinal()] = true;
      this.appendChannel = channel;
      this.testChannel = true;
      return this;
   }

   public GetOptionSession appendChannelInfo() {
      this.appendStatus[OptionField.CHANNEL_INFO.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendChannelStatus() {
      this.appendStatus[OptionField.CHANNEL_STATUS.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendLedPwm() {
      this.appendStatus[OptionField.LED.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendRecord() {
      this.appendStatus[OptionField.RECORD.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendTFCardManager(boolean enableSchedule) {
      this.appendStatus[OptionField.TFCARD.ordinal()] = true;
      this.appendSchedule = enableSchedule;
      return this;
   }

   public GetOptionSession appendRecordManager() {
      this.appendStatus[OptionField.RECORD_MANAGER.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendFisheyeSetting() {
      this.appendStatus[OptionField.FISHEYE.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendOSSCloudSetting() {
      this.appendStatus[OptionField.CLOUD.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendWirelessManager() {
      this.appendStatus[OptionField.WIRELESS_MANAGER.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendWirelessCheck() {
      this.appendStatus[OptionField.WIRELESS_CHECK.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendWirelessStation() {
      this.appendStatus[OptionField.WIRELESS_STATION.ordinal()] = true;
      this.mWirelessStationWithoutAps = false;
      return this;
   }

   public GetOptionSession appendWirelessStationWithoutAps() {
      this.appendStatus[OptionField.WIRELESS_STATION.ordinal()] = true;
      this.mWirelessStationWithoutAps = true;
      return this;
   }

   public GetOptionSession appendPtzManager() {
      this.appendStatus[OptionField.PTZ.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendVideoManager() {
      this.appendStatus[OptionField.VIDEO_MANAGER.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendCoverSetting() {
      this.appendStatus[OptionField.COVER.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendWorkMode() {
      this.appendStatus[OptionField.WORKMODE.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendLTE() {
      this.appendStatus[OptionField.LTE.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendLteModule() {
      this.appendStatus[OptionField.LTE_MODULE.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendAlexa() {
      this.appendStatus[OptionField.ALEXA.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendFeature() {
      this.appendStatus[OptionField.FEATURE.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendRecordInfo(int channel, int beginTimeS, int endTimeS) {
      this.appendChannel = channel;
      this.mBeginTimestamp = beginTimeS;
      this.mEndTimestamp = endTimeS;
      this.appendStatus[OptionField.RECORD_INFO.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendFrequencyMode() {
      this.appendStatus[OptionField.FREQUENCY_MODE.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendOSDTextSetting() {
      this.appendStatus[OptionField.OSD.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendNetworkMode() {
      this.appendStatus[OptionField.NETWORK_MODE.ordinal()] = true;
      return this;
   }

   public GetOptionSession addChannelOperation(int channel) {
      this.appendStatus[OptionField.CHANNEL_MANAGER.ordinal()] = true;
      this.appendChannel = channel;
      this.addChannel = true;
      return this;
   }

   public GetOptionSession abortAddChannelOperation() {
      this.appendStatus[OptionField.CHANNEL_MANAGER.ordinal()] = true;
      this.abortAddChannel = true;
      return this;
   }

   private void appendSplitIfNotEmpty(StringBuilder sb) {
      if (sb.length() > 1) {
         sb.append(',');
      }

   }

   protected void createSubJSON(JSONObject optionJSON) throws JSONException, IllegalArgumentException {
      StringBuilder sb = new StringBuilder();
      sb.append('{');
      if (this.appendStatus[OptionField.DEVICE_INFO.ordinal()]) {
         sb.append("\"").append(OptionField.DEVICE_INFO.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.MODE.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         if (this.mModeSettingWithIRCutMode) {
            sb.append("\"").append(OptionField.MODE.getFieldName()).append("\":{\"IRCutFilterMode\":\"\"}");
         } else {
            sb.append("\"").append(OptionField.MODE.getFieldName()).append("\":{\"").append("AudioVolume").append("\":{}}");
         }
      }

      if (this.appendStatus[OptionField.ALARM.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.ALARM.getFieldName()).append("\":{\"").append("MotionDetection").append("\":{},");
         if (this.appendStatus[OptionField.CHANNEL_MANAGER.ordinal()]) {
            sb.append("\"").append("PIRSetting").append("\":{}");
         } else {
            sb.append("\"MessagePushSchedule\":[]");
            this.appendSplitIfNotEmpty(sb);
            sb.append("\"MessagePushBitSchedule\":[]");
         }

         sb.append('}');
      }

      if (this.appendStatus[OptionField.SYSTEM_OPERATION.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.SYSTEM_OPERATION.getFieldName()).append("\":{\"");
         if (this.appendUpgradeStatus) {
            sb.append("UpgradeStatus\":{}");
         } else if (this.isGateway) {
            sb.append("TimeSync\":{},\"").append("DaylightSavingTime").append("\":{}");
         } else {
            sb.append("TimeSync\":{},\"").append("DaylightSavingTime").append("\":{\"Week\":[{},{}]}");
         }

         sb.append('}');
      }

      if (this.appendStatus[OptionField.PROMPT_SOUND.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.PROMPT_SOUND.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.RECORD_MANAGER.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.RECORD_MANAGER.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.CHANNEL_MANAGER.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.CHANNEL_MANAGER.getFieldName()).append("\":{");
         if (!this.abortAddChannel && this.appendChannel >= 0) {
            sb.append("\"ChannelList\":\"");
            sb.append(this.appendChannel);
            sb.append("\",\"Operation\":\"");
            sb.append(this.addChannel ? "Add" : (this.testChannel ? "Test" : "Get"));
            sb.append("Channel\"");
         }

         if (this.abortAddChannel) {
            sb.append("\"Operation\":\"");
            sb.append("AbortAddChannel");
            sb.append("\"");
         }

         sb.append('}');
      }

      if (this.appendStatus[OptionField.CHANNEL_INFO.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.CHANNEL_INFO.getFieldName()).append("\":[]");
      }

      if (this.appendStatus[OptionField.CHANNEL_STATUS.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.CHANNEL_STATUS.getFieldName()).append("\":[]");
      }

      if (this.appendStatus[OptionField.TFCARD.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.TFCARD.getFieldName()).append("\":{");
         if (this.appendSchedule) {
            sb.append("\"TFcard_recordSchedule\":[]");
         }

         sb.append('}');
      }

      if (this.appendStatus[OptionField.RECORD.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.RECORD.getFieldName()).append("\":{\"BitSchedule\":[]}");
      }

      if (this.appendStatus[OptionField.FISHEYE.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.FISHEYE.getFieldName()).append("\":{\"FixParam\":[]}");
      }

      if (this.appendStatus[OptionField.LED.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.LED.getFieldName()).append("\":{\"channelInfo\":[]}");
      }

      if (this.appendStatus[OptionField.CLOUD.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.CLOUD.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.WIRELESS_MANAGER.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.WIRELESS_MANAGER.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.WIRELESS_CHECK.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.WIRELESS_CHECK.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.WIRELESS_STATION.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.WIRELESS_STATION.getFieldName()).append("\":{");
         if (!this.mWirelessStationWithoutAps) {
            sb.append("\"APs\":[]");
         }

         sb.append("}");
      }

      if (this.appendStatus[OptionField.PTZ.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.PTZ.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.VIDEO_MANAGER.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.VIDEO_MANAGER.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.COVER.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.COVER.getFieldName()).append("\":[]");
      }

      if (this.appendStatus[OptionField.FEATURE.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.FEATURE.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.RECORD_INFO.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.RECORD_INFO.getFieldName()).append("\":{\"recordScheduleDateInfo\":[{\"chnNum\":");
         sb.append(this.appendChannel);
         sb.append(",\"beginTimeS\":");
         sb.append(this.mBeginTimestamp);
         sb.append(",\"endTimeS\":");
         sb.append(this.mEndTimestamp);
         sb.append(",\"recordDay\":[]}]}");
      }

      if (this.appendStatus[OptionField.LTE.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.LTE.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.LTE_MODULE.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.LTE_MODULE.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.ALEXA.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.ALEXA.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.WORKMODE.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.WORKMODE.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.FREQUENCY_MODE.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.FREQUENCY_MODE.getFieldName()).append("\":0");
      }

      if (this.appendStatus[OptionField.OSD.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.OSD.getFieldName()).append("\":{\"Title\":{}}");
      }

      if (this.appendStatus[OptionField.NETWORK_MODE.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append(OptionField.NETWORK_MODE.getFieldName()).append("\":{}");
      }

      if (this.appendStatus[OptionField.V2_STATUS.ordinal()] || this.appendStatus[OptionField.V2_R_LIGHT_MAN_CTRL.ordinal()] || this.appendStatus[OptionField.V2_R_SOUND_MAN_CTRL.ordinal()] || this.appendStatus[OptionField.V2_ALARM.ordinal()] || this.appendStatus[OptionField.V2_RECORD.ordinal()] || this.appendStatus[OptionField.V2_LENS_CTRL.ordinal()]) {
         this.appendSplitIfNotEmpty(sb);
         sb.append("\"").append("V2").append("\":{");
         boolean split = false;
         if (this.appendStatus[OptionField.V2_STATUS.ordinal()]) {
            sb.append("\"").append(OptionField.V2_STATUS.getFieldName()).append("\":{}");
            split = true;
         }

         if (this.appendStatus[OptionField.V2_R_LIGHT_MAN_CTRL.ordinal()]) {
            if (split) {
               this.appendSplitIfNotEmpty(sb);
            } else {
               split = true;
            }

            sb.append("\"").append(OptionField.V2_R_LIGHT_MAN_CTRL.getFieldName()).append("\":{}");
         }

         if (this.appendStatus[OptionField.V2_R_SOUND_MAN_CTRL.ordinal()]) {
            if (split) {
               this.appendSplitIfNotEmpty(sb);
            } else {
               split = true;
            }

            sb.append("\"").append(OptionField.V2_R_SOUND_MAN_CTRL.getFieldName()).append("\":{}");
         }

         if (this.appendStatus[OptionField.V2_ALARM.ordinal()]) {
            if (split) {
               this.appendSplitIfNotEmpty(sb);
            } else {
               split = true;
            }

            sb.append("\"").append(OptionField.V2_ALARM.getFieldName()).append("\":{}");
         }

         if (this.appendStatus[OptionField.V2_RECORD.ordinal()]) {
            if (split) {
               this.appendSplitIfNotEmpty(sb);
            }

            sb.append("\"").append(OptionField.V2_RECORD.getFieldName()).append("\":{}");
         }

         if (this.appendStatus[OptionField.V2_LENS_CTRL.ordinal()]) {
            if (split) {
               this.appendSplitIfNotEmpty(sb);
            } else {
               split = true;
            }

            sb.append("\"").append(OptionField.V2_LENS_CTRL.getFieldName());
            if (this.appendNvrV2LensCtrl) {
               sb.append("\":{").append("\"Magnification\":").append("{\"Max\":\"\"}").append(",").append("\"CurPosition\":").append("{\"Magnification\":\"\"}").append("}");
            } else {
               sb.append("\":{}");
            }
         }

         if (this.appendStatus[OptionField.V2_R_LINKAGE_MAN_CTRL.ordinal()]) {
            if (split) {
               this.appendSplitIfNotEmpty(sb);
            } else {
               split = true;
            }

            sb.append("\"").append(OptionField.V2_R_LINKAGE_MAN_CTRL.getFieldName()).append("\":{}");
         }

         sb.append("}");
      }

      if (sb.length() == 1 && !this.appendStatus[OptionField.CHN_CAPABILITY_SET.ordinal()] && !this.appendStatus[OptionField.CHN_CAPABILITY_SET_V2_REQ.ordinal()]) {
         throw new IllegalArgumentException("Without getting option!");
      } else {
         sb.append('}');
         JSONObject ipCamJSON = new JSONObject(sb.toString());
         optionJSON.put("IPCam", ipCamJSON);
         if (this.appendStatus[OptionField.CAPABILITY_SET.ordinal()]) {
            if (this.mOption.optObject("CapabilitySet") != null) {
               this.appendStatus[OptionField.CAPABILITY_SET.ordinal()] = false;
            } else {
               optionJSON.put("CapabilitySet", new JSONObject());
            }
         }

         if (this.appendStatus[OptionField.CHN_CAPABILITY_SET.ordinal()]) {
            optionJSON.put("ChnCapabilitySet", new JSONArray());
         }

         if (this.appendStatus[OptionField.CHN_CAPABILITY_SET_V2_REQ.ordinal()]) {
            JSONArray jsonArray = new JSONArray();
            int[] var5 = this.reqChannels;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               int channel = var5[var7];
               jsonArray.put(channel);
            }

            optionJSON.put("ChnCapabilitySetV2Req", jsonArray);
            optionJSON.put("ChnCapabilitySetV2Rsp", new JSONArray());
         }

      }
   }

   protected void handleResult(String result, JSONObject jsonResult) {
      String option = (String)jsonResult.opt("option");
      if (option != null) {
         if ("Authorization failed".equals(option)) {
            this.performVconResult(2, 0);
         }

      } else {
         JSONObject ipcam = jsonResult.optJSONObject("IPCam");
         if (this.mModeSettingWithIRCutMode && this.appendStatus[OptionField.CHANNEL_MANAGER.ordinal()] && ipcam != null && ipcam.optJSONObject("ChannelManager") == null) {
            try {
               ipcam.putOpt("ChannelManager", new JSONObject("{\"Operation\":\"GetChannel\",\"ChannelList\":\"" + this.appendChannel + "\"}"));
               result = jsonResult.toString();
            } catch (JSONException var7) {
               var7.printStackTrace();
            }
         }

         if (result.contains("\"powerLineFrequencyMode\"") && ipcam != null) {
            int powerLine = ipcam.optInt("powerLineFrequencyMode");
            if (powerLine <= 0) {
               ipcam.remove("powerLineFrequencyMode");
            }
         }

         if (this.isMatchSelfRequest(result)) {
            if (this.mOption.mGettingOptionObj == null) {
               this.mOption.mGettingOptionObj = jsonResult;
            } else {
               try {
                  OptionHelper.combineSubJson(this.mOption.mGettingOptionObj, jsonResult);
               } catch (JSONException var6) {
                  var6.printStackTrace();
               }
            }

            this.mOption.clearCacheGetArray();
            this.mOption.attemptSetObject((Object)null, "IPCam", "TfcardManager", "Operation");
            if (!this.mOption.mIsGot && this.mOption.optObject("IPCam", "DeviceInfo") != null) {
               this.mOption.mIsGot = true;
               this.mOption.mGotTimeInMillis = System.currentTimeMillis();
            }

            this.performVconResult(0, 0);
         }

      }
   }

   public GetOptionSession autoConnect(boolean enable) {
      this.mAutoConnect = enable;
      return this;
   }

   public GetOptionSession appendV2Status() {
      this.appendStatus[OptionField.V2_STATUS.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendV2RLightManCtr() {
      this.appendStatus[OptionField.V2_R_LIGHT_MAN_CTRL.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendV2RSoundManCtrl() {
      this.appendStatus[OptionField.V2_R_SOUND_MAN_CTRL.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendV2Alarm() {
      this.appendStatus[OptionField.V2_ALARM.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendV2Record() {
      this.appendStatus[OptionField.V2_RECORD.ordinal()] = true;
      return this;
   }

   public GetOptionSession appendV2LensCtrl(boolean isNvr) {
      this.appendStatus[OptionField.V2_LENS_CTRL.ordinal()] = true;
      this.appendNvrV2LensCtrl = isNvr;
      return this;
   }

   public GetOptionSession appendV2LensLinkage() {
      this.appendStatus[OptionField.V2_R_LINKAGE_MAN_CTRL.ordinal()] = true;
      return this;
   }

   private boolean isMatchSelfRequest(String result) {
      for(int i = 0; i < this.appendStatus.length; ++i) {
         if (this.appendStatus[i]) {
            OptionField field = OptionField.values()[i];
            if (!field.isMaybeNotReturn() && !result.contains(field.getFieldName())) {
               Log.w("BaseGettingV21", "isMatchSelfRequest: not contain " + field.getFieldName());
               return false;
            }
         }
      }

      return true;
   }
}
