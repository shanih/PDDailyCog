package il.ac.pddailycogresearch.pddailycog.activities.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;

public class GoodByeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_bye);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.buttonGoodByeOK)
    public void onViewClicked() {
        if(CommonUtils.isAirplaneMode(this))
            startActivity(new Intent(this,ExitActivity.class));
        CommonUtils.closeApp(this);
    }
}
