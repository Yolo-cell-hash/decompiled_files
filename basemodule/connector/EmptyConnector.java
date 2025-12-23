package com.eseeiot.basemodule.connector;

import androidx.annotation.NonNull;
import com.eseeiot.basemodule.device.IParamEntry;
import com.eseeiot.basemodule.device.dispatcher.DeviceEventDispatchEntry;
import com.eseeiot.basemodule.listener.CommandResultListener;
import com.eseeiot.basemodule.player.APlay;

public class EmptyConnector implements Connector {
   private static final String TAG = "EmptyConnector";

   public void register(String connectKey, DeviceEventDispatchEntry entry, IParamEntry entry2) {
   }

   public void unregister(String connectKey, DeviceEventDispatchEntry entry, IParamEntry entry2) {
   }

   public void unregisterVcon(String connectKey, Object entry) {
   }

   public void bindPlayer(APlay aPlay) {
   }

   public boolean unbindPlayer(APlay aPlay) {
      return false;
   }

   public boolean isConnected(String connectKey, int channel) {
      return false;
   }

   public boolean isConnecting(String connectKey, int channel) {
      return false;
   }

   public boolean isOffline(String connectKey, int channel) {
      return false;
   }

   public boolean isDisconnected(String connectKey, int channel) {
      return false;
   }

   public boolean isAuthFailed(String connectKey, int channel) {
      return false;
   }

   public int connect(String connectKey, String serialId, String verify, int channelCount, int channel, CommandResultListener listener) {
      return 0;
   }

   public void disconnect(String connectKey, int channel, CommandResultListener listener) {
   }

   public int openStream(String connectKey, int bitrate, int channel, int renderIndex, CommandResultListener listener) {
      return 0;
   }

   public int closeStream(String connectKey, int bitrate, int channel, CommandResultListener listener) {
      return 0;
   }

   public int startPlayback(String connectKey, int time, int type, int channel, int renderIndex, CommandResultListener listener) {
      return 0;
   }

   public int stopPlayback(String connectKey, int channel, CommandResultListener listener) {
      return 0;
   }

   public int pausePlayback(String connectKey, int channel, CommandResultListener listener) {
      return 0;
   }

   public int resumePlayback(String connectKey, int channel, CommandResultListener listener) {
      return 0;
   }

   public int startBackup(String connectKey, int startTime, int endTime, String fileName, int offsetTime, int channel) {
      return 0;
   }

   public int stopBackup(String connectKey, int startTime, int channel) {
      return 0;
   }

   public int searchRecord(String connectKey, int startTime, int endTime, int type, int channel, int requestCode, Object object, CommandResultListener listener) {
      return 0;
   }

   public int searchRecordPage(String connectKey, int startTime, int endTime, int channel, int pageNum, int pageSize, int requestCode, Object object) {
      return 0;
   }

   public int sendGettingData(String connectKey, String data, int channel, Object entry, CommandResultListener listener) {
      return 0;
   }

   public int sendSettingData(String connectKey, String data, int channel, Object entry, CommandResultListener listener) {
      return 0;
   }

   public int sendData(String connectKey, int[] header, byte[] data, int channel, Object entry) {
      return 0;
   }

   public int call(String connectKey, int channel) {
      return 0;
   }

   public int hangup(String connectKey, int channel) {
      return 0;
   }

   public int sendAudioPacket(String connectKey, byte[] buffer, int size, long tsMs, String enc, int sampleRate, int sampleWidth, int chn, float compressRatio, int channel) {
      return 0;
   }

   public int ptzControl(String connectKey, int action, int param1, int param2, int param3, int channel) {
      return 0;
   }

   public int startCaptureImage(String connectKey, String absoluteOutputFilePath, int type, int requestCode, int channel) {
      return 0;
   }

   public int cancelCaptureImage(String connectKey, int requestCode, int channel) {
      return 0;
   }

   public int startRecord(String connectKey, String absoluteOutputFilePath, int channel) {
      return 0;
   }

   public int stopRecord(String connectKey, int channel) {
      return 0;
   }

   public int setTimezone(String connectKey, int timezone, int channel) {
      return 0;
   }

   public int setOSDFormat(String connectKey, int format, int channel) {
      return 0;
   }

   public boolean enableHardwareDecoder(String connectKey, boolean enable, int width, int height, int channel) {
      return false;
   }

   public int enableDecodeIFrameOnly(String connectKey, boolean enable, int channel) {
      return 0;
   }

   public int setPlayMode(String connectKey, int mode) {
      return 0;
   }

   public void setPlayAudioIndex(int index) {
   }

   public long getConnectionContext(String connectKey, int channel) {
      return 0L;
   }

   public int getStreamSpeed(String connectKey, int channel) {
      return 0;
   }

   public int getAllStreamSpeed(String connectKey) {
      return 0;
   }

   public int getTutkOnlineStatus(String tutkId, int timeout, @NonNull CommandResultListener listener) {
      return 0;
   }

   public int getConnectCount(String connectKey) {
      return 0;
   }
}
