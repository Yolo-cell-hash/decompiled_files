package com.eseeiot.device;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.eseeiot.basemodule.device.common.DevProperty;
import java.util.Map;

public class JADevProperty extends DevProperty {
   public String eseeId;
   public String tutkId;
   public String serialId;
   public String system;
   public String ddns;
   public int port = -1;

   @NonNull
   public DevProperty clone() {
      JADevProperty property = new JADevProperty();
      property.eseeId = this.eseeId;
      property.tutkId = this.tutkId;
      property.serialId = this.serialId;
      property.system = this.system;
      property.user = this.user;
      property.password = this.password;
      property.verify = this.verify;
      property.port = this.port;
      return property;
   }

   public final String getUID() {
      return this.eseeId;
   }

   public final String getConnectKey() {
      return !TextUtils.isEmpty(this.tutkId) ? this.tutkId : this.eseeId;
   }

   public String getSerialId() {
      return this.serialId;
   }

   public Map<String, String> getThirdProperty() {
      return null;
   }

   public final String getVerify(boolean strict) {
      if (!TextUtils.isEmpty(this.verify)) {
         return this.verify;
      } else {
         return !strict ? this.user + ":" + this.password : null;
      }
   }

   public String getDDNS() {
      return this.ddns;
   }

   public int getPort() {
      return this.port;
   }
}
