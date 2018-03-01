package com.example.sgw.accelerometer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private ImageView sunImageView, sunImageView1;
    private ObjectAnimator valueAnimator = new ObjectAnimator();
    RelativeLayout rl;
    int counter = 0;
    final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sunImageView = findViewById(R.id.sun);
        sunImageView1 = findViewById(R.id.sun1);
        rl = findViewById(R.id.rl);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

               Log.d(MainActivity.class.getSimpleName(), "x = " + x + "; y = " + y + "; z = " + z);

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                //Log.d(MainActivity.class.getSimpleName(),"speed = " + speed);
                if (counter==30){
                    Intent intent = new Intent(this,ResultActivity.class);
                    startActivity(intent);
                }
                if (speed > SHAKE_THRESHOLD) {
                    counter++;
                    valueAnimator = ObjectAnimator.ofFloat(sunImageView, "translationY", last_y*10);
                   // Log.d(MainActivity.class.getSimpleName(), String.valueOf(sunImageView.getTranslationX()));
                    valueAnimator.setDuration(20000L);
                    valueAnimator.setCurrentPlayTime(((long) speed * 100));
                    startAnim();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    private void startAnim() {
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animV = (float) valueAnimator.getAnimatedValue();

                sunImageView.setTranslationX(last_x*5);
                sunImageView1.setTranslationX(last_x*5);
                sunImageView1.setTranslationY(animV);
                //Log.d(MainActivity.class.getSimpleName(), String.valueOf(sunImageView.getTranslationY()));
            }
        });
        valueAnimator.start();
    }
}
