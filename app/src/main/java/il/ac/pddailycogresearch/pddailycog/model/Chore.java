package il.ac.pddailycogresearch.pddailycog.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;

/**
 * Created by שני on 08/12/2017.
 */

public class Chore implements Serializable {

    public static final class PartsConstants {
        public static final int INSTRUCTION = 1;
        public static final int TAKE_PICTURE = 2;
        public static final int TEXT_INPUT = 3;
        public static final int RATING = 4;

        public static final int PARTS_AMOUNT = 4;
    }

    public Chore() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class) - Firebase requirement
    }

    public Chore(Integer taskNum) {
        this.taskNum = taskNum;
        this.currentPartNum=1;
        this.completed = false;
        this.instructionClicksNum = 0;
        this.takePicClicksNum =0;
        this.instructionTotalTime=0;
        this.takePictureTotalTime=0;
        this.textInputTotalTime=0;
        this.deletedCharactersNum=0;
        this.addedCharactersNum=0;
    }

    private Boolean completed;

    private int taskNum;

    private int currentPartNum;

    private int instructionClicksNum;

    private int soundInstrClicksNum;

    private int takePicClicksNum;

    private String resultImg;

    private String resultText;

    private int resultRating;

    private long instructionTotalTime;

    private long takePictureTotalTime;

    private long textInputTotalTime;

    private long instructionTotalSteps;

    private long takePictureTotalSteps;

    private long textInputTotalSteps;

    private long textInputTimeBeforeFstChar;

    private int addedCharactersNum;

    private int deletedCharactersNum;


    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
    public Boolean isCompleted() {
        return completed;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }
    public int getTaskNum() {
        return taskNum;
    }

    public int getCurrentPartNum() {
        return currentPartNum;
    }
    public void setCurrentPartNum(int currentPartNum) {
        this.currentPartNum = currentPartNum;
    }

    public void increaseInstrcClicksNum() {
        this.instructionClicksNum++;
    }
    public int getInstructionClicksNum() {
        return instructionClicksNum;
    }
    public void setInstructionClicksNum(int instructionClicksNum) {
        this.instructionClicksNum = instructionClicksNum;
    }

    public void increaseSoundInstrClicksNum() {
        this.soundInstrClicksNum++;
    }
    public int getSoundInstrClicksNum() {
        return soundInstrClicksNum;
    }
    public void setSoundInstrClicksNum(int soundInstrClicksNum) {
        this.soundInstrClicksNum = soundInstrClicksNum;
    }

    public void increaseTakePicClickNum() {
        this.takePicClicksNum++;
    }
    public int getTakePicClicksNum() {
        return takePicClicksNum;
    }
    public void setTakePicClicksNum(int takePicClicksNum) {
        this.takePicClicksNum = takePicClicksNum;
    }

    public String getResultImg() {
        return resultImg;
    }
    public void setResultImg(String resultImg) {
        this.resultImg = resultImg;
    }

    public String getResultText() {
        return resultText;
    }
    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    public int getResultRating() {
        return resultRating;
    }
    public void setResultRating(int resultRating) {
        this.resultRating = resultRating;
    }

    public void addTimeToInstructionTotalTime(long timeToAdd) {
        this.instructionTotalTime = this.instructionTotalTime + timeToAdd;
    }
    public long getInstructionTotalTime() {
        return instructionTotalTime;
    }
    public void setInstructionTotalTime(long instructionTotalTime) {
        this.instructionTotalTime = instructionTotalTime;
    }

    public void addTimeToTakePictureTotalTime(long timeToAdd) {
        this.takePictureTotalTime = this.takePictureTotalTime + timeToAdd;
    }
    public long getTakePictureTotalTime() {
        return takePictureTotalTime;
    }
    public void setTakePictureTotalTime(long takePictureTotalTime) {
        this.takePictureTotalTime = takePictureTotalTime;
    }

    public void addTimeToTextInputTotalTime(long timeToAdd) {
        this.textInputTotalTime = this.textInputTotalTime + timeToAdd;
    }
    public long getTextInputTotalTime() {
        return textInputTotalTime;
    }
    public void setTextInputTotalTime(long textInputTotalTime) {
        this.textInputTotalTime = textInputTotalTime;
    }

    public void addStepToInstructionTotalSteps(long stepsToAdd){
        this.instructionTotalSteps = this.instructionTotalSteps+stepsToAdd;
    }
    public long getInstructionTotalSteps() {
        return instructionTotalSteps;
    }
    public void setInstructionTotalSteps(long instructionTotalSteps) {
        this.instructionTotalSteps = instructionTotalSteps;
    }

    public void addStepsToTakePictureTotalSteps(long stepsToAdd){
        this.takePictureTotalSteps=this.takePictureTotalSteps+stepsToAdd;
    }
    public long getTakePictureTotalSteps() {
        return takePictureTotalSteps;
    }
    public void setTakePictureTotalSteps(long takePictureTotalSteps) {
        this.takePictureTotalSteps = takePictureTotalSteps;
    }

    public void addStepsToTextInputTotalSteps(long stepsToAdd) {
        this.textInputTotalSteps=this.textInputTotalSteps+stepsToAdd;
    }
    public long getTextInputTotalSteps() {
        return textInputTotalSteps;
    }
    public void setTextInputTotalSteps(long textInputTotalSteps) {
        this.textInputTotalSteps = textInputTotalSteps;
    }

    public void addTimeToTextInputTimeBeforeFstChar(long timeToAdd) {
        this.textInputTimeBeforeFstChar = this.textInputTimeBeforeFstChar + timeToAdd;
    }
    public long getTextInputTimeBeforeFstChar() {
        return textInputTimeBeforeFstChar;
    }
    public void setTextInputTimeBeforeFstChar(long textInputTimeBeforeFstChar) {
        this.textInputTimeBeforeFstChar = textInputTimeBeforeFstChar;
    }

    public void increaseDeletedCharaters() {
        this.deletedCharactersNum++;
    }
    public int getDeletedCharactersNum() {
        return deletedCharactersNum;
    }
    public void setDeletedCharactersNum(int deletedCharactersNum) {
        this.deletedCharactersNum = deletedCharactersNum;
    }

    public void increaseAddedCharacters() {
        this.addedCharactersNum++;
    }
    public int getAddedCharactersNum() {
        return addedCharactersNum;
    }
    public void setAddedCharactersNum(int addedCharactersNum) {
        this.addedCharactersNum = addedCharactersNum;
    }


}
