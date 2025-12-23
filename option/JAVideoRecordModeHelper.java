package com.eseeiot.option;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.option.Options;
import com.eseeiot.option.pojo.WorkMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class JAVideoRecordModeHelper {
   private MonitorDevice mDeviceWrapper;
   private Options mDeviceOption;
   private boolean mSupportUltraLowPower;
   private boolean mSupportLowPower;
   private boolean mSupportNormal;
   private boolean mSupportAuto;
   private boolean mSupportDuration;
   private List<String> mItems;
   private List<Integer> mDuration;
   private Integer mCurrentDuration;

   public JAVideoRecordModeHelper(@NonNull MonitorDevice wrapper) {
      this.mDeviceWrapper = wrapper;
      this.mDeviceOption = this.mDeviceWrapper.getOptions();
   }

   public void configWorkMore() {
      if (this.mItems == null) {
         this.mItems = new ArrayList();
         this.configWorkModeSupport();
         this.configUltraLowPowerMode(this.mItems);
         this.configLowPowerMode(this.mItems);
         this.configNormalAndAutoMode(this.mItems);
      }
   }

   public String getCurrentWorkModeDesc() {
      if (this.mItems != null) {
         String workMode = this.mDeviceOption.getWorkMode(false);
         Iterator var2 = this.mItems.iterator();

         while(var2.hasNext()) {
            String item = (String)var2.next();
            if (workMode.equals(item)) {
               return item;
            }
         }
      }

      return null;
   }

   public List<String> getRecordModeItems() {
      return this.mItems;
   }

   public boolean supportRecordDuration() {
      return this.mSupportDuration;
   }

   public Integer getCurrentRecordDuration() {
      return this.mCurrentDuration;
   }

   public List<Integer> getRecordDurationList() {
      return this.mDuration;
   }

   private void configWorkModeSupport() {
      if (this.noWorkModeCapabilities()) {
         this.mSupportUltraLowPower = true;
         this.mSupportLowPower = true;
         this.mSupportNormal = true;
         this.mSupportAuto = false;
         if (this.mDeviceWrapper.getChannelCount() == 1) {
            this.mSupportUltraLowPower = false;
         }
      } else {
         this.mSupportUltraLowPower = isSupportWorkMode(this.mDeviceWrapper, WorkMode.BEST_POWER.getOptionName());
         this.mSupportLowPower = isSupportWorkMode(this.mDeviceWrapper, WorkMode.BEST_WORKING_VIDEO.getOptionName());
         this.mSupportNormal = isSupportWorkMode(this.mDeviceWrapper, WorkMode.ALWAYS_WORKING.getOptionName());
         this.mSupportAuto = isSupportWorkMode(this.mDeviceWrapper, WorkMode.ADAPTIVE_MODE.getOptionName());
      }

   }

   private void configUltraLowPowerMode(List<String> items) {
      if (this.mSupportUltraLowPower) {
         items.add(WorkMode.BEST_POWER.getOptionName());
         int channelCount = this.mDeviceWrapper.getChannelCount();
         Integer duration = channelCount > 1 ? this.mDeviceOption.getPIRPushTime(false) : this.mDeviceOption.getMotionRecordDuration(false);
         if (duration != null) {
            this.mSupportDuration = true;
            this.mCurrentDuration = duration;
            List<Integer> list = new ArrayList();
            list.add(5);
            list.add(10);
            list.add(20);
            list.add(30);
            this.mDuration = list;
         }
      }
   }

   private void configLowPowerMode(List<String> items) {
      if (this.mSupportLowPower) {
         items.add(WorkMode.BEST_WORKING_VIDEO.getOptionName());
      }
   }

   private void configNormalAndAutoMode(List<String> items) {
      if (this.mSupportAuto) {
         if (!this.needTransformNormalWorkMode()) {
            items.add(WorkMode.ALWAYS_WORKING.getOptionName());
         }

         items.add(WorkMode.ADAPTIVE_MODE.getOptionName());
      } else if (this.mSupportNormal) {
         items.add(WorkMode.ALWAYS_WORKING.getOptionName());
      }

   }

   private boolean noWorkModeCapabilities() {
      List<String> workModes = this.mDeviceOption.getWorkModes();
      return workModes == null || workModes.isEmpty();
   }

   private boolean needTransformNormalWorkMode() {
      return this.mSupportUltraLowPower && this.mSupportLowPower && this.mSupportAuto && !this.mSupportNormal && this.mDeviceWrapper.getChannelCount() == 1;
   }

   @SuppressLint({"StringFormatInvalid"})
   private String getAdaptive2NormalModelDetailDescription(Context context) {
      Integer min = null;
      Integer max = null;
      if (this.mDeviceOption != null) {
         min = this.mDeviceOption.getBatteryDevMinPowerLimit();
         max = this.mDeviceOption.getBatterDevMaxPowerLimit();
      }

      return min != null && min > 0 && max != null && max > 0 ? "设备一直持续录像，当电量降至%1$s以下时自动切换为最佳录像，当电量提升至%2$s以上时恢复一直录像，该模式耗电较快，适合外接电源使用。" : WorkMode.ADAPTIVE_MODE.getDescription(context);
   }

   public static String getCurrentWorkModeDesc(Context context, MonitorDevice wrapper) {
      String workModeText = null;
      Options options = wrapper.getOptions();
      String workMode = options.getWorkMode(false);
      if (!TextUtils.isEmpty(workMode)) {
         if (WorkMode.ALWAYS_WORKING.getOptionName().equals(workMode)) {
            workModeText = WorkMode.ALWAYS_WORKING.getOptionName();
         }

         if (WorkMode.BEST_WORKING_VIDEO.getOptionName().equals(workMode)) {
            workModeText = WorkMode.BEST_WORKING_VIDEO.getOptionName();
         }

         if (WorkMode.BEST_POWER.getOptionName().equals(workMode)) {
            workModeText = WorkMode.BEST_POWER.getOptionName();
         }

         if (WorkMode.ADAPTIVE_MODE.getOptionName().equals(workMode)) {
            workModeText = isSupportWorkMode(wrapper, WorkMode.ALWAYS_WORKING.getOptionName()) ? WorkMode.ADAPTIVE_MODE.getOptionName() : WorkMode.ALWAYS_WORKING.getOptionName();
         }
      } else {
         Boolean isTimeRecordEnabled = options.isTimeRecordEnabled(false);
         Boolean isMotionRecordEnabled = options.isMotionRecordEnabled(false);
         if (isTimeRecordEnabled != null || isMotionRecordEnabled != null) {
            if (Objects.equals(isTimeRecordEnabled, true)) {
               return WorkMode.ALWAYS_WORKING.getOptionName();
            }

            if (Objects.equals(isMotionRecordEnabled, true)) {
               return WorkMode.EVENT_MODE.getOptionName();
            }
         }
      }

      return workModeText;
   }

   private static boolean isSupportWorkMode(MonitorDevice wrapper, @NonNull String workMode) {
      if (WorkMode.BEST_POWER.getOptionName().equals(workMode)) {
         if (wrapper.getChannelCount() > 1) {
            return true;
         }
      } else if (WorkMode.ADAPTIVE_MODE.getOptionName().equals(workMode) && wrapper.getChannelCount() > 1) {
         return false;
      }

      Options options = wrapper.getOptions();
      List<String> workModes = options.getWorkModes();
      return workModes != null && !workModes.isEmpty() ? workModes.contains(workMode) : false;
   }
}
