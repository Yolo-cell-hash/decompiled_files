package com.eseeiot.basemodule.device.option;

public interface GetOptionSession {
   void close();

   int commit();

   GetOptionSession closeAfterFinish();

   GetOptionSession holdSession();

   GetOptionSession useVerify();

   GetOptionSession usePassword();

   GetOptionSession setVersion(String var1);

   GetOptionSession setTimeout(int var1);

   GetOptionSession addListener(OptionSessionCallback var1);

   GetOptionSession appendCapabilitySet();

   GetOptionSession appendChnCapabilitySet();

   GetOptionSession appendChnCapabilitySetV2Req(int... var1);

   GetOptionSession appendDeviceInfo();

   GetOptionSession appendModeSetting();

   GetOptionSession appendModeSettingWithIRCutMode();

   GetOptionSession appendAlarmSetting();

   GetOptionSession appendSystemOperation(boolean var1);

   GetOptionSession appendSystemOperation(boolean var1, boolean var2);

   GetOptionSession appendPromptSounds();

   GetOptionSession appendChannelManager(int... var1);

   GetOptionSession testChannelManager(int var1);

   GetOptionSession appendChannelInfo();

   GetOptionSession appendChannelStatus();

   GetOptionSession appendLedPwm();

   GetOptionSession appendRecord();

   GetOptionSession appendTFCardManager(boolean var1);

   GetOptionSession appendRecordManager();

   GetOptionSession appendFisheyeSetting();

   GetOptionSession appendOSSCloudSetting();

   GetOptionSession appendWirelessManager();

   GetOptionSession appendWirelessCheck();

   GetOptionSession appendWirelessStation();

   GetOptionSession appendWirelessStationWithoutAps();

   GetOptionSession appendPtzManager();

   GetOptionSession appendVideoManager();

   GetOptionSession appendCoverSetting();

   GetOptionSession appendWorkMode();

   GetOptionSession appendLTE();

   GetOptionSession appendLteModule();

   GetOptionSession appendAlexa();

   GetOptionSession appendFeature();

   GetOptionSession appendRecordInfo(int var1, int var2, int var3);

   GetOptionSession appendFrequencyMode();

   GetOptionSession appendOSDTextSetting();

   GetOptionSession appendNetworkMode();

   GetOptionSession addChannelOperation(int var1);

   GetOptionSession abortAddChannelOperation();

   GetOptionSession autoConnect(boolean var1);

   GetOptionSession appendV2Status();

   GetOptionSession appendV2RLightManCtr();

   GetOptionSession appendV2RSoundManCtrl();

   GetOptionSession appendV2Alarm();

   GetOptionSession appendV2Record();

   GetOptionSession appendV2LensCtrl(boolean var1);

   GetOptionSession appendV2LensLinkage();
}
