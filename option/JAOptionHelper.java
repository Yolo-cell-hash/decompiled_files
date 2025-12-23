package com.eseeiot.option;

import android.text.TextUtils;
import com.eseeiot.basemodule.device.option.ExtOptionGetter;
import com.eseeiot.basemodule.device.option.ExtOptionSetter;
import com.eseeiot.basemodule.device.option.GetOptionSession;
import com.eseeiot.basemodule.device.option.OptionOperationCallback;
import com.eseeiot.basemodule.device.option.Options;
import com.eseeiot.basemodule.device.option.base.BaseOptionOperation;

public class JAOptionHelper implements BaseOptionOperation {
   private static final int DEFAULT_CACHE_LIFE = 60000;
   protected Options mOptions;
   protected ExtOptionGetter mGetter;
   protected ExtOptionSetter mSetter;
   private int mCacheLifeInMillis = 60000;
   private final long[] mTimeInMillisOfPackageReceived = new long[3];

   public JAOptionHelper(Options options) {
      this.mOptions = options;
   }

   public void setCacheTime(int time) {
      this.mCacheLifeInMillis = time;
   }

   public void refreshOptionData(OptionOperationCallback callback) {
      this.requestFirstPackage(this.mCacheLifeInMillis <= 0, callback);
   }

   public void refreshNVROptionData(OptionOperationCallback callback) {
      this.mOptions.newGetSession().usePassword().closeAfterFinish().appendDeviceInfo().appendModeSetting().setTimeout(20).autoConnect(true).appendTFCardManager(true).appendAlexa().appendCapabilitySet().appendChannelInfo().appendSystemOperation(false, true).appendWirelessManager().appendChannelManager().appendOSSCloudSetting().appendFeature().appendPromptSounds().addListener((device, resultCode, errorCode, requestCode) -> {
         if (resultCode == 0) {
            if (callback != null) {
               callback.onSuccess();
            }
         } else if (callback != null) {
            callback.onFail(resultCode);
         }

      }).commit();
   }

   public void refreshNVRChannelOptionData(int channel, OptionOperationCallback callback) {
      this.mOptions.disableMatchExistsGettingObj().newGetSession().closeAfterFinish().usePassword().appendDeviceInfo().appendModeSetting().appendAlarmSetting().appendRecordManager().appendChannelManager(channel).appendChannelInfo().appendChannelStatus().appendWirelessCheck().appendFeature().appendWorkMode().appendCoverSetting().appendChnCapabilitySet().addListener((device, resultCode, errorCode, requestCode) -> {
         if (resultCode == 0) {
            if (callback != null) {
               callback.onSuccess();
            }
         } else if (callback != null) {
            callback.onFail(resultCode);
         }

      }).commit();
   }

   private void requestFirstPackage(boolean forceReq, OptionOperationCallback callback) {
      if (!forceReq && System.currentTimeMillis() - this.mTimeInMillisOfPackageReceived[0] < (long)this.mCacheLifeInMillis) {
         this.requestSecondPackage(forceReq, callback);
      } else {
         this.mOptions.newGetSession().usePassword().closeAfterFinish().appendDeviceInfo().appendModeSetting().appendTFCardManager(true).appendChannelInfo().appendLTE().appendSystemOperation(false).appendCapabilitySet().appendChannelStatus().appendPtzManager().appendAlexa().addListener((device, resultCode, errorCode, requestCode) -> {
            if (resultCode == 0) {
               this.mTimeInMillisOfPackageReceived[0] = System.currentTimeMillis();
               this.requestSecondPackage(forceReq, callback);
            } else if (callback != null) {
               callback.onFail(resultCode);
            }

         }).commit();
      }
   }

