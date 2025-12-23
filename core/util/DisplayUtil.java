package com.eseeiot.core.util;

import android.text.TextUtils;
import android.util.Log;
import java.io.File;

public class DisplayUtil {
   public static final int MIN_MEMORY = 52428800;
   public static final int COMPRESS_MAX = 20;
   public static final int[] SPLIT_MODE = new int[]{1, 4, 6, 8, 9, 13, 16};
   private static final String TAG = "DisplayUtil";

   public static int getSplitMode(int cameraList) {
      int split = 0;

      for(int i = 0; i < SPLIT_MODE.length; ++i) {
         if (cameraList == SPLIT_MODE[i]) {
            split = i;
            break;
         }

         if (SPLIT_MODE[i] > cameraList) {
            split = i - 1;
            break;
         }
      }

      return split;
   }

   public static void compressImage(String fileName, int max) {
      if (!TextUtils.isEmpty(fileName)) {
         File file = new File(fileName);
         if (!file.exists()) {
            Log.d("DisplayUtil", "compressImage: ---->" + file);
         } else {
            if (file != null) {
               ImageUtil.compressImage(file, max);
            }

         }
      }
   }

   public static boolean enoughMemory(int min) {
      long maxMemory = Runtime.getRuntime().maxMemory();
      long freeMemory = Runtime.getRuntime().freeMemory();
      long usedMemory = Runtime.getRuntime().totalMemory() - freeMemory;
      long canUseMemory = maxMemory - usedMemory;
      Log.d("DisplayUtil", "hasEnoughMemoryToCapture: maxMemory = " + maxMemory + ", freeMemory = " + freeMemory + ", usedMemory = " + usedMemory + ", canUseMemory = " + canUseMemory);
      return canUseMemory > (long)min;
   }
}
