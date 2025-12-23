package com.eseeiot.setup.pojo;

public class ConnectInfo {
   private String id;
   private String serialId;
   private int status = -1;
   private boolean hasReceiveConnectingStatus;
   private int authFailedCount;
   private boolean isNotConnectYet = true;
   private String mVerify;

   public ConnectInfo() {
      this.reset();
   }

   public void reset() {
      this.authFailedCount = 0;
      this.hasReceiveConnectingStatus = true;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      if (this.id != null && !this.id.equals(id)) {
         this.isNotConnectYet = true;
      }

      this.id = id;
   }

   public String getSerialId() {
      return this.serialId;
   }

   public void setSerialId(String serialId) {
      this.serialId = serialId;
   }

   public int getStatus() {
      return this.status;
   }

   public void setStatus(int status) {
      this.status = status;
   }

   public boolean hasReceiveConnectingStatus() {
      return this.hasReceiveConnectingStatus;
   }

   public void setHasReceiveConnectingStatus(boolean hasReceiveConnectingStatus) {
      this.hasReceiveConnectingStatus = hasReceiveConnectingStatus;
   }

   public int getAuthFailedCount() {
      return this.authFailedCount;
   }

   public void setAuthFailedCount(int authFailedCount) {
      this.authFailedCount = authFailedCount;
   }

   public void addAuthFailedCount() {
      ++this.authFailedCount;
   }

   public boolean isNotConnectYet() {
      return this.isNotConnectYet;
   }

   public void setNotConnectYet(boolean notConnectYet) {
      this.isNotConnectYet = notConnectYet;
   }

   public String getVerify() {
      return this.mVerify;
   }

   public void setVerify(String verify) {
      this.mVerify = verify;
   }

   public String toString() {
      return "ConnectInfo{id='" + this.id + '\'' + ", status=" + this.status + ", hasReceiveConnectingStatus=" + this.hasReceiveConnectingStatus + ", authFailedCount=" + this.authFailedCount + ", isNotConnectYet=" + this.isNotConnectYet + ", mVerify='" + this.mVerify + '\'' + '}';
   }
}
