package com.eseeiot.setup.service;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.eseeiot.basemodule.util.ThreadPool;
import com.eseeiot.setup.task.controller.BLEPeripheralController;
import java.nio.charset.StandardCharsets;

public class BLEPeripheralService {
   private static final String TAG = "BLEPeripheralService";
   private static final String[] hexDigIts = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
   private static final int VALID_DATA_SIZE_FOR_PACKET = 11;
   private final BLEPeripheralController mBLE;
   private boolean mIsRunning;
   private boolean mIsSending;
   private final byte[] mSrcData;
   private final int mSendInterval;

   private BLEPeripheralService(@NonNull Context context, byte[] data, int interval) {
      this.mIsRunning = false;
      this.mIsSending = false;
      this.mSrcData = data;
      this.mSendInterval = interval;
      this.mBLE = new BLEPeripheralController(context.getApplicationContext());
      this.mBLE.setCallback(new BLEPeripheralController.Callback() {
         public void onAdvertReady() {
            if (BLEPeripheralService.this.mIsRunning && !BLEPeripheralService.this.mIsSending) {
               BLEPeripheralService.this.startSendingProcess();
            }

         }

         public void onAdvertStop() {
            BLEPeripheralService.this.mIsSending = false;
         }
      });
   }

   public void run() {
      if (!this.mIsRunning) {
         this.mIsRunning = true;
         if (this.mBLE.isAdvertReady()) {
            this.startSendingProcess();
         }

      }
   }

   public void stop() {
      this.mIsRunning = false;
      this.mIsSending = false;
      this.mBLE.stopAdvertising();
      this.mBLE.release();
   }

   private void startSendingProcess() {
      ThreadPool.initialize();
      ThreadPool.execute(() -> {
         this.mIsSending = true;

         do {
            try {
               this.sendingData();
            } catch (InterruptedException var2) {
            }

            if (this.mSendInterval <= 0) {
               break;
            }

            try {
               Thread.sleep((long)this.mSendInterval);
            } catch (InterruptedException var3) {
               var3.printStackTrace();
            }
         } while(this.mIsSending);

         this.mIsSending = false;
      });
   }

   private void sendingData() throws InterruptedException {
      int packetCount = this.mSrcData.length / 11;
      if (this.mSrcData.length % 11 != 0) {
         ++packetCount;
      }

      for(int packetNo = 0; packetNo < packetCount && this.mIsSending; ++packetNo) {
         byte[] manufacturerSpecificData = this.buildDataPacket(this.mSrcData, packetCount, packetNo);
         this.mBLE.startAdvertising("JA", manufacturerSpecificData);
         Thread.sleep(300L);
         this.mBLE.stopAdvertising();
      }

   }

   private byte[] buildDataPacket(byte[] srcData, int packetCount, int packetNo) {
      int offset = packetNo * 11;
      int validDataSize = srcData.length - offset;
      if (validDataSize > 11) {
         validDataSize = 11;
      }

      byte[] packet = new byte[16];
      packet[0] = (byte)(packetCount > 1 ? packetCount : 0);
      packet[1] = (byte)(packetCount > 1 ? packetNo + 1 : 0);
      packet[2] = (byte)validDataSize;
      System.arraycopy(srcData, offset, packet, 3, validDataSize);
      short verifyCode = 0;

      for(int i = 0; i < validDataSize + 3; ++i) {
         verifyCode = (short)((verifyCode & '\uffff') + (packet[i] & 255) & '\uffff');
      }

      packet[3 + validDataSize] = (byte)(verifyCode & 255);
      packet[3 + validDataSize + 1] = (byte)(verifyCode >> 8 & 255);
      return packet;
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

   // $FF: synthetic method
   BLEPeripheralService(Context x0, byte[] x1, int x2, Object x3) {
      this(x0, x1, x2);
   }

   public static class Builder {
      private Context context;
      private String codeText;
      private int interval;

      public BLEPeripheralService.Builder with(Context context) {
         this.context = context;
         return this;
      }

      public BLEPeripheralService.Builder interval(int interval) {
         this.interval = interval;
         return this;
      }

      public BLEPeripheralService.Builder configCodeText(String codeText) {
         this.codeText = codeText;
         return this;
      }

      public BLEPeripheralService build() {
         if (this.context != null && !TextUtils.isEmpty(this.codeText)) {
            byte[] srcByte = this.codeText.getBytes(StandardCharsets.UTF_8);
            byte verifyByte = 0;
            byte[] dataByte = srcByte;
            int var4 = srcByte.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               byte data = dataByte[var5];
               verifyByte += data;
            }

            dataByte = new byte[1 + srcByte.length];
            dataByte[0] = (byte)(-verifyByte);
            System.arraycopy(srcByte, 0, dataByte, 1, srcByte.length);
            return new BLEPeripheralService(this.context, dataByte, this.interval);
         } else {
            return null;
         }
      }
   }
}
