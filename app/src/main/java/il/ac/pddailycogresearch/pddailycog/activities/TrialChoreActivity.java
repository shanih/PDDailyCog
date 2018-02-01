package il.ac.pddailycogresearch.pddailycog.activities;


import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import il.ac.pddailycogresearch.pddailycog.activities.simple.GoodByeActivity;
import il.ac.pddailycogresearch.pddailycog.fragments.InstructionFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.RatingFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.TakePictureFragment;
import il.ac.pddailycogresearch.pddailycog.fragments.TextInputFragment;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseRetrieveLastChoreListener;
import il.ac.pddailycogresearch.pddailycog.model.Chore;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;
import il.ac.pddailycogresearch.pddailycog.utils.DialogUtils;
import il.ac.pddailycogresearch.pddailycog.utils.ImageUtils;
import il.ac.pddailycogresearch.pddailycog.stepdetector.StepCounter;


public class TrialChoreActivity extends AppCompatActivity implements
        TakePictureFragment.OnFragmentInteractionListener,
        InstructionFragment.OnFragmentInteractionListener,
        TextInputFragment.OnFragmentInteractionListener,
        RatingFragment.OnFragmentInteractionListener {

    private static final String TAG = TrialChoreActivity.class.getSimpleName();

    @BindView(R.id.buttonTrialChoreOk)
    Button buttonTrialChoreOk;

    @BindView(R.id.buttonTrialChoreInstruction)
    Button buttonTrialChoreInstruction;

    @BindView(R.id.button_sound_trial_activity)
    FloatingActionButton button_sound;

    ArrayList<Fragment> partsFragments = new ArrayList<>();
    MediaPlayer mpori;

    private Chore currentChore;
    private FirebaseIO firebaseIO = FirebaseIO.getInstance();
    private StepCounter stepCounter = StepCounter.getInstance();
    private long startCurrentViewedPartTime;
    private long startCurrentViewPartStepsNum = -1;
    private boolean isInstructionClicked;//TODO put in save instance


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_chore);
        ButterKnife.bind(this);
        stepCounter.registerSensors(this);
        initMembers();
    }

    private void initMembers() {
        partsFragments.add(new InstructionFragment());
        partsFragments.add(new TakePictureFragment());
        partsFragments.add(new TextInputFragment());
        partsFragments.add(new RatingFragment());
        initChore();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!CommonUtils.isAirplaneMode(this)) //TODO think if useful
            DialogUtils.createTurnOnAirPlaneModeDialog(this);
        button_sound.setVisibility(View.GONE);
        stepCounter.registerSensors(this);
        startCurrentViewedPartTime = System.currentTimeMillis();
        startCurrentViewPartStepsNum = stepCounter.getStepsNum();
       /* InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);*/
    }

    @Override
    protected void onStop() {
        terminateChore();
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stepCounter.unregisterSensors();
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
            int nextChore = chore.getTaskNum() + 1;
            if (nextChore <= Consts.CHORES_AMOUNT)
                currentChore = new Chore(nextChore);
            else {
                CommonUtils.showMessage(this, R.string.error_no_more_chores);
                currentChore = new Chore(1);//TODO delete, decide what to do
            }
        } else
            this.currentChore = chore;
        replaceFragment(currentChore.getCurrentPartNum());
    }


    @OnClick({R.id.buttonTrialChoreOk, R.id.buttonTrialChoreInstruction, R.id.buttonExit, R.id.button_sound_trial_activity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonExit:
                currentChore.increaseExitClickNum();
                showExitAlertDialog();
                break;
            case R.id.buttonTrialChoreInstruction:
                this.isInstructionClicked = true;
                updateTimingAndSteps(currentChore.getCurrentPartNum());
                currentChore.increaseInstrcClicksNum();
                replaceFragment(Chore.PartsConstants.INSTRUCTION);
                break;
            case R.id.button_sound_trial_activity:
                toggleMediaPlayer();
                break;
            case R.id.buttonTrialChoreOk:
                CommonUtils.hideKeyboard(this);
                moveToNextPart();
                replaceFragment(currentChore.getCurrentPartNum());
                break;
        }
    }

    private void toggleMediaPlayer() {
        if(mpori==null) {
            mpori = MediaPlayer.create(getApplicationContext(), R.raw.trial_instrc_male_sound);
        }
        if (mpori.isPlaying()) {
            mpori.pause();
            button_sound.setImageResource(R.drawable.sound_icon);
        }
        else {
            button_sound.setImageResource(R.drawable.stop_ic);
            mpori.start();
            onSoundButtonClick();
        }
    }

    private void replaceFragment(int nextPart) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutTrialFragmentContainer, partsFragments.get(nextPart - 1));
        //   transaction.addToBackStack(null); //we block back so this is useless
        transaction.commit();

    }

    private void moveToNextPart() {

        if (this.isInstructionClicked) {
            this.isInstructionClicked = false;
            updateTimingAndSteps(Chore.PartsConstants.INSTRUCTION);
        } else {
            updateTimingAndSteps(currentChore.getCurrentPartNum());
            int nextPart = currentChore.getCurrentPartNum() + 1;
            if (nextPart <= Chore.PartsConstants.PARTS_AMOUNT) {
                currentChore.setCurrentPartNum(nextPart);
            } else
                finishChore();
        }
    }

    private void showExitAlertDialog() {
        DialogUtils.createAlertDialog(this, R.string.exit_alert_header, R.string.exit_alert_message,
                android.R.string.ok, android.R.string.cancel,
                new IOnAlertDialogResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            terminateChore();
                            DialogUtils.createTurnOffAirplaneModeAlertDialog(TrialChoreActivity.this);
                            // finish();
                           // CommonUtils.closeApp(TrialChoreActivity.this);
                        }
                    }
                });
    }

    private void updateTimingAndSteps(Integer partEnded) {
        if (this.startCurrentViewedPartTime != 0) {
            long timeElapsed = System.currentTimeMillis() - this.startCurrentViewedPartTime;
            long stepsSinceStart = stepCounter.getStepsNum() - startCurrentViewPartStepsNum;
            switch (partEnded) {
                case Chore.PartsConstants.INSTRUCTION:
                    currentChore.addTimeToInstructionTotalTime(timeElapsed);
                    currentChore.addStepToInstructionTotalSteps(stepsSinceStart);
                    break;
                case Chore.PartsConstants.TAKE_PICTURE:
                    currentChore.addTimeToTakePictureTotalTime(timeElapsed);
                    currentChore.addStepsToTakePictureTotalSteps(stepsSinceStart);
                    break;
                case Chore.PartsConstants.TEXT_INPUT:
                    currentChore.addTimeToTextInputTotalTime(timeElapsed);
                    currentChore.addStepsToTextInputTotalSteps(stepsSinceStart);
                    break;
            }
        }
        this.startCurrentViewPartStepsNum = stepCounter.getStepsNum();
        this.startCurrentViewedPartTime = System.currentTimeMillis();
    }

    private void unableOkButton() {
        buttonTrialChoreOk.setEnabled(false);
        buttonTrialChoreOk.setBackgroundColor(Color.parseColor("#979797"));
    }

    private void finishChore() {
        currentChore.setCompleted(true);
        terminateChore();
        // DialogUtils.createGoodbyeDialog(this);
        startActivity(new Intent(TrialChoreActivity.this, GoodByeActivity.class));

    }

    private void terminateChore() {
        if (isInstructionClicked)
            updateTimingAndSteps(Chore.PartsConstants.INSTRUCTION);
        else
            updateTimingAndSteps(currentChore.getCurrentPartNum());
        firebaseIO.saveChore(currentChore);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    //region fragments callbacks

    //instructionFragment callback
    @Override
    public void onSoundButtonClick() {
        currentChore.increaseSoundInstrClicksNum();
    }

    @Override
    public void onInstructionFragmentAttach() {
        buttonTrialChoreInstruction.setVisibility(View.GONE);
        button_sound.setVisibility(View.VISIBLE);
    }

    @Override
    public void onInstructionFragmentDetach() {
        buttonTrialChoreInstruction.setVisibility(View.VISIBLE);
        button_sound.setVisibility(View.GONE);
        if (mpori!=null){
            mpori.stop();
        }
    }

    //takePictureFragment callback

    @Override
    public void onPictureBeenTaken(String imgPath) {
        currentChore.increaseTakePicClickNum();
        currentChore.setResultImg(imgPath);
        enableOkButton();
    }

    @Override
    public void onTakePictureFragmentViewCreated() {
        if (ImageUtils.lastTakenImageAbsolutePath == null) {
            unableOkButton();
        }
        else
            ((TakePictureFragment) partsFragments.get(Chore.PartsConstants.TAKE_PICTURE - 1))
                    .setLastTakenImageToView();
    }

    @Override
    public void onTakePictureFragmentDetach() {
        enableOkButton();
    }

    private void enableOkButton() {
        buttonTrialChoreOk.setEnabled(true);
        buttonTrialChoreOk.setBackgroundColor(Color.parseColor("#4656AC"));
    }

    //text input fragment callback

    @Override
    public void onTextInputFragmentCreateView() {
        if (currentChore.getResultText() == null || currentChore.getResultText().isEmpty()) {
            unableOkButton();
        } else {
            ((TextInputFragment) partsFragments.get(Chore.PartsConstants.TEXT_INPUT - 1))
                    .setTextToEditText(currentChore.getResultText());
        }
    }

    @Override
    public void onCharacterAdded(String inputText, long timeBeforeCharacter) {
        enableOkButton();

        if (currentChore.getAddedCharactersNum() == 0)
            currentChore.addTimeToTextInputTimeBeforeFstChar(timeBeforeCharacter);
        currentChore.increaseAddedCharacters();
        currentChore.setResultText(inputText);
    }

    @Override
    public void onCharacterDeleted(String inputText) {
        if (inputText.isEmpty()) {
            unableOkButton();
        }
        currentChore.increaseDeletedCharaters();
        currentChore.setResultText(inputText);
    }


    @Override
    public void onTextInputFragmentStop(long timeBeforeCharacter) {
        enableOkButton();
        if (currentChore.getAddedCharactersNum() == 0)
            currentChore.addTimeToTextInputTimeBeforeFstChar(timeBeforeCharacter);

    }

    @Override
    public void onTextInputFragmentDetach() {
        enableOkButton();
    }


    //rating fragment callback
    @Override
    public void onRatingFragmentCraeteView() {
        if (currentChore.getResultRating() == 0) {
            ((RatingFragment) partsFragments.get(Chore.PartsConstants.RATING - 1))
                    .setRatingSelection(0);
            unableOkButton();
        } else {
            ((RatingFragment) partsFragments.get(Chore.PartsConstants.RATING - 1))
                    .setRatingSelection(currentChore.getResultRating());
            enableOkButton();
        }
    }

    @Override
    public void onRatingChanged(int rating) {
        currentChore.setResultRating(rating);
        enableOkButton();
    }

    @Override
    public void onRatingFragmentDetach() {
        enableOkButton();
    }
    //endregion
}
