package com.eseeiot.option;

import com.eseeiot.option.pojo.JADayLight;
import java.util.Iterator;
import java.util.List;

public class DaylightSavingTimeUtil {
   public static final int DST_Germany = 101;
   public static final int DST_Netherlands = 102;
   public static final int DST_Poland = 103;
   public static final int DST_Iran = 104;
   public static final int DST_Israel = 105;
   public static final int DST_Greenland = 106;
   public static final int DST_Washington = 107;
   public static final int DST_Canberra = 108;
   private static JADayLight sJADayLight;

   public static void setDayLightInfo(JADayLight jaDayLight) {
      sJADayLight = jaDayLight;
   }

   public static JADayLight getDayLightInfo() {
      return sJADayLight;
   }

   public static String getString(int dstCountry, int year) {
      String country = null;
      int startMonth = 3;
      int startWeek = 5;
      int startWeekDay = 0;
      int startHour = 2;
      int endMonth = 10;
      int endWeek = 5;
      int endWeekDay = 0;
      int endHour = 3;
      switch(dstCountry) {
      case 101:
         country = "Germany";
         break;
      case 102:
         country = "Netherlands";
         break;
      case 103:
         country = "Poland";
         break;
      case 104:
         country = "Iran";
         break;
      case 105:
         country = "Israel";
         break;
      case 106:
         country = "Greenland";
         break;
      case 107:
         country = "Washington";
         break;
      case 108:
         country = "Canberra";
      }

      JADayLight jaDayLight = getDayLightInfo();
      if (jaDayLight != null) {
         List<JADayLight.Data> mData = jaDayLight.getData();
         Iterator var13 = mData.iterator();

         while(true) {
            JADayLight.Data data;
            do {
               if (!var13.hasNext()) {
                  return "{\n\"Enabled\":true,\n\"Country\":" + country + ",\n\"Offset\":60,\n\"Week\":[{\n           \"Type\":\"start\",\n           \"Month\":" + startMonth + ",\n           \"Week\":" + startWeek + ",\n           \"Weekday\":" + startWeekDay + ",\n           \"Hour\":" + startHour + ",\n           \"Minute\":0},\n          {\n           \"Type\":\"end\",\n           \"Month\":" + endMonth + ",\n           \"Week\":" + endWeek + ",\n           \"Weekday\":" + endWeekDay + ",\n           \"Hour\":" + endHour + ",\n           \"Minute\":0}\n       ]\n}";
               }

               data = (JADayLight.Data)var13.next();
            } while(data.getYear() != year);

            Iterator var15 = data.getDaylight().iterator();

            while(var15.hasNext()) {
               JADayLight.Daylight dayLight = (JADayLight.Daylight)var15.next();
               if (dayLight.getCountry().equals(country)) {
                  startMonth = dayLight.getStart().getMonth();
                  startWeek = dayLight.getStart().getWeek();
                  startWeekDay = dayLight.getStart().getWeekday();
                  startHour = dayLight.getStart().getHour();
                  endMonth = dayLight.getEnd().getMonth();
                  endWeek = dayLight.getEnd().getWeek();
                  endWeekDay = dayLight.getEnd().getWeekday();
                  endHour = dayLight.getEnd().getHour();
               }
            }
         }
      } else {
         return "{\n\"Enabled\":true,\n\"Country\":" + country + ",\n\"Offset\":60,\n\"Week\":[{\n           \"Type\":\"start\",\n           \"Month\":" + startMonth + ",\n           \"Week\":" + startWeek + ",\n           \"Weekday\":" + startWeekDay + ",\n           \"Hour\":" + startHour + ",\n           \"Minute\":0},\n          {\n           \"Type\":\"end\",\n           \"Month\":" + endMonth + ",\n           \"Week\":" + endWeek + ",\n           \"Weekday\":" + endWeekDay + ",\n           \"Hour\":" + endHour + ",\n           \"Minute\":0}\n       ]\n}";
      }
   }

