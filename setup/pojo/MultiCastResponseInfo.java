package com.eseeiot.setup.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class MultiCastResponseInfo implements Parcelable {
   private boolean fromApp;
   private String deviceID;
   private String version;
   private String UID;
   private String devKey;
   private int requestID;
   private MultiCastResponseInfo.DevInfo devinfo;
   public static final Creator<MultiCastResponseInfo> CREATOR = new Creator<MultiCastResponseInfo>() {
      public MultiCastResponseInfo createFromParcel(Parcel source) {
         return new MultiCastResponseInfo(source);
      }

      public MultiCastResponseInfo[] newArray(int size) {
         return new MultiCastResponseInfo[size];
      }
   };

   public boolean isFromApp() {
      return this.fromApp;
   }

   public void setFromApp(boolean fromApp) {
      this.fromApp = fromApp;
   }

   public String getUID() {
      return this.UID;
   }

   public void setUID(String UID) {
      this.UID = UID;
   }

   public String getDevKey() {
      return this.devKey;
   }

   public void setDevKey(String devKey) {
      this.devKey = devKey;
   }

   public int getRequestID() {
      return this.requestID;
   }

   public void setRequestID(int requestID) {
      this.requestID = requestID;
   }

   public MultiCastResponseInfo.DevInfo getDevinfo() {
      return this.devinfo;
   }

   public void setDevinfo(MultiCastResponseInfo.DevInfo devinfo) {
      this.devinfo = devinfo;
   }

   public String getDeviceID() {
      return this.deviceID;
   }

   public void setDeviceID(String deviceID) {
      this.deviceID = deviceID;
   }

   public String getVersion() {
      return this.version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String toString() {
      return "MultiCastResponseInfo{fromApp=" + this.fromApp + ", deviceID='" + this.deviceID + '\'' + ", version='" + this.version + '\'' + ", UID='" + this.UID + '\'' + ", devKey='" + this.devKey + '\'' + ", requestID=" + this.requestID + ", devinfo=" + this.devinfo + '}';
   }

   public int describeContents() {
      return 0;
   }

   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte((byte)(this.fromApp ? 1 : 0));
      dest.writeString(this.deviceID);
      dest.writeString(this.version);
      dest.writeString(this.UID);
      dest.writeString(this.devKey);
      dest.writeInt(this.requestID);
      dest.writeParcelable(this.devinfo, flags);
   }

   public void readFromParcel(Parcel source) {
      this.fromApp = source.readByte() != 0;
      this.deviceID = source.readString();
      this.version = source.readString();
      this.UID = source.readString();
      this.devKey = source.readString();
      this.requestID = source.readInt();
      this.devinfo = (MultiCastResponseInfo.DevInfo)source.readParcelable(MultiCastResponseInfo.DevInfo.class.getClassLoader());
   }

   public MultiCastResponseInfo() {
   }

   protected MultiCastResponseInfo(Parcel in) {
      this.fromApp = in.readByte() != 0;
      this.deviceID = in.readString();
      this.version = in.readString();
      this.UID = in.readString();
      this.devKey = in.readString();
      this.requestID = in.readInt();
      this.devinfo = (MultiCastResponseInfo.DevInfo)in.readParcelable(MultiCastResponseInfo.DevInfo.class.getClassLoader());
   }

   public static class DevInfo implements Parcelable {
      private boolean monopoly;
      private String deviceType;
      private int maxChannel;
      private int enableChannel;
      private String ulinkToken;
      public static final Creator<MultiCastResponseInfo.DevInfo> CREATOR = new Creator<MultiCastResponseInfo.DevInfo>() {
         public MultiCastResponseInfo.DevInfo createFromParcel(Parcel source) {
            return new MultiCastResponseInfo.DevInfo(source);
         }

         public MultiCastResponseInfo.DevInfo[] newArray(int size) {
            return new MultiCastResponseInfo.DevInfo[size];
         }
      };

      public String getUlinkToken() {
         return this.ulinkToken;
      }

      public void setUlinkToken(String ulinkToken) {
         this.ulinkToken = ulinkToken;
      }

      public boolean isMonopoly() {
         return this.monopoly;
      }

      public void setMonopoly(boolean monopoly) {
         this.monopoly = monopoly;
      }

      public String getDeviceType() {
         return this.deviceType;
      }

      public void setDeviceType(String deviceType) {
         this.deviceType = deviceType;
      }

      public int getMaxChannel() {
         return this.maxChannel;
      }

      public void setMaxChannel(int maxChannel) {
         this.maxChannel = maxChannel;
      }

      public int getEnableChannel() {
         return this.enableChannel;
      }

      public void setEnableChannel(int enableChannel) {
         this.enableChannel = enableChannel;
      }

      public String toString() {
         return "DevInfo{monopoly=" + this.monopoly + ", deviceType='" + this.deviceType + '\'' + ", maxChannel=" + this.maxChannel + ", enableChannel=" + this.enableChannel + ", ulinkToken='" + this.ulinkToken + '\'' + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeByte((byte)(this.monopoly ? 1 : 0));
         dest.writeString(this.deviceType);
         dest.writeInt(this.maxChannel);
         dest.writeInt(this.enableChannel);
         dest.writeString(this.ulinkToken);
      }

      public void readFromParcel(Parcel source) {
         this.monopoly = source.readByte() != 0;
         this.deviceType = source.readString();
         this.maxChannel = source.readInt();
         this.enableChannel = source.readInt();
         this.ulinkToken = source.readString();
      }

      public DevInfo() {
      }

      protected DevInfo(Parcel in) {
         this.monopoly = in.readByte() != 0;
         this.deviceType = in.readString();
         this.maxChannel = in.readInt();
         this.enableChannel = in.readInt();
         this.ulinkToken = in.readString();
      }
   }
}
