package com.eseeiot.device;

import android.content.Context;
import android.text.TextUtils;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.util.TimeoutManager;
import com.eseeiot.core.connect.JAConnectorV2;
import com.eseeiot.device.pojo.DeviceInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeviceManager {
   List<MonitorDevice> mMonitorDeviceList;
   private static volatile DeviceManager sManager;
   private Context mContext;

   public static DeviceManager initialize(Context context) {
      if (sManager == null) {
         Class var1 = DeviceManager.class;
         synchronized(DeviceManager.class) {
            if (sManager == null) {
               sManager = new DeviceManager(context);
            }
         }
      }

      JAConnectorV2.initialize(false);
      TimeoutManager.initialize();
      return sManager;
   }

   public static DeviceManager getDefault() {
      return sManager;
   }

   private DeviceManager(Context context) {
      this.mContext = context.getApplicationContext();
      this.mMonitorDeviceList = new ArrayList();
   }

   public MonitorDevice createDevice(DeviceInfo deviceInfo) {
      MonitorDevice monitorDevice = (new DeviceBuilder()).setIotId(deviceInfo.getDeviceId()).setUsername(deviceInfo.getUsername()).setPassword(deviceInfo.getPwd()).setChannelCount(deviceInfo.getChannelCount()).setSerialID(deviceInfo.getSerialID()).build();
      this.mMonitorDeviceList.add(monitorDevice);
      return monitorDevice;
   }

   public MonitorDevice destroyDevice(String deviceId) {
      if (this.mMonitorDeviceList == null) {
         return null;
      } else {
         MonitorDevice targetDevice = null;
         Iterator var3 = this.mMonitorDeviceList.iterator();

         while(var3.hasNext()) {
            MonitorDevice tempDev = (MonitorDevice)var3.next();
            if (tempDev != null && TextUtils.equals(deviceId, tempDev.getConnectKey())) {
               targetDevice = tempDev;
            }
         }

         if (targetDevice != null) {
            this.mMonitorDeviceList.remove(targetDevice);
            targetDevice.release();
         }

         return targetDevice;
      }
   }

   public MonitorDevice destroyDevice(int position) {
      MonitorDevice monitorDevice = (MonitorDevice)this.mMonitorDeviceList.remove(position);
      monitorDevice.release();
      return monitorDevice;
   }

   public List<MonitorDevice> getDeviceList() {
      return this.mMonitorDeviceList;
   }

   public MonitorDevice getDevice(String deviceId) {
      for(int i = 0; i < this.mMonitorDeviceList.size(); ++i) {
         if (((MonitorDevice)this.mMonitorDeviceList.get(i)).getConnectKey().equals(deviceId)) {
            return (MonitorDevice)this.mMonitorDeviceList.get(i);
         }
      }

      return null;
   }

   public void resetList() {
      this.mMonitorDeviceList.clear();
   }
}
