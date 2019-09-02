package com.chaos.gpiorecoderlauncher;


import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AiService.class);
        context.startService(service);
    }
}