package com.eseeiot.option;

import com.eseeiot.basemodule.device.dispatcher.VconEventDispatchEntry;
import com.eseeiot.basemodule.device.option.GetOptionSession;
import com.eseeiot.basemodule.device.option.SetOptionSession;
import com.eseeiot.basemodule.device.option.base.BaseGettingSession;
import com.eseeiot.basemodule.device.option.base.BaseSettingSession;
import com.eseeiot.basemodule.device.option.base.CommonOption;
import com.eseeiot.basemodule.listener.CommandResultListener;
import org.json.JSONException;
import org.json.JSONObject;

public final class JAOption extends CommonOption {
   private static final String TAG = "JAOption";

   public SetOptionSession newSetSession() throws IllegalStateException {
      super.newSetSession();
      return new JAOption.JASettingSession(this);
   }

   public GetOptionSession newGetSession() {
      super.newGetSession();
      return new JAOption.JAGettingSession(this);
   }

   private class JAGettingSession extends BaseGettingSession implements VconEventDispatchEntry {
      private JAGettingSession(CommonOption option) {
         super(option);
      }

      protected void onSessionClosed() {
         JAOption.this.mDevice.cancelVcon(this);
      }

      protected int sendOptionData(String data, int channel) {
         return JAOption.this.mDevice.sendGettingData(data, channel, this, new CommandResultListener() {
            public void onCommandResult(String connectKey, int status, int index) {
               if (status != 0) {
                  JAGettingSession.this.performVconResult(3, status);
               }

            }
         });
      }

      public final void dispatchVconEvent(String message, int channel) {
         if (this.isStarted) {
            Object var3 = null;

            try {
               this.handleResult(message, new JSONObject(message));
            } catch (JSONException var5) {
            }

         }
      }

      public void dispatchVconResendEvent(int magic, int version, int fileType, int packageNo, int endFlag, int reverse, byte[] buffer) {
      }

      // $FF: synthetic method
      JAGettingSession(CommonOption x1, Object x2) {
         this(x1);
      }
   }

   private class JASettingSession extends BaseSettingSession implements VconEventDispatchEntry {
      private JASettingSession(CommonOption option) {
         super(option);
      }

      protected void onSessionClosed() {
         JAOption.this.mDevice.cancelVcon(this);
      }

      protected int sendOptionData(String data, int channel) {
         return JAOption.this.mDevice.sendSettingData(data, channel, this, new CommandResultListener() {
            public void onCommandResult(String connectKey, int status, int index) {
               if (status != 0) {
                  JASettingSession.this.performVconResult(3, status);
               }

            }
         });
      }

      public final void dispatchVconEvent(String message, int channel) {
         if (this.isStarted) {
            Object var3 = null;

            try {
               this.handleResult(message, new JSONObject(message));
            } catch (JSONException var5) {
            }

         }
      }

      public void dispatchVconResendEvent(int magic, int version, int fileType, int packageNo, int endFlag, int reverse, byte[] buffer) {
      }

      // $FF: synthetic method
      JASettingSession(CommonOption x1, Object x2) {
         this(x1);
      }
   }
}
