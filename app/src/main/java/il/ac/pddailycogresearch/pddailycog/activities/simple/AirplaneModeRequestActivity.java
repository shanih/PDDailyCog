package il.ac.pddailycogresearch.pddailycog.activities.simple;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.TrialChoreActivity;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;

public class AirplaneModeRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airplane_mode_request);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.buttonOpenAirplaneModeSettings, R.id.buttonAirplaneOk})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonOpenAirplaneModeSettings:
                startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
               // FirebaseIO.getInstance().logout();
                break;
            case R.id.buttonAirplaneOk:
                if(CommonUtils.isAirplaneMode(this)) //TODO uncomment but its annoying
                    openNextChoreActivity();
               else
                   CommonUtils.showMessage(this,R.string.error_not_in_airplane_mode);
                break;
        }
    }

    private void openNextChoreActivity() {
        //TODO decide which chore should be made
        Intent nextActivity = new Intent(AirplaneModeRequestActivity.this,
                TrialChoreActivity.class);
        startActivity(nextActivity);
    }

}
