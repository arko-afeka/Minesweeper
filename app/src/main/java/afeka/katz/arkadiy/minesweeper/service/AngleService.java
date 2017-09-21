package afeka.katz.arkadiy.minesweeper.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import java.util.Observer;

import afeka.katz.arkadiy.minesweeper.model.enums.AngleState;

public class AngleService extends Service implements SensorEventListener {
    private static final double CRITICAL_ANGLE_DELTA = 30;

    private AngleState state = AngleState.NORMAL;

    protected AngleServiceBinder angleServiceBinder = new AngleServiceBinder();
    private SensorManager sensorManager;
    boolean isListening = false;
    HandlerThread sensorThread;
    private Handler sensorHandler;

    private Double startAxisX = null;
    private Double startAxisY = null;
    private Double startAxisZ = null;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    @Override
    public void onCreate() {
        super.onCreate();

        sensorThread = new HandlerThread(AngleService.class.getSimpleName());
        sensorThread.start();
        sensorHandler = new Handler(sensorThread.getLooper());
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        angleServiceBinder.sensorService = this;

        return angleServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            sensorManager = null;
        }

        return super.onUnbind(intent);
    }

    protected void notifyChange() {
        this.angleServiceBinder.observer.update(null, this.state);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }

        final float[] mRotationMatrix = new float[9];
        final float[] mOrientationAngles = new float[3];

        if (!SensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading)) {
            return;
        }

        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        if (startAxisX == null) {
            startAxisX = Math.toDegrees(mOrientationAngles[0]);
            startAxisY = Math.toDegrees(mOrientationAngles[1]);
            startAxisZ = Math.toDegrees(mOrientationAngles[2]);
        } else {
            double axisXDelta = Math.abs(startAxisX - Math.toDegrees(mOrientationAngles[0])) + 1;
            double axisYDelta = Math.abs(startAxisY - Math.toDegrees(mOrientationAngles[1])) + 1;
            double axisZDelta = Math.abs(startAxisZ - Math.toDegrees(mOrientationAngles[2])) + 1;

            if ((axisXDelta > CRITICAL_ANGLE_DELTA ||
                    axisYDelta > CRITICAL_ANGLE_DELTA ||
                    axisZDelta > CRITICAL_ANGLE_DELTA) &&
                    state == AngleState.NORMAL) {
                state = AngleState.CRITICAL;
                notifyChange();
            } else if (axisXDelta <= CRITICAL_ANGLE_DELTA &&
                    axisYDelta <= CRITICAL_ANGLE_DELTA &&
                    axisZDelta <= CRITICAL_ANGLE_DELTA && state == AngleState.CRITICAL) {
                state = AngleState.NORMAL;
                notifyChange();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public class AngleServiceBinder extends Binder {
        private AngleService sensorService;
        private Observer observer;

        private AngleService getService() {
            return sensorService;
        }

        public void startListening(Observer observer) {
            if (!isListening) {

                sensorManager.registerListener(getService(), sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
                sensorManager.registerListener(getService(), sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                        SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
                isListening = true;
                this.observer = observer;
            }
        }
    }
}