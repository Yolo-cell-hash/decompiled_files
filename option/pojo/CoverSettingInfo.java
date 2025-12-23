package com.eseeiot.option.pojo;

import androidx.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

public class CoverSettingInfo {
   private int channelNum;
   private boolean enabled;
   private int maxRegion;
   private List<CoverSettingInfo.Regions> Regions;

   public int getChannelNum() {
      return this.channelNum;
   }

   public void setChannelNum(int channelNum) {
      this.channelNum = channelNum;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public int getMaxRegion() {
      return this.maxRegion;
   }

   public void setMaxRegion(int maxRegion) {
      this.maxRegion = maxRegion;
   }

   public List<CoverSettingInfo.Regions> getRegions() {
      return this.Regions;
   }

   public void setRegions(List<CoverSettingInfo.Regions> regions) {
      this.Regions = regions;
   }

   public static class Regions implements Serializable {
      private float regionX;
      private float regionY;
      private float regionW;
      private float regionH;
      private String regionColor;

      public float getRegionX() {
         return this.regionX;
      }

      public void setRegionX(float regionX) {
         this.regionX = regionX;
      }

      public float getRegionY() {
         return this.regionY;
      }

      public void setRegionY(float regionY) {
         this.regionY = regionY;
      }

      public float getRegionW() {
         return this.regionW;
      }

      public void setRegionW(float regionW) {
         this.regionW = regionW;
      }

      public float getRegionH() {
         return this.regionH;
      }

      public void setRegionH(float regionH) {
         this.regionH = regionH;
      }

      public String getRegionColor() {
         return this.regionColor;
      }

      public void setRegionColor(String regionColor) {
         this.regionColor = regionColor;
      }

      public boolean equals(@Nullable Object obj) {
         if (!(obj instanceof CoverSettingInfo.Regions)) {
            return false;
         } else {
            CoverSettingInfo.Regions regions = (CoverSettingInfo.Regions)obj;
            return Float.compare(this.regionX, regions.regionX) == 0 && Float.compare(this.regionY, regions.regionY) == 0 && Float.compare(this.regionW, regions.regionW) == 0 && Float.compare(this.regionH, regions.regionH) == 0 && this.regionColor.equals(regions.regionColor);
         }
      }
   }
}
