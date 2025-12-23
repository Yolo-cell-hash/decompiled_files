package com.eseeiot.basemodule.listener;

import java.io.IOException;

public abstract class BusCallback {
   public abstract void onDataAvailable(int var1, String var2, IOException var3);
}
