package com.eseeiot.setup.task;

import androidx.annotation.NonNull;
import com.eseeiot.setup.task.tag.TaskTag;

public final class TaskCollections {
   private static final Object[][] AP_SETUP_STEPS;
   private static final Object[][] QR_OR_BLE_SETUP_STEPS;

   private TaskCollections() {
   }

   public static Object getStep(@NonNull DeviceSetupType type, int index, int index2) {
      Object object = null;

      try {
         switch(type) {
         case AP:
            if (AP_SETUP_STEPS.length > index && AP_SETUP_STEPS[index].length > index2) {
               object = AP_SETUP_STEPS[index][index2];
            }
            break;
         case QR:
         case BLE:
            if (QR_OR_BLE_SETUP_STEPS.length > index && QR_OR_BLE_SETUP_STEPS[index].length > index2) {
               object = QR_OR_BLE_SETUP_STEPS[index][index2];
            }
         }
      } catch (IndexOutOfBoundsException var5) {
         var5.printStackTrace();
      }

      return object;
   }

   static {
      AP_SETUP_STEPS = new Object[][]{{TaskTag.CONNECT_DEVICE_AP, 0}, {TaskTag.SEARCH_DEVICE_ON_AP, 10}, {TaskTag.SEND_WIFI_INFO_TO_DEVICE, 30}, {TaskTag.WAIT_FOR_CONNECT_WIFI_AUTO, 50}, {TaskTag.CONNECT_DEVICE, 70}, {TaskTag.GET_DEVICE_SETUP_INFO, 90}, {TaskTag.SET_TIMEZONE_FOR_DEVICE, 100}};
      QR_OR_BLE_SETUP_STEPS = new Object[][]{{TaskTag.CONNECT_DEVICE, 100}};
   }
}
