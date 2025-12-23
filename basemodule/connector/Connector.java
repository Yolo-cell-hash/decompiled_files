package com.eseeiot.basemodule.connector;

import androidx.annotation.NonNull;
import com.eseeiot.basemodule.device.IParamEntry;
import com.eseeiot.basemodule.device.dispatcher.DeviceEventDispatchEntry;
import com.eseeiot.basemodule.listener.CommandResultListener;
import com.eseeiot.basemodule.player.APlay;

public interface Connector {
   void register(String var1, DeviceEventDispatchEntry var2, IParamEntry var3);

   void unregister(String var1, DeviceEventDispatchEntry var2, IParamEntry var3);

   void unregisterVcon(String var1, Object var2);

   void bindPlayer(APlay var1);

   boolean unbindPlayer(APlay var1);

   boolean isConnected(String var1, int var2);

   boolean isConnecting(String var1, int var2);

   boolean isOffline(String var1, int var2);

   boolean isDisconnected(String var1, int var2);

   boolean isAuthFailed(String var1, int var2);

   int connect(String var1, String var2, String var3, int var4, int var5, CommandResultListener var6);

   void disconnect(String var1, int var2, CommandResultListener var3);

   int openStream(String var1, int var2, int var3, int var4, CommandResultListener var5);

   int closeStream(String var1, int var2, int var3, CommandResultListener var4);

   int startPlayback(String var1, int var2, int var3, int var4, int var5, CommandResultListener var6);

   int stopPlayback(String var1, int var2, CommandResultListener var3);

   int pausePlayback(String var1, int var2, CommandResultListener var3);

   int resumePlayback(String var1, int var2, CommandResultListener var3);

   int startBackup(String var1, int var2, int var3, String var4, int var5, int var6);

   int stopBackup(String var1, int var2, int var3);

   int searchRecord(String var1, int var2, int var3, int var4, int var5, int var6, Object var7, CommandResultListener var8);

   int searchRecordPage(String var1, int var2, int var3, int var4, int var5, int var6, int var7, Object var8);

   int sendGettingData(String var1, String var2, int var3, Object var4, CommandResultListener var5);

   int sendSettingData(String var1, String var2, int var3, Object var4, CommandResultListener var5);

   int sendData(String var1, int[] var2, byte[] var3, int var4, Object var5);

   int call(String var1, int var2);

   int hangup(String var1, int var2);

   int sendAudioPacket(String var1, byte[] var2, int var3, long var4, String var6, int var7, int var8, int var9, float var10, int var11);

   int ptzControl(String var1, int var2, int var3, int var4, int var5, int var6);

   int startCaptureImage(String var1, String var2, int var3, int var4, int var5);

   int cancelCaptureImage(String var1, int var2, int var3);

   int startRecord(String var1, String var2, int var3);

   int stopRecord(String var1, int var2);

   int setTimezone(String var1, int var2, int var3);

   int setOSDFormat(String var1, int var2, int var3);

   boolean enableHardwareDecoder(String var1, boolean var2, int var3, int var4, int var5);

   int enableDecodeIFrameOnly(String var1, boolean var2, int var3);

   int setPlayMode(String var1, int var2);

   void setPlayAudioIndex(int var1);

   long getConnectionContext(String var1, int var2);

   int getStreamSpeed(String var1, int var2);

   int getAllStreamSpeed(String var1);

   int getTutkOnlineStatus(String var1, int var2, @NonNull CommandResultListener var3);

   int getConnectCount(String var1);
}
