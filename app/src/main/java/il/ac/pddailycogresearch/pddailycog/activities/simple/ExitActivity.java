package il.ac.pddailycogresearch.pddailycog.activities.simple;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;

public class ExitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.buttonExitOpenSettings, R.id.buttonExit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonExitOpenSettings:
                startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
                break;
            case R.id.buttonExit:
                if(!CommonUtils.isAirplaneMode(this))
                    CommonUtils.closeApp(this);
                else
                    CommonUtils.showMessage(this,R.string.error_in_airplane_mode);
                break;
        }
    }
}
