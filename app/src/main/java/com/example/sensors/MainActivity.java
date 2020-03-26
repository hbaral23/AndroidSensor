package com.example.sensors;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    private Sensor mProximity;

    private CameraManager mCameraManager;
    private String mCameraId;

    private TextView tvPositionx;
    private TextView tvPositiony;
    private TextView tvPositionz;
    private TextView positionsensor;

    private static final float SHAKE_THRESHOLD_GRAVITY = 1.7F;
    private static final int SENSOR_SENSITIVITY = 4;

    final SensorEventListener mSensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        public void onSensorChanged(SensorEvent sensorEvent) {

            float axex = 0;
            float axey = 0;
            float axez = 0;


            LinearLayout linearl = findViewById(R.id.mainview);


            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                axex = sensorEvent.values[0];
                axey = sensorEvent.values[1];
                axez = sensorEvent.values[2];

                if (axez > 1) {
                    tvPositionz.setText("avant");
                }

                if (axez < 0) {
                    tvPositionz.setText("arriere");
                }

                if (axex < 0) {
                    tvPositionx.setText("droite");
                }

                if (axex > 0) {
                    tvPositionx.setText("gauche");
                }

                if (axey < 0) {
                    tvPositiony.setText("bas");
                }

                if (axey > 0) {
                    tvPositiony.setText("haut");
                }
            }

            float gX = axex / SensorManager.GRAVITY_EARTH;
            float gY = axey / SensorManager.GRAVITY_EARTH;
            float gZ = axez / SensorManager.GRAVITY_EARTH;

            float intensity = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (intensity > SHAKE_THRESHOLD_GRAVITY) {
                flashLight(true);
            } else
                flashLight(false);

            if (intensity > 2) {
                linearl.setBackgroundColor(Color.RED);
            } else if (axex > 1.5) {
                linearl.setBackgroundColor(Color.GREEN);
            } else {
                linearl.setBackgroundColor(Color.BLACK);
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                positionsensor.setText("test");
                if (sensorEvent.values[0] >= -SENSOR_SENSITIVITY && sensorEvent.values[0] <= SENSOR_SENSITIVITY) {
                    positionsensor.setText("proche");
                } else {
                    positionsensor.setText("loin");
                }
            }
        }

    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        TextView textView = (TextView) findViewById(R.id.sensorlist);
        TextView sensorTemp = (TextView) findViewById(R.id.work);
        TextView sensorAccele = (TextView) findViewById(R.id.work2);
        tvPositionx = (TextView) findViewById(R.id.tvpositionx);
        tvPositiony = (TextView) findViewById(R.id.tvpositiony);
        tvPositionz = (TextView) findViewById(R.id.tvpositionz);
        positionsensor = (TextView) findViewById(R.id.positionsensor);

        StringBuilder builder = new StringBuilder();

        // textView.setText(builder.toString());

        ArrayAdapter<Sensor> dataAdapter = new ArrayAdapter<Sensor>(this, android.R.layout.simple_spinner_item, sensors);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        // Test if sensor AMBIENT_TEMPERATURE is present
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            sensorTemp.setText("sensor exist(AMBIENT_TEMPERATURE)");
        } else {
            sensorTemp.setText("sensor doesn't exist (AMBIENT_TEMPERATURE)");
        }

        // Test if sensor ACCELEROMETER is present
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            sensorAccele.setText("sensor exist(ACCELEROMETER)");
        } else {
            sensorAccele.setText("sensor doesn't exist (ACCELEROMETER)");
        }

        boolean isFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void flashLight(boolean status) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, status);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(mSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mSensorEventListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(mSensorEventListener, mAccelerometer);
        sensorManager.registerListener(mSensorEventListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
