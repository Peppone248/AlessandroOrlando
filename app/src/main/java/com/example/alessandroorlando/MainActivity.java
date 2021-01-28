package com.example.alessandroorlando;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private MediaPlayer voi, mettilo, figlio, agitato, buio;
    private Button voi_btn;
    private Button mettilo_btn;
    private Button figlio_btn;
    private Button buio_btn;
    private boolean isAccelerometerAvailable;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voi = MediaPlayer.create(this, R.raw.voi);
        mettilo = MediaPlayer.create(this, R.raw.mettilodaparte);
        figlio = MediaPlayer.create(this, R.raw.figlio);
        agitato = MediaPlayer.create(this, R.raw.agitato);
        buio = MediaPlayer.create(this, R.raw.buio);

        voi_btn = (Button) this.findViewById(R.id.voi_btn);
        mettilo_btn = (Button) this.findViewById(R.id.da_parte_btn);
        figlio_btn = (Button) this.findViewById((R.id.figlio_btn));
        buio_btn = (Button) this.findViewById((R.id.buio_btn));

        voi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voi.start();
            }
        });

        mettilo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mettilo.start();
            }
        });

        figlio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                figlio.start();
            }
        });

        buio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buio.start();
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.screenBrightness=0;
                getWindow().setAttributes(params);
            }
        });

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerAvailable=true;
        } else {
            // do something
            isAccelerometerAvailable=false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            // assign directions
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];
            if (x>10){
                Intent intent = new Intent(this, Page1.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                agitato.start();
                startActivity(intent);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAccelerometerAvailable){
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isAccelerometerAvailable){
            sensorManager.unregisterListener(this);
        }
    }
}