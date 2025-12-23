package com.eseeiot.setup.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.List;

public class VConInfo implements Parcelable {
   private String Version;
   private String Method;
   private VConInfo.IPCamClass IPCam;
   private VConInfo.CapabilitySetClass CapabilitySet;
   private VConInfo.AuthorizationClass Authorization;
   public static final Creator<VConInfo> CREATOR = new Creator<VConInfo>() {
      public VConInfo createFromParcel(Parcel source) {
         return new VConInfo(source);
      }

      public VConInfo[] newArray(int size) {
         return new VConInfo[size];
      }
   };

   public VConInfo.AuthorizationClass getAuthorization() {
      return this.Authorization;
   }

   public void setAuthorization(VConInfo.AuthorizationClass authorization) {
      this.Authorization = authorization;
   }

   public String getVersion() {
      return this.Version;
   }

   public void setVersion(String version) {
      this.Version = version;
   }

   public String getMethod() {
      return this.Method;
   }

   public void setMethod(String method) {
      this.Method = method;
   }

   public VConInfo.IPCamClass getIPCam() {
      return this.IPCam;
   }

   public void setIPCam(VConInfo.IPCamClass IPCam) {
      this.IPCam = IPCam;
   }

   public VConInfo.CapabilitySetClass getCapabilitySet() {
      return this.CapabilitySet;
   }

   public void setCapabilitySet(VConInfo.CapabilitySetClass capabilitySet) {
      this.CapabilitySet = capabilitySet;
   }

