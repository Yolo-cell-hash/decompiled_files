package com.eseeiot.setup.task;

import android.os.Handler;
import android.os.Message;
import java.util.Random;

public final class TimerHandler extends Handler {
   private static final int STATE_STOP = 0;
   private static final int STATE_START = 1;
   private static final int STATE_PAUSE = 2;
   private static final int ADD_VALUE = 7;
   private static final int SET_VALUE = 8;
   private int mState = 0;
   private int mValue;
   private int mTimeout = 95;
   private final Random mRandom = new Random();
   private int mBaseTimeRoot = 1000;
   private int mRandomTimeRoot = 200;
   private TimerHandler.OnTimerChangedListener mListener;

   private void sendMsg(int what, int val, int delay) {
      Message message = Message.obtain();
      message.what = what;
      message.arg1 = val;
      if (delay > 0) {
         this.sendMessageDelayed(message, (long)delay);
      } else {
         this.sendMessage(message);
      }

   }

   public void handleMessage(Message msg) {
      int adjustValue = 0;
      int value = msg.arg1;
      if (this.mListener != null) {
         adjustValue = this.mListener.onValueChanged(this, value);
      }

      if (this.mState == 2) {
         this.mValue = value;
      }

      if (msg.what == 7 && this.mState == 1) {
         int delayTime = this.mBaseTimeRoot + this.mRandom.nextInt(12) * this.mRandomTimeRoot;
         if (adjustValue != 0) {
            value = adjustValue + 1;
         } else {
            ++value;
         }

         if (value >= 100) {
            this.mState = 0;
         } else if (value == this.mTimeout) {
            if (this.mListener != null && !this.mListener.onTimeout(this)) {
               this.sendMsg(7, 100, delayTime);
            } else {
               this.mState = 0;
            }
         } else {
            this.sendMsg(7, value, delayTime);
         }
      }

   }

   public void setBaseTimeRoot(int value) {
      this.mBaseTimeRoot = value;
   }

   public void setRandomTimeRoot(int value) {
      this.mRandomTimeRoot = value;
   }

   public void setValue(int value) {
      if (this.mState == 1) {
         this.removeMessages(7);
         this.sendMsg(7, value, 0);
      } else {
         this.sendMsg(8, value, 0);
      }

   }

   public void start() {
      this.start(0);
   }

   public void start(int value) {
      switch(this.mState) {
      case 0:
         this.mState = 1;
         this.sendMsg(7, value, 0);
         break;
      case 2:
         this.mState = 1;
         this.sendMsg(7, this.mValue, 0);
      }

   }

   public void pause() {
      if (this.mState == 1) {
         this.mState = 2;
      }

   }

   public void stop() {
      this.mState = 0;
      this.removeCallbacksAndMessages((Object)null);
   }

   public void setTimeout(int value) {
      this.mTimeout = value;
   }

   public void setOnTimerChangedListener(TimerHandler.OnTimerChangedListener listener) {
      this.mListener = listener;
   }

   public interface OnTimerChangedListener {
      boolean onTimeout(TimerHandler var1);

      int onValueChanged(TimerHandler var1, int var2);
   }
}
