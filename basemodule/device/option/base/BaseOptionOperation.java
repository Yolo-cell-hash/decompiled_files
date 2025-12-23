package com.eseeiot.basemodule.device.option.base;

import com.eseeiot.basemodule.device.option.ExtOptionGetter;
import com.eseeiot.basemodule.device.option.ExtOptionSetter;
import com.eseeiot.basemodule.device.option.OptionOperationCallback;

public interface BaseOptionOperation {
   void setCacheTime(int var1);

   void refreshOptionData(OptionOperationCallback var1);

   void refreshNVROptionData(OptionOperationCallback var1);

   void refreshNVRChannelOptionData(int var1, OptionOperationCallback var2);

   void getWifiList(OptionOperationCallback var1);

   void getTFCardData(OptionOperationCallback var1);

   void getOssCloudSetting(OptionOperationCallback var1);

   void checkNVRUpgradeFirmwareStatus(OptionOperationCallback var1);

   ExtOptionGetter getter();

   ExtOptionSetter setter();
}
