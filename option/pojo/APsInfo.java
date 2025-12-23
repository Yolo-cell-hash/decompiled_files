package com.eseeiot.option.pojo;

import java.io.Serializable;

public class APsInfo implements Serializable {
   private String ssid;
   private int rssi;
   private boolean encrypt;

   public String getSsid() {
      return this.ssid;
   }

   public void setSsid(String ssid) {
      this.ssid = ssid;
   }

   public int getRssi() {
      return this.rssi;
   }

   public void setRssi(int rssi) {
      this.rssi = rssi;
   }

   public boolean isEncrypt() {
      return this.encrypt;
   }

   public void setEncrypt(boolean encrypt) {
      this.encrypt = encrypt;
   }
}
