package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Intent;
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
import il.ac.pddailycogresearch.pddailycog.activities.simple.ExitActivity;
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

public class TrialChoreActivity extends AppCompatActivity implements
        TakePictureFragment.OnFragmentInteractionListener,
        InstructionFragment.OnFragmentInteractionListener,
        TextInputFragment.OnFragmentInteractionListener,
RatingFragment.OnFragmentInteractionListener{

    private static final String TAG = TrialChoreActivity.class.getSimpleName();

    @BindView(R.id.buttonTrialChoreOk)
    Button buttonTrialChoreOk;

    @BindView(R.id.buttonTrialChoreInstruction)
    Button buttonTrialChoreInstruction;

    @BindView(R.id.button_sound)
    FloatingActionButton button_sound;

    ArrayList<Fragment> partsFragments = new ArrayList<>();

    private Chore currentChore;
    private FirebaseIO firebaseIO = FirebaseIO.getInstance();
    private long startCurrentViewedPartTime;
    private boolean isInstructionClicked;//TODO put in save instance


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_chore);
        ButterKnife.bind(this);
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
        button_sound.setVisibility(View.GONE);
        startCurrentViewedPartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        terminateChore();
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


    @OnClick({R.id.buttonTrialChoreOk, R.id.buttonTrialChoreInstruction,R.id.buttonExit, R.id.button_sound})
    public void onViewClicked(View view) {
        switch (view.getId()) {
                        case R.id.buttonExit:
                showExitAlertDialog();
                break;
            case R.id.buttonTrialChoreInstruction:
                this.isInstructionClicked = true;
                updateTiming(currentChore.getCurrentPartNum());
                currentChore.increaseInstrcClicksNum();
                replaceFragment(Chore.PartsConstants.INSTRUCTION);
                break;
            case R.id.button_sound:
                MediaPlayer mpori;
                mpori = MediaPlayer.create(getApplicationContext(), R.raw.temp_audio_instr);
                mpori.start();
                onSoundButtonClick();
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
        DialogUtils.createAlertDialog(this, R.string.exit_alert_header, R.string.exit_alert_message,
                android.R.string.ok, android.R.string.cancel,
                new IOnAlertDialogResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            terminateChore();
                            DialogUtils.createTurnOffAirplaneModeAlertDialog(TrialChoreActivity.this);
                           // finish();
                        }
                    }
                });
    }

    private void updateTiming(Integer partEnded) {
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
        currentChore.setCompleted(true);
        terminateChore();
       // DialogUtils.createGoodbyeDialog(this);
        startActivity(new Intent(TrialChoreActivity.this, GoodByeActivity.class));

    }

    private void terminateChore(){
       if(isInstructionClicked)
           updateTiming(Chore.PartsConstants.INSTRUCTION);
        else
            updateTiming(currentChore.getCurrentPartNum());
        firebaseIO.saveChore(currentChore);
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
    }

    //takePictureFragment callback

    @Override
    public void onPictureBeenTaken(String imgPath) {
        currentChore.increaseTakePicClickNum();
        currentChore.setResultImg(imgPath);
        buttonTrialChoreOk.setEnabled(true);
        buttonTrialChoreOk.setBackgroundColor(getResources().getColor(R.color.colorButtons));
    }

    @Override
    public void onTakePictureFragmentViewCreated() {
        if (ImageUtils.lastTakenImageAbsolutePath == null) {
            buttonTrialChoreOk.setEnabled(false);

            buttonTrialChoreOk.setBackgroundColor(getResources().getColor(R.color.semi_gray));
        }
        else
            ((TakePictureFragment)partsFragments.get(Chore.PartsConstants.TAKE_PICTURE-1))
                    .setLastTakenImageToView();
    }

    @Override
    public void onTakePictureFragmentDetach() {
        buttonTrialChoreOk.setEnabled(true);

        buttonTrialChoreOk.setBackgroundColor(getResources().getColor(R.color.colorButtons));
    }

    //text input fragment callback

    @Override
    public void onTextInputFragmentCreateView() {
        buttonTrialChoreOk.setEnabled(false);

        buttonTrialChoreOk.setBackgroundColor(getResources().getColor(R.color.semi_gray));
    }

    @Override
    public void onCharacterAdded(String inputText, long timeBeforeCharacter) {
        buttonTrialChoreOk.setEnabled(true);

        buttonTrialChoreOk.setBackgroundColor(getResources().getColor(R.color.colorButtons));
        if(currentChore.getAddedCharactersNum()==0)
            currentChore.addTimeToTextInputTimeBeforeFstChar(timeBeforeCharacter);
        currentChore.increaseAddedCharacters();
        currentChore.setResultText(inputText);
    }

    @Override
    public void onCharacterDeleted(String inputText) {
        if(inputText.isEmpty())
            buttonTrialChoreOk.setEnabled(false);

        buttonTrialChoreOk.setBackgroundColor(getResources().getColor(R.color.semi_gray));
        currentChore.increaseDeletedCharaters();
        currentChore.setResultText(inputText);
    }

    @Override
    public void onTextInputFragmentDetach(long timeBeforeCharacter) {
        buttonTrialChoreOk.setEnabled(true);

        buttonTrialChoreOk.setBackgroundColor(getResources().getColor(R.color.colorButtons));
        if(currentChore.getAddedCharactersNum()==0)
            currentChore.addTimeToTextInputTimeBeforeFstChar(timeBeforeCharacter);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    //rating fragment callback
    @Override
    public void onRatingChanged(int rating) {
        currentChore.setResultRating(rating);
    }
    //endregion
}
