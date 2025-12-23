package com.eseeiot.option.pojo;

import java.io.Serializable;

public class LineInfo implements Serializable {
   private float beginX;
   private float beginY;
   private float endX;
   private float endY;

   public float getBeginX() {
      return this.beginX;
   }

   public void setBeginX(float beginX) {
      this.beginX = beginX;
   }

   public float getBeginY() {
      return this.beginY;
   }

   public void setBeginY(float beginY) {
      this.beginY = beginY;
   }

   public float getEndX() {
      return this.endX;
   }

   public void setEndX(float endX) {
      this.endX = endX;
   }

   public float getEndY() {
      return this.endY;
   }

   public void setEndY(float endY) {
      this.endY = endY;
   }
}
