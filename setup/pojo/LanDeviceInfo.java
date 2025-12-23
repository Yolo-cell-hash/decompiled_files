package com.eseeiot.setup.pojo;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Objects;

public class LanDeviceInfo extends MultiCastResponseInfo {
   private String eseeId;
   private int channelCount;
   private int deviceType;
   private String model;
   public static final Creator<LanDeviceInfo> CREATOR = new Creator<LanDeviceInfo>() {
      public LanDeviceInfo createFromParcel(Parcel source) {
         return new LanDeviceInfo(source);
      }

      public LanDeviceInfo[] newArray(int size) {
         return new LanDeviceInfo[size];
      }
   };

   public LanDeviceInfo() {
      this.channelCount = 1;
      this.deviceType = 0;
   }

   public String getEseeId() {
      return this.eseeId;
   }

   public void setEseeId(String eseeId) {
      this.eseeId = eseeId;
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

   public String getModel() {
      return this.model;
   }

   public void setModel(String model) {
      this.model = model;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         LanDeviceInfo that = (LanDeviceInfo)o;
         return this.channelCount == that.channelCount && this.deviceType == that.deviceType && this.eseeId.equals(that.eseeId) && Objects.equals(this.model, that.model);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.eseeId, this.channelCount, this.deviceType, this.model});
   }

   public String toString() {
      return "LanDeviceInfo{eseeId='" + this.eseeId + '\'' + ", channelCount=" + this.channelCount + ", deviceType=" + this.deviceType + ", model='" + this.model + '\'' + '}';
   }

   public int describeContents() {
      return 0;
   }

   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeString(this.eseeId);
      dest.writeInt(this.channelCount);
      dest.writeInt(this.deviceType);
      dest.writeString(this.model);
   }

   public void readFromParcel(Parcel source) {
      super.readFromParcel(source);
      this.eseeId = source.readString();
      this.channelCount = source.readInt();
      this.deviceType = source.readInt();
      this.model = source.readString();
   }

   protected LanDeviceInfo(Parcel in) {
      super(in);
      this.eseeId = in.readString();
      this.channelCount = in.readInt();
      this.deviceType = in.readInt();
      this.model = in.readString();
   }
}
