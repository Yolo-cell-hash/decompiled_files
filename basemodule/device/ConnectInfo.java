package com.eseeiot.basemodule.device;

import android.util.SparseIntArray;
import com.eseeiot.basemodule.device.dispatcher.DeviceEventDispatchEntry;
import com.eseeiot.basemodule.device.dispatcher.RecordEventDispatchEntry;
import com.eseeiot.basemodule.device.dispatcher.VconEventDispatchEntry;
import com.eseeiot.basemodule.util.SynchronizedSparseIntArray;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectInfo {
   public long mHandle;
   public final SparseIntArray status = new SynchronizedSparseIntArray();
   private List<DeviceEventDispatchEntry> mEventEntries = new CopyOnWriteArrayList();
   private List<VconEventDispatchEntry> mVconEntries = new CopyOnWriteArrayList();
   private List<RecordEventDispatchEntry> mRecordEntries = new CopyOnWriteArrayList();
   public IParamEntry mParamEntry;

   public List<DeviceEventDispatchEntry> getEventEntries() {
      return this.mEventEntries;
   }

   public void addEventEntry(DeviceEventDispatchEntry entry) {
      if (!this.mEventEntries.contains(entry)) {
         this.mEventEntries.add(entry);
      }

   }

   public void removeEventEntry(DeviceEventDispatchEntry entry) {
      this.mEventEntries.remove(entry);
   }

   public List<VconEventDispatchEntry> getVconEntries() {
      return this.mVconEntries;
   }

   public void addVconEntry(VconEventDispatchEntry entry) {
      if (!this.mVconEntries.contains(entry)) {
         this.mVconEntries.add(entry);
      }

   }

   public void removeVconEntry(Object entry) {
      this.mVconEntries.remove(entry);
   }

   public List<RecordEventDispatchEntry> getRecordEntries() {
      return this.mRecordEntries;
   }

   public void addRecordEntry(RecordEventDispatchEntry entry) {
      if (!this.mRecordEntries.contains(entry)) {
         this.mRecordEntries.add(entry);
      }

   }
}
