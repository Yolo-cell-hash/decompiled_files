package com.eseeiot.core.HWCodec;

import android.util.Log;

public class PreRenderClass {
   private static final String TAG = "PreRenderClass";
   private static final long ONE_MILLION = 1000000L;
   private static final boolean CHECK_SLEEP_TIME = false;
   private long prevPresentUs;
   private long prevMonoUs;
   private boolean loopReset;
   private long fixedFrameDurationUs;
   private long waitTimeMs;
   private int waitTimeNs;
   private long outputTimeMs;
   private int outputTimeNs;

   public PreRenderClass() {
      this.reset(true);
   }

   public void reset(boolean all) {
      if (all) {
         this.prevMonoUs = 0L;
      }

      this.waitTimeMs = 0L;
      this.waitTimeNs = 0;
   }

   public long[] waitTime() {
      return new long[]{this.waitTimeMs, (long)this.waitTimeNs};
   }

   public void update(long sleepTimeMs, int sleepTimeNs) {
      if (this.waitTimeMs > 0L || this.waitTimeNs > 0) {
         this.waitTimeMs -= sleepTimeMs;
         this.waitTimeNs -= sleepTimeNs;
         if (this.waitTimeMs == 0L) {
            this.waitTimeMs = 0L;
         }

         if (this.waitTimeNs < 0) {
            this.waitTimeNs = 0;
         }
      }

   }

   public boolean waitOver() {
      if (this.waitTimeMs == 0L && this.waitTimeNs == 0) {
         return true;
      } else {
         long currentTimeUs = System.nanoTime() / 1000L;
         long currentTimeMs = currentTimeUs / 1000L;
         int currentTimeNs = (int)(currentTimeUs % 1000L) * 1000;
         long deltaMs = currentTimeMs - this.outputTimeMs;
         int deltaNs = currentTimeNs - this.outputTimeNs;
         return deltaMs == this.waitTimeMs && deltaNs > this.waitTimeNs || deltaMs > this.waitTimeMs;
      }
   }

   public void mark(long presentationTimeUs) {
      this.waitTimeMs = 0L;
      this.waitTimeNs = 0;
      if (this.prevMonoUs == 0L) {
         this.prevMonoUs = System.nanoTime() / 1000L;
         this.prevPresentUs = presentationTimeUs;
      } else {
         if (this.loopReset) {
            this.prevPresentUs = presentationTimeUs - 33333L;
            this.loopReset = false;
         }

         long frameDelta;
         if (this.fixedFrameDurationUs != 0L) {
            frameDelta = this.fixedFrameDurationUs;
         } else {
            frameDelta = presentationTimeUs - this.prevPresentUs;
         }

         if (frameDelta < 0L) {
            Log.w("PreRenderClass", "Weird, video times went backward");
            frameDelta = 0L;
         } else if (frameDelta == 0L) {
            Log.i("PreRenderClass", "Warning: current frame and previous frame had same timestamp");
         } else if (frameDelta > 10000000L) {
            Log.i("PreRenderClass", "Inter-frame pause was " + frameDelta / 1000000L + "sec, capping at 5 sec");
            frameDelta = 5000000L;
         }

         long desiredUs = this.prevMonoUs + frameDelta;
         long nowUs = System.nanoTime() / 1000L;
         this.outputTimeMs = nowUs / 1000L;

         long sleepTimeUs;
         for(this.outputTimeNs = (int)(nowUs % 1000L) * 1000; nowUs < desiredUs - 100L; nowUs += sleepTimeUs) {
            sleepTimeUs = desiredUs - nowUs;
            if (sleepTimeUs > 500000L) {
               sleepTimeUs = 500000L;
            }

            this.waitTimeMs += sleepTimeUs / 1000L;
            this.waitTimeNs += (int)(sleepTimeUs % 1000L) * 1000;
         }

         this.prevMonoUs += frameDelta;
         this.prevPresentUs += frameDelta;
      }

   }
}
