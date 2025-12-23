package com.eseeiot.option.pojo;

import java.util.List;

public class JADayLight {
   private List<JADayLight.Data> Data;

   public List<JADayLight.Data> getData() {
      return this.Data;
   }

   public void setData(List<JADayLight.Data> data) {
      this.Data = data;
   }

   public class End {
      private int Month;
      private int Week;
      private int Weekday;
      private int Hour;
      private int Minute;

      public void setMonth(int Month) {
         this.Month = Month;
      }

      public int getMonth() {
         return this.Month;
      }

      public void setWeek(int Week) {
         this.Week = Week;
      }

      public int getWeek() {
         return this.Week;
      }

      public void setWeekday(int Weekday) {
         this.Weekday = Weekday;
      }

      public int getWeekday() {
         return this.Weekday;
      }

      public void setHour(int Hour) {
         this.Hour = Hour;
      }

      public int getHour() {
         return this.Hour;
      }

      public void setMinute(int Minute) {
         this.Minute = Minute;
      }

      public int getMinute() {
         return this.Minute;
      }
   }

   public class Start {
      private int Month;
      private int Week;
      private int Weekday;
      private int Hour;
      private int Minute;

      public void setMonth(int Month) {
         this.Month = Month;
      }

      public int getMonth() {
         return this.Month;
      }

      public void setWeek(int Week) {
         this.Week = Week;
      }

      public int getWeek() {
         return this.Week;
      }

      public void setWeekday(int Weekday) {
         this.Weekday = Weekday;
      }

      public int getWeekday() {
         return this.Weekday;
      }

      public void setHour(int Hour) {
         this.Hour = Hour;
      }

      public int getHour() {
         return this.Hour;
      }

      public void setMinute(int Minute) {
         this.Minute = Minute;
      }

      public int getMinute() {
         return this.Minute;
      }
   }

   public class Daylight {
      private String Country;
      private int Offset;
      private JADayLight.Start Start;
      private JADayLight.End End;

      public void setCountry(String Country) {
         this.Country = Country;
      }

      public String getCountry() {
         return this.Country;
      }

      public void setOffset(int Offset) {
         this.Offset = Offset;
      }

      public int getOffset() {
         return this.Offset;
      }

      public void setStart(JADayLight.Start Start) {
         this.Start = Start;
      }

      public JADayLight.Start getStart() {
         return this.Start;
      }

      public void setEnd(JADayLight.End End) {
         this.End = End;
      }

      public JADayLight.End getEnd() {
         return this.End;
      }
   }

   public class Data {
      private int Year;
      private List<JADayLight.Daylight> Daylight;

      public void setYear(int Year) {
         this.Year = Year;
      }

      public int getYear() {
         return this.Year;
      }

      public void setDaylight(List<JADayLight.Daylight> Daylight) {
         this.Daylight = Daylight;
      }

      public List<JADayLight.Daylight> getDaylight() {
         return this.Daylight;
      }
   }
}
