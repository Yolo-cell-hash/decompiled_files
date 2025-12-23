package com.eseeiot.basemodule.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtil {
   public static float px2dip(Context context, float pxValue) {
      float scale = context.getResources().getDisplayMetrics().density;
      return pxValue / scale + 0.5F;
   }

   public static float px2sp(Context context, float pxValue) {
      float scale = context.getResources().getDisplayMetrics().scaledDensity;
      return pxValue / scale + 0.5F;
   }

   public static float dip2px(Context context, float dpValue) {
      float scale = context.getResources().getDisplayMetrics().density;
      return dpValue * scale + 0.5F;
   }

   public static int sp2px(Context context, float spValue) {
      float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
      return (int)(spValue * fontScale + 0.5F);
   }

   public static DisplayMetrics getDisplayMetrics(Context context) {
      return context.getResources().getDisplayMetrics();
   }
}
