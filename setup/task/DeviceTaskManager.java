package com.eseeiot.setup.task;

import android.content.Context;
import androidx.annotation.NonNull;
import com.eseeiot.setup.task.base.BaseTask;
import com.eseeiot.setup.task.listener.OnTaskChangedListener;
import com.eseeiot.setup.task.tag.TaskTag;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeviceTaskManager {
   private static final String TAG = "DeviceTaskManager";
   private final Context mContext;
   private OnTaskChangedListener mCallback;
   private DeviceSetupType mType;
   private int mStepIndex;
   private BaseTask mTask;
   private final List<BaseTask> mTaskList;
   private long mStartTime;
   private final List<Long> mCostTimeList;
   private boolean mLogPrint;

   public DeviceTaskManager(Context context) {
      this.mContext = context;
      this.mType = DeviceSetupType.AP;
      this.mStepIndex = -1;
      this.mTaskList = new ArrayList();
      this.mStartTime = 0L;
      this.mCostTimeList = new ArrayList();
   }

   public void setType(DeviceSetupType type) {
      this.mType = type;
   }

   private BaseTask generateTask(TaskTag step) {
      BaseTask task = null;
      switch(step) {
      case CONNECT_DEVICE_AP:
         task = new TaskConnectWifi(this.mContext, step, 0);
         break;
      case WAIT_FOR_CONNECT_WIFI_AUTO:
         task = new TaskWait4ConnectAuto(this.mContext, step, 5000);
         break;
      case SEARCH_DEVICE_ON_AP:
         task = new TaskScanLanDevice(this.mContext, step, 10000);
         break;
      case SEARCH_DEVICE:
         task = new TaskScanLanDevice(this.mContext, step, 20000);
         break;
      case SEND_WIFI_INFO_TO_DEVICE:
         task = new TaskSendWifiInfo(this.mContext, step, 10000);
         break;
      case CONNECT_DEVICE:
         task = new TaskConnectDevice(this.mContext, step, 8000);
         break;
      case GET_DEVICE_SETUP_INFO:
         task = new TaskGetDeviceInfo(this.mContext, step, 10000);
         break;
      case SET_TIMEZONE_FOR_DEVICE:
         task = new TaskSetTimezone(this.mContext, step, 6000);
      }

      if (task != null) {
         ((BaseTask)task).setCallback(this.mCallback);
         ((BaseTask)task).switchLogPrint(this.mLogPrint);
      }

      return (BaseTask)task;
   }

   private BaseTask getTask(TaskTag taskTag) {
      Iterator var2 = this.mTaskList.iterator();

      BaseTask task;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         task = (BaseTask)var2.next();
      } while(task.getTag() != taskTag);

      task.switchLogPrint(this.mLogPrint);
      return task;
   }

   private TaskTag getTaskStepByIndex(int index) {
      TaskTag step;
      if (this.mTaskList.size() > index) {
         this.mTask = (BaseTask)this.mTaskList.get(index);
         step = this.mTask.getTag();
      } else {
         step = (TaskTag)TaskCollections.getStep(this.mType, index, 0);
         if (step != null) {
            this.mTask = this.getTask(step);
            if (this.mTask == null) {
               this.mTask = this.generateTask(step);
               if (this.mTask != null) {
                  this.mTaskList.add(this.mTask);
               }
            }
         }
      }

      return step;
   }

   public TaskTag nextTask() {
      ++this.mStepIndex;
      return this.getTaskStepByIndex(this.mStepIndex);
   }

   public TaskTag getTaskTag() {
      return this.mTask != null ? this.mTask.getTag() : null;
   }

   public boolean doTask(long delayTime, Object... object) {
      if (this.mTask != null) {
         if (this.mStartTime == 0L) {
            this.mStartTime = System.currentTimeMillis();
         }

         return this.mTask.exec(delayTime, object);
      } else {
         return false;
      }
   }

   public boolean doTask(@NonNull TaskTag taskTag, long delayTime, Object... object) {
      if (this.mTask != null && this.mTask.getTag() == taskTag) {
         return this.doTask(delayTime, object);
      } else {
         for(int i = 0; i < this.mTaskList.size(); ++i) {
            BaseTask task = (BaseTask)this.mTaskList.get(i);
            if (task.getTag() == taskTag) {
               if (this.mStartTime == 0L) {
                  this.mStartTime = System.currentTimeMillis();
               }

               this.mStepIndex = i;
               this.mTask = task;
               return this.mTask.exec(delayTime, object);
            }
         }

         return false;
      }
   }

   public boolean stopTask() {
      if (this.mTask != null) {
         long useTime = System.currentTimeMillis() - this.mStartTime;
         this.mStartTime = 0L;
         this.mCostTimeList.add(useTime);
         this.mTask.requestStop();
         return true;
      } else {
         return false;
      }
   }

   public void clearTask() {
      Iterator var1 = this.mTaskList.iterator();

      while(var1.hasNext()) {
         BaseTask task = (BaseTask)var1.next();
         task.release();
      }

      this.mTaskList.clear();
   }

   public int getProgress() {
      if (this.mTask != null) {
         Object obj = TaskCollections.getStep(this.mType, this.mStepIndex, 1);
         if (obj != null) {
            return (Integer)obj;
         }
      }

      return 0;
   }

   public long getTimeoutDuration() {
      return this.mTask != null ? this.mTask.getTimeoutDuration() : 0L;
   }

   public boolean isRunning() {
      return this.mTask != null ? this.mTask.isRunning() : false;
   }

   public long getCostTime() {
      long costTime = 0L;

      long time;
      for(Iterator var3 = this.mCostTimeList.iterator(); var3.hasNext(); costTime += time) {
         time = (Long)var3.next();
      }

      return costTime;
   }

   public void setCallback(OnTaskChangedListener callback) {
      this.mCallback = callback;
   }

   public void setDebugMode(boolean enable) {
      this.mLogPrint = enable;
   }
}
