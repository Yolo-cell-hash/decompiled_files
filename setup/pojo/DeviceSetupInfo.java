package com.eseeiot.setup.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class DeviceSetupInfo implements Parcelable {
   private VConInfo vconInfo;
   private boolean mIsPreConnected;
   private String corseeVersion;
   protected String eseeId;
   private String sDeviceId;
   private String serialId;
   protected int channelCount;
   protected int deviceType;
   protected String devicePassword;
   protected String deviceUser;
   private CommonWifiInfo userWifi;
   private boolean isSSIDNeedEncrypt;
   public static final Creator<DeviceSetupInfo> CREATOR = new Creator<DeviceSetupInfo>() {
      public DeviceSetupInfo createFromParcel(Parcel source) {
         return new DeviceSetupInfo(source);
      }

      public DeviceSetupInfo[] newArray(int size) {
         return new DeviceSetupInfo[size];
      }
   };

   public DeviceSetupInfo() {
      this.devicePassword = "";
      this.deviceUser = "admin";
      this.channelCount = 1;
   }

   public boolean isPreConnected() {
      return this.mIsPreConnected;
   }

   public void setPreConnected(boolean preConnected) {
      this.mIsPreConnected = preConnected;
   }

   public String getCorseeVersion() {
      return this.corseeVersion;
   }

   public void setCorseeVersion(String corseeVersion) {
      this.corseeVersion = corseeVersion;
   }

   public String getEseeId() {
      return this.eseeId;
   }

   public void setEseeId(String eseeId) {
      this.eseeId = eseeId;
   }

   public String getDeviceId() {
      return this.sDeviceId;
   }

   public void setDeviceId(String deviceId) {
      this.sDeviceId = deviceId;
   }

   public String getDeviceUser() {
      return this.deviceUser;
   }

   public void setDeviceUser(String deviceUser) {
      this.deviceUser = deviceUser;
   }

   public void clearDeviceUser() {
      this.deviceUser = null;
   }

   public String getDevicePassword() {
      return this.devicePassword;
   }

   public void setDevicePassword(String devicePassword) {
      if (devicePassword != null) {
         this.devicePassword = devicePassword;
      }

   }

   public void clearDevicePassword() {
      this.devicePassword = null;
   }

   public CommonWifiInfo getUserWifi() {
      if (this.userWifi == null) {
         this.userWifi = new CommonWifiInfo();
      }

      return this.userWifi;
   }

   public VConInfo getVconInfo() {
      return this.vconInfo;
   }

   public void setVconInfo(VConInfo vconInfo) {
      this.vconInfo = vconInfo;
   }

   public String getSerialId() {
      return this.serialId;
   }

   public void setSerialId(String serialId) {
      this.serialId = serialId;
   }

   public int getChannelCount() {
      return this.channelCount;
   }

   public void setChannelCount(int channelCount) {
      this.channelCount = channelCount;
   }

   public int getDeviceType() {
      return this.deviceType;
   }

   public void setDeviceType(int deviceType) {
      this.deviceType = deviceType;
   }

   public boolean isSSIDNeedEncrypt() {
      return this.isSSIDNeedEncrypt;
   }

   public void setSSIDNeedEncrypt(boolean SSIDNeedEncrypt) {
      this.isSSIDNeedEncrypt = SSIDNeedEncrypt;
   }

   public String toString() {
      return "DeviceSetupInfo{vconInfo=" + this.vconInfo + ", mIsPreConnected=" + this.mIsPreConnected + ", corseeVersion='" + this.corseeVersion + '\'' + ", eseeId='" + this.eseeId + '\'' + ", sDeviceId='" + this.sDeviceId + '\'' + ", serialId='" + this.serialId + '\'' + ", channelCount=" + this.channelCount + ", deviceType=" + this.deviceType + ", devicePassword='" + this.devicePassword + '\'' + ", deviceUser='" + this.deviceUser + '\'' + ", userWifi=" + this.userWifi + ", isSSIDNeedEncrypt=" + this.isSSIDNeedEncrypt + '}';
   }

   public int describeContents() {
      return 0;
   }

   public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(this.vconInfo, flags);
      dest.writeByte((byte)(this.mIsPreConnected ? 1 : 0));
      dest.writeString(this.corseeVersion);
      dest.writeString(this.eseeId);
      dest.writeString(this.sDeviceId);
      dest.writeString(this.serialId);
      dest.writeInt(this.channelCount);
      dest.writeInt(this.deviceType);
      dest.writeString(this.devicePassword);
      dest.writeString(this.deviceUser);
      dest.writeParcelable(this.userWifi, flags);
      dest.writeByte((byte)(this.isSSIDNeedEncrypt ? 1 : 0));
   }

   public void readFromParcel(Parcel source) {
      this.vconInfo = (VConInfo)source.readParcelable(VConInfo.class.getClassLoader());
      this.mIsPreConnected = source.readByte() != 0;
      this.corseeVersion = source.readString();
      this.eseeId = source.readString();
      this.sDeviceId = source.readString();
      this.serialId = source.readString();
      this.channelCount = source.readInt();
      this.deviceType = source.readInt();
      this.devicePassword = source.readString();
      this.deviceUser = source.readString();
      this.userWifi = (CommonWifiInfo)source.readParcelable(CommonWifiInfo.class.getClassLoader());
      this.isSSIDNeedEncrypt = source.readByte() != 0;
   }

   protected DeviceSetupInfo(Parcel in) {
      this.vconInfo = (VConInfo)in.readParcelable(VConInfo.class.getClassLoader());
      this.mIsPreConnected = in.readByte() != 0;
      this.corseeVersion = in.readString();
      this.eseeId = in.readString();
      this.sDeviceId = in.readString();
      this.serialId = in.readString();
      this.channelCount = in.readInt();
      this.deviceType = in.readInt();
      this.devicePassword = in.readString();
      this.deviceUser = in.readString();
      this.userWifi = (CommonWifiInfo)in.readParcelable(CommonWifiInfo.class.getClassLoader());
      this.isSSIDNeedEncrypt = in.readByte() != 0;
   }
}
