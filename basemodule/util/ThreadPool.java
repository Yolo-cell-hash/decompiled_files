package com.eseeiot.basemodule.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ThreadPool {
   private static final int CORE_THREAD_COUNT = 2;
   private static final int MAX_THREAD_COUNT = 5;
   private static ThreadPoolExecutor sExecutor;

   private ThreadPool() {
   }

   public static synchronized void initialize() {
      if (sExecutor == null) {
         Class var0 = ThreadPool.class;
         synchronized(ThreadPool.class) {
            if (sExecutor == null) {
               sExecutor = new ThreadPoolExecutor(2, 5, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
            }
         }
      }

   }

   public static void execute(Runnable runnable) {
      if (sExecutor != null) {
         sExecutor.execute(runnable);
      }

   }

   public static synchronized void release(boolean now) {
      if (sExecutor != null) {
         if (now) {
            sExecutor.shutdownNow();
         } else {
            sExecutor.shutdown();
         }

         sExecutor = null;
      }

   }
}
