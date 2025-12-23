package com.eseeiot.core.animation;

import com.eseeiot.core.video.GLVideoRender;

public class Animation {
   public static final int JA_ANI_TYPE_POSITION = 0;
   public static final int JA_ANI_TYPE_SCALE = 1;
   public static final int JA_ANI_TYPE_ROTATE = 2;
   private GLVideoRender mRender;

   public Animation(GLVideoRender mRender) {
      this.mRender = mRender;
   }

   public long rotate(float angleX, float angleY, float angleZ, int step, int duration, boolean isloop, boolean isTexture, int index, boolean inertia, int msg) {
      float[] pend = new float[]{angleX, angleY, angleZ};
      return this.mRender.StartAnimation(this.mRender.mParametricManager, pend, step, duration, isloop, 2, isTexture, index, inertia, msg);
   }

   public long scale(float scale, int step, int duration, boolean isloop, boolean isTexture, int index, boolean inertia, int msg) {
      float[] pend = new float[]{scale, scale, scale};
      return this.mRender.StartAnimation(this.mRender.mParametricManager, pend, step, duration, isloop, 1, isTexture, index, inertia, msg);
   }

   public long position(float x, float y, float z, int step, int duration, boolean isloop, boolean isTexture, int index, boolean inertia, int msg) {
      float[] pend = new float[]{x, y, z};
      return this.mRender.StartAnimation(this.mRender.mParametricManager, pend, step, duration, isloop, 0, isTexture, index, inertia, msg);
   }
}
