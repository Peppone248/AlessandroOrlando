package com.example.alessandroorlando;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private MediaPlayer voi, mettilo, figlio, agitato, buio, dove;
    private Button voi_btn;
    private Button mettilo_btn;
    private Button figlio_btn;
    private Button buio_btn;
    private Button dove_btn;
    private boolean isAccelerometerAvailable;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Intent intentGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voi = MediaPlayer.create(this, R.raw.voi);
        mettilo = MediaPlayer.create(this, R.raw.mettilodaparte);
        figlio = MediaPlayer.create(this, R.raw.figlio);
        agitato = MediaPlayer.create(this, R.raw.agitato);
        buio = MediaPlayer.create(this, R.raw.buio);
        dove = MediaPlayer.create(this, R.raw.dove);

        voi_btn = (Button) this.findViewById(R.id.voi_btn);
        mettilo_btn = (Button) this.findViewById(R.id.da_parte_btn);
        figlio_btn = (Button) this.findViewById((R.id.figlio_btn));
        buio_btn = (Button) this.findViewById((R.id.buio_btn));
        dove_btn = (Button) this.findViewById((R.id.dove_btn));

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
                params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.screenBrightness=0;
                getWindow().setAttributes(params);
                buio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //buio.stop();
                        WindowManager.LayoutParams params = getWindow().getAttributes();
                        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                        params.screenBrightness=200;
                        getWindow().setAttributes(params);
                    }
                });
            }
        });

        dove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocationEnabled(MainActivity.this)){
                    dove.start();
                } else {
                    intentGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intentGPS);
                }
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

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        CloseAlert("VUOI DAVVERO ALLONTANARTI DAL MAESTRO?");
    }

    public void CloseAlert(String alertmessage){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        android.os.Process.killProcess(android.os.Process.myPid());
                        break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(alertmessage).setPositiveButton("Sì", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }
}