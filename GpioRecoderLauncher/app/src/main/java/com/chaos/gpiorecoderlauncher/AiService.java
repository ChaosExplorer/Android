package com.chaos.gpiorecoderlauncher;

import android.app.IntentService;
import android.content.Intent;
import android.provider.MediaStore;

public class AiService extends IntentService {

    char mGpioCharB    ='b';
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
    protected void onHandleIntent(Intent intent) {
        try {
            String pb2_ret;
            int ret;
            while (true) {
                pb2_ret = Gpio.readGpio(mGpioCharB, 2);
                ret = Integer.parseInt(pb2_ret);

                if (ret != State) {
                    if (ret > 0) {
                        Intent subintent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        startActivity(subintent);
                    }

                    State = ret;
                }

                Thread.sleep(100);

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
