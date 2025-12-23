package com.eseeiot.basemodule.device.option.base;

import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.dispatcher.DeviceEventCallback;
import com.eseeiot.basemodule.device.option.OptionSessionCallback;
import com.eseeiot.basemodule.util.TimeoutManager;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseOptionSession {
   private static final String TAG = "BaseSessionV21";
   protected CommonOption mOption;
   protected boolean isStarted = false;
   protected boolean isClosed = false;
   protected boolean toBeClosed = true;
   protected int requestCode;
   protected OptionSessionCallback callback;
   protected String version = "1.0.0";
   protected boolean get;
   protected boolean useVerify = false;
   protected boolean mAutoConnect;
   private int timeoutTag = -1;
   protected int timeoutMs = 16000;
   protected boolean mModeSettingWithIRCutMode;
   protected boolean mSkipMatchExistsGettingField;
   private DeviceEventCallback mEventCallback;

   protected BaseOptionSession(@NonNull CommonOption option) {
      this.mOption = option;
   }

   protected final void closeSession() {
      if (!this.isClosed) {
         this.isClosed = true;
         this.onSessionClosed();
         TimeoutManager.getInstance().removeTask(this.timeoutTag);
         if (this.mEventCallback != null && this.mOption.mDevice != null) {
            this.mOption.mDevice.unregisterEventCallback(this.mEventCallback);
            this.mEventCallback = null;
         }
      }

   }

   protected void onSessionClosed() {
   }

   protected final void timeout(int timeoutMs) {
      if (timeoutMs > 2000 && timeoutMs < 60000) {
         this.timeoutMs = timeoutMs;
      }

   }

   private void startTimer() {
      this.isStarted = true;
      TimeoutManager tm = TimeoutManager.getInstance();
      if (this.timeoutTag == -1) {
         this.timeoutTag = tm.addTask(this.timeoutMs, new TimeoutManager.TimeoutCallback() {
            public void onTimeout(int tag) {
               Log.d("BaseSessionV21", "[onTimeout] " + BaseOptionSession.this.mOption.mDevice.getConnectKey());
               BaseOptionSession.this.performVconResult(4, 0);
            }
         });
      }

      if (this.timeoutTag >= 0) {
         tm.doTask(this.timeoutTag);
      }

   }

   protected final int performCommit() {
      if (this.isClosed) {
         return -5007;
      } else if (this.isStarted) {
         return -5006;
      } else {
         final String optionJSON = null;

         try {
            optionJSON = this.createOptionJSON();
         } catch (JSONException var5) {
            var5.printStackTrace();
         } catch (IllegalArgumentException var6) {
            Log.w("BaseSessionV21", "performCommit: " + var6);
            return -5008;
         }

         if (TextUtils.isEmpty(optionJSON)) {
            return -5003;
         } else {
            int ret = 0;
            boolean send = true;
            if (this.mAutoConnect && this.mOption.mDevice != null && !this.mOption.mDevice.isConnected(this.mOption.mChannel)) {
               send = false;
               if (this.mEventCallback == null) {
                  this.mEventCallback = new DeviceEventCallback() {
                     public int onRegisterParamGet() {
                        return 1;
                     }

                     public void onConnectChanged(MonitorDevice device, int status, int channel) {
                        super.onConnectChanged(device, status, channel);
                        boolean shouldUnregister = false;
                        switch(status) {
                        case 1:
                        case 6:
                           int ret = BaseOptionSession.this.sendOptionData(optionJSON, BaseOptionSession.this.mOption.mChannel);
                           if (ret == 0) {
                              BaseOptionSession.this.startTimer();
                           } else {
                              BaseOptionSession.this.performVconResult(3, ret);
                           }

                           shouldUnregister = true;
                           break;
                        case 2:
                        case 9:
                        case 11:
                        case 17:
                           BaseOptionSession.this.performVconResult(1, 0);
                           shouldUnregister = true;
                        case 3:
                        case 4:
                        case 5:
                        case 7:
                        case 8:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        default:
                           break;
                        case 10:
                           BaseOptionSession.this.performVconResult(2, 0);
                           shouldUnregister = true;
                        }

                        if (shouldUnregister) {
                           BaseOptionSession.this.mOption.mDevice.unregisterEventCallback(this);
                           BaseOptionSession.this.mEventCallback = null;
                        }

                     }
                  };
               }

               this.mOption.mDevice.registerEventCallback(this.mEventCallback);
               if (!this.mOption.mDevice.isConnecting(this.mOption.mChannel)) {
                  this.mOption.mDevice.connect(this.mOption.mChannel);
               }
            }

            if (send) {
               ret = this.sendOptionData(optionJSON, this.mOption.mChannel);
               if (ret == 0) {
                  this.startTimer();
               }
            }

            return ret;
         }
      }
   }

   protected abstract int sendOptionData(String var1, int var2);

   private String createOptionJSON() throws JSONException, IllegalArgumentException {
      JSONObject optionJSON = new JSONObject();
      this.createSubJSON(optionJSON);
      String verify = null;
      if (this.useVerify) {
         verify = this.mOption.mDevice.getVerify(true);
      }

      if (verify == null) {
         verify = "";
      }

      JSONObject authorizationJSON = new JSONObject();
      authorizationJSON.put("Verify", verify);
      authorizationJSON.put("username", this.useVerify ? "" : this.mOption.mDevice.getProperty().user);
      authorizationJSON.put("password", this.useVerify ? "" : this.mOption.mDevice.getProperty().password);
      optionJSON.put("Version", this.version);
      optionJSON.put("Method", this.get ? "get" : "set");
      optionJSON.put("Authorization", authorizationJSON);
      return optionJSON.toString();
   }

   protected abstract void createSubJSON(JSONObject var1) throws JSONException, IllegalArgumentException;

   protected abstract void handleResult(String var1, JSONObject var2);

   protected final void performVconResult(int resultCode, int errorCode) {
      this.isStarted = false;
      this.mOption.mGettingSessions.remove(this);
      if (this.toBeClosed) {
         this.closeSession();
      } else {
         TimeoutManager.getInstance().cancelTask(this.timeoutTag);
      }

      if (this.callback != null) {
         this.callback.onSessionListener(this.mOption.mDevice, resultCode, errorCode, this.requestCode);
      }

   }
}
