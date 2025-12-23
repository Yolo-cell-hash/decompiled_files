package com.eseeiot.core.util;

import android.util.Log;

public class Memory {
   public static final int MIN_MEMORY = 52428800;

   public static boolean hasEnoughMemory(int size) {
      long maxMemory = Runtime.getRuntime().maxMemory();
      long freeMemory = Runtime.getRuntime().freeMemory();
      long usedMemory = Runtime.getRuntime().totalMemory() - freeMemory;
      long canUseMemory = maxMemory - usedMemory;
      Log.d("Memory", "hasEnoughMemory: maxMemory = " + maxMemory + ", freeMemory = " + freeMemory + ", usedMemory = " + usedMemory + ", canUseMemory = " + canUseMemory);
      return canUseMemory >= (long)size;
   }
}
