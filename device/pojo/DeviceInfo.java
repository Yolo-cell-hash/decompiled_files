package com.eseeiot.device.pojo;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
   private String deviceId;
   private String username;
   private String pwd;
   private int channelCount;
   private String serialID;

   public String getDeviceId() {
      return this.deviceId;
   }

   public void setDeviceId(String deviceId) {
      this.deviceId = deviceId;
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPwd() {
      return this.pwd;
   }

   public void setPwd(String pwd) {
      this.pwd = pwd;
   }

   public int getChannelCount() {
      return this.channelCount;
   }

   public void setChannelCount(int channelCount) {
      this.channelCount = channelCount;
   }

   public String getSerialID() {
      return this.serialID;
   }

   public void setSerialID(String serialID) {
      this.serialID = serialID;
   }
}
