package com.eseeiot.setup.task;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.eseeiot.basemodule.helper.MulticastHelper;
import com.eseeiot.basemodule.util.DeviceTool;
import com.eseeiot.setup.pojo.DeviceSetupInfo;
import com.eseeiot.setup.pojo.LanDeviceInfo;
import com.eseeiot.setup.pojo.MultiCastResponseInfo;
import com.eseeiot.setup.pojo.NVRResponseInfo;
import com.eseeiot.setup.pojo.OldNVRResponseInfo;
import com.eseeiot.setup.task.base.BaseTask;
import com.eseeiot.setup.task.tag.TaskTag;
import com.google.gson.Gson;

public class TaskScanLanDevice extends BaseTask implements MulticastHelper.OnMultiCastCallbackListener {
   private static final int REQUEST_SEARCH_DEVICE = 134489421;
   private final WifiManager mWifiManager;
   private MulticastHelper mHelper;
   private DeviceSetupInfo mSetupInfo;
   private boolean mScanCommon;
   private boolean mScanOldNVR;
   private boolean mScanNewNVR;
   private volatile boolean mReleased;
   private final Gson mGson;

   public TaskScanLanDevice(@NonNull Context context, @NonNull TaskTag taskTag, int timeout) {
      super(context, taskTag, timeout);
      this.mWifiManager = (WifiManager)this.mContext.getApplicationContext().getSystemService("wifi");
      this.mGson = new Gson();
   }

   public void release() {
      if (this.mIsRunning) {
         this.mReleased = true;
      } else {
         this.releaseHelper();
      }

      super.release();
   }

