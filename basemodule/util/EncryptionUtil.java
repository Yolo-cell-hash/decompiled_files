package com.eseeiot.basemodule.util;

import android.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtil {
   private EncryptionUtil() {
   }

   public static String encode(byte[] source) {
      return encode(source, false);
   }

   public static String encode(byte[] source, boolean shortStr) {
      try {
         char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
         MessageDigest md = MessageDigest.getInstance("MD5");
         md.update(source);
         byte[] tmp = md.digest();
         char[] str = new char[32];
         int k = 0;

         for(int i = 0; i < 16; ++i) {
            byte byte0 = tmp[i];
            str[k++] = hexDigits[byte0 >>> 4 & 15];
            str[k++] = hexDigits[byte0 & 15];
         }

         String s = new String(str);
         md = null;
         return shortStr ? s.substring(8, 24) : s;
      } catch (NoSuchAlgorithmException var9) {
         var9.printStackTrace();
         return null;
      }
   }

   public static String encodeBase64(String str) {
      return Base64.encodeToString(str.getBytes(), 2);
   }

   public static String decodeBase64(String str) {
      try {
         byte[] res = Base64.decode(str.getBytes(), 2);
         return new String(res);
      } catch (IllegalArgumentException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static String encode(File file) {
      try {
         MessageDigest md5 = MessageDigest.getInstance("MD5");
         FileInputStream fis = new FileInputStream(file);
         byte[] arr = new byte[8192];
         boolean var4 = false;

         int len;
         while((len = fis.read(arr)) != -1) {
            md5.update(arr, 0, len);
         }

         return byteArrayToHex(md5.digest()).toLowerCase();
      } catch (IOException | NoSuchAlgorithmException var5) {
         var5.printStackTrace();
         return null;
      }
   }

   private static String byteArrayToHex(byte[] byteArray) {
      char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
      char[] resultCharArray = new char[byteArray.length * 2];
      int index = 0;
      byte[] var4 = byteArray;
      int var5 = byteArray.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         byte b = var4[var6];
         resultCharArray[index++] = hexDigits[b >>> 4 & 15];
         resultCharArray[index++] = hexDigits[b & 15];
      }

      return new String(resultCharArray);
   }

   public static String getMD5Sum(String file) {
      try {
         MessageDigest digest = MessageDigest.getInstance("MD5");
         InputStream is = new FileInputStream(file);
         byte[] buffer = new byte[4096];

         int read;
         while((read = is.read(buffer)) > 0) {
            digest.update(buffer, 0, read);
         }

         is.close();
         byte[] md5sum = digest.digest();
         BigInteger bigInt = new BigInteger(1, md5sum);
         return bigInt.toString(16);
      } catch (Exception var7) {
         return "";
      }
   }
}
