package il.ac.pddailycogresearch.pddailycog.activities;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.stepdetector.StepDetector;
import il.ac.pddailycogresearch.pddailycog.stepdetector.StepDetector2;
import il.ac.pddailycogresearch.pddailycog.stepdetector.StepListener;

/**
 * Created by User on 24/01/2018.
 */

public class StepActivity extends AppCompatActivity implements SensorEventListener, StepListener {
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.editText2)
    EditText editText2;
    private TextView TvSteps;
    private TextView TvSteps2;
    private StepDetector simpleStepDetector;
    private StepDetector2 stepDetector2;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    private int numSteps2;
    private Sensor accel2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);


        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accel2 = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        stepDetector2 = new StepDetector2();
        stepDetector2.addStepListener(this);
        simpleStepDetector.registerListener(this);

        TvSteps = (TextView) findViewById(R.id.tv_steps);
        TvSteps2 = (TextView) findViewById(R.id.textView2);
        Button BtnStart = (Button) findViewById(R.id.btn_start);
        Button BtnStop = (Button) findViewById(R.id.btn_stop);


        BtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                numSteps = 0;
                numSteps2 = 0;
                sensorManager.registerListener(StepActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManager.registerListener(stepDetector2, accel2, SensorManager.SENSOR_DELAY_FASTEST);

            }
        });


        BtnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                sensorManager.unregisterListener(StepActivity.this);
                sensorManager.unregisterListener(stepDetector2);

            }
        });


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {

        if (timeNs > 0) {
            numSteps++;
            TvSteps.setText(TEXT_NUM_STEPS + numSteps);
        } else {
            numSteps2++;
            TvSteps2.setText(TEXT_NUM_STEPS + "~2~ " + numSteps2);
        }
    }

    @OnClick({R.id.button2, R.id.button22})
    public void onViewClicked(View v) {
        if (v.getId() == R.id.button2)
            simpleStepDetector.setStepThreshold(Float.parseFloat(editText.getText().toString()));
        else
            stepDetector2.setSensitivity(Float.parseFloat(editText2.getText().toString()));
        Toast.makeText(this, "changed", Toast.LENGTH_SHORT).show();
    }
}

