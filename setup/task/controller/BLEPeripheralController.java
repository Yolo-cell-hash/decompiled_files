package com.eseeiot.setup.task.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.AdvertiseSettings.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.text.TextUtils;

public class BLEPeripheralController {
   private static final String TAG = "BLEPeripheral";
   private static final String[] hexDigIts = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
   public static final int STATE_ADVERT_OFF = 0;
   public static final int STATE_ADVERT_TURNING_OFF = 1;
   public static final int STATE_ADVERT_ON = 2;
   public static final int STATE_ADVERT_TURNING_ON = 3;
   private Context mAppContext;
   private BluetoothManager mBluetoothManager;
   private BluetoothAdapter mBluetoothAdapter;
   private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
   private AdvertiseCallback mAdvertiseCallback;
   private int mAdvertState;
   private BLEPeripheralController.Callback mCallback;
   private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
      public void onReceive(Context context, Intent intent) {
         String var3 = intent.getAction();
         byte var4 = -1;
         switch(var3.hashCode()) {
         case -1530327060:
            if (var3.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
               var4 = 0;
            }
         default:
            switch(var4) {
            case 0:
               int bleState = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1);
               if (bleState == 12) {
                  BLEPeripheralController.this.reInit();
               } else if (bleState == 13) {
                  BLEPeripheralController.this.stopAdvertising();
                  if (BLEPeripheralController.this.mCallback != null) {
                     BLEPeripheralController.this.mCallback.onAdvertStop();
                  }
               }
            default:
            }
         }
      }
   };

   public BLEPeripheralController(Context appContext) {
      this.init(appContext);
   }

   private void init(Context context) {
      this.mAppContext = context;
      this.mBluetoothManager = (BluetoothManager)context.getSystemService("bluetooth");
      this.reInit();
      context.registerReceiver(this.mBluetoothReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
   }

   private void reInit() {
      this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
      if (this.mBluetoothAdapter.isEnabled()) {
         this.mBluetoothLeAdvertiser = this.mBluetoothAdapter.getBluetoothLeAdvertiser();
         if (this.mCallback != null) {
            this.mCallback.onAdvertReady();
         }
      }

   }

   public void release() {
      this.mAppContext.unregisterReceiver(this.mBluetoothReceiver);
   }

   public boolean isAdvertReady() {
      return this.mBluetoothAdapter.isEnabled() && this.mBluetoothLeAdvertiser != null;
   }

   public boolean isAdvertising() {
      return this.mAdvertState == 2;
   }

   public boolean startAdvertising(String bleName, byte[] manufacturerSpecificData) {
      if (this.mBluetoothLeAdvertiser == null) {
         return false;
      } else {
         String hexString = this.byteArrayToHexString(manufacturerSpecificData);
         String uuid = this.createUUIDFromString(hexString);
         if (TextUtils.isEmpty(uuid)) {
            return false;
         } else {
            this.mAdvertState = 3;
            this.mBluetoothAdapter.setName(bleName);
            AdvertiseSettings settings = (new Builder()).setConnectable(false).setTimeout(0).setAdvertiseMode(2).setTxPowerLevel(3).build();
            AdvertiseData advertiseData = (new android.bluetooth.le.AdvertiseData.Builder()).setIncludeDeviceName(true).setIncludeTxPowerLevel(true).addServiceUuid(ParcelUuid.fromString(uuid)).build();
            AdvertiseData scanResponseData = (new android.bluetooth.le.AdvertiseData.Builder()).setIncludeTxPowerLevel(true).build();
            if (this.mAdvertiseCallback == null) {
               this.mAdvertiseCallback = new AdvertiseCallback() {
                  public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                     super.onStartSuccess(settingsInEffect);
                     BLEPeripheralController.this.mAdvertState = 2;
                  }

                  public void onStartFailure(int errorCode) {
                     super.onStartFailure(errorCode);
                     BLEPeripheralController.this.mAdvertState = 0;
                  }
               };
            }

            this.mBluetoothLeAdvertiser.startAdvertising(settings, advertiseData, scanResponseData, this.mAdvertiseCallback);
            return true;
         }
      }
   }

   public void stopAdvertising() {
      if (this.mBluetoothLeAdvertiser != null) {
         if (this.mAdvertState == 2 || this.mAdvertState == 3) {
            this.mAdvertState = 1;
         }

         if (this.mAdvertiseCallback != null) {
            this.mBluetoothLeAdvertiser.stopAdvertising(this.mAdvertiseCallback);
            this.mAdvertiseCallback = null;
         }
      }

   }

   private String createUUIDFromString(String src) {
      return src != null && src.length() == 32 ? src.substring(0, 8) + "-" + src.substring(8, 12) + "-" + src.substring(12, 16) + "-" + src.substring(16, 20) + "-" + src.substring(20) : null;
   }

   private String byteArrayToHexString(byte[] b) {
      StringBuilder resultSb = new StringBuilder();
      byte[] var3 = b;
      int var4 = b.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         byte value = var3[var5];
         resultSb.append(this.byteToHexString(value));
      }

      return resultSb.toString();
   }

   private String byteToHexString(byte b) {
      int n = b;
      if (b < 0) {
         n = b + 256;
      }

      int d1 = n / 16;
      int d2 = n % 16;
      return hexDigIts[d1] + hexDigIts[d2];
   }

   public void setCallback(BLEPeripheralController.Callback callback) {
      this.mCallback = callback;
   }

   public interface Callback {
      void onAdvertReady();

      void onAdvertStop();
   }
}
