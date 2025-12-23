package com.eseeiot.core.HWCodec;

public interface IFrameCallback {
   void onPrepared();

   void onFinished();

   boolean onFrameAvailable(long var1, boolean var3);

   void OnAudioData(byte[] var1);
}
