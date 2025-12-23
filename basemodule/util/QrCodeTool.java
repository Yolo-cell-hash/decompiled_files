package com.eseeiot.basemodule.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.nio.charset.StandardCharsets;

public class QrCodeTool {
   public static Bitmap createQRCodeImage(String encodeMsg, int picWidth, int picHeight) {
      byte[] temp = null;
      byte[] temp = encodeMsg.getBytes(StandardCharsets.UTF_8);
      String Msg = new String(temp);
      BitMatrix matrix = null;

      try {
         matrix = (new MultiFormatWriter()).encode(Msg, BarcodeFormat.QR_CODE, picWidth, picHeight);
      } catch (WriterException var12) {
         var12.printStackTrace();
      }

      if (matrix == null) {
         return null;
      } else {
         int whitePixelOffset = 0;

         int width;
         for(width = 0; width < matrix.getHeight(); ++width) {
            if (matrix.get(width, width)) {
               whitePixelOffset = width - 1;
               break;
            }
         }

         width = matrix.getWidth() - whitePixelOffset * 2;
         int height = matrix.getHeight() - whitePixelOffset * 2;
         int[] pixels = new int[width * height];

         for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
               if (matrix.get(x + whitePixelOffset, y + whitePixelOffset)) {
                  pixels[y * width + x] = -16777216;
               } else {
                  pixels[y * width + x] = -1;
               }
            }
         }

         Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
         bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
         return bitmap;
      }
   }
}
