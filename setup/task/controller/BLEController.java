package com.eseeiot.setup.task.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.eseeiot.setup.service.BLEPeripheralService;
import com.eseeiot.setup.step.ContextProvider;
import com.eseeiot.setup.task.DeviceSetupType;
import com.eseeiot.setup.task.tag.TaskTag;

public class BLEController extends QRController {
   private static final String TAG = "QRController";
   private BLEPeripheralService mBleService;

   public BLEController(String devId) {
      super(devId);
      this.mTaskManager.setType(DeviceSetupType.BLE);
   }

   public void doTask() {
      super.doTask();
      if (this.mCurrentState == 1) {
         if (this.mBleService != null) {
            this.mBleService.stop();
         }

         BLEPeripheralService.Builder builder = new BLEPeripheralService.Builder();
         this.mBleService = builder.with(ContextProvider.getApplicationContext()).configCodeText(this.getConfigQrCode()).interval(300).build();
         this.mBleService.run();
      }

   }

   public void pauseTask() {
      super.pauseTask();
      if (this.mCurrentState == 2 && this.mBleService != null) {
         this.mBleService.stop();
      }

   }

   public void stopTask() {
      super.stopTask();
      if (this.mBleService != null) {
         this.mBleService.stop();
         this.mBleService = null;
      }

   }

   public void onTaskError(@NonNull TaskTag taskTag, @Nullable Object object) {
      super.onTaskError(taskTag, object);
      if (taskTag == TaskTag.CONNECT_DEVICE && object instanceof Integer) {
         int errCode = (Integer)object;
         if (errCode != -24) {
            return;
         }

         if (this.mBleService != null) {
            this.mBleService.stop();
            this.mBleService = null;
         }
      }

   }

   protected void notifyConfigFailed(@Nullable TaskTag taskTag, int errCode, @Nullable String errMsg) {
      super.notifyConfigFailed(taskTag, errCode, errMsg);
      if (this.mBleService != null) {
         this.mBleService.stop();
         this.mBleService = null;
      }

   }

   protected long getPreConnectTaskDelayTime() {
      return 3000L;
   }

   protected boolean shouldAppendIDSuffixInQrCode() {
      return true;
   }

   protected String getTag() {
      return "QRController";
   }
}
