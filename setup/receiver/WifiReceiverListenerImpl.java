package com.eseeiot.setup.receiver;

import android.content.Intent;
import android.net.NetworkInfo;

public abstract class WifiReceiverListenerImpl implements WifiReceiverListener {
   public void onWifiConnected(Intent intent, NetworkInfo info) {
   }

   public void onWifiFailed(Intent intent, NetworkInfo info) {
   }

   public void onWifiDisconnected(Intent intent, NetworkInfo info) {
   }

   public void onWifiConnecting(Intent intent, NetworkInfo info) {
   }

   public void onWifiDisconnecting(Intent intent, NetworkInfo info) {
   }

   public void onWifiDefault(Intent intent, NetworkInfo info) {
   }

   public void onWifiScan(Intent intent) {
   }

   public void onErrorAuthenticating() {
   }
}
