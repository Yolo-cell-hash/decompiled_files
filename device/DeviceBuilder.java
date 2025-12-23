package com.eseeiot.device;

import android.text.TextUtils;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.core.connect.JAConnectorV2;

public class DeviceBuilder {
   private String iotId;
   private String verify;
   private String username;
   private String password;
   private int channelCount;
   private String serialID;

   public DeviceBuilder setIotId(String iotId) {
      this.iotId = iotId;
      return this;
   }

   public DeviceBuilder setVerify(String verify) {
      this.verify = verify;
      return this;
   }

   public DeviceBuilder setUsername(String username) {
      this.username = username;
      return this;
   }

   public DeviceBuilder setPassword(String password) {
      this.password = password;
      return this;
   }

   public DeviceBuilder setChannelCount(int channelCount) {
      this.channelCount = channelCount;
      return this;
   }

   public DeviceBuilder setSerialID(String serialID) {
      this.serialID = serialID;
      return this;
   }

   public MonitorDevice build() throws IllegalArgumentException {
      if (TextUtils.isEmpty(this.iotId)) {
         throw new IllegalArgumentException("iotId must be set first!");
      } else if (this.channelCount <= 0) {
         throw new IllegalArgumentException("channelCount must be greater than 0!");
      } else {
         JADevProperty property = new JADevProperty();
         property.eseeId = this.iotId;
         property.serialId = this.serialID;
         if (!TextUtils.isEmpty(this.verify)) {
            property.verify = this.verify;
         }

         property.user = TextUtils.isEmpty(this.username) ? "admin" : this.username;
         property.password = TextUtils.isEmpty(this.password) ? "" : this.password;
         if (JADevice.sConnector == null) {
            JADevice.sConnector = JAConnectorV2.getInstance();
         }

         return new JADevice(property, this.channelCount);
      }
   }
}
