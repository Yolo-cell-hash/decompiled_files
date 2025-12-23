package com.eseeiot.setup.task.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanSettings.Builder;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Build.VERSION;
import android.text.TextUtils;
import androidx.core.app.ActivityCompat;
import com.eseeiot.setup.pojo.CodeExtra;
import java.util.List;
import java.util.Objects;

public class BLEScanController {
   private static final String TAG = "BLEScanController";
   public static int ERROR_BLUETOOTH_UN_SUPPORT = 16;
   public static int ERROR_LE_BLUETOOTH_UN_SUPPORT = 17;
   public static int ERROR_PERMISSION_MISS = 18;
   public static BLEScanController sController;
   private Context mContext;
   private Handler mHandler;
   private BluetoothAdapter mBluetoothAdapter;
   private ScanSettings mScanSettings;
   private ScanCallback mScanCallback;
   private boolean mIsScanning;
   private BLEScanController.OnScanCallback mCallback;

   public static BLEScanController getInstance(Context context) {
      if (sController == null) {
         Class var1 = BLEScanController.class;
         synchronized(BLEScanController.class) {
            if (sController == null) {
               sController = new BLEScanController(context);
            }
         }
      }

      return sController;
   }

   private BLEScanController(Context context) {
      this.mContext = context.getApplicationContext();
      this.mHandler = new Handler(Looper.getMainLooper());
   }

   private void init() {
      if (this.mBluetoothAdapter != null) {
         BluetoothLeScanner leScanner = this.mBluetoothAdapter.getBluetoothLeScanner();
         if (leScanner != null) {
            Builder builder = new Builder();
            builder.setScanMode(2);
            if (VERSION.SDK_INT >= 23) {
               builder.setCallbackType(1);
               builder.setMatchMode(2);
            }

            if (this.mBluetoothAdapter.isOffloadedScanBatchingSupported()) {
               builder.setReportDelay(0L);
            }

            this.mScanSettings = builder.build();
            this.mScanCallback = new ScanCallback() {
               public void onScanResult(int callbackType, ScanResult result) {
                  super.onScanResult(callbackType, result);
                  BLEScanController.this.parseScanResult(result);
               }

               public void onScanFailed(int errorCode) {
                  super.onScanFailed(errorCode);
                  if (BLEScanController.this.mCallback != null && BLEScanController.this.mHandler != null) {
                     BLEScanController.this.mHandler.post(() -> {
                        BLEScanController.this.mCallback.scanFailed(errorCode);
                     });
                  }

               }
            };
         }
      }

   }

   public void startScan(BLEScanController.OnScanCallback callback) {
      if (this.mContext != null && this.mBluetoothAdapter == null) {
         BluetoothManager manager = (BluetoothManager)this.mContext.getSystemService("bluetooth");
         if (manager != null) {
            this.mBluetoothAdapter = manager.getAdapter();
            this.init();
         }
      }

      if (this.mBluetoothAdapter != null) {
         if (this.mIsScanning) {
            this.mCallback = callback;
            return;
         }

         if (!this.mBluetoothAdapter.isEnabled()) {
            if (callback != null) {
               callback.scanFailed(ERROR_BLUETOOTH_UN_SUPPORT);
            }

            return;
         }

         if (this.mScanCallback == null) {
            if (callback != null) {
               callback.scanFailed(ERROR_LE_BLUETOOTH_UN_SUPPORT);
            }

            return;
         }

         if (ActivityCompat.checkSelfPermission(this.mContext, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            if (callback != null) {
               callback.scanFailed(ERROR_PERMISSION_MISS);
            }

            return;
         }

         this.mCallback = callback;
         this.mIsScanning = true;
         this.mBluetoothAdapter.getBluetoothLeScanner().startScan((List)null, this.mScanSettings, this.mScanCallback);
      }

   }

   public void stopScan() {
      if (this.mBluetoothAdapter != null && this.mScanCallback != null && this.mIsScanning) {
         BluetoothLeScanner leScanner = this.mBluetoothAdapter.getBluetoothLeScanner();
         if (leScanner != null) {
            leScanner.stopScan(this.mScanCallback);
         }

         this.mIsScanning = false;
      }

   }

   public boolean isScanning() {
      return this.mIsScanning;
   }

   public void release() {
      this.stopScan();
      this.mScanCallback = null;
      this.mBluetoothAdapter = null;
      this.mScanSettings = null;
      this.mContext = null;
      if (this.mHandler != null) {
         this.mHandler.removeCallbacksAndMessages((Object)null);
         this.mHandler = null;
      }

      sController = null;
   }

   private void parseScanResult(ScanResult result) {
      if (result != null && result.getDevice() != null && result.getScanRecord() != null && this.mCallback != null && this.mHandler != null) {
         BluetoothDevice device = result.getDevice();
         ScanRecord scanRecord = result.getScanRecord();
         if (!TextUtils.isEmpty(scanRecord.getDeviceName())) {
            CodeExtra codeExtra = new CodeExtra(scanRecord.getDeviceName());
            if ((codeExtra.isBleExtraCode() || codeExtra.isBLEGateWayExtraCode()) && codeExtra.hasAbilityBlueToothMatch()) {
               this.mHandler.post(() -> {
                  BLEScanController.ScanInfo scanInfo = new BLEScanController.ScanInfo();
                  scanInfo.mDeviceName = scanRecord.getDeviceName();
                  scanInfo.mRssi = result.getRssi();
                  scanInfo.mDevice = device;
                  scanInfo.mDeviceId = codeExtra.getEseeId();
                  scanInfo.mDeviceType = codeExtra.getDeviceType();
                  this.mCallback.scanResult(scanInfo);
               });
            }

         }
      }
   }

   public static class ScanInfo {
      private BluetoothDevice mDevice;
      private String mDeviceName;
      private String mDeviceId;
      private int mRssi;
      private int mDeviceType;

      public BluetoothDevice getDevice() {
         return this.mDevice;
      }

      public String getDeviceName() {
         return this.mDeviceName;
      }

      public int getRssi() {
         return this.mRssi;
      }

      public String getDeviceId() {
         return this.mDeviceId;
      }

      public int getDeviceType() {
         return this.mDeviceType;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            BLEScanController.ScanInfo scanInfo = (BLEScanController.ScanInfo)o;
            return this.mDeviceType == scanInfo.mDeviceType && this.mDevice.equals(scanInfo.mDevice) && this.mDeviceName.equals(scanInfo.mDeviceName) && this.mDeviceId.equals(scanInfo.mDeviceId);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.mDevice, this.mDeviceName, this.mDeviceId, this.mDeviceType});
      }
   }

   public interface OnScanCallback {
      void scanFailed(int var1);

      void scanResult(BLEScanController.ScanInfo var1);
   }
}
