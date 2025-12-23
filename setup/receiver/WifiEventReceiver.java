package com.eseeiot.setup.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;

public class WifiEventReceiver extends BroadcastReceiver {
   private static final String TAG = "WifiEventReceiver";
   private WifiReceiverListenerImpl mListener;

   public void setListener(WifiReceiverListenerImpl listener) {
      this.mListener = listener;
   }

   public void onReceive(Context context, Intent intent) {
      if (this.mListener != null) {
         if ("android.net.wifi.SCAN_RESULTS".equals(intent.getAction())) {
            this.mListener.onWifiScan(intent);
         } else if ("android.net.wifi.STATE_CHANGE".equals(intent.getAction())) {
            NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra("networkInfo");
            DetailedState detailedState = networkInfo.getDetailedState();
            switch(detailedState) {
            case CONNECTED:
               this.mListener.onWifiConnected(intent, networkInfo);
               break;
            case CONNECTING:
               this.mListener.onWifiConnecting(intent, networkInfo);
               break;
            case FAILED:
               this.mListener.onWifiFailed(intent, networkInfo);
               break;
            case DISCONNECTED:
               this.mListener.onWifiDisconnected(intent, networkInfo);
               break;
            case DISCONNECTING:
               this.mListener.onWifiDisconnecting(intent, networkInfo);
               break;
            default:
               this.mListener.onWifiDefault(intent, networkInfo);
            }
         } else if ("android.net.wifi.supplicant.STATE_CHANGE".equals(intent.getAction())) {
            int err = intent.getIntExtra("supplicantError", -1);
            if (err == 1) {
               this.mListener.onErrorAuthenticating();
            }
         }

      }
   }
}
