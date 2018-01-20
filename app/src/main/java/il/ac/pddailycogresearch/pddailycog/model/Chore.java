package il.ac.pddailycogresearch.pddailycog.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by שני on 08/12/2017.
 */

public class Chore implements Serializable {

    public static final class PartsConstants {
        public static final int INSTRUCTION = 1;
        public static final int TAKE_PICTURE = 2;
        public static final int TEXT_INPUT = 3;

        public static final int PARTS_AMOUNT = 3;
    }

    public Chore() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class) - Firebase requirement
    }

    public Chore(Integer choreNum) {
        this.choreNum = choreNum;
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

    private int choreNum;

    private int currentPartNum;

    private int instructionClicksNum;

    private int takePicClicksNum;

    private int soundInstrClicksNum;

    private String resultImg;

    private String resultText;

    private long instructionTotalTime;

    private long textInputTotalTime;

    private long takePictureTotalTime;

    private int addedCharactersNum;

    private int deletedCharactersNum;


    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean isCompleted() {
        return completed;
    }



    public void setChoreNum(int choreNum) {
        this.choreNum = choreNum;
    }

    public int getChoreNum() {
        return choreNum;
    }



    public int getCurrentPartNum() {
        return currentPartNum;
    }

    public void setCurrentPartNum(int currentPartNum) {
        this.currentPartNum = currentPartNum;
    }

    public int getInstructionClicksNum() {
        return instructionClicksNum;
    }

    public void setInstructionClicksNum(int instructionClicksNum) {
        this.instructionClicksNum = instructionClicksNum;
    }

    public void increaseInstrcClicksNum() {
        this.instructionClicksNum++;
    }

    public int getTakePicClicksNum() {
        return takePicClicksNum;
    }

    public void setTakePicClicksNum(int takePicClicksNum) {
        this.takePicClicksNum = takePicClicksNum;
    }

    public void increaseTakePicClickNum() {
        this.takePicClicksNum++;
    }

    public int getSoundInstrClicksNum() {
        return soundInstrClicksNum;
    }

    public void setSoundInstrClicksNum(int soundInstrClicksNum) {
        this.soundInstrClicksNum = soundInstrClicksNum;
    }

    public void increaseSoundInstrClicksNum() {
        this.soundInstrClicksNum++;
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


    @Exclude
    public void addTimeToInstructionTotalTime(long timeToAdd) {
        this.instructionTotalTime = this.instructionTotalTime + timeToAdd;
    }

    public long getInstructionTotalTime() {
        return instructionTotalTime;
    }

    public void setInstructionTotalTime(long instructionTotalTime) {
        this.instructionTotalTime = instructionTotalTime;
    }

    @Exclude
    public void addTimeToTextInputTotalTime(long timeToAdd) {
        this.textInputTotalTime = this.textInputTotalTime + timeToAdd;
    }
    public long getTextInputTotalTime() {
        return textInputTotalTime;
    }

    public void setTextInputTotalTime(long textInputTotalTime) {
        this.textInputTotalTime = textInputTotalTime;
    }

    @Exclude
    public void addTimeToTakePictureTotalTime(long timeToAdd) {
        this.takePictureTotalTime = this.takePictureTotalTime + timeToAdd;
    }

    public long getTakePictureTotalTime() {
        return takePictureTotalTime;
    }

    public void setTakePictureTotalTime(long takePictureTotalTime) {
        this.takePictureTotalTime = takePictureTotalTime;
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
