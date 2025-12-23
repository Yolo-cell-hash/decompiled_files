package com.eseeiot.setup.pojo;

public class OldNVRResponseInfo {
   private String ipAddr;
   private String eseeId;
   private int port;
   private int httpPort;
   private int channelCount;
   private String model;
   private String ver;

   public String getIpAddr() {
      return this.ipAddr;
   }

   public void setIpAddr(String ipAddr) {
      this.ipAddr = ipAddr;
   }

   public String getEseeId() {
      return this.eseeId;
   }

   public void setEseeId(String eseeId) {
      this.eseeId = eseeId;
   }

   public int getPort() {
      return this.port;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public int getHttpPort() {
      return this.httpPort;
   }

   public void setHttpPort(int httpPort) {
      this.httpPort = httpPort;
   }

   public int getChannelCount() {
      return this.channelCount;
   }

   public void setChannelCount(int channelCount) {
      this.channelCount = channelCount;
   }

   public String getModel() {
      return this.model;
   }

   public void setModel(String model) {
      this.model = model;
   }

   public String getVer() {
      return this.ver;
   }

   public void setVer(String ver) {
      this.ver = ver;
   }

   public String toString() {
      return "OldNVRResponseInfo{ipAddr='" + this.ipAddr + '\'' + ", eseeId='" + this.eseeId + '\'' + ", port=" + this.port + ", httpPort=" + this.httpPort + ", channelCount=" + this.channelCount + ", model='" + this.model + '\'' + ", ver='" + this.ver + '\'' + '}';
   }
}
