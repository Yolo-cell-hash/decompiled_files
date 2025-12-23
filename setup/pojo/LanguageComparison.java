package com.eseeiot.setup.pojo;

import java.util.Objects;

public class LanguageComparison {
   private String country;
   private String abbreviation;
   private String remark;

   public String getCountry() {
      return this.country;
   }

   public void setCountry(String country) {
      this.country = country;
   }

   public String getAbbreviation() {
      return this.abbreviation;
   }

   public void setAbbreviation(String abbreviation) {
      this.abbreviation = abbreviation;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setRemark(String remark) {
      this.remark = remark;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         LanguageComparison that = (LanguageComparison)o;
         return Objects.equals(this.country, that.country) && Objects.equals(this.abbreviation, that.abbreviation) && Objects.equals(this.remark, that.remark);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.country, this.abbreviation, this.remark});
   }

   public String toString() {
      return "LanguageComparison{country='" + this.country + '\'' + ", abbreviation='" + this.abbreviation + '\'' + ", remark='" + this.remark + '\'' + '}';
   }
}
