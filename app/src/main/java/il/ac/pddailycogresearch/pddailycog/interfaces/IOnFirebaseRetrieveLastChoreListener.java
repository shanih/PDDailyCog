package il.ac.pddailycogresearch.pddailycog.interfaces;

import il.ac.pddailycogresearch.pddailycog.model.Chore;

/**
 * Created by User on 16/01/2018.
 */

public interface IOnFirebaseRetrieveLastChoreListener {
    void onChoreRetrieved(Chore chore);
    void onError(String msg);
}
