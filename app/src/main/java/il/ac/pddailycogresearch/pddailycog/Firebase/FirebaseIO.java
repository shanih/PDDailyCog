package il.ac.pddailycogresearch.pddailycog.Firebase;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFireBasLoginEventListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseRetrieveLastChoreListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseSaveImageListener;
import il.ac.pddailycogresearch.pddailycog.model.Chore;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;

import static il.ac.pddailycogresearch.pddailycog.utils.Consts.FIREBASE_LOGIN_STATE_IN;
import static il.ac.pddailycogresearch.pddailycog.utils.Consts.FIREBASE_LOGIN_STATE_NOT_AVAILBLE;

/**
 * Created by User on 15/01/2018.
 */

public class FirebaseIO {
    private static final String TAG = FirebaseIO.class.getSimpleName();

    private static FirebaseIO sInstance;

    // DB..
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mUserReference;

    // AUTH..
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private int mCurrentUserLoginState = FIREBASE_LOGIN_STATE_NOT_AVAILBLE;

    private IOnFireBasLoginEventListener mIOnFireBasLoginEventListener;
    private StorageReference mStorageReference;

    private FirebaseIO() {
        database.setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        initAuthListener();
        initUserDatabaseReference();
    }

    public static FirebaseIO getInstance() {
        if (sInstance == null)
            sInstance = new FirebaseIO();

        return sInstance;
    }

    private void initUserDatabaseReference() {
        if (mAuth.getCurrentUser() != null) {
            mUserReference = database.getReference(Consts.USERS_KEY).child(mAuth.getCurrentUser().getUid());
            mUserReference.keepSynced(true);//because persistence is enable, need to make sure the data is synced with database
            mStorageReference = FirebaseStorage.getInstance().getReference().child(mAuth.getCurrentUser().getUid());
        }
    }

    private void initAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    mCurrentUserLoginState = FIREBASE_LOGIN_STATE_IN;
                    initUserDatabaseReference();
                    if (mIOnFireBasLoginEventListener != null)
                        mIOnFireBasLoginEventListener.onUserLogin();

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    mCurrentUserLoginState = FIREBASE_LOGIN_STATE_NOT_AVAILBLE;

                    if (mIOnFireBasLoginEventListener != null)
                        mIOnFireBasLoginEventListener.onUserLoginError("User Is Signed Out.."); //ask Tal

                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }


    public void saveChore(Chore chore) {
        mUserReference.child(Consts.CHORES_KEY)
                .child(String.valueOf(chore.getTaskNum())).setValue(chore);

    }

    public void retrieveLastChore(final IOnFirebaseRetrieveLastChoreListener onFirebaseRetrieveLastChoreListener) {
        Query lastChoreQuery = mUserReference.child(Consts.CHORES_KEY).orderByKey().limitToLast(1);
        lastChoreQuery.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Chore chore = null;
                        if (dataSnapshot.getChildren().iterator().hasNext()) {
                            DataSnapshot ds = dataSnapshot.getChildren().iterator().next();
                            chore = ds.getValue(Chore.class);
                        }
                        onFirebaseRetrieveLastChoreListener.onChoreRetrieved(chore);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                        onFirebaseRetrieveLastChoreListener.onError(databaseError.getMessage());
                    }
                }
        );
    }

    public void retrieveQuestionnaire(final IOnFirebaseQuestionnaireListener firebaseQuestionnaireListener){
        mUserReference.child(Consts.QUESTIONNAIRE_KEY).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Integer> answers = new ArrayList<Integer>();
                        for (DataSnapshot valSnapshot: dataSnapshot.getChildren()) {
                            answers.add(valSnapshot.getValue(Integer.class));
                        }
                        firebaseQuestionnaireListener.onAnswersRetreived(answers);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        firebaseQuestionnaireListener.onError(databaseError.toException());

                    }
                }
        );
    }

    public void saveImage(Uri imageUri, final IOnFirebaseSaveImageListener onFirebaseSaveImageListener) {
        UploadTask uploadTask = mStorageReference.child(imageUri.getLastPathSegment()).putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
                onFirebaseSaveImageListener.onError(exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d(TAG, "image been saved in: " + downloadUrl.toString());
                onFirebaseSaveImageListener.onImageSaved(downloadUrl);
            }
        });
    }

    public void saveQuestionnaireAnswer(int key, int value){
        mUserReference.child(Consts.QUESTIONNAIRE_KEY).child(String.valueOf(key)).setValue(value);
    }
    public void saveQuestionnaireAnswer(List<Integer> answers){
        mUserReference.child(Consts.QUESTIONNAIRE_KEY).setValue(answers);
    }

    public void signUpNewUser(final Activity activity, String email, String password) {
    public void signUpNewUser(Activity activity, String email, String password,
                              final IOnFirebaseErrorListener firebaseErrorListener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful() && task.getException() != null) {
                            firebaseErrorListener.onError(task.getException());
                        }

                    }
                });
    }

    public void signInExistingUser(final Activity activity, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void onLoginActivityStart() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onLoginActivityStop() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    public void setIOnFireBaseLoginEventListener(IOnFireBasLoginEventListener iOnFireBaseLoginEventListener) {
        if (iOnFireBaseLoginEventListener != null)
            mIOnFireBasLoginEventListener = iOnFireBaseLoginEventListener;
    }

    public int getCurrentLoginState() {
        return mCurrentUserLoginState;
    }

    public boolean isUserLogged() {
        return mAuth.getCurrentUser() != null;
    }

    public void logout() {
        mAuth.signOut();
    }
}
