package com.example.sensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor accelerometer;
    private TextView lightData, accelData, alertMessage;
    private Button startButton, stopButton;

    private SensorEventListener lightListener;
    private SensorEventListener accelListener;

    private static final float LIGHT_THRESHOLD = 10.0f;
    private static final float ACCEL_THRESHOLD = 15.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightData = findViewById(R.id.light_data);
        accelData = findViewById(R.id.accel_data);
        alertMessage = findViewById(R.id.alert_message);
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        lightListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float lightIntensity = event.values[0];
                lightData.setText("Light Intensity: " + lightIntensity + " lux");

                if (lightIntensity < LIGHT_THRESHOLD) {
                    alertMessage.setText("Alert: Low Light Detected!");
                } else {
                    alertMessage.setText("");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        accelListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                accelData.setText("Acceleration (X, Y, Z): " + x + ", " + y + ", " + z);

                double acceleration = Math.sqrt(x * x + y * y + z * z);
                if (acceleration > ACCEL_THRESHOLD) {
                    alertMessage.setText("Alert: Sudden Movement Detected!");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        startButton.setOnClickListener(v -> startMonitoring());
        stopButton.setOnClickListener(v -> stopMonitoring());
    }

    private void startMonitoring() {
        if (lightSensor != null) {
            sensorManager.registerListener(lightListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            lightData.setText("Light sensor not available");
        }

        if (accelerometer != null) {
            sensorManager.registerListener(accelListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            accelData.setText("Accelerometer not available");
        }
    }

    private void stopMonitoring() {
        sensorManager.unregisterListener(lightListener);
        sensorManager.unregisterListener(accelListener);

        alertMessage.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMonitoring();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
