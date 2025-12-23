package com.eseeiot.setup.task.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.eseeiot.setup.task.listener.OnTaskChangedListener;
import com.eseeiot.setup.task.tag.TaskTag;

public abstract class BaseTask {
   protected Context mContext;
   protected boolean mIsRunning;
   protected Thread mThread;
   protected boolean mLogPrint;
   private OnTaskChangedListener mCallback;
   private TaskTag mTag;
   private ValueAnimator mTimeoutTimer;
   private CountDownTimer mDelayTimer;
   private long mDelayDuration;
   private long mStartTime;
   private Handler mHandler;

   public BaseTask(@NonNull Context context, @NonNull TaskTag taskTag, int timeout) {
      this.mContext = context;
      this.mTag = taskTag;
      this.mHandler = new Handler(Looper.getMainLooper());
      this.initTimer(timeout);
   }

   private void initTimer(int timeout) {
      if (timeout > 0) {
         this.mTimeoutTimer = ValueAnimator.ofInt(new int[]{0, 1});
         this.mTimeoutTimer.setRepeatMode(1);
         this.mTimeoutTimer.setRepeatCount(-1);
         this.mTimeoutTimer.setDuration((long)timeout);
         this.mTimeoutTimer.addListener(new AnimatorListenerAdapter() {
            public void onAnimationRepeat(Animator animation) {
               if (BaseTask.this.mIsRunning) {
                  BaseTask.this.printLog("Task timeout called with " + BaseTask.this.mTag);
                  BaseTask.this.onTaskTimeout();
               }

            }
         });
      }

   }

   private void initDelayTimer(long duration) {
      if (this.mDelayDuration != duration) {
         this.mDelayDuration = duration;
         this.mDelayTimer = new CountDownTimer(duration, duration / 10L) {
            public void onTick(long l) {
            }

            public void onFinish() {
               BaseTask.this.printLog("onFinish: delay over with tag " + BaseTask.this.mTag);
               BaseTask.this.mIsRunning = true;
               if (BaseTask.this.mTimeoutTimer != null) {
                  BaseTask.this.mTimeoutTimer.start();
               }

               BaseTask.this.onTaskStart();
            }
         };
      }

      this.mDelayTimer.start();
   }

   protected final void printLog(@NonNull String msg) {
      if (this.mLogPrint) {
         Log.d(this.getClass().getSimpleName(), msg);
      }

   }

   protected final void requestComplete(Object object, boolean finish) {
      if (finish) {
         this.mIsRunning = false;
      }

      if (this.mHandler != null) {
         this.mHandler.post(() -> {
            this.printLog("run: requestComplete with tag " + this.mTag);
            if (finish) {
               this.reset();
            }

            if (this.mCallback != null) {
               this.mCallback.onTaskChanged(this.mTag, object, finish);
            }

         });
      }

   }

   protected final void requestTimeout(Object object, boolean handleCallbackResult) {
      this.requestTimeout(object, handleCallbackResult ? 0L : -1L);
   }

   protected final void requestTimeout(Object object, long delay) {
      if (this.mHandler != null) {
         this.mHandler.post(() -> {
            this.printLog("run: requestTimeout with tag " + this.mTag);
            if (this.mCallback != null) {
               long costTime = System.currentTimeMillis() - this.mStartTime;
               if (!this.mCallback.onTaskTimeout(this.mTag, object, costTime)) {
                  if (delay >= 0L) {
                     this.requestRestart(delay);
                  }
               } else {
                  this.printLog("requestTimeout handle true with tag " + this.mTag);
                  this.reset();
               }
            }

         });
      }

   }

   protected final void requestError(Object object) {
      this.mIsRunning = false;
      if (this.mHandler != null) {
         this.mHandler.post(() -> {
            this.printLog("run: requestError with tag " + this.mTag);
            this.reset();
            if (this.mCallback != null) {
               this.mCallback.onTaskError(this.mTag, object);
            }

         });
      }

   }

   protected final void requestRestart(long delay) {
      this.mIsRunning = false;
      if (this.mHandler != null) {
         this.mHandler.post(() -> {
            this.printLog("run: requestRestart with tag " + this.mTag + ", delay = " + delay);
            this.reset();
            this.start(delay);
         });
      }

   }

   public final void requestStop() {
      this.printLog("run: requestStop with tag " + this.mTag + " / " + this.mIsRunning);
      if (this.mIsRunning) {
         this.onTaskStop();
      }

      this.reset();
   }

   public final boolean exec(long delayTime, Object... object) {
      if (this.mIsRunning) {
         this.printLog("exec() called with tag " + this.mTag + " but task already running yet");
         return true;
      } else {
         this.printLog("exec() called with tag " + this.mTag + ", delay = " + delayTime);
         this.mStartTime = System.currentTimeMillis();
         if (this.onTaskInit(object)) {
            this.start(delayTime);
            return true;
         } else {
            this.printLog("something wrong when exec " + this.mTag);
            return false;
         }
      }
   }

   public void release() {
      this.reset();
      if (this.mDelayTimer != null) {
         this.mDelayTimer.cancel();
      }

      if (this.mHandler != null) {
         this.mHandler.removeCallbacksAndMessages((Object)null);
         this.mHandler = null;
      }

   }

   private void start(long delayTime) {
      if (delayTime > 0L) {
         this.initDelayTimer(delayTime);
      } else {
         this.mIsRunning = true;
         if (this.mTimeoutTimer != null) {
            this.mTimeoutTimer.start();
         }

         this.onTaskStart();
      }

   }

   private void reset() {
      this.mIsRunning = false;
      if (this.mThread != null) {
         this.mThread.interrupt();
         this.mThread = null;
      }

      if (this.mTimeoutTimer != null && this.mTimeoutTimer.isRunning()) {
         this.mTimeoutTimer.cancel();
      }

   }

   protected abstract boolean onTaskInit(Object... var1);

   protected abstract void onTaskStart();

   protected abstract void onTaskStop();

   protected void onTaskTimeout() {
      this.reset();
   }

   public final long getTimeoutDuration() {
      return this.mTimeoutTimer != null ? this.mTimeoutTimer.getDuration() : 0L;
   }

   @NonNull
   public final TaskTag getTag() {
      return this.mTag;
   }

   public final void setTag(@NonNull TaskTag taskTag) {
      this.mTag = taskTag;
   }

   public final boolean isRunning() {
      return this.mIsRunning;
   }

   public final void setCallback(@Nullable OnTaskChangedListener callback) {
      this.mCallback = callback;
   }

   public final void switchLogPrint(boolean enable) {
      this.mLogPrint = enable;
   }
}
