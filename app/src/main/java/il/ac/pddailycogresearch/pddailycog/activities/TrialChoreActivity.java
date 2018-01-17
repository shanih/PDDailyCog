package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseRetrieveLastChoreListener;
import il.ac.pddailycogresearch.pddailycog.model.Chore;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.ImageUtils;

public class TrialChoreActivity extends AppCompatActivity {

    private static final String TAG = TrialChoreActivity.class.getSimpleName();

    private Chore currentChore;
    private FirebaseIO firebaseIO = FirebaseIO.getInstance();

    @BindView(R.id.buttonTrialChoreOk)
    Button buttonTrialChoreOk;
    @BindView(R.id.imageViewTrial)
    ImageView imageViewTrial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_chore);
        ButterKnife.bind(this);
        initChore();
    }

    @Override
    protected void onStop() {
        showExitAlertDialog();
        super.onStop();
    }

    private void initChore() {
        firebaseIO.retrieveLastChore(
                new IOnFirebaseRetrieveLastChoreListener() {
                    @Override
                    public void onChoreRetrieved(Chore chore) {
                        if (chore != null)
                            updateCurrentChore(chore);
                        else
                            updateCurrentChore(new Chore(1)); //new user, create first chore
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e(TAG, msg);
                        CommonUtils.showMessage(TrialChoreActivity.this, msg);
                    }
                }
        );
    }

    private void updateCurrentChore(Chore chore) {
        if (chore.isCompleted()) {
            int nextChore = chore.getChoreNum() + 1;
            if (nextChore <= Consts.CHORES_AMOUNT)
                currentChore = new Chore(nextChore);
            else {
                CommonUtils.showMessage(this, R.string.error_no_more_chores);
                currentChore = new Chore(1);
            }
        } else
            this.currentChore = chore;
    }


    @OnClick({R.id.buttonTrialChoreExit, R.id.buttonTrialChoreOk})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonTrialChoreExit:
                showExitAlertDialog();
                break;
            case R.id.buttonTrialChoreOk:
                dispatchTakePictureIntent(imageViewTrial);
                break;
        }
    }

    private void showExitAlertDialog() {
        CommonUtils.createAlertDialog(this, R.string.exit_alert_header, R.string.exit_alert_message,
                new IOnAlertDialogResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        if(result) {
                            firebaseIO.saveChore(currentChore);
                            finish();
                        }
                    }
                });
    }


    //region take pictures, should be refactored
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String imgAbsolutePath;
    private Uri imgUri;

    public void dispatchTakePictureIntent(ImageView imageView) {
        Intent takePictureIntent = ImageUtils.createTakePictureIntent(this);
        imgAbsolutePath = takePictureIntent.getStringExtra(ImageUtils.IMAGE_ABSOLUTE_PATH);
        Bundle extras = takePictureIntent.getExtras();
        imgUri = (Uri) extras.get(MediaStore.EXTRA_OUTPUT);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            setImageViewHeight(imageView);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void setImageViewHeight(ImageView imageView) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int imageHeight = (int) Math.round(screenHeight * Consts.IMAGEVIEW_HEIGHT_PERCENTAGE);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, imageHeight));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                ImageUtils.setPic(imageViewTrial, imgAbsolutePath);
            } else
                imageViewTrial.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)); //TODO change hard-coded

        }
    }

    //endregion
}
