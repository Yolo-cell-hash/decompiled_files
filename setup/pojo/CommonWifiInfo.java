package com.eseeiot.setup.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class CommonWifiInfo implements Parcelable {
   private String SSID;
   private String BSSID;
   private String capabilities;
   private String password;
   public static final Creator<CommonWifiInfo> CREATOR = new Creator<CommonWifiInfo>() {
      public CommonWifiInfo createFromParcel(Parcel source) {
         return new CommonWifiInfo(source);
      }

      public CommonWifiInfo[] newArray(int size) {
         return new CommonWifiInfo[size];
      }
   };

   public String getSSID() {
      return this.SSID;
   }

   public void setSSID(String SSID) {
      this.SSID = SSID;
   }

   public String getBSSID() {
      return this.BSSID;
   }

   public void setBSSID(String BSSID) {
      this.BSSID = BSSID;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getCapabilities() {
      return this.capabilities;
   }

   public void setCapabilities(String capabilities) {
      this.capabilities = capabilities;
   }

   public String toString() {
      return "CommonWifiInfo{SSID='" + this.SSID + '\'' + ", BSSID='" + this.BSSID + '\'' + ", capabilities='" + this.capabilities + '\'' + ", password='" + this.password + '\'' + '}';
   }

   public int describeContents() {
      return 0;
   }

   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.SSID);
      dest.writeString(this.BSSID);
      dest.writeString(this.capabilities);
      dest.writeString(this.password);
   }

   public void readFromParcel(Parcel source) {
      this.SSID = source.readString();
      this.BSSID = source.readString();
      this.capabilities = source.readString();
      this.password = source.readString();
   }

   public CommonWifiInfo() {
   }

   protected CommonWifiInfo(Parcel in) {
      this.SSID = in.readString();
      this.BSSID = in.readString();
      this.capabilities = in.readString();
      this.password = in.readString();
   }
}
