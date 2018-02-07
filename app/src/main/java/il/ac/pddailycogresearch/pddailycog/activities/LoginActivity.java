package il.ac.pddailycogresearch.pddailycog.activities;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFireBasLoginEventListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseErrorListener;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.DialogUtils;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.EditTextUserName)
    EditText editTextUserName;
    @BindView(R.id.EditTextPassword)
    EditText editTextPassword;
    private FirebaseIO mFirebaseIO = FirebaseIO.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mFirebaseIO.setIOnFireBaseLoginEventListener(
                new IOnFireBasLoginEventListener() {
                    @Override
                    public void onUserLogin() {
                        DialogUtils.hideLoading();
                        finish();
                    }

                    @Override
                    public void onUserLoginError(String Msg) {
                        DialogUtils.hideLoading();
                      //  CommonUtils.showMessage(LoginActivity.this, Msg);
                    }
                }
        );

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

    @OnClick(R.id.BtnSignIn)
    public void onViewClicked() {
        String password = editTextPassword.getText().toString();
        String username = editTextUserName.getText().toString();

        if (username == null || username.isEmpty()) {
            onError(R.string.empty_username);
            return;
        }
        if (password == null || password.length() < 6) {
           onError(R.string.short_password);
            return;
        }

        String legalEmial = username+ Consts.EMAIL_SUFFIX;

        if(!isEmailValid(legalEmial)){
            onError(R.string.wrong_username);
            return;
        }

        mFirebaseIO.signUpNewUser(LoginActivity.this, legalEmial, password,
                new IOnFirebaseErrorListener() {
                    @Override
                    public void onError(Exception exception) {
                        DialogUtils.hideLoading();
                        CommonUtils.showMessage(LoginActivity.this,exception.getMessage());
                    }
                });
        DialogUtils.showLoading(this);
    }

    public  boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void onError(@StringRes int msgID) {
        CommonUtils.showMessage(this, msgID);
    }
}
