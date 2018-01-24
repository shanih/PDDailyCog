package il.ac.pddailycogresearch.pddailycog.interfaces;

import com.google.firebase.database.DatabaseException;

import java.util.List;

/**
 * Created by User on 24/01/2018.
 */

public interface IOnFirebaseQuestionnaireListener {
    void onAnswersRetreived(List<Integer> answers);

    void onError(DatabaseException e);
}
