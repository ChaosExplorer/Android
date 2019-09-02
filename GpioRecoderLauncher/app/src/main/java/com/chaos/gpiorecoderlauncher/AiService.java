package com.chaos.gpiorecoderlauncher;

import android.app.IntentService;
import android.content.Intent;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

public class AiService extends IntentService {

    private final char mGpioCharB    ='b';
    private int State;

    public AiService(){
        super("AiService");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        State = 0;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        runTask();
    }

    private void runTask() {
        try {
            String pb1_ret;
            int ret;
            while (true) {
                pb1_ret = Gpio.readGpio(mGpioCharB, 2);
                ret = Integer.parseInt(pb1_ret);

                if (ret != State) {
                    if (ret > -1) {
                        Intent subintent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        startActivity(subintent);
                    }

                    State = ret;
                }

                Thread.sleep(99);

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
