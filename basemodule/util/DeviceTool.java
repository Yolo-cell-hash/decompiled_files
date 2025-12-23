package com.eseeiot.basemodule.util;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;
import androidx.core.app.ActivityCompat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceTool {
   public static boolean isEseeDevice(String deviceSeriesNum) {
      if (deviceSeriesNum != null && deviceSeriesNum.startsWith("IPC") && deviceSeriesNum.length() >= 14) {
         try {
            String tempStr = deviceSeriesNum.substring(deviceSeriesNum.length() - 10);
            char c = tempStr.charAt(0);
            if (c < '1' || c > '9') {
               tempStr = tempStr.substring(1);
            }

            return isEseeId(tempStr);
         } catch (Exception var3) {
            var3.printStackTrace();
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean isEseeId(String id) {
      return isCommonEseeId(id);
   }

   public static boolean isCommonEseeId(String id) {
      if (id == null) {
         return false;
      } else {
         Pattern p = Pattern.compile("^([1-9]\\d{8,9}$)");
         Matcher m = p.matcher(id);
         return m.matches();
      }
   }

   public static boolean isConformToAnEseeId(String id) {
      if (id == null) {
         return false;
      } else {
         Pattern p = Pattern.compile("^([0-9a-zA-Z]{6,20}$)");
         Matcher m = p.matcher(id);
         return m.matches();
      }
   }

   public static String getEseeIdFromSSID(String ssid) {
      String eseeId = null;
      if (TextUtils.isEmpty(ssid)) {
         return null;
      } else {
         if (ssid.length() >= 10) {
            eseeId = ssid.substring(ssid.length() - 10);
            char firstChar = eseeId.charAt(0);
            if (firstChar <= '0' || firstChar > '9') {
               eseeId = eseeId.substring(1);
            }

            Pattern p = Pattern.compile("^[0-9]*$");
            Matcher m = p.matcher(eseeId);
            boolean res = m.matches();
            if (!res) {
               return null;
            }
         }

         return eseeId;
      }
   }

   public static boolean isConnectOnIPC(Context context) {
      if (context != null) {
         if (VERSION.SDK_INT >= 29) {
            boolean permissionGrant = ActivityCompat.checkSelfPermission(context, "android.permission.ACCESS_COARSE_LOCATION") == 0;
            if (!NetworkUtil.isGpsEnable(context) || !permissionGrant) {
               String[] connectedInfo = NetworkUtil.getCurrentWifiConnectedInfo(context);
               return connectedInfo != null && "172.14.10.1".equals(connectedInfo[0]);
            }
         }

         Object[] objs = NetworkUtil.getCurrentConnectWifi(context);
         if (objs != null) {
            String SSID = (String)objs[0];
            return SSID.startsWith("IPC") && getEseeIdFromSSID(SSID) != null;
         }
      }

      return false;
   }

   public static boolean isExtraEseeId(String id) {
      if (!TextUtils.isEmpty(id)) {
         Pattern p = Pattern.compile("[E][0-9a-zA-Z][0-9A-F]{6}[1-9A-Z][_]");
         Matcher m = p.matcher(id);
         return m.find();
      } else {
         return false;
      }
   }

   public static boolean isBleExtraEseeId(String id) {
      if (TextUtils.isEmpty(id)) {
         return false;
      } else {
         return id.startsWith("IPC") && isExtraEseeId(id.substring(3)) || id.startsWith("GW") && isExtraEseeId(id.substring(2));
      }
   }

   public static boolean isBleEseeId(String id) {
      if (id == null) {
         return false;
      } else {
         Pattern p = Pattern.compile("^([0-9]{4}$)");
         Matcher m = p.matcher(id);
         return m.matches();
      }
   }
}
