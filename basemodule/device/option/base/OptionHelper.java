package com.eseeiot.basemodule.device.option.base;

import android.util.Log;
import androidx.collection.ArraySet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OptionHelper {
   private static final String TAG = "OptionHelperV21";
   public static final String METHOD_GET = "get";
   public static final String METHOD_SET = "set";
   public static final String AUTHORIZATION_FAILED = "Authorization failed";
   public static final String FAILED = "failed";
   public static final String FAIL = "fail";
   public static final String SUCCESS = "success";
   public static final String ENABLED = "Enabled";
   public static final String CHANNEL_UP = "Channel";
   public static final String CHANNEL_LOW = "channel";
   public static final String OPTION_IPCAM = "IPCam";
   public static final String OPTION_APP_SET = "AppSet";
   public static final String OPTION_CAPABILITY_SET = "CapabilitySet";
   public static final String OPTION_CHN_CAPABILITY_SET = "ChnCapabilitySet";
   public static final String OPTION_CHN_CAPABILITY_SET_V2_REQ = "ChnCapabilitySetV2Req";
   public static final String OPTION_CHN_CAPABILITY_SET_V2_RSP = "ChnCapabilitySetV2Rsp";
   public static final String OPTION_DEVICE_INFO = "DeviceInfo";
   public static final String OPTION_PROMPT_SOUND = "PromptSounds";
   public static final String OPTION_TFCARD = "TfcardManager";
   public static final String OPTION_VIDEO_MANAGER = "videoManager";
   public static final String OPTION_FISHEYE = "FisheyeSetting";
   public static final String OPTION_PTZ = "ptzManager";
   public static final String OPTION_ALEXA = "Alexa";
   public static final String OPTION_LED = "ledPwm";
   public static final String OPTION_CHANNEL_MANAGER = "ChannelManager";
   public static final String OPTION_CHANNEL_INFO = "ChannelInfo";
   public static final String OPTION_CHANNEL_STATUS = "ChannelStatus";
   public static final String OPTION_WIRELESS_MANAGER = "WirelessManager";
   public static final String OPTION_WIRELESS_STATION = "WirelessStation";
   public static final String OPTION_CLOUD = "OsscloudSetting";
   public static final String OPTION_COVER = "devCoverSetting";
   public static final String OPTION_FEATURE = "Feature";
   public static final String OPTION_LTE = "Lte";
   public static final String OPTION_LTE_MODULE = "LteModuleDetail";
   public static final String OPTION_RECORD_INFO = "recordInfo";
   public static final String OPTION_RECORD = "Record";
   public static final String OPTION_APP_BOUND = "AppBound";
   public static final String OPTION_SHUT_DOWN = "AutoShutdown";
   public static final String OPTION_V2 = "V2";
   public static final String OPTION_V2_STAT = "Stat";
   public static final String OPTION_V2_LIGHT_MANCTRL = "R/LightManCtrl";
   public static final String OPTION_V2_SOUND_MANCTRL = "R/SoundManCtrl";
   public static final String OPTION_V2_ALARM_LIGHT_MANCTRL = "R/AlarmLightManCtrl";
   public static final String OPTION_V2_LINKAGE_MANCTRL = "R/LensLinkageCtrl";
   public static final String OPTION_V2_LENS_CTRL = "LensCtrl";
   public static final String OPTION_SYSTEM_OPERATION = "SystemOperation";
   public static final String OPTION_SYSTEM_DST = "DaylightSavingTime";
   public static final String OPTION_MODE = "ModeSetting";
   public static final String OPTION_VOLUME = "AudioVolume";
   public static final String OPTION_CONVENIENT = "ConvenientSetting";
   public static final String OPTION_ALARM = "AlarmSetting";
   public static final String OPTION_MOTION = "MotionDetection";
   public static final String OPTION_PIR = "PIRSetting";
   public static final String OPTION_FREQUENCY_MODE = "powerLineFrequencyMode";
   public static final String OPTION_GUARD_POS = "guardPos";
   public static final Map<String, String> DEPEND_MAP = new HashMap();
   public static final Set<String> RO_SET = new ArraySet();

   protected static void combineSubJson(JSONObject srcObj, JSONObject newObj) throws JSONException {
      if (srcObj != null && newObj != null) {
         if (!srcObj.has("CapabilitySet") && newObj.has("CapabilitySet")) {
            srcObj.put("CapabilitySet", newObj.opt("CapabilitySet"));
         }

         if (!srcObj.has("ChnCapabilitySet") && newObj.has("ChnCapabilitySet")) {
            srcObj.put("ChnCapabilitySet", newObj.opt("ChnCapabilitySet"));
         }

         if (!srcObj.has("ChnCapabilitySetV2Rsp") && newObj.has("ChnCapabilitySetV2Rsp")) {
            srcObj.put("ChnCapabilitySetV2Rsp", newObj.opt("ChnCapabilitySetV2Rsp"));
         }

         JSONObject srcIPCamObject = (JSONObject)srcObj.get("IPCam");
         JSONObject newIPCamObject = (JSONObject)newObj.get("IPCam");
         combineSubJson(0, srcIPCamObject, newIPCamObject);
      }
   }

   private static void combineSubJson(int level, JSONObject srcObj, JSONObject newObj) throws JSONException {
      Iterator addKeys = newObj.keys();

      while(true) {
         while(addKeys.hasNext()) {
            String key = (String)addKeys.next();
            Object value = newObj.get(key);
            if (value instanceof JSONObject && srcObj.has(key)) {
               JSONObject srcSubObj = srcObj.getJSONObject(key);
               combineSubJson(level + 1, srcSubObj, (JSONObject)value);
            } else {
               srcObj.put(key, value);
            }
         }

         return;
      }
   }

   protected static boolean mergeJSONObject(String mergeKey, JSONObject srcObject, JSONObject dstObject, List<String> keyListOfDstObject) throws JSONException {
      if (compareAndMergeJSONObject(srcObject, dstObject)) {
         return true;
      } else {
         mergeDependedJSONObject(mergeKey, srcObject, dstObject, keyListOfDstObject);
         mergeDefaultJSONObject(mergeKey, srcObject, dstObject, keyListOfDstObject);
         return false;
      }
   }

   private static boolean compareAndMergeJSONObject(JSONObject srcObject, JSONObject dstObject) throws JSONException {
      boolean removeThisKeyFromDst = true;
      List<String> keyListOfDst = new ArrayList();
      Iterator dstKeys = dstObject.keys();

      String dstKey;
      while(dstKeys.hasNext()) {
         dstKey = (String)dstKeys.next();
         keyListOfDst.add(dstKey);
      }

      dstKeys = dstObject.keys();

      while(true) {
         while(dstKeys.hasNext()) {
            dstKey = (String)dstKeys.next();
            Object dstObj = dstObject.get(dstKey);
            Object srcObj = srcObject.opt(dstKey);
            if (dstObj instanceof JSONObject) {
               JSONObject object = (JSONObject)dstObj;
               if (object.length() == 0) {
                  Log.d("OptionHelperV21", "【merge】 [" + dstKey + "] is empty, remove it!");
                  dstKeys.remove();
                  keyListOfDst.remove(dstKey);
               } else if (srcObj != null) {
                  boolean removed = mergeJSONObject(dstKey, (JSONObject)srcObj, object, keyListOfDst);
                  if (removed) {
                     Log.d("OptionHelperV21", "【merge】 [" + dstKey + "] is same as getting result, remove it!");
                     dstKeys.remove();
                     keyListOfDst.remove(dstKey);
                  } else {
                     removeThisKeyFromDst = false;
                  }
               } else {
                  removeThisKeyFromDst = false;
               }
            } else if (dstObj instanceof JSONArray || !dstObj.equals(srcObj)) {
               removeThisKeyFromDst = false;
            }
         }

         return removeThisKeyFromDst;
      }
   }

   private static void mergeDependedJSONObject(String mergeKey, JSONObject srcObject, JSONObject dstObject, List<String> keyListOfDstObject) throws JSONException {
      if (keyListOfDstObject == null) {
         Iterator dstKeys = dstObject.keys();

         while(dstKeys.hasNext()) {
            String dstKey = (String)dstKeys.next();
            mergeDependedJSONObject(mergeKey, dstKey, srcObject, dstObject, (List)null);
         }
      } else {
         int keySize = keyListOfDstObject.size();

         for(int i = 0; i < keySize; ++i) {
            String dstKey = (String)keyListOfDstObject.get(i);
            mergeDependedJSONObject(mergeKey, dstKey, srcObject, dstObject, keyListOfDstObject);
         }
      }

   }

   private static void mergeDependedJSONObject(String mergeKey, String dstKey, JSONObject srcObject, JSONObject dstObject, List<String> keyListOfDstObject) throws JSONException {
      String dependKey = (String)DEPEND_MAP.get(mergeKey + ":" + dstKey);
      if (dependKey != null) {
         Object srcObj = srcObject.opt(dependKey);
         if (srcObj != null && !dstObject.has(dependKey)) {
            dstObject.put(dependKey, srcObj);
            if (keyListOfDstObject != null && !keyListOfDstObject.contains(dependKey)) {
               keyListOfDstObject.add(dependKey);
            }
         }

      }
   }

   private static void mergeDefaultJSONObject(String key, JSONObject srcObject, JSONObject dstObject, List<String> keyListOfDstObject) throws JSONException {
      if (!key.equals("WirelessStation") && !key.equals("TimeSync")) {
         Iterator srcKeys = srcObject.keys();

         while(true) {
            String srcKey;
            do {
               if (!srcKeys.hasNext()) {
                  return;
               }

               srcKey = (String)srcKeys.next();
            } while(RO_SET.contains(srcKey));

            boolean merge = false;
            Object srcObj = srcObject.get(srcKey);
            if (!(srcObj instanceof Boolean) && !(srcObj instanceof Integer) && !(srcObj instanceof String)) {
               if ("PIRSetting".equals(key) && "Schedule".equals(srcKey)) {
                  merge = true;
               }
            } else {
               merge = true;
            }

            if (merge && !dstObject.has(srcKey)) {
               dstObject.put(srcKey, srcObj);
               if (keyListOfDstObject != null && !keyListOfDstObject.contains(srcKey)) {
                  keyListOfDstObject.add(srcKey);
               }
            }
         }
      }
   }

   static {
      DEPEND_MAP.put("AlarmSetting:MessagePushEnabled", "MotionDetection");
      DEPEND_MAP.put("PIRSetting:Enabled", "Schedule");
      RO_SET.add("enableChannel");
      RO_SET.add("maxChannel");
      RO_SET.add("maxColumns");
      RO_SET.add("maxRows");
      RO_SET.add("width");
      RO_SET.add("height");
      RO_SET.add("TotalSpacesize");
      RO_SET.add("LeaveSpacesize");
      RO_SET.add("Status");
      RO_SET.add("ChNum");
      RO_SET.add("streamCount");
      RO_SET.add("deviceOnline");
      RO_SET.add("area");
      RO_SET.add("Reboot");
      RO_SET.add("ResetDefault");
   }
}
