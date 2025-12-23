package com.eseeiot.core.listener;

import android.view.ScaleGestureDetector;

public interface GestureListener {
   void onSingleClick(int var1, int var2, int var3, boolean var4, int var5, int var6);

   void onDoubleClick(int var1, int var2, int var3, boolean var4, int var5, int var6);

   void onPageChange(int var1, int var2, int var3, boolean var4, int var5, int var6);

   void onScale(ScaleGestureDetector var1);

   void onScroll();

   void onUp();
}
