package com.eseeiot.setup.task.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.eseeiot.setup.task.tag.TaskTag;

public interface OnTaskChangedListener {
   boolean onTaskTimeout(@NonNull TaskTag var1, @Nullable Object var2, long var3);

   void onTaskChanged(@NonNull TaskTag var1, @Nullable Object var2, boolean var3);

   void onTaskError(@NonNull TaskTag var1, @Nullable Object var2);
}
