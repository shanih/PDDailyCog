package il.ac.pddailycogresearch.pddailycog.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.fragments.InstructionFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.TakePictureFragment;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseRetrieveLastChoreListener;
import il.ac.pddailycogresearch.pddailycog.model.Chore;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;

public class TrialChoreActivity extends AppCompatActivity implements
        TakePictureFragment.OnFragmentInteractionListener,
        InstructionFragment.OnFragmentInteractionListener {

    private static final String TAG = TrialChoreActivity.class.getSimpleName();

    @BindView(R.id.buttonTrialChoreOk)
    Button buttonTrialChoreOk;

    @BindView(R.id.buttonTrialChoreInstruction)
    Button buttonTrialChoreInstruction;

    ArrayList<Fragment> partsFragments = new ArrayList<>();

    private Chore currentChore;
    private FirebaseIO firebaseIO = FirebaseIO.getInstance();
    private long startCurrentViewedPartTime = 0;//TODO put in saved instance
    private boolean isInstructionClicked = false;//TODO put in saved instance


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_chore);
        ButterKnife.bind(this);
        initMembers();
    }

    private void initMembers() {
        startCurrentViewedPartTime = System.currentTimeMillis();
        partsFragments.add(new InstructionFragment());
        partsFragments.add(new TakePictureFragment());
        initChore();
    }

    @Override
    protected void onStop() {
        firebaseIO.saveChore(currentChore);//TODO ask Tal?
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
        replaceFragment(currentChore.getCurrentPartNum());
    }


    @OnClick({R.id.buttonTrialChoreExit, R.id.buttonTrialChoreOk, R.id.buttonTrialChoreInstruction})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonTrialChoreExit:
                showExitAlertDialog();
                break;
            case R.id.buttonTrialChoreInstruction:
                this.isInstructionClicked = true;
                updateTiming(currentChore.getCurrentPartNum());
                currentChore.increaseInstrcClicksNum();
                replaceFragment(Chore.PartsConstants.INSTRUCTION);
                break;
            case R.id.buttonTrialChoreOk:
                moveToNextPart();
                replaceFragment(currentChore.getCurrentPartNum());
                break;
        }
    }

    private void replaceFragment(int nextPart) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutTrialFragmentContainer, partsFragments.get(nextPart - 1));
        //   transaction.addToBackStack(null);//TODO decide if its right
        transaction.commit();
    }

    private void moveToNextPart() {
        if (this.isInstructionClicked) {
            this.isInstructionClicked = false;
            updateTiming(Chore.PartsConstants.INSTRUCTION);
        } else {
            updateTiming(currentChore.getCurrentPartNum());
            int nextPart = currentChore.getCurrentPartNum() + 1;
            if (nextPart <= Chore.PartsConstants.PARTS_AMOUNT)
                currentChore.setCurrentPartNum(nextPart);
            else
                finishChore();
        }
    }

    private void showExitAlertDialog() {
        CommonUtils.createAlertDialog(this, R.string.exit_alert_header, R.string.exit_alert_message,
                new IOnAlertDialogResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            firebaseIO.saveChore(currentChore);
                            finish();
                        }
                    }
                });
    }

    private void updateTiming(Integer partEnded) { //TODO test
        if (this.startCurrentViewedPartTime != 0) {
            long timeElapsed = System.currentTimeMillis() - this.startCurrentViewedPartTime;
            switch (partEnded) {
                case Chore.PartsConstants.INSTRUCTION:
                    currentChore.addTimeToInstructionTotalTime(timeElapsed);
                    break;
                case Chore.PartsConstants.TAKE_PICTURE:
                    currentChore.addTimeToTakePictureTotalTime(timeElapsed);
                    break;
                case Chore.PartsConstants.TEXT_INPUT:
                    currentChore.addTimeToTextInputTotalTime(timeElapsed);
                    break;
            }
        }
        this.startCurrentViewedPartTime = System.currentTimeMillis();
    }


    private void finishChore() {
        //TODO UI, ask Tal pretty way to close things
        updateTiming(Chore.PartsConstants.PARTS_AMOUNT);//update last part timing
        currentChore.setCompleted(true);
        firebaseIO.saveChore(currentChore);
        /*getMvpView().showMessage(R.string.chore_finished);
        finish();*/
        //TODO
    }


    //takePictureFragment callback

    @Override
    public void onPictureBeenTaken(String imgUri) {
        currentChore.increaseTakePicClickNum();
        currentChore.setResultImg(imgUri.toString());
        buttonTrialChoreOk.setEnabled(true);
    }

    @Override
    public void onTakePictureFragmentViewCreated() {
        if (currentChore.getResultImg() == null)
            buttonTrialChoreOk.setEnabled(false);
        else
            ((TakePictureFragment)partsFragments.get(Chore.PartsConstants.TAKE_PICTURE-1))
                    .setLastTakenImageToView();
    }

    @Override
    public void onTakePictureFragmentDetach() {
        buttonTrialChoreOk.setEnabled(true);
    }

    //instructionFragment callback
    @Override
    public void onSoundButtonClick(Uri uri) {

    }

    @Override
    public void onInstructionFragmentAttach() {
        buttonTrialChoreInstruction.setVisibility(View.GONE);
    }

    @Override
    public void onInstructionFragmentDetach() {
        buttonTrialChoreInstruction.setVisibility(View.VISIBLE);
    }
}
