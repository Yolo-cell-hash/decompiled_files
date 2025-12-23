package com.eseeiot.live;

import android.text.TextUtils;
import com.eseeiot.basemodule.device.ptz.base.CommonPTZController;

public class JAPTZController extends CommonPTZController {
   private String mCruiseMode = "none";

   public void moveLeft() {
      this.onCruiseStop();
      if (this.mPtzReverseHorizontal) {
         this.positiveAction(3);
      } else {
         this.positiveAction(2);
      }

   }

   public void moveRight() {
      this.onCruiseStop();
      if (this.mPtzReverseHorizontal) {
         this.positiveAction(2);
      } else {
         this.positiveAction(3);
      }

   }

   public void moveUp() {
      this.onCruiseStop();
      if (this.mPtzReverseVertical) {
         this.positiveAction(1);
      } else {
         this.positiveAction(0);
      }

   }

   public void moveDown() {
      this.onCruiseStop();
      if (this.mPtzReverseVertical) {
         this.positiveAction(0);
      } else {
         this.positiveAction(1);
      }

   }

   public void stepLeft() {
      this.onCruiseStop();
      if (this.mPtzReverseHorizontal) {
         this.positiveAction(120);
      } else {
         this.positiveAction(119);
      }

   }

   public void stepRight() {
      this.onCruiseStop();
      if (this.mPtzReverseHorizontal) {
         this.positiveAction(119);
      } else {
         this.positiveAction(120);
      }

   }

   public void stepUp() {
      this.onCruiseStop();
      if (this.mPtzReverseVertical) {
         this.positiveAction(118);
      } else {
         this.positiveAction(117);
      }

   }

   public void stepDown() {
      this.onCruiseStop();
      if (this.mPtzReverseVertical) {
         this.positiveAction(117);
      } else {
         this.positiveAction(118);
      }

   }

   public void cruise() {
      this.mIsCruising = true;
      this.mCamera.ptzControl(8, 1, this.getSpeed(), 0);
   }

   public void cruiseByPosition() {
      this.mIsCruising = true;
      this.mCamera.ptzControl(8, 1, this.getSpeed(), 1);
   }

   public boolean isCruising() {
      return this.mIsCruising;
   }

   public void resetCruise() {
      this.mIsCruising = false;
   }

   public void zoomIn() {
      this.positiveAction(11);
   }

   public void zoomOut() {
      this.positiveAction(12);
   }

   public void focusNear() {
      this.positiveAction(14);
   }

   public void focusFar() {
      this.positiveAction(13);
   }

   public void selfCheck() {
      this.mCamera.ptzControl(116, 1, this.getSpeed(), 0);
   }

   private void positiveAction(int action) {
      this.mIsCruising = false;
      this.mCamera.ptzControl(action, 1, this.getSpeed(), 0);
   }

   public void stop() {
      this.mIsCruising = false;
      this.mCamera.ptzControl(15, 0, this.getSpeed(), 0);
   }

   public void go(int position) {
      this.onCruiseStop();
      this.mCamera.ptzControl(30, 1, position + 1, 0);
   }

   public void addPreset(int position) {
      this.mCamera.ptzControl(28, 1, position + 1, 0);
   }

   public void removePreset(int position) {
      this.mCamera.ptzControl(29, 1, position + 1, 0);
   }

   public String getPTZCruiseMode() {
      return this.mCruiseMode;
   }

   public void setPTZCruiseMode(String cruiseMode) {
      this.mCruiseMode = cruiseMode;
   }

   public void updateCruiseStatus(String cruiseMode) {
      super.updateCruiseStatus(cruiseMode);
      if (!this.mIsCruising && !TextUtils.isEmpty(cruiseMode) && !"none".equals(cruiseMode)) {
         this.mIsCruising = true;
      }

   }
}
