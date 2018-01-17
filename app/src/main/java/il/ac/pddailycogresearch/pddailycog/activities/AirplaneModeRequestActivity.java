package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.R;
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
                break;
            case R.id.buttonAirplaneOk:
                if(isAirplaneMode())
                    openNextChoreActivity();
                else
                    CommonUtils.showMessage(this,R.string.error_not_in_airplane_mode);
                break;
        }
    }

    private void openNextChoreActivity() {
        //decide which chore should be made
        Intent nextActivity = new Intent(AirplaneModeRequestActivity.this,
                TrialChoreActivity.class);
        startActivity(nextActivity);
    }

    public boolean isAirplaneMode() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            return Settings.System.getInt(this.getContentResolver(),Settings.Global.AIRPLANE_MODE_ON,0)==1;
        } else {
            return Settings.System.getInt(this.getContentResolver(),Settings.System.AIRPLANE_MODE_ON,0)==1;

        }
    }
}
