package com.eseeiot.basemodule.device.dispatcher;

import com.eseeiot.basemodule.device.base.MonitorCamera;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DeviceEventReceiver implements DeviceEventDispatchEntry {
   private MonitorDevice mDevice;
   private final List<DeviceEventListener> mListeners = new CopyOnWriteArrayList();
   private boolean mActive = true;

   public DeviceEventReceiver(MonitorDevice device) {
      this.mDevice = device;
   }

   public void active(boolean active) {
      this.mActive = active;
   }

   public boolean isActive() {
      return this.mActive;
   }

   public void release() {
      this.mListeners.clear();
   }

   public void registerEventCallback(DeviceEventListener listener) {
      if (!this.mListeners.contains(listener)) {
         this.mListeners.add(listener);
      }

   }

   public void unregisterEventCallback(DeviceEventListener listener) {
      this.mListeners.remove(listener);
   }

   public boolean hasRegisterEventCallback() {
      return !this.mListeners.isEmpty();
   }

   private int convert2RealChannel(String connectKey, int inChannel) {
      if (this.mDevice != null) {
         int i = 0;

         while(true) {
            MonitorCamera camera = this.mDevice.getCamera(i);
            if (camera == null) {
               break;
            }

            if (connectKey.equals(camera.getConnectKey()) && camera.getChannel() == inChannel) {
               return i;
            }

            ++i;
         }
      }

      return inChannel;
   }

   public void dispatchConnectEvent(String connectKey, int status, int channel) {
      if (this.mDevice.getConnectKey() == null) {
         channel = this.convert2RealChannel(connectKey, channel);
      } else if (channel >= this.mDevice.getChannelCount()) {
         return;
      }

      Iterator var4 = this.mListeners.iterator();

      while(true) {
         while(var4.hasNext()) {
            DeviceEventListener listener = (DeviceEventListener)var4.next();
            if (status != 17 && status > 12) {
               if (this.isRegisterParam(listener, 2)) {
                  listener.onConnectChanged(this.mDevice, status, channel);
               }
            } else if (this.isRegisterParam(listener, 1)) {
               listener.onConnectChanged(this.mDevice, status, channel);
            }
         }

         return;
      }
   }

   public boolean dispatchDisconnectEvent(String connectKey, int status, int channel) {
      boolean ret = false;
      Iterator var5 = this.mListeners.iterator();

      while(var5.hasNext()) {
         DeviceEventListener listener = (DeviceEventListener)var5.next();
         if (this.isRegisterParam(listener, 1) && listener.onDisconnected(this.mDevice, status, channel)) {
            ret = true;
         }
      }

      return ret;
   }

   public void dispatchOpenEvent(int status, int channel) {
      Iterator var3 = this.mListeners.iterator();

      while(var3.hasNext()) {
         DeviceEventListener listener = (DeviceEventListener)var3.next();
         if (this.isRegisterParam(listener, 2)) {
            listener.onOpenChanged(this.mDevice, status, channel);
         }
      }

   }

   public void dispatchOOBEvent(int installMode, int scene) {
      Iterator var3 = this.mListeners.iterator();

      while(var3.hasNext()) {
         DeviceEventListener listener = (DeviceEventListener)var3.next();
         if (this.isRegisterParam(listener, 2)) {
            listener.onOOBParamAvailable(installMode, scene);
         }
      }

   }

   public void dispatchFishParamEvent(float centerX, float centerY, float radius, float angleX, float angleY, float angleZ, byte[] angleData, int angleDataLen, int index) {
      Iterator var10 = this.mListeners.iterator();

      while(var10.hasNext()) {
         DeviceEventListener listener = (DeviceEventListener)var10.next();
         if (this.isRegisterParam(listener, 2)) {
            listener.onFishParamAvailable(centerX, centerY, radius, angleX, angleY, angleZ, angleData, angleDataLen, index);
         }
      }

   }

   public void dispatchGSensorParamEvent(long timestamp, double x, double y, double z) {
      Iterator var9 = this.mListeners.iterator();

      while(var9.hasNext()) {
         DeviceEventListener listener = (DeviceEventListener)var9.next();
         if (this.isRegisterParam(listener, 2)) {
            listener.onGSensorParamAvailable(timestamp, x, y, z);
         }
      }

   }

   public void dispatchCaptureEvent(String connectKey, int success, int channel, int requestCode) {
      if (this.mDevice == null || this.mDevice.getConnectKey() == null) {
         channel = this.convert2RealChannel(connectKey, channel);
      }

      Iterator var5 = this.mListeners.iterator();

      while(var5.hasNext()) {
         DeviceEventListener listener = (DeviceEventListener)var5.next();
         if (this.isRegisterParam(listener, 2)) {
            listener.onCaptureResult(success, channel, requestCode);
         }
      }

   }

   public void dispatchPlaybackOSD(String connectKey, int time, int index) {
      if (this.mDevice.getConnectKey() == null) {
         index = this.convert2RealChannel(connectKey, index);
      }

      Iterator var4 = this.mListeners.iterator();

      while(var4.hasNext()) {
         DeviceEventListener listener = (DeviceEventListener)var4.next();
         if (this.isRegisterParam(listener, 2)) {
            listener.onPlaybackOSDAvailable(time, index);
         }
      }

   }

   public void dispatchRecordDuration(String connectKey, long time, int channel) {
      if (this.mDevice.getConnectKey() == null) {
         channel = this.convert2RealChannel(connectKey, channel);
      }

      Iterator var5 = this.mListeners.iterator();

      while(var5.hasNext()) {
         DeviceEventListener listener = (DeviceEventListener)var5.next();
         if (this.isRegisterParam(listener, 2)) {
            listener.onRecordDuration(time, channel);
         }
      }

   }

   public void dispatchDownloadProgress(String connectKey, int total, int progress) {
      Iterator var4 = this.mListeners.iterator();

      while(var4.hasNext()) {
         DeviceEventListener listener = (DeviceEventListener)var4.next();
         if (this.isRegisterParam(listener, 2)) {
            listener.onDownloadProgress(connectKey, total, progress);
         }
      }

   }

   public void dispatchPTZSelfCheckBack(String connectKey, int ret, int channel) {
      Iterator var4 = this.mListeners.iterator();

      while(var4.hasNext()) {
         DeviceEventListener listener = (DeviceEventListener)var4.next();
         if (this.isRegisterParam(listener, 8)) {
            listener.onPTZSelfCheckBack(connectKey, ret, channel);
         }
      }

   }

   private boolean isRegisterParam(DeviceEventListener listener, int param) {
      int registerParam = listener.onRegisterParamGet();
      return (registerParam & param) > 0;
   }
}
