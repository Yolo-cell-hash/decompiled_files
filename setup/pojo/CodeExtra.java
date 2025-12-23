package com.eseeiot.setup.pojo;

import android.text.TextUtils;
import com.eseeiot.basemodule.util.DeviceTool;
import java.io.Serializable;

public class CodeExtra implements Serializable {
   private static final String TAG = "CodeExtra";
   private static final long serialVersionUID = 8403409805539018055L;
   private static final String CRC_STR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
   public static final int DEVICE_TYPE_UNDEFINED = 48;
   public static final int DEVICE_TYPE_IPCAM = 49;
   public static final int DEVICE_TYPE_DVR = 50;
   public static final int DEVICE_TYPE_NVR = 51;
   public static final int DEVICE_TYPE_LAMP = 52;
   public static final int DEVICE_TYPE_FLOORLIGHT = 53;
   public static final int DEVICE_TYPE_DOORBELL = 54;
   public static final int DEVICE_TYPE_GATEWAY = 55;
   public static final int DEVICE_TYPE_TOUCHNVR = 56;
   public static final int DEVICE_TYPE_BATTERY = 57;
   public static final int DEVICE_TYPE_BATTERY_DOORBELL = 65;
   public static final int DEVICE_TYPE_QIANGUI_DOORBELL = 66;
   public static final int DEVICE_TYPE_LENSLINKAGE = 67;
   public static final int DEVICE_TYPE_BINOCULAR = 68;
   public static final int DEVICE_ABILITY_UNKNOWN = 0;
   public static final int DEVICE_ABILITY_4G = 1;
   public static final int DEVICE_ABILITY_STA_WIFI = 2;
   public static final int DEVICE_ABILITY_SONIC_PAIRING = 4;
   public static final int DEVICE_ABILITY_QR_PAIRING = 8;
   public static final int DEVICE_ABILITY_FISH_EYE = 16;
   public static final int DEVICE_ABILITY_LINK_VISUAL = 32;
   public static final int DEVICE_ABILITY_ONE_NET = 64;
   public static final int DEVICE_ABILITY_NVR_MATCH = 128;
   public static final int DEVICE_ABILITY_LIMIT_MATCH = 256;
   public static final int DEVICE_ABILITY_BLE_MATCH = 512;
   public static final int DEVICE_ABILITY_MULTINET = 1024;
   public static final int DEVICE_ABILITY_5G_WIFI_MATCH = 2048;
   public static final int DEVICE_ABILITY_CLOUD_SIM_MATCH = 4096;
   public static final int DEVICE_ABILITY_APN_QR_PAIR_MATCH = 8192;
   public static final int DEVICE_ABILITY_BLUETOOTH_MATCH = 16384;
   public static final int DEVICE_ABILITY_BLUETOOTH_AND_AP = 32768;
   private static final int DEVICE_RESERVE_DEFAULT = 0;
   private String mEseeId = "";
   private String mOriginVerifyStr = "";
   private int mDeviceType = 48;
   private int mAbilitySet = 0;
   private int mReserveData = 0;

   public CodeExtra(String extraId) {
      if (!TextUtils.isEmpty(extraId) && (DeviceTool.isExtraEseeId(extraId) || DeviceTool.isBleExtraEseeId(extraId))) {
         if (!extraId.contains("_")) {
            return;
         }

         this.mOriginVerifyStr = extraId;

         try {
            int crcVerify = extraId.charAt(1);
            int verify = this.calculateVerify(extraId.substring(2));
            if (DeviceTool.isBleExtraEseeId(extraId)) {
               if (extraId.startsWith("GW")) {
                  crcVerify = extraId.charAt(3);
                  verify = this.calculateVerify(extraId.substring(4));
               } else {
                  crcVerify = extraId.charAt(4);
                  verify = this.calculateVerify(extraId.substring(5));
               }
            }

            if (crcVerify == verify) {
               String[] messages = extraId.split("_");
               if (messages.length <= 1) {
                  return;
               }

               String id = messages[1];
               int separateIndex;
               String reserveData;
               String abilitySetStr;
               if (DeviceTool.isEseeId(id)) {
                  this.mEseeId = id;
                  separateIndex = extraId.indexOf("_");
                  this.mDeviceType = extraId.charAt(separateIndex - 1);
                  if (extraId.startsWith("GW")) {
                     reserveData = extraId.substring(4, 6);
                     abilitySetStr = extraId.substring(6, 10);
                  } else {
                     reserveData = extraId.substring(2, 4);
                     abilitySetStr = extraId.substring(4, 8);
                  }

                  this.mAbilitySet = Integer.parseInt(abilitySetStr, 16);
                  this.mReserveData = Integer.parseInt(reserveData, 16);
               } else if (DeviceTool.isBleEseeId(id)) {
                  this.mEseeId = "******" + id;
                  separateIndex = extraId.indexOf("_");
                  this.mDeviceType = extraId.charAt(separateIndex - 1);
                  if (extraId.startsWith("GW")) {
                     reserveData = extraId.substring(4, 6);
                     abilitySetStr = extraId.substring(6, 10);
                  } else {
                     reserveData = extraId.substring(5, 7);
                     abilitySetStr = extraId.substring(7, 11);
                  }

                  this.mAbilitySet = Integer.parseInt(abilitySetStr, 16);
                  this.mReserveData = Integer.parseInt(reserveData, 16);
               }
            }
         } catch (Exception var9) {
            var9.printStackTrace();
         }
      }

   }

