package com.eseeiot.basemodule.util;

import androidx.annotation.Nullable;

public class Interval {
   public static final int NONE_CONTAIN = 0;
   public static final int LEFT_CONTAIN = 1;
   public static final int RIGHT_CONTAIN = 2;
   public static final int ALL_CONTAIN = 3;
   public static final int EQUAL_CONTAIN = 4;
   public int left;
   public int right;

   public Interval() {
   }

   public Interval(Interval interval) {
      this(interval.left, interval.right);
   }

   public Interval(int left, int right) {
      this.left = left;
      this.right = right;
   }

   public int getLength() {
      return this.right - this.left;
   }

   public final boolean contain(Interval inInterval) {
      return this.contain(inInterval.left, inInterval.right);
   }

   public final boolean contain(int left, int right) {
      return this.left <= left && this.right >= right;
   }

   public final int getContainPosition(Interval inInterval) {
      return this.getContainPosition(inInterval.left, inInterval.right);
   }

   public final int getContainPosition(int left, int right) {
      int pos = 0;
      if (this.contain(left, right)) {
         if (this.equals(left, right)) {
            pos = 4;
         } else if (this.left < left && this.right > right) {
            pos = 3;
         } else if (this.left == left) {
            pos = 1;
         } else {
            pos = 2;
         }
      }

      return pos;
   }

   public boolean equals(@Nullable Object obj) {
      if (obj instanceof Interval) {
         Interval interval = (Interval)obj;
         return this.equals(interval.left, interval.right);
      } else {
         return false;
      }
   }

   public boolean equals(int left, int right) {
      return this.left == left && this.right == right;
   }

   public final boolean isCross(Interval inInterval) {
      return this.isCross(inInterval.left, inInterval.right);
   }

   public final boolean isCross(int left, int right) {
      int maxLeft = Math.max(this.left, left);
      int minRight = Math.min(this.right, right);
      return minRight > maxLeft;
   }

   public final boolean isConsequent(Interval inInterval) {
      return this.isConsequent(inInterval.left, inInterval.right);
   }

   public final boolean isConsequent(int left, int right) {
      return isConsequent(this.left, this.right, left, right);
   }

   public static boolean isConsequent(int left1, int right1, int left2, int right2) {
      int delta = left2 - right1;
      if (delta != 0 && delta != 1) {
         delta = left1 - right2;
         return delta == 0 || delta == 1;
      } else {
         return true;
      }
   }

   public final Interval getCrossInterval(Interval inInterval) {
      return this.getCrossInterval(inInterval.left, inInterval.right);
   }

   public final Interval getCrossInterval(int left, int right) {
      int maxLeft = Math.max(this.left, left);
      int minRight = Math.min(this.right, right);
      return minRight > maxLeft ? new Interval(maxLeft, minRight) : null;
   }

   public final Interval[] getUncrossInterval(Interval inInterval) {
      Interval[] res = null;
      if (this.isCross(inInterval)) {
         int position = this.getContainPosition(inInterval);
         Interval out;
         if (position == 3) {
            out = new Interval(this.left, inInterval.left);
            Interval out2 = new Interval(inInterval.right, this.right);
            res = new Interval[]{out, out2};
         } else if (!inInterval.contain(this)) {
            out = position == 1 ? new Interval(inInterval.right, this.right) : new Interval(this.left, inInterval.left);
            res = new Interval[]{out};
         }
      } else {
         res = new Interval[]{new Interval(this)};
      }

      return res;
   }
}
