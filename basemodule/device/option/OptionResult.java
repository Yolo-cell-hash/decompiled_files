package com.eseeiot.basemodule.device.option;

public class OptionResult {
   public static final int OK = 0;
   public static final int FAILED = 1;
   public static final int AUTH_FAILED = 2;
   public static final int API_ERR = 3;
   public static final int TIMEOUT = 4;

   public static enum RecordMode {
      NONE("none"),
      RECORD_WITH_EVENT("event"),
      RECORD_WITH_TIME("time");

      private String name;

      private RecordMode(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public static OptionResult.RecordMode valueOfName(String name) {
         OptionResult.RecordMode[] values = values();

         for(int i = 0; i < values.length; ++i) {
            if (values[i].getName().equals(name)) {
               return values[i];
            }
         }

         return NONE;
      }
   }
}
