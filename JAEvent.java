package com.eseeiot.event;

import com.eseeiot.basemodule.device.dispatcher.RecordEventDispatchEntry;
import com.eseeiot.basemodule.device.event.EventProperty;
import com.eseeiot.basemodule.device.event.SearchSession;
import com.eseeiot.basemodule.device.event.base.BaseSearchSession;
import com.eseeiot.basemodule.device.event.base.CommonEvent;
import com.eseeiot.basemodule.listener.CommandResultListener;
import com.eseeiot.basemodule.pojo.RecordInfo;
import com.eseeiot.basemodule.util.Interval;
import com.eseeiot.device.JADevProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class JAEvent extends CommonEvent {
   private static final String TAG = "JAEventV21";
   private Integer mSearchTimes;

   public SearchSession newSession() {
      if (this.mRecordList == null) {
         this.mRecordList = new ArrayList();
      }

      if (this.mSearchTimes == null) {
         JADevProperty var1 = (JADevProperty)this.mCamera.getParentDevice().getProperty();
      }

      this.mSession = new JAEvent.EventSearchSession(this);
      return this.mSession;
   }

   public class EventSearchSession extends BaseSearchSession implements RecordEventDispatchEntry {
      private RecordInfo previousRecordSeg;

      private EventSearchSession(CommonEvent event) {
         super(event);
      }

      protected void onSessionCanceled() {
      }

      protected int getSearchTimes() {
         return JAEvent.this.mSearchTimes != null ? JAEvent.this.mSearchTimes : super.getSearchTimes();
      }

      protected int searchRecord(int startTime, int endTime, int requestCode, CommandResultListener listener) {
         Boolean isSupportMultiRecType = JAEvent.this.mCamera.getOptions().isSupportMultiRecType() != null && JAEvent.this.mCamera.getOptions().isSupportMultiRecType();
         return JAEvent.this.mCamera.searchRecord(startTime, endTime, isSupportMultiRecType ? '\uffff' : 15, requestCode, this, listener);
      }

      public boolean onRecordAvailable(int startTime, int endTime, String localStartTime, String localEndTime, int recType, String name, int cloudType, int prefix, boolean end) {
         return false;
      }

      public boolean onRecordAvailable(int startTime, int endTime, int recType, int channel, int requestCode, boolean end) {
         if (end) {
            this.recordSuccess();
         }

         return this.dispatchRecord(startTime, endTime, (String)null, (String)null, recType, channel, requestCode, end);
      }

      protected void addRecordSeg(Interval newRecordInterval, String localStartTime, String localEndTime, int recType) {
         int type = recType;
         if (this.isContinuousType(recType)) {
            type = 1;
         }

         if (this.isMotionType(recType)) {
            type = 2;
         }

         if (this.previousRecordSeg == null || !this.previousRecordSeg.addSubSeg(newRecordInterval, recType)) {
            this.previousRecordSeg = new RecordInfo(newRecordInterval, type);
            if (this.isHumanoidType(recType)) {
               this.previousRecordSeg.setHumanoidType(true);
            }

            JAEvent.this.mRecordList.add(this.previousRecordSeg);
            Collections.sort(JAEvent.this.mRecordList, new Comparator<EventProperty>() {
               public int compare(EventProperty e1, EventProperty e2) {
                  return Integer.compare(e1.getStartTime(), e2.getStartTime());
               }
            });
         }
      }

      private boolean isContinuousType(int recType) {
         return (recType >> 0 & 1) == 1 || (recType >> 3 & 1) == 1;
      }

      private boolean isMotionType(int recType) {
         return (recType >> 1 & 1) == 1;
      }

      private boolean isHumanoidType(int recType) {
         return (recType >> 4 & 1) == 1 || (recType >> 5 & 1) == 1;
      }

      // $FF: synthetic method
      EventSearchSession(CommonEvent x1, Object x2) {
         this(x1);
      }
   }
}
