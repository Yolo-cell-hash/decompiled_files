package com.eseeiot.basemodule.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public class BitmapUtil {
   public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
      int width = bm.getWidth();
      int height = bm.getHeight();
      float scaleWidth = (float)newWidth / (float)width;
      float scaleHeight = (float)newHeight / (float)height;
      Matrix matrix = new Matrix();
      matrix.postScale(scaleWidth, scaleHeight);
      Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
      return newbm;
   }

   public static Bitmap drawable2Bitmap(Drawable drawable) {
      Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
      drawable.draw(canvas);
      return bitmap;
   }

   public static Bitmap rotateBitmap(Bitmap origin, float rotate) {
      if (origin != null && !origin.isRecycled()) {
         int width = origin.getWidth();
         int height = origin.getHeight();
         Matrix matrix = new Matrix();
         matrix.setRotate(rotate);
         Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
         if (newBM.equals(origin)) {
            return newBM;
         } else {
            origin.recycle();
            return newBM;
         }
      } else {
         return origin;
      }
   }

   public static BitmapDrawable rotateBitmap(BitmapDrawable origin, float rotate) {
      if (origin == null) {
         return null;
      } else {
         Bitmap bitmap = rotateBitmap(origin.getBitmap(), rotate);
         return new BitmapDrawable((Resources)null, bitmap);
      }
   }

   public static Bitmap blurBitmap(Context context, Bitmap bitmap, float radius) {
      if (bitmap != null && !bitmap.isRecycled()) {
         Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
         RenderScript rs = RenderScript.create(context);
         ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
         Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
         Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
         blurScript.setRadius(radius);
         blurScript.setInput(allIn);
         blurScript.forEach(allOut);
         allOut.copyTo(outBitmap);
         rs.destroy();
         return outBitmap;
      } else {
         return bitmap;
      }
   }

   public static Bitmap roundBitmap(Context context, @DrawableRes int resId, float radius) {
      if (context == null) {
         return null;
      } else {
         Bitmap inBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
         return inBitmap == null ? null : roundBitmap(inBitmap, radius);
      }
   }

   public static Bitmap roundBitmap(@NonNull Bitmap bitmap, float radius) {
      if (bitmap.isRecycled()) {
         return bitmap;
      } else {
         Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
         Canvas canvas = new Canvas(outBitmap);
         Paint paint = new Paint();
         paint.setAntiAlias(true);
         Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
         RectF rectF = new RectF(rect);
         canvas.drawRoundRect(rectF, radius, radius, paint);
         paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
         canvas.drawBitmap(bitmap, rect, rect, paint);
         bitmap.recycle();
         return outBitmap;
      }
   }

   public static Bitmap crop4To3Bitmap(@NonNull Bitmap bitmap) {
      if (bitmap.isRecycled()) {
         return bitmap;
      } else {
         int width = bitmap.getWidth();
         int height = bitmap.getHeight();
         if ((float)width * 1.0F / (float)height == 1.3333334F) {
            return bitmap;
         } else {
            int nWith = (int)(1.3333334F * (float)height);
            int offset = (int)((float)(width - nWith) / 2.0F);
            if (offset >= 0 && height >= 0) {
               Bitmap nBitmap = Bitmap.createBitmap(bitmap, offset, 0, nWith, height);
               if (!bitmap.equals(nBitmap)) {
                  bitmap.recycle();
               }

               return nBitmap;
            } else {
               return null;
            }
         }
      }
   }

   public static Bitmap loadBitmap(String path, int scale) {
      return loadBitmap(path, scale, 0);
   }

   public static Bitmap loadBitmap(String path, int scale, int minWidth) {
      if (TextUtils.isEmpty(path)) {
         return null;
      } else {
         Options options = new Options();
         options.inJustDecodeBounds = true;
         BitmapFactory.decodeFile(path, options);
         if (options.outWidth == 0) {
            return null;
         } else {
            if (minWidth > 0 && options.outWidth <= minWidth) {
               options.inSampleSize = 1;
            } else {
               options.inSampleSize = scale;
            }

            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, options);
         }
      }
   }
}