   public int describeContents() {
      return 0;
   }

   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.Version);
      dest.writeString(this.Method);
      dest.writeParcelable(this.IPCam, flags);
      dest.writeParcelable(this.CapabilitySet, flags);
      dest.writeParcelable(this.Authorization, flags);
   }

   public void readFromParcel(Parcel source) {
      this.Version = source.readString();
      this.Method = source.readString();
      this.IPCam = (VConInfo.IPCamClass)source.readParcelable(VConInfo.IPCamClass.class.getClassLoader());
      this.CapabilitySet = (VConInfo.CapabilitySetClass)source.readParcelable(VConInfo.CapabilitySetClass.class.getClassLoader());
      this.Authorization = (VConInfo.AuthorizationClass)source.readParcelable(VConInfo.AuthorizationClass.class.getClassLoader());
   }

   public VConInfo() {
   }

   protected VConInfo(Parcel in) {
      this.Version = in.readString();
      this.Method = in.readString();
      this.IPCam = (VConInfo.IPCamClass)in.readParcelable(VConInfo.IPCamClass.class.getClassLoader());
      this.CapabilitySet = (VConInfo.CapabilitySetClass)in.readParcelable(VConInfo.CapabilitySetClass.class.getClassLoader());
      this.Authorization = (VConInfo.AuthorizationClass)in.readParcelable(VConInfo.AuthorizationClass.class.getClassLoader());
   }

   public static class CapabilitySetClass implements Parcelable {
      private int version;
      private int maxChannel;
      private String model;
      private boolean powerBattery;
      private boolean audioInput;
      private boolean audioOutput;
      private boolean bluetooth;
      private int lightControl;
      private int bulbControl;
      private boolean ptz;
      private boolean sdCard;
      private boolean lte;
      private boolean wifi;
      private boolean wifiStationCanSet;
      private boolean rj45;
      private boolean rtc;
      private int fisheye;
      public static final Creator<VConInfo.CapabilitySetClass> CREATOR = new Creator<VConInfo.CapabilitySetClass>() {
         public VConInfo.CapabilitySetClass createFromParcel(Parcel source) {
            return new VConInfo.CapabilitySetClass(source);
         }

         public VConInfo.CapabilitySetClass[] newArray(int size) {
            return new VConInfo.CapabilitySetClass[size];
         }
      };

      public boolean isAudioInput() {
         return this.audioInput;
      }

      public void setAudioInput(boolean audioInput) {
         this.audioInput = audioInput;
      }

      public boolean isAudioOutput() {
         return this.audioOutput;
      }

      public void setAudioOutput(boolean audioOutput) {
         this.audioOutput = audioOutput;
      }

      public int getVersion() {
         return this.version;
      }

      public void setVersion(int version) {
         this.version = version;
      }

      public boolean isPtz() {
         return this.ptz;
      }

      public void setPtz(boolean ptz) {
         this.ptz = ptz;
      }

      public boolean isWifiStationCanSet() {
         return this.wifiStationCanSet;
      }

      public void setWifiStationCanSet(boolean wifiStationCanSet) {
         this.wifiStationCanSet = wifiStationCanSet;
      }

      public boolean isRj45() {
         return this.rj45;
      }

      public void setRj45(boolean rj45) {
         this.rj45 = rj45;
      }

      public int getFisheye() {
         return this.fisheye;
      }

      public void setFisheye(int fisheye) {
         this.fisheye = fisheye;
      }

      public int getMaxChannel() {
         return this.maxChannel;
      }

      public void setMaxChannel(int maxChannel) {
         this.maxChannel = maxChannel;
      }

      public String getModel() {
         return this.model;
      }

      public void setModel(String model) {
         this.model = model;
      }

      public boolean isPowerBattery() {
         return this.powerBattery;
      }

      public void setPowerBattery(boolean powerBattery) {
         this.powerBattery = powerBattery;
      }

      public boolean isBluetooth() {
         return this.bluetooth;
      }

      public void setBluetooth(boolean bluetooth) {
         this.bluetooth = bluetooth;
      }

      public int getLightControl() {
         return this.lightControl;
      }

      public void setLightControl(int lightControl) {
         this.lightControl = lightControl;
      }

      public int getBulbControl() {
         return this.bulbControl;
      }

      public void setBulbControl(int bulbControl) {
         this.bulbControl = bulbControl;
      }

      public boolean isSdCard() {
         return this.sdCard;
      }

      public void setSdCard(boolean sdCard) {
         this.sdCard = sdCard;
      }

      public boolean isLte() {
         return this.lte;
      }

      public void setLte(boolean lte) {
         this.lte = lte;
      }

      public boolean isWifi() {
         return this.wifi;
      }

      public void setWifi(boolean wifi) {
         this.wifi = wifi;
      }

      public boolean isRtc() {
         return this.rtc;
      }

      public void setRtc(boolean rtc) {
         this.rtc = rtc;
      }

      public String toString() {
         return "CapabilitySetClass{version=" + this.version + ", maxChannel=" + this.maxChannel + ", model='" + this.model + '\'' + ", powerBattery=" + this.powerBattery + ", audioInput=" + this.audioInput + ", audioOutput=" + this.audioOutput + ", bluetooth=" + this.bluetooth + ", lightControl=" + this.lightControl + ", bulbControl=" + this.bulbControl + ", ptz=" + this.ptz + ", sdCard=" + this.sdCard + ", lte=" + this.lte + ", wifi=" + this.wifi + ", wifiStationCanSet=" + this.wifiStationCanSet + ", rj45=" + this.rj45 + ", rtc=" + this.rtc + ", fisheye=" + this.fisheye + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeInt(this.version);
         dest.writeInt(this.maxChannel);
         dest.writeString(this.model);
         dest.writeByte((byte)(this.powerBattery ? 1 : 0));
         dest.writeByte((byte)(this.audioInput ? 1 : 0));
         dest.writeByte((byte)(this.audioOutput ? 1 : 0));
         dest.writeByte((byte)(this.bluetooth ? 1 : 0));
         dest.writeInt(this.lightControl);
         dest.writeInt(this.bulbControl);
         dest.writeByte((byte)(this.ptz ? 1 : 0));
         dest.writeByte((byte)(this.sdCard ? 1 : 0));
         dest.writeByte((byte)(this.lte ? 1 : 0));
         dest.writeByte((byte)(this.wifi ? 1 : 0));
         dest.writeByte((byte)(this.wifiStationCanSet ? 1 : 0));
         dest.writeByte((byte)(this.rj45 ? 1 : 0));
         dest.writeByte((byte)(this.rtc ? 1 : 0));
         dest.writeInt(this.fisheye);
      }

      public void readFromParcel(Parcel source) {
         this.version = source.readInt();
         this.maxChannel = source.readInt();
         this.model = source.readString();
         this.powerBattery = source.readByte() != 0;
         this.audioInput = source.readByte() != 0;
         this.audioOutput = source.readByte() != 0;
         this.bluetooth = source.readByte() != 0;
         this.lightControl = source.readInt();
         this.bulbControl = source.readInt();
         this.ptz = source.readByte() != 0;
         this.sdCard = source.readByte() != 0;
         this.lte = source.readByte() != 0;
         this.wifi = source.readByte() != 0;
         this.wifiStationCanSet = source.readByte() != 0;
         this.rj45 = source.readByte() != 0;
         this.rtc = source.readByte() != 0;
         this.fisheye = source.readInt();
      }

      public CapabilitySetClass() {
      }

      protected CapabilitySetClass(Parcel in) {
         this.version = in.readInt();
         this.maxChannel = in.readInt();
         this.model = in.readString();
         this.powerBattery = in.readByte() != 0;
         this.audioInput = in.readByte() != 0;
         this.audioOutput = in.readByte() != 0;
         this.bluetooth = in.readByte() != 0;
         this.lightControl = in.readInt();
         this.bulbControl = in.readInt();
         this.ptz = in.readByte() != 0;
         this.sdCard = in.readByte() != 0;
         this.lte = in.readByte() != 0;
         this.wifi = in.readByte() != 0;
         this.wifiStationCanSet = in.readByte() != 0;
         this.rj45 = in.readByte() != 0;
         this.rtc = in.readByte() != 0;
         this.fisheye = in.readInt();
      }
   }

   public static class AuthorizationClass implements Parcelable {
      private String Verify;
      private String username;
      private String password;
      public static final Creator<VConInfo.AuthorizationClass> CREATOR = new Creator<VConInfo.AuthorizationClass>() {
         public VConInfo.AuthorizationClass createFromParcel(Parcel source) {
            return new VConInfo.AuthorizationClass(source);
         }

         public VConInfo.AuthorizationClass[] newArray(int size) {
            return new VConInfo.AuthorizationClass[size];
         }
      };

      public String getVerify() {
         return this.Verify;
      }

      public void setVerify(String verify) {
         this.Verify = verify;
      }

      public String getUsername() {
         return this.username;
      }

      public void setUsername(String username) {
         this.username = username;
      }

      public String getPassword() {
         return this.password;
      }

      public void setPassword(String password) {
         this.password = password;
      }

      public String toString() {
         return "AuthorizationClass{Verify='" + this.Verify + '\'' + ", username='" + this.username + '\'' + ", password='" + this.password + '\'' + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(this.Verify);
         dest.writeString(this.username);
         dest.writeString(this.password);
      }

      public void readFromParcel(Parcel source) {
         this.Verify = source.readString();
         this.username = source.readString();
         this.password = source.readString();
      }

      public AuthorizationClass() {
      }

      protected AuthorizationClass(Parcel in) {
         this.Verify = in.readString();
         this.username = in.readString();
         this.password = in.readString();
      }
   }

   public static class WeekClass implements Parcelable {
      private String Type;
      private int Month;
      private int Week;
      private int Weekday;
      private int Hour;
      private int Minute;
      public static final Creator<VConInfo.WeekClass> CREATOR = new Creator<VConInfo.WeekClass>() {
         public VConInfo.WeekClass createFromParcel(Parcel source) {
            return new VConInfo.WeekClass(source);
         }

         public VConInfo.WeekClass[] newArray(int size) {
            return new VConInfo.WeekClass[size];
         }
      };

      public String getType() {
         return this.Type;
      }

      public void setType(String type) {
         this.Type = type;
      }

      public int getMonth() {
         return this.Month;
      }

      public void setMonth(int month) {
         this.Month = month;
      }

      public int getWeek() {
         return this.Week;
      }

      public void setWeek(int week) {
         this.Week = week;
      }

      public int getWeekday() {
         return this.Weekday;
      }

      public void setWeekday(int weekday) {
         this.Weekday = weekday;
      }

      public int getHour() {
         return this.Hour;
      }

      public void setHour(int hour) {
         this.Hour = hour;
      }

      public int getMinute() {
         return this.Minute;
      }

      public void setMinute(int minute) {
         this.Minute = minute;
      }

      public String toString() {
         return "WeekClass{Type='" + this.Type + '\'' + ", Month=" + this.Month + ", Week=" + this.Week + ", Weekday=" + this.Weekday + ", Hour=" + this.Hour + ", Minute=" + this.Minute + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(this.Type);
         dest.writeInt(this.Month);
         dest.writeInt(this.Week);
         dest.writeInt(this.Weekday);
         dest.writeInt(this.Hour);
         dest.writeInt(this.Minute);
      }

      public void readFromParcel(Parcel source) {
         this.Type = source.readString();
         this.Month = source.readInt();
         this.Week = source.readInt();
         this.Weekday = source.readInt();
         this.Hour = source.readInt();
         this.Minute = source.readInt();
      }

      public WeekClass() {
      }

      protected WeekClass(Parcel in) {
         this.Type = in.readString();
         this.Month = in.readInt();
         this.Week = in.readInt();
         this.Weekday = in.readInt();
         this.Hour = in.readInt();
         this.Minute = in.readInt();
      }
   }

   public static class UpgradeStatusClass implements Parcelable {
      private String Status;
      private int DeviceIndex;
      private int Progress;
      private String error;
      public static final Creator<VConInfo.UpgradeStatusClass> CREATOR = new Creator<VConInfo.UpgradeStatusClass>() {
         public VConInfo.UpgradeStatusClass createFromParcel(Parcel source) {
            return new VConInfo.UpgradeStatusClass(source);
         }

         public VConInfo.UpgradeStatusClass[] newArray(int size) {
            return new VConInfo.UpgradeStatusClass[size];
         }
      };

      public String getStatus() {
         return this.Status;
      }

      public void setStatus(String status) {
         this.Status = status;
      }

      public int getDeviceIndex() {
         return this.DeviceIndex;
      }

      public void setDeviceIndex(int deviceIndex) {
         this.DeviceIndex = deviceIndex;
      }

      public int getProgress() {
         return this.Progress;
      }

      public void setProgress(int progress) {
         this.Progress = progress;
      }

      public String getError() {
         return this.error;
      }

      public void setError(String error) {
         this.error = error;
      }

      public String toString() {
         return "UpgradeStatusClass{Status='" + this.Status + '\'' + ", DeviceIndex=" + this.DeviceIndex + ", Progress=" + this.Progress + ", error='" + this.error + '\'' + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(this.Status);
         dest.writeInt(this.DeviceIndex);
         dest.writeInt(this.Progress);
         dest.writeString(this.error);
      }

      public void readFromParcel(Parcel source) {
         this.Status = source.readString();
         this.DeviceIndex = source.readInt();
         this.Progress = source.readInt();
         this.error = source.readString();
      }

      public UpgradeStatusClass() {
      }

      protected UpgradeStatusClass(Parcel in) {
         this.Status = in.readString();
         this.DeviceIndex = in.readInt();
         this.Progress = in.readInt();
         this.error = in.readString();
      }
   }

   public static class UpgradeClass implements Parcelable {
      private boolean Enabled;
      private boolean EnabledUpgradeChannel;
      public static final Creator<VConInfo.UpgradeClass> CREATOR = new Creator<VConInfo.UpgradeClass>() {
         public VConInfo.UpgradeClass createFromParcel(Parcel source) {
            return new VConInfo.UpgradeClass(source);
         }

         public VConInfo.UpgradeClass[] newArray(int size) {
            return new VConInfo.UpgradeClass[size];
         }
      };

      public boolean isEnabled() {
         return this.Enabled;
      }

      public void setEnabled(boolean enabled) {
         this.Enabled = enabled;
      }

      public boolean isEnabledUpgradeChannel() {
         return this.EnabledUpgradeChannel;
      }

      public void setEnabledUpgradeChannel(boolean enabledUpgradeChannel) {
         this.EnabledUpgradeChannel = enabledUpgradeChannel;
      }

      public String toString() {
         return "UpgradeClass{Enabled=" + this.Enabled + ", EnabledUpgradeChannel=" + this.EnabledUpgradeChannel + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeByte((byte)(this.Enabled ? 1 : 0));
         dest.writeByte((byte)(this.EnabledUpgradeChannel ? 1 : 0));
      }

      public void readFromParcel(Parcel source) {
         this.Enabled = source.readByte() != 0;
         this.EnabledUpgradeChannel = source.readByte() != 0;
      }

      public UpgradeClass() {
      }

      protected UpgradeClass(Parcel in) {
         this.Enabled = in.readByte() != 0;
         this.EnabledUpgradeChannel = in.readByte() != 0;
      }
   }

   public static class DaylightSavingTimeClass implements Parcelable {
      private boolean Enabled;
      private int Offset;
      private List<VConInfo.WeekClass> Week;
      public static final Creator<VConInfo.DaylightSavingTimeClass> CREATOR = new Creator<VConInfo.DaylightSavingTimeClass>() {
         public VConInfo.DaylightSavingTimeClass createFromParcel(Parcel source) {
            return new VConInfo.DaylightSavingTimeClass(source);
         }

         public VConInfo.DaylightSavingTimeClass[] newArray(int size) {
            return new VConInfo.DaylightSavingTimeClass[size];
         }
      };

      public boolean isEnabled() {
         return this.Enabled;
      }

      public void setEnabled(boolean enabled) {
         this.Enabled = enabled;
      }

      public int getOffset() {
         return this.Offset;
      }

      public void setOffset(int offset) {
         this.Offset = offset;
      }

      public List<VConInfo.WeekClass> getWeek() {
         return this.Week;
      }

      public void setWeek(List<VConInfo.WeekClass> week) {
         this.Week = week;
      }

      public String toString() {
         return "DaylightSavingTimeClass{Enabled=" + this.Enabled + ", Offset=" + this.Offset + ", Week=" + this.Week + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeByte((byte)(this.Enabled ? 1 : 0));
         dest.writeInt(this.Offset);
         dest.writeTypedList(this.Week);
      }

      public void readFromParcel(Parcel source) {
         this.Enabled = source.readByte() != 0;
         this.Offset = source.readInt();
         this.Week = source.createTypedArrayList(VConInfo.WeekClass.CREATOR);
      }

      public DaylightSavingTimeClass() {
      }

      protected DaylightSavingTimeClass(Parcel in) {
         this.Enabled = in.readByte() != 0;
         this.Offset = in.readInt();
         this.Week = in.createTypedArrayList(VConInfo.WeekClass.CREATOR);
      }
   }

   public static class TimeSyncClass implements Parcelable {
      private String UTCTime;
      private int TimeZone;
      public static final Creator<VConInfo.TimeSyncClass> CREATOR = new Creator<VConInfo.TimeSyncClass>() {
         public VConInfo.TimeSyncClass createFromParcel(Parcel source) {
            return new VConInfo.TimeSyncClass(source);
         }

         public VConInfo.TimeSyncClass[] newArray(int size) {
            return new VConInfo.TimeSyncClass[size];
         }
      };

      public String getUTCTime() {
         return this.UTCTime;
      }

      public void setUTCTime(String UTCTime) {
         this.UTCTime = UTCTime;
      }

      public int getTimeZone() {
         return this.TimeZone;
      }

      public void setTimeZone(int timeZone) {
         this.TimeZone = timeZone;
      }

      public String toString() {
         return "TimeSyncClass{UTCTime='" + this.UTCTime + '\'' + ", TimeZone=" + this.TimeZone + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(this.UTCTime);
         dest.writeInt(this.TimeZone);
      }

      public void readFromParcel(Parcel source) {
         this.UTCTime = source.readString();
         this.TimeZone = source.readInt();
      }

      public TimeSyncClass() {
      }

      protected TimeSyncClass(Parcel in) {
         this.UTCTime = in.readString();
         this.TimeZone = in.readInt();
      }
   }

   public static class SystemOperationClass implements Parcelable {
      private VConInfo.TimeSyncClass TimeSync;
      private VConInfo.DaylightSavingTimeClass DaylightSavingTime;
      private VConInfo.UpgradeClass Upgrade;
      private VConInfo.UpgradeStatusClass UpgradeStatus;
      public static final Creator<VConInfo.SystemOperationClass> CREATOR = new Creator<VConInfo.SystemOperationClass>() {
         public VConInfo.SystemOperationClass createFromParcel(Parcel source) {
            return new VConInfo.SystemOperationClass(source);
         }

         public VConInfo.SystemOperationClass[] newArray(int size) {
            return new VConInfo.SystemOperationClass[size];
         }
      };

      public VConInfo.TimeSyncClass getTimeSync() {
         return this.TimeSync;
      }

      public void setTimeSync(VConInfo.TimeSyncClass timeSync) {
         this.TimeSync = timeSync;
      }

      public VConInfo.DaylightSavingTimeClass getDaylightSavingTime() {
         return this.DaylightSavingTime;
      }

      public void setDaylightSavingTime(VConInfo.DaylightSavingTimeClass daylightSavingTime) {
         this.DaylightSavingTime = daylightSavingTime;
      }

      public VConInfo.UpgradeClass getUpgrade() {
         return this.Upgrade;
      }

      public void setUpgrade(VConInfo.UpgradeClass upgrade) {
         this.Upgrade = upgrade;
      }

      public VConInfo.UpgradeStatusClass getUpgradeStatus() {
         return this.UpgradeStatus;
      }

      public void setUpgradeStatus(VConInfo.UpgradeStatusClass upgradeStatus) {
         this.UpgradeStatus = upgradeStatus;
      }

      public String toString() {
         return "SystemOperationClass{TimeSync=" + this.TimeSync + ", DaylightSavingTime=" + this.DaylightSavingTime + ", Upgrade=" + this.Upgrade + ", UpgradeStatus=" + this.UpgradeStatus + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeParcelable(this.TimeSync, flags);
         dest.writeParcelable(this.DaylightSavingTime, flags);
         dest.writeParcelable(this.Upgrade, flags);
         dest.writeParcelable(this.UpgradeStatus, flags);
      }

      public void readFromParcel(Parcel source) {
         this.TimeSync = (VConInfo.TimeSyncClass)source.readParcelable(VConInfo.TimeSyncClass.class.getClassLoader());
         this.DaylightSavingTime = (VConInfo.DaylightSavingTimeClass)source.readParcelable(VConInfo.DaylightSavingTimeClass.class.getClassLoader());
         this.Upgrade = (VConInfo.UpgradeClass)source.readParcelable(VConInfo.UpgradeClass.class.getClassLoader());
         this.UpgradeStatus = (VConInfo.UpgradeStatusClass)source.readParcelable(VConInfo.UpgradeStatusClass.class.getClassLoader());
      }

      public SystemOperationClass() {
      }

      protected SystemOperationClass(Parcel in) {
         this.TimeSync = (VConInfo.TimeSyncClass)in.readParcelable(VConInfo.TimeSyncClass.class.getClassLoader());
         this.DaylightSavingTime = (VConInfo.DaylightSavingTimeClass)in.readParcelable(VConInfo.DaylightSavingTimeClass.class.getClassLoader());
         this.Upgrade = (VConInfo.UpgradeClass)in.readParcelable(VConInfo.UpgradeClass.class.getClassLoader());
         this.UpgradeStatus = (VConInfo.UpgradeStatusClass)in.readParcelable(VConInfo.UpgradeStatusClass.class.getClassLoader());
      }
   }

   public static class RecordManagerClass implements Parcelable {
      private String Mode;
      public static final Creator<VConInfo.RecordManagerClass> CREATOR = new Creator<VConInfo.RecordManagerClass>() {
         public VConInfo.RecordManagerClass createFromParcel(Parcel source) {
            return new VConInfo.RecordManagerClass(source);
         }

         public VConInfo.RecordManagerClass[] newArray(int size) {
            return new VConInfo.RecordManagerClass[size];
         }
      };

      public String getMode() {
         return this.Mode;
      }

      public void setMode(String mode) {
         this.Mode = mode;
      }

      public String toString() {
         return "RecordManagerClass{Mode='" + this.Mode + '\'' + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(this.Mode);
      }

      public void readFromParcel(Parcel source) {
         this.Mode = source.readString();
      }

      public RecordManagerClass() {
      }

      protected RecordManagerClass(Parcel in) {
         this.Mode = in.readString();
      }
   }

   public static class MotionDetectionClass implements Parcelable {
      private boolean Enabled;
      private String SensitivityLevel;
      public static final Creator<VConInfo.MotionDetectionClass> CREATOR = new Creator<VConInfo.MotionDetectionClass>() {
         public VConInfo.MotionDetectionClass createFromParcel(Parcel source) {
            return new VConInfo.MotionDetectionClass(source);
         }

         public VConInfo.MotionDetectionClass[] newArray(int size) {
            return new VConInfo.MotionDetectionClass[size];
         }
      };

      public boolean isEnabled() {
         return this.Enabled;
      }

      public void setEnabled(boolean enabled) {
         this.Enabled = enabled;
      }

      public String getSensitivityLevel() {
         return this.SensitivityLevel;
      }

      public void setSensitivityLevel(String sensitivityLevel) {
         this.SensitivityLevel = sensitivityLevel;
      }

      public String toString() {
         return "MotionDetectionClass{Enabled=" + this.Enabled + ", SensitivityLevel='" + this.SensitivityLevel + '\'' + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeByte((byte)(this.Enabled ? 1 : 0));
         dest.writeString(this.SensitivityLevel);
      }

      public void readFromParcel(Parcel source) {
         this.Enabled = source.readByte() != 0;
         this.SensitivityLevel = source.readString();
      }

      public MotionDetectionClass() {
      }

      protected MotionDetectionClass(Parcel in) {
         this.Enabled = in.readByte() != 0;
         this.SensitivityLevel = in.readString();
      }
   }

   public static class AlarmSettingClass implements Parcelable {
      private VConInfo.MotionDetectionClass MotionDetection;
      private boolean MessagePushEnabled;
      private boolean ScheduleSupport;
      public static final Creator<VConInfo.AlarmSettingClass> CREATOR = new Creator<VConInfo.AlarmSettingClass>() {
         public VConInfo.AlarmSettingClass createFromParcel(Parcel source) {
            return new VConInfo.AlarmSettingClass(source);
         }

         public VConInfo.AlarmSettingClass[] newArray(int size) {
            return new VConInfo.AlarmSettingClass[size];
         }
      };

      public boolean isMessagePushEnabled() {
         return this.MessagePushEnabled;
      }

      public void setMessagePushEnabled(boolean messagePushEnabled) {
         this.MessagePushEnabled = messagePushEnabled;
      }

      public boolean isScheduleSupport() {
         return this.ScheduleSupport;
      }

      public void setScheduleSupport(boolean scheduleSupport) {
         this.ScheduleSupport = scheduleSupport;
      }

      public VConInfo.MotionDetectionClass getMotionDetection() {
         return this.MotionDetection;
      }

      public void setMotionDetection(VConInfo.MotionDetectionClass motionDetection) {
         this.MotionDetection = motionDetection;
      }

      public String toString() {
         return "AlarmSettingClass{MotionDetection=" + this.MotionDetection + ", MessagePushEnabled=" + this.MessagePushEnabled + ", ScheduleSupport=" + this.ScheduleSupport + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeParcelable(this.MotionDetection, flags);
         dest.writeByte((byte)(this.MessagePushEnabled ? 1 : 0));
         dest.writeByte((byte)(this.ScheduleSupport ? 1 : 0));
      }

      public void readFromParcel(Parcel source) {
         this.MotionDetection = (VConInfo.MotionDetectionClass)source.readParcelable(VConInfo.MotionDetectionClass.class.getClassLoader());
         this.MessagePushEnabled = source.readByte() != 0;
         this.ScheduleSupport = source.readByte() != 0;
      }

      public AlarmSettingClass() {
      }

      protected AlarmSettingClass(Parcel in) {
         this.MotionDetection = (VConInfo.MotionDetectionClass)in.readParcelable(VConInfo.MotionDetectionClass.class.getClassLoader());
         this.MessagePushEnabled = in.readByte() != 0;
         this.ScheduleSupport = in.readByte() != 0;
      }
   }

   public static class ModeSettingClass implements Parcelable {
      private boolean AudioEnabled;
      private boolean ledType;
      private int ledBrightness;
      private int ledColour;
      private String SceneMode;
      private String IRCutFilterMode;
      private String ConvenientSetting;
      private String imageStyle;
      private String Definition;
      public static final Creator<VConInfo.ModeSettingClass> CREATOR = new Creator<VConInfo.ModeSettingClass>() {
         public VConInfo.ModeSettingClass createFromParcel(Parcel source) {
            return new VConInfo.ModeSettingClass(source);
         }

         public VConInfo.ModeSettingClass[] newArray(int size) {
            return new VConInfo.ModeSettingClass[size];
         }
      };

      public boolean isAudioEnabled() {
         return this.AudioEnabled;
      }

      public void setAudioEnabled(boolean audioEnabled) {
         this.AudioEnabled = audioEnabled;
      }

      public boolean isLedType() {
         return this.ledType;
      }

      public void setLedType(boolean ledType) {
         this.ledType = ledType;
      }

      public int getLedBrightness() {
         return this.ledBrightness;
      }

      public void setLedBrightness(int ledBrightness) {
         this.ledBrightness = ledBrightness;
      }

      public int getLedColour() {
         return this.ledColour;
      }

      public void setLedColour(int ledColour) {
         this.ledColour = ledColour;
      }

      public String getSceneMode() {
         return this.SceneMode;
      }

      public void setSceneMode(String sceneMode) {
         this.SceneMode = sceneMode;
      }

      public String getIRCutFilterMode() {
         return this.IRCutFilterMode;
      }

      public void setIRCutFilterMode(String IRCutFilterMode) {
         this.IRCutFilterMode = IRCutFilterMode;
      }

      public String getConvenientSetting() {
         return this.ConvenientSetting;
      }

      public void setConvenientSetting(String convenientSetting) {
         this.ConvenientSetting = convenientSetting;
      }

      public String getImageStyle() {
         return this.imageStyle;
      }

      public void setImageStyle(String imageStyle) {
         this.imageStyle = imageStyle;
      }

      public String getDefinition() {
         return this.Definition;
      }

      public void setDefinition(String definition) {
         this.Definition = definition;
      }

      public String toString() {
         return "ModeSettingClass{AudioEnabled=" + this.AudioEnabled + ", ledType=" + this.ledType + ", ledBrightness=" + this.ledBrightness + ", ledColour=" + this.ledColour + ", SceneMode='" + this.SceneMode + '\'' + ", IRCutFilterMode='" + this.IRCutFilterMode + '\'' + ", ConvenientSetting='" + this.ConvenientSetting + '\'' + ", imageStyle='" + this.imageStyle + '\'' + ", Definition='" + this.Definition + '\'' + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeByte((byte)(this.AudioEnabled ? 1 : 0));
         dest.writeByte((byte)(this.ledType ? 1 : 0));
         dest.writeInt(this.ledBrightness);
         dest.writeInt(this.ledColour);
         dest.writeString(this.SceneMode);
         dest.writeString(this.IRCutFilterMode);
         dest.writeString(this.ConvenientSetting);
         dest.writeString(this.imageStyle);
         dest.writeString(this.Definition);
      }

      public void readFromParcel(Parcel source) {
         this.AudioEnabled = source.readByte() != 0;
         this.ledType = source.readByte() != 0;
         this.ledBrightness = source.readInt();
         this.ledColour = source.readInt();
         this.SceneMode = source.readString();
         this.IRCutFilterMode = source.readString();
         this.ConvenientSetting = source.readString();
         this.imageStyle = source.readString();
         this.Definition = source.readString();
      }

      public ModeSettingClass() {
      }

      protected ModeSettingClass(Parcel in) {
         this.AudioEnabled = in.readByte() != 0;
         this.ledType = in.readByte() != 0;
         this.ledBrightness = in.readInt();
         this.ledColour = in.readInt();
         this.SceneMode = in.readString();
         this.IRCutFilterMode = in.readString();
         this.ConvenientSetting = in.readString();
         this.imageStyle = in.readString();
         this.Definition = in.readString();
      }
   }

   public static class DeviceInfoClass implements Parcelable {
      private String OEMNumber;
      private String FWVersion;
      private String Model;
      private String ID;
      private String FWMagic;
      public static final Creator<VConInfo.DeviceInfoClass> CREATOR = new Creator<VConInfo.DeviceInfoClass>() {
         public VConInfo.DeviceInfoClass createFromParcel(Parcel source) {
            return new VConInfo.DeviceInfoClass(source);
         }

         public VConInfo.DeviceInfoClass[] newArray(int size) {
            return new VConInfo.DeviceInfoClass[size];
         }
      };

      public String getOEMNumber() {
         return this.OEMNumber;
      }

      public void setOEMNumber(String OEMNumber) {
         this.OEMNumber = OEMNumber;
      }

      public String getFWVersion() {
         return this.FWVersion;
      }

      public void setFWVersion(String FWVersion) {
         this.FWVersion = FWVersion;
      }

      public String getModel() {
         return this.Model;
      }

      public void setModel(String model) {
         this.Model = model;
      }

      public String getID() {
         return this.ID;
      }

      public void setID(String ID) {
         this.ID = ID;
      }

      public String getFWMagic() {
         return this.FWMagic;
      }

      public void setFWMagic(String FWMagic) {
         this.FWMagic = FWMagic;
      }

      public String toString() {
         return "DeviceInfoClass{OEMNumber='" + this.OEMNumber + '\'' + ", FWVersion='" + this.FWVersion + '\'' + ", Model='" + this.Model + '\'' + ", ID='" + this.ID + '\'' + ", FWMagic='" + this.FWMagic + '\'' + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(this.OEMNumber);
         dest.writeString(this.FWVersion);
         dest.writeString(this.Model);
         dest.writeString(this.ID);
         dest.writeString(this.FWMagic);
      }

      public void readFromParcel(Parcel source) {
         this.OEMNumber = source.readString();
         this.FWVersion = source.readString();
         this.Model = source.readString();
         this.ID = source.readString();
         this.FWMagic = source.readString();
      }

      public DeviceInfoClass() {
      }

      protected DeviceInfoClass(Parcel in) {
         this.OEMNumber = in.readString();
         this.FWVersion = in.readString();
         this.Model = in.readString();
         this.ID = in.readString();
         this.FWMagic = in.readString();
      }
   }

   public static class IPCamClass implements Parcelable {
      private VConInfo.DeviceInfoClass DeviceInfo;
      private VConInfo.ModeSettingClass ModeSetting;
      private VConInfo.AlarmSettingClass AlarmSetting;
      private VConInfo.RecordManagerClass RecordManager;
      private VConInfo.SystemOperationClass SystemOperation;
      public static final Creator<VConInfo.IPCamClass> CREATOR = new Creator<VConInfo.IPCamClass>() {
         public VConInfo.IPCamClass createFromParcel(Parcel source) {
            return new VConInfo.IPCamClass(source);
         }

         public VConInfo.IPCamClass[] newArray(int size) {
            return new VConInfo.IPCamClass[size];
         }
      };

      public VConInfo.SystemOperationClass getSystemOperation() {
         return this.SystemOperation;
      }

      public void setSystemOperation(VConInfo.SystemOperationClass systemOperation) {
         this.SystemOperation = systemOperation;
      }

      public VConInfo.RecordManagerClass getRecordManager() {
         return this.RecordManager;
      }

      public void setRecordManager(VConInfo.RecordManagerClass recordManager) {
         this.RecordManager = recordManager;
      }

      public VConInfo.AlarmSettingClass getAlarmSetting() {
         return this.AlarmSetting;
      }

      public void setAlarmSetting(VConInfo.AlarmSettingClass alarmSetting) {
         this.AlarmSetting = alarmSetting;
      }

      public VConInfo.ModeSettingClass getModeSetting() {
         return this.ModeSetting;
      }

      public void setModeSetting(VConInfo.ModeSettingClass modeSetting) {
         this.ModeSetting = modeSetting;
      }

      public VConInfo.DeviceInfoClass getDeviceInfo() {
         return this.DeviceInfo;
      }

      public void setDeviceInfo(VConInfo.DeviceInfoClass deviceInfo) {
         this.DeviceInfo = deviceInfo;
      }

      public String toString() {
         return "IPCamClass{DeviceInfo=" + this.DeviceInfo + ", ModeSetting=" + this.ModeSetting + ", AlarmSetting=" + this.AlarmSetting + ", RecordManager=" + this.RecordManager + ", SystemOperation=" + this.SystemOperation + '}';
      }

      public int describeContents() {
         return 0;
      }

      public void writeToParcel(Parcel dest, int flags) {
         dest.writeParcelable(this.DeviceInfo, flags);
         dest.writeParcelable(this.ModeSetting, flags);
         dest.writeParcelable(this.AlarmSetting, flags);
         dest.writeParcelable(this.RecordManager, flags);
         dest.writeParcelable(this.SystemOperation, flags);
      }

      public void readFromParcel(Parcel source) {
         this.DeviceInfo = (VConInfo.DeviceInfoClass)source.readParcelable(VConInfo.DeviceInfoClass.class.getClassLoader());
         this.ModeSetting = (VConInfo.ModeSettingClass)source.readParcelable(VConInfo.ModeSettingClass.class.getClassLoader());
         this.AlarmSetting = (VConInfo.AlarmSettingClass)source.readParcelable(VConInfo.AlarmSettingClass.class.getClassLoader());
         this.RecordManager = (VConInfo.RecordManagerClass)source.readParcelable(VConInfo.RecordManagerClass.class.getClassLoader());
         this.SystemOperation = (VConInfo.SystemOperationClass)source.readParcelable(VConInfo.SystemOperationClass.class.getClassLoader());
      }

      public IPCamClass() {
      }

      protected IPCamClass(Parcel in) {
         this.DeviceInfo = (VConInfo.DeviceInfoClass)in.readParcelable(VConInfo.DeviceInfoClass.class.getClassLoader());
         this.ModeSetting = (VConInfo.ModeSettingClass)in.readParcelable(VConInfo.ModeSettingClass.class.getClassLoader());
         this.AlarmSetting = (VConInfo.AlarmSettingClass)in.readParcelable(VConInfo.AlarmSettingClass.class.getClassLoader());
         this.RecordManager = (VConInfo.RecordManagerClass)in.readParcelable(VConInfo.RecordManagerClass.class.getClassLoader());
         this.SystemOperation = (VConInfo.SystemOperationClass)in.readParcelable(VConInfo.SystemOperationClass.class.getClassLoader());
      }
   }
}