   private int calculateVerify(String code) {
      if (TextUtils.isEmpty(code)) {
         return -1;
      } else {
         int sum = 0;

         int index;
         for(index = 0; index < code.length(); ++index) {
            sum += code.charAt(index);
         }

         index = sum % "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length();
         return "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(index);
      }
   }

   public String getEseeId() {
      return this.mEseeId;
   }

   public int getDeviceType() {
      return this.mDeviceType;
   }

   public boolean hasAbility4G() {
      return (this.mAbilitySet & 1) == 1;
   }

   public boolean hasAbilityStaWifi() {
      return (this.mAbilitySet & 2) == 2;
   }

   public boolean hasAbilitySonicePairing() {
      return (this.mAbilitySet & 4) == 4;
   }

   public boolean hasAbilityQrPairing() {
      return (this.mAbilitySet & 8) == 8;
   }

   public boolean hasAbilityFisheye() {
      return (this.mAbilitySet & 16) == 16;
   }

   public boolean hasAbilityLinkvisual() {
      return (this.mAbilitySet & 32) == 32;
   }

   public boolean hasAbilityOnenet() {
      return (this.mAbilitySet & 64) == 64;
   }

   public boolean hasAbilityNVRMatch() {
      return (this.mAbilitySet & 128) == 128;
   }

   public boolean hasAbilityLimitMatch() {
      return (this.mAbilitySet & 256) == 256;
   }

   public boolean hasAbilityBleMatch() {
      return (this.mAbilitySet & 512) == 512;
   }

   public boolean hasAbilityMultiNet() {
      return (this.mAbilitySet & 1024) == 1024;
   }

   public boolean hasAbility5GWiFiMatch() {
      return (this.mAbilitySet & 2048) == 2048;
   }

   public boolean hasAbilityCloudSimMatch() {
      return (this.mAbilitySet & 4096) == 4096;
   }

   public boolean hasAbilityAPNQRPairMatch() {
      return (this.mAbilitySet & 8192) == 8192;
   }

   public boolean hasAbilityBlueToothMatch() {
      return (this.mAbilitySet & 16384) == 16384 || this.hasAbilityBlueToothAndAp();
   }

   public boolean hasAbilityBlueToothAndAp() {
      return (this.mAbilitySet & '耀') == 32768;
   }

   public boolean hasReserveData() {
      return this.mReserveData > 0;
   }

   public boolean hasAbilityThird() {
      return this.hasAbilityLinkvisual() || this.hasAbilityOnenet();
   }

   public String getOriginVerifyStr() {
      return this.mOriginVerifyStr;
   }

   public boolean isBleExtraCode() {
      return this.mEseeId.startsWith("******");
   }

   public boolean isBLEGateWayExtraCode() {
      return this.mOriginVerifyStr.startsWith("GW") && this.getDeviceType() == 55 && !this.mEseeId.contains("*");
   }

   public String toString() {
      return "CodeExtra{mEseeId='" + this.mEseeId + '\'' + ", mOriginVerifyStr='" + this.mOriginVerifyStr + '\'' + ", mDeviceType=" + this.mDeviceType + ", mAbilitySet=" + this.mAbilitySet + ", mReserveData=" + this.mReserveData + '}';
   }
}
