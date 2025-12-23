package com.eseeiot.setup.pojo;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NVRResponseInfo {
   @SerializedName("Ver")
   private String ver;
   @SerializedName("Nonce")
   private String nonce;
   @SerializedName("Device-ID")
   private String deviceId;
   @SerializedName("Device-Model")
   private String deviceModel;
   @SerializedName("Device-Type")
   private String deviceType;
   @SerializedName("Esee-ID")
   private String eseeId;
   @SerializedName("Software-Version")
   private String softwareVersion;
   @SerializedName("Channel-Cnt")
   private int channelCount;
   @SerializedName("Wired")
   private List<NVRResponseInfo.WiredInfo> wired;
   @SerializedName("Capabilities")
   private NVRResponseInfo.CapabilityInfo capabilities;

   public String getVer() {
      return this.ver;
   }

   public void setVer(String ver) {
      this.ver = ver;
   }

   public String getNonce() {
      return this.nonce;
   }

   public void setNonce(String nonce) {
      this.nonce = nonce;
   }

   public String getDeviceId() {
      return this.deviceId;
   }

   public void setDeviceId(String deviceId) {
      this.deviceId = deviceId;
   }

   public String getDeviceModel() {
      return this.deviceModel;
   }

   public void setDeviceModel(String deviceModel) {
      this.deviceModel = deviceModel;
   }

   public String getDeviceType() {
      return this.deviceType;
   }

   public void setDeviceType(String deviceType) {
      this.deviceType = deviceType;
   }

   public String getEseeId() {
      return this.eseeId;
   }

   public void setEseeId(String eseeId) {
      this.eseeId = eseeId;
   }

   public String getSoftwareVersion() {
      return this.softwareVersion;
   }

   public void setSoftwareVersion(String softwareVersion) {
      this.softwareVersion = softwareVersion;
   }

   public int getChannelCount() {
      return this.channelCount;
   }

   public void setChannelCount(int channelCount) {
      this.channelCount = channelCount;
   }

   public List<NVRResponseInfo.WiredInfo> getWired() {
      return this.wired;
   }

   public void setWired(List<NVRResponseInfo.WiredInfo> wired) {
      this.wired = wired;
   }

   public NVRResponseInfo.CapabilityInfo getCapabilities() {
      return this.capabilities;
   }

   public void setCapabilities(NVRResponseInfo.CapabilityInfo capabilities) {
      this.capabilities = capabilities;
   }

   public String toString() {
      return "NVRResponseInfo{ver='" + this.ver + '\'' + ", nonce='" + this.nonce + '\'' + ", deviceId='" + this.deviceId + '\'' + ", deviceModel='" + this.deviceModel + '\'' + ", deviceType='" + this.deviceType + '\'' + ", eseeId='" + this.eseeId + '\'' + ", softwareVersion='" + this.softwareVersion + '\'' + ", channelCount=" + this.channelCount + ", wired=" + this.wired + ", capabilities=" + this.capabilities + '}';
   }

   public static class CapabilityInfo {
      @SerializedName("Http-Port")
      private int httpPort;
      @SerializedName("MaxHardDiskDrivers")
      private int maxDiskDrivers;
      @SerializedName("MaxTFCards")
      private int maxTfCards;

      public int getHttpPort() {
         return this.httpPort;
      }

      public void setHttpPort(int httpPort) {
         this.httpPort = httpPort;
      }

      public int getMaxDiskDrivers() {
         return this.maxDiskDrivers;
      }

      public void setMaxDiskDrivers(int maxDiskDrivers) {
         this.maxDiskDrivers = maxDiskDrivers;
      }

      public int getMaxTfCards() {
         return this.maxTfCards;
      }

      public void setMaxTfCards(int maxTfCards) {
         this.maxTfCards = maxTfCards;
      }

      public String toString() {
         return "CapabilityInfo{httpPort=" + this.httpPort + ", maxDiskDrivers=" + this.maxDiskDrivers + ", maxTfCards=" + this.maxTfCards + '}';
      }
   }

   public static class WiredInfo {
      @SerializedName("DHCP")
      private boolean dhcp;
      @SerializedName("Connected")
      private boolean connected;
      private String IP;
      @SerializedName("Netmask")
      private String netMask;
      @SerializedName("Gateway")
      private String gateway;
      private String MAC;

      public boolean isDhcp() {
         return this.dhcp;
      }

      public void setDhcp(boolean dhcp) {
         this.dhcp = dhcp;
      }

      public boolean isConnected() {
         return this.connected;
      }

      public void setConnected(boolean connected) {
         this.connected = connected;
      }

      public String getIP() {
         return this.IP;
      }

      public void setIP(String IP) {
         this.IP = IP;
      }

      public String getNetMask() {
         return this.netMask;
      }

      public void setNetMask(String netMask) {
         this.netMask = netMask;
      }

      public String getGateway() {
         return this.gateway;
      }

      public void setGateway(String gateway) {
         this.gateway = gateway;
      }

      public String getMAC() {
         return this.MAC;
      }

      public void setMAC(String MAC) {
         this.MAC = MAC;
      }

      public String toString() {
         return "WiredInfo{dhcp=" + this.dhcp + ", connected=" + this.connected + ", IP='" + this.IP + '\'' + ", netMask='" + this.netMask + '\'' + ", gateway='" + this.gateway + '\'' + ", MAC='" + this.MAC + '\'' + '}';
      }
   }
}
