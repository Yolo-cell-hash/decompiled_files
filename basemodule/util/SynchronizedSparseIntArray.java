package com.eseeiot.basemodule.util;

import android.util.SparseIntArray;

public class SynchronizedSparseIntArray extends SparseIntArray {
   public synchronized void put(int key, int value) {
      super.put(key, value);
   }
}
