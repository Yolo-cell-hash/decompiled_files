package com.eseeiot.device;

import android.content.Context;
import com.eseeiot.basemodule.device.base.MonitorCamera;
import com.eseeiot.basemodule.device.common.CamProperty;
import com.eseeiot.basemodule.device.event.Events;
import com.eseeiot.basemodule.device.ptz.PTZ;
import com.eseeiot.basemodule.device.talk.TalkSession;

public class JACamera extends MonitorCamera {
   protected JACamera(int channel) {
      super(channel);
   }

   protected JACamera(CamProperty property) {
      super(property);
   }

   public Events getEvents() {
      if (this.mEvents == null) {
         try {
            Class<?> clazz = Class.forName("com.eseeiot.event.JAEvent");
            Object instance = clazz.newInstance();
            this.mEvents = (Events)instance;
            this.mEvents.bindCamera(this);
         } catch (ClassNotFoundException var3) {
         } catch (IllegalAccessException var4) {
         } catch (InstantiationException var5) {
         }
      }

      return this.mEvents;
   }

   public TalkSession getTalkSession(Context context) {
      if (this.mTalk == null) {
      }

      return this.mTalk.getSession(context);
   }

   public PTZ getPTZ() {
      if (this.mPTZ == null) {
      }

      return this.mPTZ;
   }
}
