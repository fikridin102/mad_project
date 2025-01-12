package com.example.mad_project;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magnetometer;
    private Sensor accelerometer;

    private float[] gravity;
    private float[] geomagnetic;

    private ImageView compassImage;
    private TextView directionText;

    private float currentAzimuth = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass);

        compassImage = findViewById(R.id.compassImage);
        directionText = findViewById(R.id.directionText);

        // Initialize the SensorManager and sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register sensor listeners
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister sensor listeners to save battery
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }

        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];

            // Calculate the rotation matrix
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float[] orientation = new float[3];

                // Get the orientation from the rotation matrix
                SensorManager.getOrientation(R, orientation);

                // Calculate the azimuth (angle of rotation)
                float azimuth = (float) Math.toDegrees(orientation[0]);

                // Normalize azimuth to [0, 360]
                azimuth = (azimuth + 360) % 360;

                // Update UI with azimuth
                directionText.setText("Direction: " + Math.round(azimuth) + "°");

                // Calculate the shortest rotation direction
                float rotationDelta = azimuth - currentAzimuth;
                if (rotationDelta > 180) {
                    rotationDelta -= 360;
                } else if (rotationDelta < -180) {
                    rotationDelta += 360;
                }

                float targetAzimuth = currentAzimuth + rotationDelta;

                // Rotate compass image (invert the angle for correct behavior)
                ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(compassImage, "rotation", -currentAzimuth, -targetAzimuth);
                rotationAnimator.setDuration(300); // Smooth transition in 300ms
                rotationAnimator.start();

                currentAzimuth = targetAzimuth;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes here if necessary
    }
}
