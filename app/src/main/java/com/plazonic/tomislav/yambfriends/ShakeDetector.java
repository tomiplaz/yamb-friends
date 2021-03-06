package com.plazonic.tomislav.yambfriends;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

    private OnShakeListener shakeListener;
    private long shakeTimestamp;
    private float forceThreshold;

    ShakeDetector(String forceThresholdName) {
        switch (forceThresholdName) {
            case "Low":
                this.forceThreshold = 1.9F;
                break;
            case "High":
                this.forceThreshold = 1.1F;
                break;
            default:
                this.forceThreshold = 1.5F;
                break;
        }
    }

    public void setOnShakeListener(OnShakeListener shakeListener) {
        this.shakeListener = shakeListener;
    }

    public interface OnShakeListener {
        void onShake();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not necessary to act upon.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (this.shakeListener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gx = x / SensorManager.GRAVITY_EARTH;
            float gy = y / SensorManager.GRAVITY_EARTH;
            float gz = z / SensorManager.GRAVITY_EARTH;

            float gForce = (float) Math.sqrt(gx * gx + gy * gy + gz * gz);

            if (gForce > this.forceThreshold) {
                final long now = System.currentTimeMillis();
                if (shakeTimestamp + 750 > now) return;
                shakeTimestamp = now;
                shakeListener.onShake();
            }
        }
    }

}