   public static final String getDefault() {
      return "{\n\"Enabled\":true,\n\"Country\":\"default\",\n\"Offset\":60,\n\"Week\":[{\n           \"Type\":\"start\",\n           \"Month\":3,\n           \"Week\":5,\n           \"Weekday\":0,\n           \"Hour\":2,\n           \"Minute\":0},\n          {\n           \"Type\":\"end\",\n           \"Month\":10,\n           \"Week\":5,\n           \"Weekday\":0,\n           \"Hour\":3,\n           \"Minute\":0}\n       ]\n}";
   }

   public static final String getGermany() {
      return "{\n\"Enabled\":true,\n\"Country\":\"Germany\",\n\"Offset\":60,\n\"Week\":[{\n           \"Type\":\"start\",\n           \"Month\":3,\n           \"Week\":5,\n           \"Weekday\":0,\n           \"Hour\":2,\n           \"Minute\":0},\n          {\n           \"Type\":\"end\",\n           \"Month\":10,\n           \"Week\":5,\n           \"Weekday\":0,\n           \"Hour\":3,\n           \"Minute\":0}\n       ]\n}";
   }

   public static final String getNetherlands() {
      return "{\n\"Enabled\":true,\n\"Country\":\"Netherlands\",\n\"Offset\":60,\n\"Week\":[{\n           \"Type\":\"start\",\n           \"Month\":3,\n           \"Week\":5,\n           \"Weekday\":0,\n           \"Hour\":2,\n           \"Minute\":0},\n          {\n           \"Type\":\"end\",\n           \"Month\":10,\n           \"Week\":5,\n           \"Weekday\":0,\n           \"Hour\":3,\n           \"Minute\":0}\n       ]\n}";
   }

   public static final String getPoland() {
      return "{\n\"Enabled\":true,\n\"Country\":\"Poland\",\n\"Offset\":60,\n\"Week\":[{\n           \"Type\":\"start\",\n           \"Month\":3,\n           \"Week\":5,\n           \"Weekday\":0,\n           \"Hour\":2,\n           \"Minute\":0},\n          {\n           \"Type\":\"end\",\n           \"Month\":10,\n           \"Week\":5,\n           \"Weekday\":0,\n           \"Hour\":3,\n           \"Minute\":0}\n       ]\n}";
   }

   public static final String getIran() {
      return "{\n\"Enabled\":true,\n\"Country\":\"Iran\",\n\"Offset\":60,\n\"Week\":[{\n           \"Type\":\"start\",\n           \"Month\":3,\n           \"Week\":4,\n           \"Weekday\":4,\n           \"Hour\":0,\n           \"Minute\":0},\n          {\n           \"Type\":\"end\",\n           \"Month\":9,\n           \"Week\":4,\n           \"Weekday\":6,\n           \"Hour\":0,\n           \"Minute\":0}\n       ]\n}";
   }

   public static final String getIsrael() {
      return "{\n\"Enabled\":true,\n\"Country\":\"Israel\",\n\"Offset\":60,\n\"Week\":[{\n           \"Type\":\"start\",\n           \"Month\":3,\n           \"Week\":4,\n           \"Weekday\":5,\n           \"Hour\":2,\n           \"Minute\":0},\n          {\n           \"Type\":\"end\",\n           \"Month\":10,\n           \"Week\":4,\n           \"Weekday\":0,\n           \"Hour\":2,\n           \"Minute\":0}\n       ]\n}";
   }

   public static final String getGreenland() {
      return "{\n\"Enabled\":true,\n\"Country\":\"Greenland\",\n\"Offset\":60,\n\"Week\":[{\n           \"Type\":\"start\",\n           \"Month\":11,\n           \"Week\":1,\n           \"Weekday\":0,\n           \"Hour\":0,\n           \"Minute\":0},\n          {\n           \"Type\":\"end\",\n           \"Month\":2,\n           \"Week\":3,\n           \"Weekday\":0,\n           \"Hour\":0,\n           \"Minute\":0}\n       ]\n}";
   }
}
