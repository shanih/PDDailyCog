package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.simple.AirplaneModeRequestActivity;

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

    @OnClick(R.id.mainButtonOk)
    public void onViewClicked() {
        startActivity(new Intent(MainActivity.this, AirplaneModeRequestActivity.class));
    }
}
