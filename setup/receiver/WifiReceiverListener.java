package com.eseeiot.setup.receiver;

import android.content.Intent;
import android.net.NetworkInfo;

public interface WifiReceiverListener {
   void onWifiConnected(Intent var1, NetworkInfo var2);

   void onWifiFailed(Intent var1, NetworkInfo var2);

   void onWifiDisconnected(Intent var1, NetworkInfo var2);

   void onWifiConnecting(Intent var1, NetworkInfo var2);

   void onWifiDisconnecting(Intent var1, NetworkInfo var2);

   void onWifiDefault(Intent var1, NetworkInfo var2);

   void onWifiScan(Intent var1);

   void onErrorAuthenticating();
}