   private void requestSecondPackage(boolean forceReq, OptionOperationCallback callback) {
      if (!forceReq && System.currentTimeMillis() - this.mTimeInMillisOfPackageReceived[1] < (long)this.mCacheLifeInMillis) {
         this.requestThirdPackage(forceReq, callback);
      } else {
         this.mOptions.newGetSession().closeAfterFinish().usePassword().appendDeviceInfo().appendModeSetting().appendAlarmSetting().appendFisheyeSetting().appendRecordManager().appendVideoManager().appendCoverSetting().addListener((device, resultCode, errorCode, requestCode) -> {
            if (resultCode == 0) {
               this.mTimeInMillisOfPackageReceived[1] = System.currentTimeMillis();
               this.requestThirdPackage(forceReq, callback);
            } else if (callback != null) {
               callback.onFail(resultCode);
            }

         }).commit();
      }
   }

   private void requestThirdPackage(boolean forceReq, OptionOperationCallback callback) {
      if (!forceReq && System.currentTimeMillis() - this.mTimeInMillisOfPackageReceived[2] < (long)this.mCacheLifeInMillis) {
         if (callback != null) {
            callback.onSuccess();
         }

      } else {
         GetOptionSession session = this.mOptions.newGetSession().closeAfterFinish().usePassword().appendDeviceInfo().appendModeSetting().appendPromptSounds().appendOSSCloudSetting().appendFeature().appendFrequencyMode().appendLteModule().addListener((device, resultCode, errorCode, requestCode) -> {
            if (resultCode == 0) {
               this.mTimeInMillisOfPackageReceived[2] = System.currentTimeMillis();
               if (callback != null) {
                  callback.onSuccess();
               }
            } else if (callback != null) {
               callback.onFail(resultCode);
            }

         });
         String version = this.mOptions.getVersion();
         boolean afterV2 = !TextUtils.isEmpty(version) && "2.0.0".compareTo(version) <= 0;
         if (afterV2) {
            session.appendV2Status().appendV2Alarm().appendV2Record();
         }

         session.commit();
      }
   }

   public void getWifiList(OptionOperationCallback callback) {
      GetOptionSession session = this.mOptions.newGetSession().usePassword().closeAfterFinish().addListener((device, resultCode, errorCode, requestCode) -> {
         if (resultCode == 0) {
            if (callback != null) {
               callback.onSuccess();
            }
         } else if (callback != null) {
            callback.onFail(resultCode);
         }

      }).setTimeout(20).appendWirelessStation();
      session.commit();
   }

   public void getTFCardData(OptionOperationCallback callback) {
      GetOptionSession session = this.mOptions.newGetSession().usePassword().appendTFCardManager(false).holdSession().addListener((device, resultCode, errorCode, requestCode) -> {
         if (resultCode == 0) {
            if (callback != null) {
               callback.onSuccess();
            }
         } else if (callback != null) {
            callback.onFail(resultCode);
         }

      });
      session.commit();
   }

   public void checkNVRUpgradeFirmwareStatus(OptionOperationCallback callback) {
      GetOptionSession session = this.mOptions.newGetSession().usePassword().appendSystemOperation(true).addListener((device, resultCode, errorCode, requestCode) -> {
         if (resultCode == 0) {
            if (callback != null) {
               callback.onSuccess();
            }
         } else if (callback != null) {
            callback.onFail(resultCode);
         }

      });
      session.commit();
   }

   public void getOssCloudSetting(OptionOperationCallback callback) {
      GetOptionSession session = this.mOptions.newGetSession().closeAfterFinish().usePassword().appendDeviceInfo().appendModeSetting().appendOSSCloudSetting().addListener((device, resultCode, errorCode, requestCode) -> {
         if (resultCode == 0) {
            if (callback != null) {
               callback.onSuccess();
            }
         } else if (callback != null) {
            callback.onFail(resultCode);
         }

      });
      session.commit();
   }

   public ExtOptionGetter getter() {
      if (this.mGetter == null) {
         this.mGetter = new JAOptionGetter(this.mOptions);
      }

      return this.mGetter;
   }

   public ExtOptionSetter setter() {
      if (this.mSetter == null) {
         this.mSetter = new JAOptionSetter(this.mOptions);
      }

      return this.mSetter;
   }
}
