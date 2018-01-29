package il.ac.pddailycogresearch.pddailycog.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseException;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.activities.simple.GoodByeActivity;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseQuestionnaireListener;
import il.ac.pddailycogresearch.pddailycog.utils.CommonUtils;

public class QuestionnaireActivity extends AppCompatActivity {

    public static String TAG = QuestionnaireActivity.class.getSimpleName();

    @BindView(R.id.textViewQuestionnaireQuest)
    TextView textViewQuestionnaireQuest;
    @BindView(R.id.radioGroupQuestionnaireAns)
    RadioGroup radioGroupQuestionnaireAns;
    @BindView(R.id.buttonQuestionnaireOK)
    Button buttonQuestionnaireOK;

    private FirebaseIO firebaseIO = FirebaseIO.getInstance();

    private int questionIdx = 0;
    private int[] answers_ids;
    private String[] questions;
    private Integer[] answers;

    private View.OnClickListener radioButtonsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        ButterKnife.bind(this);
        init();
        firebaseIO.retrieveQuestionnaire(new IOnFirebaseQuestionnaireListener() {
            @Override
            public void onAnswersRetreived(List<Integer> prevAnswers) {
                if (prevAnswers.size() > 0)
                    answers = prevAnswers.toArray(new Integer[0]);
                else {
                    initEmptyAnswersArray();
                }

                setCurrentQuestion();
            }

            @Override
            public void onError(DatabaseException e) {
                e.printStackTrace();
                initEmptyAnswersArray();
                setCurrentQuestion();
            }
        });
    }

    private void init() {

        TypedArray ta= getResources().obtainTypedArray(R.array.answers_id);
        int n = ta.length();
        answers_ids = new int[n];
        for (int i = 0; i < n; ++i) {
            int id = ta.getResourceId(i, 0);
            if (id > 0) {
                answers_ids[i] = id;
            } else {
                Log.e(TAG,"answers xml error");
                CommonUtils.showMessage(this,R.string.answers_xml_error);
            }
        }
        questions = getResources().getStringArray(R.array.questions);
        radioButtonsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonQuestionnaireOK.setEnabled(true);
                buttonQuestionnaireOK.setBackgroundColor(getResources().getColor(R.color.colorButtons));
                int ansIdx = radioGroupQuestionnaireAns.indexOfChild(v);
                answers[questionIdx] = ansIdx;

            }
        };
    }

    private void initEmptyAnswersArray() {
        //inilize answers array to illegal value
        answers = new Integer[questions.length];
        for (int i = 0; i < questions.length; i++) {
            answers[i] = -1;
        }
    }

    private void setCurrentQuestion() {
        textViewQuestionnaireQuest.setText(CommonUtils.fromHtml(questions[questionIdx]));
        String[] answersTexts = getResources().getStringArray(answers_ids[questionIdx]);
        radioGroupQuestionnaireAns.removeAllViews();
        for (String answer : answersTexts) {
            RadioButton rb = new RadioButton(this);
            rb.setText(answer);
            rb.setOnClickListener(radioButtonsListener);
            radioGroupQuestionnaireAns.addView(rb);
        }
        if (answers[questionIdx] != -1) {
            int id = radioGroupQuestionnaireAns.getChildAt(answers[questionIdx]).getId();
            radioGroupQuestionnaireAns.check(id);
            buttonQuestionnaireOK.setEnabled(true);
            buttonQuestionnaireOK.setBackgroundColor(getResources().getColor(R.color.colorButtons));
        } else {
            buttonQuestionnaireOK.setEnabled(false);
            buttonQuestionnaireOK.setBackgroundColor(getResources().getColor(R.color.semi_gray));
        }

    }

    @OnClick(R.id.buttonQuestionnaireOK)
    public void onViewClicked() {
        saveAnswer();
        if (++questionIdx < questions.length) {
            setCurrentQuestion();
        } else
            startActivity(new Intent(this, GoodByeActivity.class));
    }

    private void saveAnswer() {
        firebaseIO.saveQuestionnaireAnswer(Arrays.asList(answers));
        // firebaseIO.saveQuestionnaireAnswer(questionIdx + 1, answers[questionIdx] + 1);
    }

    @Override
    public void onBackPressed() {
        if (questionIdx > 0) {
            questionIdx--;
            setCurrentQuestion();
        } else
            super.onBackPressed();
    }
}
