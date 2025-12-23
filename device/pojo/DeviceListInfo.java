package com.eseeiot.device.pojo;

import java.io.Serializable;
import java.util.List;

public class DeviceListInfo implements Serializable {
   private List<DeviceInfo> deviceInfoList;

   public List<DeviceInfo> getDeviceInfoList() {
      return this.deviceInfoList;
   }

   public void setDeviceInfoList(List<DeviceInfo> deviceInfoList) {
      this.deviceInfoList = deviceInfoList;
   }
}
