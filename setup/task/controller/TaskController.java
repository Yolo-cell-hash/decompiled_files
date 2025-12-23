package com.eseeiot.setup.task.controller;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.eseeiot.device.pojo.DeviceInfo;
import com.eseeiot.setup.task.tag.TaskTag;

public interface TaskController {
   void doTask();

   void pauseTask();

   void stopTask();

   public interface Callback {
      void receivedErrMsg(@NonNull TaskTag var1, int var2, @NonNull String var3);

      void progressValueChange(@IntRange(from = 0L,to = 100L) int var1);

      void onStepChange(@NonNull TaskTag var1, @Nullable String var2);

      void onConfigResult(boolean var1, @Nullable DeviceInfo var2);
   }
}
