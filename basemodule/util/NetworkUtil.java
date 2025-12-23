package com.eseeiot.basemodule.util;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class NetworkUtil {
   public static final int NETWORK_NO = -1;
   public static final int NETWORK_WIFI = 1;
   public static final int NETWORK_2G = 2;
   public static final int NETWORK_3G = 3;
   public static final int NETWORK_4G = 4;
   public static final int NETWORK_5G = 5;
   public static final int NETWORK_UNKNOWN = 6;
   public static final int WIFI_NOT_OPEN = 1;
   public static final int REMOVE_FAILED = 2;
   public static final int ENABLE_FAILED = 3;
   public static final int INVOKE_FAILED = 4;
   private static final String TAG = "NetworkUtil";
   private static final String MAC_ADDRESS = "02:00:00:00:00:00";
   private static final String FILE_MAC_ADDRESS = "/sys/class/net/wlan0/address";
   public static final String DEVICE_ROUTE_IP_GATEWAY = "172.14.10.1";
   private static final String HUAWEI_WIFI_PRO_SWITCHING = "smart_network_switching";
   private static final String MI_WIFI_ASSISTANT_SWITCHING = "data_and_wifi_roam";

   public static boolean isNetworkConnected(Context context) {
      ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
      Network[] networks = connectivityManager.getAllNetworks();
      Network[] var3 = networks;
      int var4 = networks.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Network network = var3[var5];
         NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
         if (networkInfo != null && networkInfo.isConnected()) {
            Log.d("NetworkUtil", "isNetworkConnected: networkInfo = " + networkInfo);
            if (!"ims".equals(networkInfo.getExtraInfo())) {
               return true;
            }
         }
      }

      return false;
   }

   public static int isWifiConnected(Context context, String SSID) {
      if (TextUtils.isEmpty(SSID)) {
         return 0;
      } else {
         if (VERSION.SDK_INT < 28) {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(1);
            if (networkInfo != null && networkInfo.isConnected() && networkInfo.getExtraInfo() != null && networkInfo.getExtraInfo().contains(SSID)) {
               return 1;
            }
         }

         WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService("wifi");
         if (!wifiManager.isWifiEnabled()) {
            return 0;
         } else {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getSSID().contains(SSID) && wifiInfo.getIpAddress() != 0 && !"00:00:00:00:00:00".equals(wifiInfo.getBSSID())) {
               if (VERSION.SDK_INT >= 28) {
               }

               return 2;
            } else {
               return 0;
            }
         }
      }
   }

   public static boolean isLookAsLanNetwork(@NonNull String SSID2G, @NonNull String SSID) {
      if (SSID.equals(SSID2G)) {
         return true;
      } else if (SSID.contains("5G")) {
         return SSID.contains(SSID2G) ? true : SSID.replace("_5G", "").equals(SSID2G);
      } else {
         return false;
      }
   }

   public static Object[] getCurrentConnectWifi(Context context) {
      WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService("wifi");
      if (!wifiManager.isWifiEnabled()) {
         return null;
      } else {
         WifiInfo wifiInfo = wifiManager.getConnectionInfo();
         if (wifiInfo == null) {
            return null;
         } else {
            String SSID = null;
            int ipAddr = wifiInfo.getIpAddress();

            try {
               SSID = wifiInfo.getSSID();
               if (SSID.contains("unknown ssid") && ipAddr != 0 && wifiInfo.getNetworkId() != 0) {
                  List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
                  Iterator var6 = configurations.iterator();

                  while(var6.hasNext()) {
                     WifiConfiguration configuration = (WifiConfiguration)var6.next();
                     if (configuration.networkId == wifiInfo.getNetworkId()) {
                        SSID = configuration.SSID;
                        break;
                     }
                  }
               }

               if (SSID.contains("unknown ssid")) {
                  ConnectivityManager manager = (ConnectivityManager)context.getSystemService("connectivity");
                  Network[] var14 = manager.getAllNetworks();
                  int var15 = var14.length;

                  for(int var8 = 0; var8 < var15; ++var8) {
                     Network network = var14[var8];
                     NetworkInfo networkInfo = manager.getNetworkInfo(network);
                     if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == 1) {
                        String extraInfo = networkInfo.getExtraInfo();
                        if (!TextUtils.isEmpty(extraInfo)) {
                           SSID = extraInfo;
                        }
                        break;
                     }
                  }
               }

               if (SSID.contains("\"")) {
                  SSID = SSID.replace("\"", "");
               }
            } catch (Exception var12) {
               SSID = null;
            }

            return ipAddr != 0 && !TextUtils.isEmpty(SSID) ? new Object[]{SSID, wifiInfo} : null;
         }
      }
   }

   public static String[] getCurrentWifiConnectedInfo(Context context) {
      if (context == null) {
         return null;
      } else {
         WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService("wifi");
         if (wifiManager != null && wifiManager.isWifiEnabled()) {
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            if (dhcpInfo == null) {
               return null;
            } else {
               String gateway = intToInetAddress(dhcpInfo.gateway).getHostAddress();
               String ipAddress = intToInetAddress(dhcpInfo.ipAddress).getHostAddress();
               String netmask = intToInetAddress(dhcpInfo.netmask).getHostAddress();
               String serverAddress = intToInetAddress(dhcpInfo.serverAddress).getHostAddress();
               return new String[]{gateway, ipAddress, netmask, serverAddress};
            }
         } else {
            return null;
         }
      }
   }

   public static boolean isWifiProEnabled(@NonNull Context context) {
      String brand = Build.BRAND.toUpperCase(Locale.US);
      if (brand.equals("HUAWEI") || brand.equals("HONOR")) {
         try {
            int val = System.getInt(context.getContentResolver(), "smart_network_switching");
            return val == 1;
         } catch (SettingNotFoundException var3) {
            var3.printStackTrace();
         }
      }

      return false;
   }

   public static boolean isWifiAssistantEnabled(@NonNull Context context) {
      String brand = Build.BRAND.toUpperCase(Locale.US);
      if (brand.equals("XIAOMI") || brand.equals("REDMI")) {
         try {
            int val = System.getInt(context.getContentResolver(), "data_and_wifi_roam");
            return val == 1;
         } catch (SettingNotFoundException var3) {
            var3.printStackTrace();
         }
      }

      return false;
   }

   public static boolean isGpsEnable(Context context) {
      LocationManager locationManager = (LocationManager)context.getSystemService("location");
      return locationManager.isProviderEnabled("gps");
   }

   public static boolean isWifiEnable(Context context) {
      WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService("wifi");
      return wifiManager != null && wifiManager.isWifiEnabled();
   }

   public static boolean scanWifi(Context context) {
      if (!isWifiEnable(context)) {
         return false;
      } else {
         WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService("wifi");
         return wifiManager.startScan();
      }
   }

   public static NetworkInfo getNetworkInfo(Context context) {
      ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
      return cm.getActiveNetworkInfo();
   }

   public static boolean isMobile(Context context) {
      NetworkInfo networkInfo = getNetworkInfo(context);
      return networkInfo != null && networkInfo.getType() == 0;
   }

   public static int getNetWorkType(Context context) {
      int netType = -1;
      NetworkInfo networkInfo = getNetworkInfo(context);
      if (networkInfo != null && networkInfo.isAvailable()) {
         if (networkInfo.getType() == 1) {
            netType = 1;
         } else if (networkInfo.getType() == 0) {
            switch(networkInfo.getSubtype()) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
               netType = 2;
               break;
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
               netType = 3;
               break;
            case 13:
               netType = 4;
               break;
            case 16:
            case 17:
            case 18:
            case 19:
            default:
               String subtypeName = networkInfo.getSubtypeName();
               if (!subtypeName.equalsIgnoreCase("TD-SCDMA") && !subtypeName.equalsIgnoreCase("WCDMA") && !subtypeName.equalsIgnoreCase("CDMA2000")) {
                  netType = 6;
               } else {
                  netType = 3;
               }
               break;
            case 20:
               netType = 5;
            }
         } else {
            netType = 6;
         }
      }

      return netType;
   }

   public static String getNetWorkTypeName(Context context) {
      switch(getNetWorkType(context)) {
      case -1:
         return "NETWORK_NO";
      case 0:
      default:
         return "NETWORK_UNKNOWN";
      case 1:
         return "NETWORK_WIFI";
      case 2:
         return "NETWORK_2G";
      case 3:
         return "NETWORK_3G";
      case 4:
         return "NETWORK_4G";
      case 5:
         return "NETWORK_5G";
      }
   }

   public static String getNetWorkOperatorName(Context context) {
      TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
      return tm != null ? tm.getNetworkOperatorName() : null;
   }

   public static String getIPAddress(Context context) {
      NetworkInfo info = ((ConnectivityManager)context.getSystemService("connectivity")).getActiveNetworkInfo();
      if (info != null && info.isConnected()) {
         if (info.getType() == 0) {
            try {
               Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
               if (networkInterfaceEnumeration != null) {
                  while(true) {
                     Enumeration inetAddressEnumeration;
                     do {
                        NetworkInterface networkInterface;
                        do {
                           if (!networkInterfaceEnumeration.hasMoreElements()) {
                              return null;
                           }

                           networkInterface = (NetworkInterface)networkInterfaceEnumeration.nextElement();
                        } while(networkInterface == null);

                        inetAddressEnumeration = networkInterface.getInetAddresses();
                     } while(inetAddressEnumeration == null);

                     while(inetAddressEnumeration.hasMoreElements()) {
                        InetAddress inetAddress = (InetAddress)inetAddressEnumeration.nextElement();
                        if (inetAddress != null && !inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                           return inetAddress.getHostAddress();
                        }
                     }
                  }
               }
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         } else if (info.getType() == 1) {
            WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService("wifi");
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return intIP2StringIP(wifiInfo.getIpAddress());
         }
      }

      return null;
   }

   private static String intIP2StringIP(int ip) {
      return (ip & 255) + "." + (ip >> 8 & 255) + "." + (ip >> 16 & 255) + "." + (ip >> 24 & 255);
   }

   public static InetAddress intToInetAddress(int hostAddress) {
      byte[] addressBytes = new byte[]{(byte)(255 & hostAddress), (byte)(255 & hostAddress >> 8), (byte)(255 & hostAddress >> 16), (byte)(255 & hostAddress >> 24)};

      try {
         return InetAddress.getByAddress(addressBytes);
      } catch (UnknownHostException var3) {
         throw new AssertionError();
      }
   }

   public static int inetAddressToInt(InetAddress inetAddr) throws IllegalArgumentException {
      byte[] addr = inetAddr.getAddress();
      return (addr[3] & 255) << 24 | (addr[2] & 255) << 16 | (addr[1] & 255) << 8 | addr[0] & 255;
   }
}
