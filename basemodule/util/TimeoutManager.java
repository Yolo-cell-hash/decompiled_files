package com.eseeiot.basemodule.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import androidx.annotation.NonNull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeoutManager {
   private static final String TAG = TimeoutManager.class.getSimpleName();
   private static volatile TimeoutManager sInstance = null;
   public static final int RESTART = 1;
   public static final int DESTROY = 2;
   private final HandlerThread mHandlerThread = new HandlerThread("timeout");
   private final Handler mHandler;
   private final AtomicInteger mTagGenerator = new AtomicInteger(0);
   private final ConcurrentMap<Integer, TimeoutManager.TimeoutTask> mTaskMap = new ConcurrentHashMap();

   public static TimeoutManager initialize() {
      if (sInstance == null) {
         Class var0 = TimeoutManager.class;
         synchronized(TimeoutManager.class) {
            sInstance = new TimeoutManager();
         }
      }

      return sInstance;
   }

   public static TimeoutManager getInstance() {
      return sInstance;
   }

   private TimeoutManager() {
      this.mHandlerThread.start();
      this.mHandler = new Handler(this.mHandlerThread.getLooper()) {
         public void handleMessage(@NonNull Message msg) {
            TimeoutManager.TimeoutTask task = (TimeoutManager.TimeoutTask)TimeoutManager.this.mTaskMap.get(msg.what);
            if (task != null) {
               if (task.isGoingRepeat()) {
                  this.sendEmptyMessageDelayed(task.tag, (long)task.timeoutMs);
               } else if (task.isGoingDestroy()) {
                  TimeoutManager.this.mTaskMap.remove(task.tag);
               }

               task.timeout();
            }

         }
      };
   }

   public int addTask(int timeoutMs, TimeoutManager.TimeoutCallback callback) {
      return this.addTask(timeoutMs, 0, callback);
   }

   public int addTask(int timeoutMs, int repeatMode, TimeoutManager.TimeoutCallback callback) {
      if (timeoutMs >= 100 && timeoutMs <= 1000000 && callback != null) {
         TimeoutManager.TimeoutTask task = new TimeoutManager.TimeoutTask();
         task.tag = this.mTagGenerator.getAndIncrement();
         task.repeatMode = repeatMode;
         task.timeoutMs = timeoutMs;
         task.callback = callback;
         this.mTaskMap.put(task.tag, task);
         return task.tag;
      } else {
         return -1;
      }
   }

   public boolean removeTask(int tag) {
      TimeoutManager.TimeoutTask task = (TimeoutManager.TimeoutTask)this.mTaskMap.remove(tag);
      if (task != null) {
         this.mHandler.removeMessages(tag);
         return true;
      } else {
         return false;
      }
   }

   public boolean doTask(int tag) {
      Log.d(TAG, "doTask() called with: tag = [" + tag + "]");
      return this.refreshTask(tag);
   }

   public boolean cancelTask(int tag) {
      Log.d(TAG, "cancelTask() called with: tag = [" + tag + "]");
      TimeoutManager.TimeoutTask task = (TimeoutManager.TimeoutTask)this.mTaskMap.get(tag);
      if (task != null) {
         this.mHandler.removeMessages(tag);
         return true;
      } else {
         return false;
      }
   }

   private boolean refreshTask(int tag) {
      TimeoutManager.TimeoutTask task = (TimeoutManager.TimeoutTask)this.mTaskMap.get(tag);
      if (task != null) {
         this.mHandler.removeMessages(tag);
         this.mHandler.sendEmptyMessageDelayed(tag, (long)task.timeoutMs);
         return true;
      } else {
         return false;
      }
   }

   public interface TimeoutCallback {
      void onTimeout(int var1);
   }

   private static class TimeoutTask {
      private int tag;
      private int repeatMode;
      private int timeoutMs;
      private TimeoutManager.TimeoutCallback callback;

      private TimeoutTask() {
      }

      public boolean isGoingRepeat() {
         return this.repeatMode == 1;
      }

      public boolean isGoingDestroy() {
         return this.repeatMode == 2;
      }

      private void timeout() {
         this.callback.onTimeout(this.tag);
      }

      // $FF: synthetic method
      TimeoutTask(Object x0) {
         this();
      }
   }
}