   protected boolean onTaskInit(Object... object) {
      this.mReleased = false;
      if (this.mHelper == null) {
         this.mHelper = new MulticastHelper(this.mContext.getApplicationContext());
         this.mHelper.setLogPrint(this.mLogPrint);
      }

      try {
         if (object != null && object.length > 0) {
            DeviceSetupInfo setupInfo = (DeviceSetupInfo)object[0];
            if (setupInfo != null && !TextUtils.isEmpty(setupInfo.getEseeId())) {
               this.mSetupInfo = setupInfo;
            }

            if (object.length > 1) {
               this.mScanCommon = (Boolean)object[1];
            }

            if (object.length > 2) {
               this.mScanOldNVR = (Boolean)object[2];
            }

            if (object.length > 3) {
               this.mScanNewNVR = (Boolean)object[3];
            }
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return this.mScanCommon || this.mScanOldNVR || this.mScanNewNVR;
   }

   protected void onTaskStart() {
      this.scanLanDevice();
   }

   protected void onTaskStop() {
      if (this.mIsRunning) {
         this.mReleased = true;
      } else {
         this.releaseHelper();
      }

   }

   protected void onTaskTimeout() {
      super.onTaskTimeout();
      if (this.mSetupInfo == null) {
         this.printLog("onTaskTimeout: search timeout.");
         this.requestTimeout((Object)null, false);
      } else {
         this.printLog("onTaskTimeout: search empty, exit!");
         this.requestTimeout((Object)null, true);
      }

   }

   private void releaseHelper() {
      this.printLog("run: released");
      if (this.mHelper != null) {
         this.mHelper.release();
         this.mHelper = null;
      }

   }

   private void scanLanDevice() {
      if (!this.mWifiManager.isWifiEnabled()) {
         this.requestError(-22);
      } else {
         this.sendDiscoverProtocol();
      }
   }

   private void sendDiscoverProtocol() {
      if (this.mHelper == null) {
         this.printLog("helper is null!!!");
      } else {
         if (!this.mHelper.isReceiverRegistered()) {
            this.mHelper.registerReceiveListener(this, this.mScanOldNVR || this.mScanNewNVR, this.mScanCommon);
         }

         String msg = null;
         if (this.mScanCommon) {
            msg = "{\"fromApp\":true,\"requestID\":134489421,\"tokenID\":\"1473433878\",\"command\":\"discovery\",\"version\":\"1.0\"}";
            this.printLog("sendDiscoverProtocol: --> msg = " + msg);
         }

         String oldNVRMsg = null;
         if (this.mScanOldNVR) {
            oldNVRMsg = "SEARCHDEV";
            this.printLog("sendDiscoverProtocol: --> oldNVRMsg = " + oldNVRMsg);
         }

         long sleepTime;
         if (this.mScanCommon && this.mScanOldNVR && this.mScanNewNVR) {
            sleepTime = 1500L;
         } else if ((!this.mScanCommon || !this.mScanOldNVR) && (!this.mScanCommon || !this.mScanNewNVR) && (!this.mScanOldNVR || !this.mScanNewNVR)) {
            sleepTime = 3000L;
         } else {
            sleepTime = 2000L;
         }

         this.startSendDiscoverProtocol(msg, oldNVRMsg, sleepTime);
      }
   }

   private void startSendDiscoverProtocol(String commonMsg, String oldNVRMsg, long sleepTime) {
      this.mThread = new Thread(() -> {
         int msgSeq = 0;
         int scanIndex = -1;

         while(this.mIsRunning) {
            do {
               ++scanIndex;
               if (scanIndex > 2) {
                  scanIndex = 0;
               }
            } while(!this.goToNextScanIndex(scanIndex));

            this.printLog("searchDevice: --> search... scanIndex = " + scanIndex);
            switch(scanIndex) {
            case 0:
               if (this.mHelper != null) {
                  this.mHelper.postData(commonMsg, 12306);
               }
               break;
            case 1:
               if (this.mHelper != null) {
                  this.mHelper.postData(oldNVRMsg, 9013);
               }
               break;
            case 2:
               if (this.mHelper != null) {
                  ++msgSeq;
                  String newNVRMsg = "SEARCH * HDS/1.1\r\nCSeq:" + msgSeq + "\r\nClient-ID:CeCOlDpmTuzViBQDgbrPgFkfnyyzNabc\r\nAccept-Type:text/HDP\r\nContent-Length:13\r\nX-Search-Type:NVR\r\n{\"Ver\":\"1.1\"}";
                  this.mHelper.postData(newNVRMsg, 9013);
               }
            }

            try {
               Thread.sleep(sleepTime);
            } catch (InterruptedException var8) {
               break;
            }
         }

         if (this.mReleased) {
            this.releaseHelper();
         }

      });
      this.mThread.start();
   }

   private boolean goToNextScanIndex(int currentIndex) {
      switch(currentIndex) {
      case 0:
         if (this.mScanCommon) {
            return true;
         }
         break;
      case 1:
         if (this.mScanOldNVR) {
            return true;
         }
         break;
      case 2:
         if (this.mScanNewNVR) {
            return true;
         }
      }

      return false;
   }

   public boolean onMultiCastCallback(String value) {
      if (!this.mIsRunning) {
         return true;
      } else {
         this.printLog("OnMultiCastCallBack: " + value);
         int startIndex;
         if (this.mScanCommon) {
            startIndex = value.indexOf("{");
            if (startIndex >= 0) {
               try {
                  MultiCastResponseInfo responseInfo = (MultiCastResponseInfo)this.mGson.fromJson(value, MultiCastResponseInfo.class);
                  return this.handleMulticastData(value, responseInfo);
               } catch (Exception var6) {
                  var6.printStackTrace();
               }
            }
         }

         if (this.mScanNewNVR) {
            startIndex = value.indexOf("{");
            if (startIndex >= 0) {
               String tmpValue = value.substring(startIndex);

               try {
                  NVRResponseInfo responseInfo = (NVRResponseInfo)this.mGson.fromJson(tmpValue, NVRResponseInfo.class);
                  return this.handleNewNVRData(tmpValue, responseInfo);
               } catch (Exception var5) {
                  var5.printStackTrace();
               }
            }
         }

         return this.mScanOldNVR ? this.handleOldNVRData(value) : false;
      }
   }

   private boolean handleMulticastData(String value, MultiCastResponseInfo responseInfo) {
      if (responseInfo == null) {
         return false;
      } else if (!responseInfo.isFromApp() && responseInfo.getRequestID() == 134489421) {
         LanDeviceInfo lanDeviceInfo = this.convertToLanDeviceInfo(responseInfo);
         if (this.mSetupInfo != null) {
            this.printLog("handleMulticastData --> received response 1 value = [" + value + "]");
            synchronized(this) {
               if (this.mIsRunning && this.mSetupInfo.getEseeId().equals(lanDeviceInfo.getEseeId())) {
                  this.mSetupInfo.setSerialId("JA" + lanDeviceInfo.getDeviceID());
                  this.mSetupInfo.setDeviceId(lanDeviceInfo.getDeviceID());
                  this.mSetupInfo.setChannelCount(lanDeviceInfo.getChannelCount());
                  this.mSetupInfo.setDeviceType(lanDeviceInfo.getDeviceType());
                  this.mSetupInfo.setCorseeVersion(lanDeviceInfo.getVersion());
                  float ver = 0.0F;

                  try {
                     ver = Float.parseFloat(lanDeviceInfo.getVersion());
                  } catch (Exception var8) {
                     var8.printStackTrace();
                  }

                  this.mSetupInfo.setSSIDNeedEncrypt(ver >= 0.4F);
                  if (responseInfo.getDeviceID() != null && responseInfo.getDeviceID().startsWith("F")) {
                     this.mSetupInfo.setDeviceType(46);
                  }

                  this.printLog("handleMulticastData: --> found device");
                  this.requestComplete(this.mSetupInfo, true);
               }

               return true;
            }
         } else {
            this.printLog("handleMulticastData --> received response 2 value = [" + value + "]");
            this.requestComplete(lanDeviceInfo, false);
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean handleNewNVRData(String value, NVRResponseInfo responseInfo) {
      this.printLog("handleNewNVRData --> received response value = [" + value + "]");
      if (!TextUtils.isEmpty(responseInfo.getEseeId())) {
         this.requestComplete(this.convertToLanDeviceInfo(responseInfo), false);
      }

      return false;
   }

   private boolean handleOldNVRData(String value) {
      if (value.startsWith("JA")) {
         this.printLog("handleOldNVRData --> received response value = [" + value + "]");
         if (value.contains("GATEWAY")) {
            return false;
         }

         OldNVRResponseInfo info = new OldNVRResponseInfo();
         String content = value.substring(2);
         String[] keys = content.split("&");
         String[] var5 = keys;
         int var6 = keys.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String keyVal = var5[var7];
            if (keyVal.startsWith("IP")) {
               info.setIpAddr(keyVal.substring(2));
            } else if (keyVal.startsWith("ID")) {
               info.setEseeId(keyVal.substring(2));
            } else {
               String sChn;
               int chn;
               if (keyVal.startsWith("PORT")) {
                  sChn = keyVal.substring(4);
                  chn = 0;

                  try {
                     chn = Integer.parseInt(sChn);
                  } catch (NumberFormatException var14) {
                     var14.printStackTrace();
                  }

                  info.setPort(chn);
               } else if (keyVal.startsWith("HTTP")) {
                  sChn = keyVal.substring(4);
                  chn = 0;

                  try {
                     chn = Integer.parseInt(sChn);
                  } catch (NumberFormatException var13) {
                     var13.printStackTrace();
                  }

                  info.setHttpPort(chn);
               } else if (keyVal.startsWith("CH")) {
                  sChn = keyVal.substring(2);
                  chn = 0;

                  try {
                     chn = Integer.parseInt(sChn);
                  } catch (NumberFormatException var12) {
                     var12.printStackTrace();
                  }

                  info.setChannelCount(chn);
               } else if (keyVal.startsWith("MODEL")) {
                  info.setModel(keyVal.substring(5));
               } else if (keyVal.startsWith("PVER")) {
                  info.setVer(keyVal.substring(4));
               }
            }
         }

         if (!TextUtils.isEmpty(info.getEseeId())) {
            this.requestComplete(this.convertToLanDeviceInfo(info), false);
         }
      }

      return false;
   }

   private LanDeviceInfo convertToLanDeviceInfo(@NonNull Object object) {
      LanDeviceInfo lanDeviceInfo = null;
      if (object instanceof MultiCastResponseInfo) {
         MultiCastResponseInfo multiCastResponseInfo = (MultiCastResponseInfo)object;
         lanDeviceInfo = new LanDeviceInfo();
         lanDeviceInfo.setEseeId(DeviceTool.getEseeIdFromSSID(multiCastResponseInfo.getDeviceID()));
         lanDeviceInfo.setFromApp(multiCastResponseInfo.isFromApp());
         lanDeviceInfo.setDeviceID(multiCastResponseInfo.getDeviceID());
         lanDeviceInfo.setVersion(multiCastResponseInfo.getVersion());
         lanDeviceInfo.setUID(multiCastResponseInfo.getUID());
         lanDeviceInfo.setDevKey(multiCastResponseInfo.getDevKey());
         lanDeviceInfo.setRequestID(multiCastResponseInfo.getRequestID());
         lanDeviceInfo.setDevinfo(multiCastResponseInfo.getDevinfo());
         if (multiCastResponseInfo.getDeviceID() != null && multiCastResponseInfo.getDeviceID().startsWith("F")) {
            lanDeviceInfo.setDeviceType(46);
         } else {
            lanDeviceInfo.setDeviceType(0);
         }

         try {
            boolean isGateway = multiCastResponseInfo.getDevinfo() != null && "GATEWAY".equals(multiCastResponseInfo.getDevinfo().getDeviceType());
            if (isGateway) {
               lanDeviceInfo.setModel("GATEWAY");
               lanDeviceInfo.setChannelCount(4);
               lanDeviceInfo.setDeviceType(0);
            } else {
               lanDeviceInfo.setModel("IPCAM");
            }
         } catch (NullPointerException var5) {
            var5.printStackTrace();
         }
      } else if (object instanceof NVRResponseInfo) {
         NVRResponseInfo nvrResponseInfo = (NVRResponseInfo)object;
         lanDeviceInfo = new LanDeviceInfo();
         lanDeviceInfo.setEseeId(nvrResponseInfo.getEseeId());
         lanDeviceInfo.setChannelCount(nvrResponseInfo.getChannelCount());
         lanDeviceInfo.setModel(nvrResponseInfo.getDeviceType());
         lanDeviceInfo.setDeviceID(nvrResponseInfo.getDeviceId());
      } else if (object instanceof OldNVRResponseInfo) {
         OldNVRResponseInfo oldNVRResponseInfo = (OldNVRResponseInfo)object;
         lanDeviceInfo = new LanDeviceInfo();
         lanDeviceInfo.setEseeId(oldNVRResponseInfo.getEseeId());
         lanDeviceInfo.setChannelCount(oldNVRResponseInfo.getChannelCount());
         lanDeviceInfo.setModel(oldNVRResponseInfo.getModel());
         lanDeviceInfo.setVersion(oldNVRResponseInfo.getVer());
      }

      return lanDeviceInfo;
   }
}
