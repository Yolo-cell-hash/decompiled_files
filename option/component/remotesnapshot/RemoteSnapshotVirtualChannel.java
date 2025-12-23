package com.eseeiot.option.component.remotesnapshot;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.dispatcher.VconEventDispatchEntry;
import com.eseeiot.basemodule.device.option.RemoteSnapshotCallback;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoteSnapshotVirtualChannel implements VconEventDispatchEntry {
   private static final String TAG = "VirtualChannel";
   private static final int P2P_VCON_CMD_SNAPSHOT = 10001;
   private static final int VCON_DATA_HEAD_LEN = 36;
   private static final String TEMP_FILE_SUFFIX = ".temp";
   private final String MSG_BUFFER_INDEX;
   private final int MSG_SEND;
   private final int MSG_RESEND;
   private final int STATUS_WAITING;
   private final int STATUS_SENDING;
   private final int STATUS_RENSENDING;
   private final int STATUS_DONE;
   private MonitorDevice mDevice;
   private int mStream;
   private String mCurrentFilePath;
   FileOutputStream mOutputStream;
   File mCurrentFile;
   RemoteSnapshotCallback mRemoteSnapshotCallback;
   private HandlerThread mThread;
   private Handler mSender;
   private RemoteSnapshotVirtualChannel.SendCallback mCallback;
   private int mStatus;
   private boolean mIsRunning;
   private Handler mMainLooperHandler;
   private int mReqMagic;
   private int mAckMagic;
   private int mVersion;
   private int mCrc;
   private int mReverse;

   private RemoteSnapshotVirtualChannel(MonitorDevice device, int stream, String filePath, RemoteSnapshotCallback snapshotCallback) {
      this.MSG_BUFFER_INDEX = "msg_buffer_index";
      this.MSG_SEND = 10;
      this.MSG_RESEND = 11;
      this.STATUS_WAITING = 100;
      this.STATUS_SENDING = 101;
      this.STATUS_RENSENDING = 101;
      this.STATUS_DONE = 102;
      this.mIsRunning = true;
      this.mMainLooperHandler = new Handler(Looper.getMainLooper());
      this.mReqMagic = 1986228078;
      this.mAckMagic = 1919250020;
      this.mVersion = 4;
      this.mCrc = 0;
      this.mReverse = 0;
      this.mDevice = device;
      this.mStream = stream;
      this.mCurrentFilePath = filePath;
      this.mRemoteSnapshotCallback = snapshotCallback;
      this.init();
   }

   public void init() {
      this.mThread = new HandlerThread("VirtualChannel");
      this.mThread.start();
      this.mSender = new RemoteSnapshotVirtualChannel.SendHandler(this.mThread.getLooper());
   }

   public void setCallback(RemoteSnapshotVirtualChannel.SendCallback callback) {
      this.mCallback = callback;
   }

   public void send() {
      if (this.mStatus != 101) {
         this.mSender.sendEmptyMessage(10);
      }
   }

   public void resend(int bufferIndex) {
      Message message = new Message();
      message.what = 11;
      Bundle bundle = new Bundle();
      bundle.putInt("msg_buffer_index", bufferIndex);
      message.setData(bundle);
      this.mSender.sendMessage(message);
   }

   public void release() {
      this.mIsRunning = false;
      this.mCallback = null;
      this.mDevice.cancelVcon(this);
      this.mRemoteSnapshotCallback = null;
      if (this.mThread != null) {
         this.mThread.quitSafely();
      }

   }

   private void sendPackage() {
      this.mIsRunning = true;
      byte[] data = new byte[0];
      List<Integer> header = new ArrayList();
      int packageFlagIndex = 3;
      header.add(this.getMagic());
      header.add(this.getVersion());
      header.add(10001);
      header.add(2);
      header.add(this.getCrc());
      header.add(0);
      header.add(0);
      header.add(0);
      header.add(this.mStream << 8);
      header.add(this.getReverse());
      int[] h = new int[header.size()];
      int sendResult = false;
      this.mStatus = 101;

      for(int i = 0; i < header.size(); ++i) {
         h[i] = (Integer)header.get(i);
      }

      while(this.mIsRunning) {
         int sendResult = this.mDevice.sendData(h, data, 0, this);
         if (sendResult == 0) {
            break;
         }

         try {
            header.set(packageFlagIndex, 4);
            Log.d("VirtualChannel", "重发");
            Thread.sleep(100L);
         } catch (InterruptedException var7) {
            var7.printStackTrace();
         }
      }

      Log.d("VirtualChannel", "发送成功:0 packageSize:0");
      this.mSender.sendEmptyMessage(102);
      this.mStatus = 100;
   }

   public void dispatchVconEvent(String message, int channel) {
   }

   public void dispatchVconResendEvent(int magic, int version, int fileType, int packageNo, int endFlag, int reverse, byte[] buffer) {
      int packedFlag = buffer[12] | buffer[13] << 8 | buffer[14] << 16 | buffer[15] << 24;
      if (magic == this.mAckMagic) {
         if (fileType == 10001) {
            try {
               if (packedFlag == 0) {
                  this.initFile();
               }

               if (this.mOutputStream == null) {
                  return;
               }

               this.mOutputStream.write(buffer, 36, buffer.length - 36);
               if (packedFlag == 2) {
                  this.closeAndRenameFile(getSnapshotFileType(buffer[32]));
               }
            } catch (Exception var10) {
            }

         }
      }
   }

   private void resendPackage(int bufferIndex) {
   }

   public int getMagic() {
      return this.mReqMagic;
   }

   public void setMagic(int magic) {
      this.mReqMagic = magic;
   }

   public int getVersion() {
      return this.mVersion;
   }

   public void setVersion(int version) {
      this.mVersion = version;
   }

   public int getCrc() {
      return this.mCrc;
   }

   public void setCrc(int crc) {
      this.mCrc = crc;
   }

   public int getReverse() {
      return this.mReverse;
   }

   public void setReverse(int reverse) {
      this.mReverse = reverse;
   }

   private void initFile() {
      try {
         if (this.mOutputStream != null) {
            try {
               this.mOutputStream.close();
            } catch (IOException var2) {
            }
         }

         this.mCurrentFile = new File(this.mCurrentFilePath);
         this.mOutputStream = new FileOutputStream(this.mCurrentFile);
         this.mCurrentFile.createNewFile();
      } catch (IOException var3) {
      }

   }

   private void closeAndRenameFile(String fileType) {
      if (this.mOutputStream != null && this.mCurrentFile != null) {
         try {
            this.mOutputStream.close();
            this.mOutputStream = null;
            String filePath = this.mCurrentFile.getAbsolutePath();
            filePath = filePath.substring(0, filePath.length() - ".temp".length());
            this.mCurrentFile.renameTo(new File(filePath + File.separator + fileType));
            String outputFilePath = this.mCurrentFile.getAbsolutePath();
            this.mCurrentFile = null;
            this.mMainLooperHandler.post(() -> {
               if (this.mRemoteSnapshotCallback != null) {
                  this.mRemoteSnapshotCallback.onSuccess(outputFilePath, fileType);
               }

            });
         } catch (IOException var4) {
         }

      }
   }

   private static String getSnapshotFileType(byte type) {
      switch(type) {
      case 0:
      default:
         return "jpg";
      }
   }

   // $FF: synthetic method
   RemoteSnapshotVirtualChannel(MonitorDevice x0, int x1, String x2, RemoteSnapshotCallback x3, Object x4) {
      this(x0, x1, x2, x3);
   }

   public interface SendCallback {
      void onComplete();
   }

   public static class Builder {
      private RemoteSnapshotVirtualChannel virtualChannel;

      public Builder(MonitorDevice device, int stream, String filePath, RemoteSnapshotCallback snapshotCallback) {
         this.virtualChannel = new RemoteSnapshotVirtualChannel(device, stream, filePath, snapshotCallback);
      }

      public RemoteSnapshotVirtualChannel build() {
         return this.virtualChannel;
      }

      public RemoteSnapshotVirtualChannel.Builder setMagic(int magic) {
         this.virtualChannel.setMagic(magic);
         return this;
      }

      public RemoteSnapshotVirtualChannel.Builder setVersion(int version) {
         this.virtualChannel.setVersion(version);
         return this;
      }

      public RemoteSnapshotVirtualChannel.Builder setCrc(int crc) {
         this.virtualChannel.setCrc(crc);
         return this;
      }

      public RemoteSnapshotVirtualChannel.Builder setReverse(int reverse) {
         this.virtualChannel.setReverse(reverse);
         return this;
      }
   }

   private class SendHandler extends Handler {
      public SendHandler(Looper looper) {
         super(looper);
      }

      public void handleMessage(Message msg) {
         switch(msg.what) {
         case 10:
            RemoteSnapshotVirtualChannel.this.sendPackage();
            break;
         case 11:
            Bundle bundle = msg.getData();
            RemoteSnapshotVirtualChannel.this.resendPackage(bundle.getInt("msg_buffer_index", -1));
            break;
         case 102:
            if (RemoteSnapshotVirtualChannel.this.mCallback != null) {
               RemoteSnapshotVirtualChannel.this.mCallback.onComplete();
            }
         }

      }
   }
}
