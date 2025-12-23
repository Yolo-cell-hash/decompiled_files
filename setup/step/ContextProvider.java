package com.eseeiot.setup.step;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContextProvider extends ContentProvider {
   private static final String TAG = "ContextProvider";
   @SuppressLint({"StaticFieldLeak"})
   private static volatile Context sContext;

   public static Context getApplicationContext() {
      if (sContext == null) {
         throw new IllegalArgumentException("ApplicationContext not initialized!");
      } else {
         return sContext;
      }
   }

   public boolean onCreate() {
      sContext = this.getContext().getApplicationContext();
      return false;
   }

   @Nullable
   public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
      return null;
   }

   @Nullable
   public String getType(@NonNull Uri uri) {
      return null;
   }

   @Nullable
   public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
      return null;
   }

   public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
      return 0;
   }

   public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
      return 0;
   }
}
