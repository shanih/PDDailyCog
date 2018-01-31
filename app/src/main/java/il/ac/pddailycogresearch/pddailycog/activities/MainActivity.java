package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.simple.AirplaneModeRequestActivity;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.ImageUtils;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainButtonOk)
    Button mainButtonOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (!FirebaseIO.getInstance().isUserLogged())
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @OnClick({R.id.mainButtonOk,R.id.buttonMainOpenQuestionnaire})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mainButtonOk:
                startActivity(new Intent(MainActivity.this, AirplaneModeRequestActivity.class));
                break;
            case R.id.buttonMainOpenQuestionnaire:
                startActivity(new Intent(MainActivity.this, QuestionnaireActivity.class));
                break;
        }
    }
}
