package com.chaos.gpiorecoderlauncher;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gpiotest.Gpio;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent service = new Intent(this, AiService.class);
        startService(service);

        /*btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pb1_ret = Gpio.readGpio('b', 2);
                Toast toast = Toast.makeText(getApplicationContext(), pb1_ret, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        */
    }

}
