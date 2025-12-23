package com.eseeiot.option;

import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.option.Options;
import java.util.List;

public class JALightHelper {
   public static final int MODE_NONE = 0;
   public static final int MODE_SINGLE_LIGHT = 1;
   public static final int MODE_OLD_GW_SINGLE_LIGHT = 2;
   public static final int MODE_NIGHT_LED = 3;
   public static final int MODE_DOUBLE_LIGHT = 4;
   public static final String MODE_LIGHT_IR = "ir";
   public static final String MODE_LIGHT_LIGHT = "light";
   public static final String MODE_LIGHT_SMART = "smart";
   public static final String MODE_LIGHT_AUTO = "auto";
   public static final String MODE_LIGHT_NIGHT = "night";
   public static final String MODE_LIGHT_DAY_LIGHT = "daylight";
   private MonitorDevice mDevice;

   public JALightHelper(MonitorDevice device) {
      this.mDevice = device;
   }

   public int getLightMode(int channel) {
      Options deviceOption = this.mDevice.getOptions();
      Boolean irControl = deviceOption.isChannelSupportIrControlV2(this.mDevice.getChannelCount() == 1 ? -1 : channel);
      Integer lightCtrl = deviceOption.getChannelLightControlV2(this.mDevice.getChannelCount() == 1 ? -1 : channel);
      List<String> irCutModeList = deviceOption.getIRCutModes();
      String irCutMode = deviceOption.getIRCutMode(false);
      if (irControl != null && !irControl) {
         return 0;
      } else if (lightCtrl != null && lightCtrl != 2) {
         if (irCutMode == null) {
            return 0;
         } else if (this.mDevice.getChannelCount() != 1 && lightCtrl == 0 && !deviceOption.isSupportChannelV2()) {
            return 0;
         } else if (irCutModeList != null && irCutModeList.size() > 0) {
            if (irCutModeList.size() == 2 && irCutModeList.contains("ir") && irCutModeList.contains("light")) {
               return 3;
            } else if (irCutModeList.size() > 3 && !irCutModeList.contains("smart") && this.mDevice.getChannelCount() != 1 && !deviceOption.isSupportChannelV2()) {
               return 3;
            } else if (irCutModeList.size() == 3 && irCutModeList.contains("ir") && irCutModeList.contains("daylight") && irCutModeList.contains("night")) {
               return 2;
            } else {
               return !irCutModeList.contains("smart") && !"ir".equals(irCutMode) && !"smart".equals(irCutMode) && !"light".equals(irCutMode) ? 1 : 4;
            }
         } else {
            return lightCtrl != 1 && !irCutMode.equals("ir") && !irCutMode.equals("smart") && !irCutMode.equals("light") ? 1 : 4;
         }
      } else {
         return irCutMode != null ? 1 : 0;
      }
   }
}
