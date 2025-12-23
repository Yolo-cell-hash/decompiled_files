package com.eseeiot.option.pojo;

import android.content.Context;
import androidx.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;

public enum WorkMode {
   ALWAYS_WORKING(2, "normalMode", -1, -1),
   BEST_WORKING_VIDEO(1, "lowPowerMode", -1, -1),
   BEST_POWER(0, "ultraLowPowerMode", -1, -1),
   ADAPTIVE_MODE(3, "autoLongAliveMode", -1, -1),
   ADAPTIVE_TO_ALWAYS_MODE(-1, "autoLongAliveMode", -1, -1),
   EVENT_MODE(4, "eventMode", -1, -1);

   private final String optionName;
   private final int mode;
   private final int name;
   private final int desc;

   private WorkMode(int mode, String optionName, int name, int description) {
      this.mode = mode;
      this.optionName = optionName;
      this.name = name;
      this.desc = description;
   }

   public String getDescription(Context context) {
      return context.getResources().getString(this.desc);
   }

   public String getOptionName() {
      return this.optionName;
   }

   public String getName(Context context) {
      return context.getResources().getString(this.name);
   }

   public int getModeCode() {
      return this.mode;
   }

   @Nullable
   public static WorkMode modeOf(int mode) {
      Iterator var1 = EnumSet.allOf(WorkMode.class).iterator();

      WorkMode workMode;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         workMode = (WorkMode)var1.next();
      } while(mode != workMode.mode);

      return workMode;
   }

   @Nullable
   public static WorkMode nameOf(int name) {
      Iterator var1 = EnumSet.allOf(WorkMode.class).iterator();

      WorkMode workMode;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         workMode = (WorkMode)var1.next();
      } while(name != workMode.mode);

      return workMode;
   }
}
