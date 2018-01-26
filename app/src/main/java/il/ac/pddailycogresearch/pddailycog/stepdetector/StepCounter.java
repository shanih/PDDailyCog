package il.ac.pddailycogresearch.pddailycog.stepdetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by User on 26/01/2018.
 */

public class StepCounter implements SensorEventListener, StepListener {
    static StepCounter sInstance;

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private Sensor accelerometer;

    private StepDetector simpleStepDetector;
    private StepDetector2 stepDetector2;

    private long stepsNum;

    private StepCounter(){
        simpleStepDetector = new StepDetector();
        stepDetector2 = new StepDetector2();
        stepDetector2.addStepListener(this);
        simpleStepDetector.registerListener(this);
        stepsNum =0;
    }

    public static StepCounter getInstance(){
        if(sInstance==null)
            sInstance=new StepCounter();
        return sInstance;
    }

    public void registerSensors(Context context){
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (stepDetectorSensor != null){
            sensorManager.registerListener(this,stepDetectorSensor,SensorManager.SENSOR_DELAY_FASTEST);//TODO consider change delay
        }
        else { //TODO choose one algorithm and delete signs of the other
    //        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(stepDetector2, accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }
    public void unregisterSensors(){
        if(stepDetectorSensor!=null)
            sensorManager.unregisterListener(this);
        else {
            sensorManager.unregisterListener(this);
        //    sensorManager.unregisterListener(stepDetector2);
        }
    }

    public long getStepsNum() {
        return stepsNum;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepsNum++;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {
        stepsNum++;
    }
}
