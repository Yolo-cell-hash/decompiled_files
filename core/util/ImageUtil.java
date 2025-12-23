package com.eseeiot.core.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ImageUtil {
   private static int maxWidth = 640;
   private static int maxHeight = 480;
   private static int quality = 80;
   private static final String TAG = "ImageUtil";

   public static void compressImage(File file, int max) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      Options options = new Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(file.getAbsolutePath(), options);
      options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
      options.inJustDecodeBounds = false;

      try {
         Bitmap scaledBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
         scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), (Matrix)null, true);
         scaledBitmap.compress(CompressFormat.JPEG, quality, baos);
         FileOutputStream fos = new FileOutputStream(file);
         fos.write(baos.toByteArray());
         fos.flush();
         fos.close();
         scaledBitmap.recycle();
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public static boolean hasEnoughMemoryToCapture(int needSize) {
      long maxMemory = Runtime.getRuntime().maxMemory();
      long freeMemory = Runtime.getRuntime().freeMemory();
      long usedMemory = Runtime.getRuntime().totalMemory() - freeMemory;
      long canUseMemory = maxMemory - usedMemory;
      Log.d("ImageUtil", "hasEnoughMemoryToCapture: maxMemory = " + maxMemory + ", freeMemory = " + freeMemory + ", usedMemory = " + usedMemory + ", canUseMemory = " + canUseMemory);
      return canUseMemory >= (long)needSize;
   }

   public static Bitmap loadBitmap(String path) {
      Options options = new Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(path, options);
      int w = options.outWidth;
      int h = options.outHeight;
      if (hasEnoughMemoryToCapture(w * h * 4)) {
         options.inJustDecodeBounds = false;
         return BitmapFactory.decodeFile(path, options);
      } else {
         return null;
      }
   }

   public static Bitmap loadBitmap(Resources res, int id) {
      Options options = new Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeResource(res, id);
      int w = options.outWidth;
      int h = options.outHeight;
      if (hasEnoughMemoryToCapture(w * h * 4)) {
         options.inJustDecodeBounds = false;
         return BitmapFactory.decodeResource(res, id, options);
      } else {
         return null;
      }
   }

   private static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
      int height = options.outHeight;
      int width = options.outWidth;
      int inSampleSize = 1;
      if (height > reqHeight || width > reqWidth) {
         int halfHeight = height / 2;

         for(int halfWidth = width / 2; halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth; inSampleSize *= 2) {
         }
      }

      return inSampleSize;
   }
}
