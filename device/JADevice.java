package com.eseeiot.device;

import com.eseeiot.basemodule.connector.Connector;
import com.eseeiot.basemodule.device.IParamEntry;
import com.eseeiot.basemodule.device.base.MonitorCamera;
import com.eseeiot.basemodule.device.base.MonitorDevice;
import com.eseeiot.basemodule.device.common.DevProperty;
import com.eseeiot.basemodule.device.option.Options;
import com.eseeiot.basemodule.device.option.base.BaseOptionOperation;
import com.eseeiot.basemodule.listener.CommandResultListener;
import com.eseeiot.basemodule.player.Player;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class JADevice extends MonitorDevice {
   protected static Connector sConnector;

   protected JADevice(DevProperty devProp, int channelCount) {
      super(devProp, channelCount);
   }

   public List<MonitorCamera> createCamera(int channelCount) {
      List<MonitorCamera> cameras = new ArrayList();

      for(int i = 0; i < channelCount; ++i) {
         cameras.add(new JACamera(i));
      }

      return cameras;
   }

   public void initCamera() {
      if (this.mCameras != null) {
         for(int i = 0; i < this.mCameras.size(); ++i) {
            MonitorCamera camera = (MonitorCamera)this.mCameras.get(i);
            if (!(camera instanceof JACamera)) {
               throw new IllegalArgumentException("Illegal camera type");
            }

            camera.bind(this);
            if (i == 0) {
               camera.registerEntry(this.mEventEntry, (IParamEntry)null);
            }
         }
      }

   }

   protected String checkParam() {
      return !(this.mProperty instanceof JADevProperty) ? "Illegal property!" : null;
   }

   public Connector getConnector() {
      return sConnector;
   }

   public Player createLivePlayer(int tag) {
      return null;
   }

   public Player createPlaybackPlayer(int tag) {
      return null;
   }

   public void release() {
      if (this.mCameras != null) {
         for(int i = 0; i < this.mCameras.size(); ++i) {
            MonitorCamera camera = (MonitorCamera)this.mCameras.get(i);
            if (i == 0) {
               camera.unregisterEntry(this.mEventEntry, (IParamEntry)null);
            }

            camera.unbind();
         }
      }

      super.release();
   }

   public Options getOptions(int... channel) {
      if (this.mOptions == null) {
         try {
            Class<?> clazz = Class.forName("com.eseeiot.option.JAOption");
            Object instance = clazz.newInstance();
            this.mOptions = (Options)instance;
            this.mOptions.bindDevice(this);
            this.mOptions.setChannel(0);
         } catch (ClassNotFoundException var4) {
         } catch (IllegalAccessException var5) {
         } catch (InstantiationException var6) {
         }
      }

      return this.mOptions;
   }

   public BaseOptionOperation getOptionHelper() {
      if (this.mOptionHelper == null) {
         try {
            Class<?> clazz = Class.forName("com.eseeiot.option.JAOptionHelper");
            Constructor<?> constructor = clazz.getConstructor(Options.class);
            if (constructor != null) {
               this.mOptionHelper = (BaseOptionOperation)constructor.newInstance(this.getOptions());
            }
         } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
         } catch (IllegalAccessException var4) {
            var4.printStackTrace();
         } catch (InstantiationException var5) {
            var5.printStackTrace();
         } catch (NoSuchMethodException var6) {
            var6.printStackTrace();
         } catch (InvocationTargetException var7) {
            var7.printStackTrace();
         }
      }

      return this.mOptionHelper;
   }

   public String getConnectKey() {
      return this.mProperty.getConnectKey();
   }

   public String getSerialId() {
      return this.mProperty.getSerialId();
   }

   public String getVerify(boolean strict) {
      return this.mProperty.getVerify(strict);
   }

   public final int connect(int... channel) {
      if (channel.length > 0) {
         return this.operateConnection(true, channel[0]);
      } else {
         if (this.mCameras != null) {
            this.connect(0, this.mCameras.size() - 1);
         }

         return 0;
      }
   }

   public final int connect(int beginChannel, int endChannel) {
      for(int ch = beginChannel; ch <= endChannel; ++ch) {
         this.operateConnection(true, ch);
      }

      return 0;
   }

   public final void disconnect(int... channel) {
      if (channel.length > 0) {
         this.operateConnection(false, channel[0]);
      } else if (this.mCameras != null) {
         this.disconnect(0, this.mCameras.size() - 1);
      }

   }

   public final void disconnect(int beginChannel, int endChannel) {
      for(int ch = beginChannel; ch <= endChannel; ++ch) {
         this.operateConnection(false, ch);
      }

   }

   private int operateConnection(boolean connect, int channel) {
      MonitorCamera camera = this.getCamera(channel);
      if (camera != null) {
         if (connect) {
            return camera.connect();
         } else {
            camera.disconnect();
            return 0;
         }
      } else {
         return -5004;
      }
   }

   public final int openStream(int bitrate, int channel) {
      return this.openStream(bitrate, channel, channel);
   }

   public final int openStream(int bitrate, int channel, int renderIndex) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.openStream(bitrate, renderIndex) : -5004;
   }

   public final int closeStream(int bitrate, int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.closeStream(bitrate) : -5004;
   }

   public final int startPlayback(int time, int channel) {
      return this.startPlayback(time, channel, channel);
   }

   public final int startPlayback(int time, int channel, int renderIndex) {
      MonitorCamera camera = this.getCamera(channel);
      if (camera == null) {
         return -5004;
      } else {
         Boolean isSupportMultiRecType = camera.getOptions().isSupportMultiRecType() != null && camera.getOptions().isSupportMultiRecType();
         return camera.startPlayback(time, isSupportMultiRecType ? '\uffff' : 15, renderIndex);
      }
   }

   public final int stopPlayback(int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.stopPlayback() : -5004;
   }

   public final int pausePlayback(int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.pausePlayback() : -5004;
   }

   public final int resumePlayback(int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.resumePlayback() : -5004;
   }

   public final int sendGettingData(String data, int channel, Object entry, CommandResultListener listener) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.sendGettingData(data, entry, listener) : -5004;
   }

   public int sendSettingData(String data, int channel, Object entry, CommandResultListener listener) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.sendSettingData(data, entry, listener) : -5004;
   }

   public int sendData(int[] header, byte[] data, int channel, Object entry) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.sendData(header, data, entry) : -5004;
   }

   public void cancelVcon(Object entry) {
      MonitorCamera camera = this.getCamera(0);
      if (camera != null) {
         camera.unregisterVconEntry(entry);
      }
   }

   public int sendAudioPacket(byte[] buffer, int size, long tsMs, String enc, int sampleRate, int sampleWidth, int chn, float compressRatio, int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.sendAudioPacket(buffer, size, tsMs, enc, sampleRate, sampleWidth, chn, compressRatio) : -5004;
   }

   public final int ptzControl(int action, int param1, int param2, int param3, int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.ptzControl(action, param1, param2, param3) : -5004;
   }

   public final int startCaptureImage(String absoluteOutputFilePath, int type, int requestCode, int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.startCaptureImage(absoluteOutputFilePath, type, requestCode) : -5004;
   }

   public final int cancelCaptureImage(int requestCode, int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.cancelCaptureImage(requestCode) : -5004;
   }

   public final int startRecord(String absoluteOutputFilePath, int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.startRecord(absoluteOutputFilePath) : -5004;
   }

   public final int stopRecord(int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.stopRecord() : -5004;
   }

   public int startBackup(int startTime, int endTime, String fileName, int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.startBackup(startTime, endTime, fileName, 0) : -5004;
   }

   public int stopBackup(int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.stopBackup(0) : -5004;
   }

   public final int setTimezone(int timezone, int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.setTimezone(timezone) : -5004;
   }

   public final int setOSDFormat(int format, int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.setOSDFormat(format) : -5004;
   }

   public final boolean enableHardwareDecoder(boolean enable, int width, int height, int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null && camera.isBound() ? camera.enableHardwareDecoder(enable, width, height) : false;
   }

   public int enableDecodeIFrameOnly(boolean enable, int channel) {
      if (this.mCameras != null) {
         for(int i = 0; i < this.mCameras.size(); ++i) {
            MonitorCamera camera = (MonitorCamera)this.mCameras.get(i);
            camera.enableDecodeIFrameOnly(enable);
         }
      }

      return 0;
   }

   public int setPlayMode(int mode) {
      return sConnector.setPlayMode(this.getConnectKey(), mode);
   }

   public final void setPlayAudioIndex(int index) {
      sConnector.setPlayAudioIndex(index);
   }

   public int getAllStreamSpeed() {
      return sConnector.getAllStreamSpeed(this.getConnectKey());
   }

   public static int getTutkOnlineStatus(String tutkId, int timeout, CommandResultListener listener) {
      return sConnector.getTutkOnlineStatus(tutkId, timeout, listener);
   }

   public final long getConnectionContext(int channel) {
      MonitorCamera camera = this.getCamera(channel);
      return camera != null ? camera.getConnectionContext() : -5004L;
   }
}
