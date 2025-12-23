package com.eseeiot.basemodule.device.common;

import java.util.Map;

public abstract class DevProperty {
   public String user;
   public String password;
   public String verify;
   protected Map<String, String> thirdProperty;
   protected Object extra;

   public abstract String getUID();

   public abstract String getConnectKey();

   public String getSerialId() {
      return null;
   }

   public abstract Map<String, String> getThirdProperty();

   public abstract String getVerify(boolean var1);

   public abstract int getPort();

   public Object getExtra() {
      return this.extra;
   }

   public void setExtra(Object object) {
      this.extra = object;
   }
}
