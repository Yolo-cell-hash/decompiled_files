package com.eseeiot.option;

import android.util.Log;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.option.FirmwareUpgradeResultCallback;
import com.eseeiot.basemodule.device.option.OptionOperationCallback;
import com.eseeiot.basemodule.device.option.SettingResultCallback;
import com.eseeiot.basemodule.device.option.base.BaseOptionOperation;
import java.util.ArrayList;
import java.util.List;

public class NVRFirmwareUpdateHelper {
   private static final String TAG = "NVRFirmwareUpdateHelper";
   private MonitorDevice mDevice;
   private BaseOptionOperation mDeviceOptionHelper;
   private List<Integer> upgradeIndexList;
   private boolean mStopUpdate;
   private boolean isNVRFault;

   public NVRFirmwareUpdateHelper(MonitorDevice device) {
      this.mDevice = device;
      this.mDeviceOptionHelper = this.mDevice.getOptionHelper();
      this.upgradeIndexList = new ArrayList();
   }

   public void stopNVRFirmwareUpdate() {
      this.mStopUpdate = true;
   }

   public void startUpdateFirmware(boolean enableChannel, final FirmwareUpgradeResultCallback callback) {
      this.upgradeIndexList.clear();
      this.upgradeIndexList.add(65536);
      if (enableChannel) {
         for(int i = 0; i < this.mDeviceOptionHelper.getter().getMaxChannel(); ++i) {
            if (this.mDeviceOptionHelper.getter().isChannelEnabled(i)) {
               this.upgradeIndexList.add(i);
            }
         }
      }

      this.mDeviceOptionHelper.setter().upgradeFirmware(enableChannel, new SettingResultCallback() {
         public void onResult(boolean isSuccess) {
            if (isSuccess) {
               NVRFirmwareUpdateHelper.this.checkChannelStatus(callback);
            } else {
               callback.onResult(false);
            }

         }
      });
   }

   private void checkChannelStatus(final FirmwareUpgradeResultCallback callback) {
      (new Thread(new Runnable() {
         public void run() {
            while(!NVRFirmwareUpdateHelper.this.mStopUpdate) {
               Log.i("NVRFirmwareUpdateHelper", "checkChannelStatus: ----------： 固件更新中");
               NVRFirmwareUpdateHelper.this.mDeviceOptionHelper.checkNVRUpgradeFirmwareStatus(new OptionOperationCallback() {
                  public void onSuccess() {
                     NVRFirmwareUpdateHelper.this.updateStatus(callback);
                  }

                  public void onFail(int errCode) {
                     callback.onResult(false);
                  }
               });

               try {
                  Thread.sleep(1000L);
               } catch (InterruptedException var2) {
                  var2.printStackTrace();
               }
            }

            Log.i("NVRFirmwareUpdateHelper", "checkChannelStatus: --------> 更新结束");
         }
      })).start();
   }

   private void updateStatus(FirmwareUpgradeResultCallback callback) {
      String status = this.mDeviceOptionHelper.getter().getUpgradeStatus();
      if (status == null) {
         this.mStopUpdate = true;
         callback.onResult(false);
      } else {
         String error = this.mDeviceOptionHelper.getter().getUpgradeErrDescription();
         if (error == null) {
            error = "";
         }

         try {
            int index = this.mDeviceOptionHelper.getter().getUpgradeIndex();
            int progress = this.mDeviceOptionHelper.getter().getUpgradeProgress();
            if (index == 65536) {
               if (status.equals("upgrading_device") || status.equals("completed")) {
                  if (progress >= 99) {
                     if (this.upgradeIndexList.contains(index)) {
                        Log.d("NVRFirmwareUpdateHelper", "updateStatus: NVR更新已完成");
                        this.upgradeIndexList.remove(index);
                     }
                  } else if (this.isNVRFault && progress >= 1) {
                     if (this.upgradeIndexList.contains(index)) {
                        Log.d("NVRFirmwareUpdateHelper", "updateStatus: NVR更新已完成");
                        this.upgradeIndexList.remove(index);
                     }
                  } else if (progress > 90) {
                     this.isNVRFault = true;
                  }
               }
            } else if ((status.equals("completed") || progress == 100) && this.upgradeIndexList.contains(index)) {
               Log.d("NVRFirmwareUpdateHelper", "updateStatus: 通道" + (index + 1) + "更新已完成");
               this.upgradeIndexList.remove(index);
            }

            boolean finished = this.upgradeIndexList.size() == 0;
            callback.onUpgradeStatus(finished, index, status, error, progress);
            if (finished) {
               this.mStopUpdate = true;
               callback.onResult(true);
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }

      }
   }
}
