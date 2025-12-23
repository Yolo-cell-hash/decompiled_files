package com.eseeiot.option;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScheduleHelper {
   public static final int DAY_BIN = 1048320;
   public static final int NIGHT_BIN = 15728895;
   public static final int ALL_DYA_BIN = 16777215;

   public static List<Boolean> intTo24HourData(int value) {
      List<Boolean> list = new ArrayList();
      int temp = value;

      for(int i = 0; i < 24; ++i) {
         list.add((temp & 1) > 0);
         temp >>= 1;
      }

      return list;
   }

   public static int dayToIntValue(List<Boolean> hours) {
      if (hours.size() != 24) {
         throw new IndexOutOfBoundsException("size=" + hours.size() + ", Size 必须为 24 ");
      } else {
         int value = 0;

         for(int i = hours.size() - 1; i >= 0; --i) {
            boolean bol = (Boolean)hours.get(i);
            if (bol) {
               value |= 1;
            }

            if (i != 0) {
               value <<= 1;
            }
         }

         return value;
      }
   }

   public static boolean isWeekDayOnly(List<Integer> weekValue) {
      Iterator var1 = weekValue.iterator();

      Integer value;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         value = (Integer)var1.next();
      } while(value == 1048320);

      return false;
   }

   public static boolean isWeekNightOnly(List<Integer> weekValue) {
      Iterator var1 = weekValue.iterator();

      Integer value;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         value = (Integer)var1.next();
      } while(value == 15728895);

      return false;
   }

   public static boolean isWeekAllDay(List<Integer> weekValue) {
      Iterator var1 = weekValue.iterator();

      Integer value;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         value = (Integer)var1.next();
      } while(value == 16777215);

      return false;
   }

   public static boolean isDayOnly(int value) {
      return value == 1048320;
   }

   public static boolean isNightOny(int value) {
      return value == 15728895;
   }

   public static boolean isAllDay(int value) {
      return value == 16777215;
   }

   public static List<Integer> getWeekDayOnlyValue() {
      List<Integer> list = new ArrayList();

      for(int i = 0; i <= 6; ++i) {
         list.add(1048320);
      }

      return list;
   }

   public static List<Integer> getWeekNightOnlyValue() {
      List<Integer> list = new ArrayList();

      for(int i = 0; i <= 6; ++i) {
         list.add(15728895);
      }

      return list;
   }

   public static List<Integer> getWeekAllDayValue() {
      List<Integer> list = new ArrayList();

      for(int i = 0; i <= 6; ++i) {
         list.add(16777215);
      }

      return list;
   }

   public static String toJson(List<Integer> weekSchedule) {
      return (new Gson()).toJson(weekSchedule);
   }
}
