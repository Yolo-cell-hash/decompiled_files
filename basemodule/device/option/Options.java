package com.eseeiot.basemodule.device.option;

import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.option.base.OptionGetter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Options extends OptionGetter {
   int PTZ_NONE = 0;
   int PTZ_VERTICAL = 1;
   int PTZ_HORIZONTAL = 2;
   int PTZ_HORIZONTAL_VERTICAL = 3;

   void bindDevice(MonitorDevice var1);

   void setChannel(int var1);

   SetOptionSession newSetSession();

   SetOptionSession restoreSession();

   GetOptionSession newGetSession();

   boolean isGot();

   boolean isGetting();

   int gotTimeFromNowInSec();

   Object optObject(String... var1);

   void clearIRCutModes();

   @Retention(RetentionPolicy.SOURCE)
   public @interface PTZ_CAPABILITY {
   }
}
