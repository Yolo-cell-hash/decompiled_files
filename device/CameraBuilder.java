package com.eseeiot.device;

import com.eseeiot.basemodule.device.common.CamProperty;
import com.eseeiot.basemodule.device.event.Events;

public class CameraBuilder {
   public CamProperty property;
   public Events events;

   public CameraBuilder setEvents(Events events) {
      this.events = events;
      return this;
   }

   public CameraBuilder setProperty(CamProperty property) {
      this.property = property;
      return this;
   }
}
