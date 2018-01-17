package il.ac.pddailycogresearch.pddailycog.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseIO mFirebaseIO = FirebaseIO.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

    }

    private void initViews() {
        findViewById(R.id.BtnSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = ((EditText) (findViewById(R.id.EditTextUserName))).getText().toString();
                String password = ((EditText) (findViewById(R.id.EditTextPassword))).getText().toString();

                mFirebaseIO.signUpNewUser(LoginActivity.this, userName, password);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseIO.onLoginActivityStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseIO.onLoginActivityStop();
    }
}