package il.ac.pddailycogresearch.pddailycog.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

//import com.crashlytics.android.Crashlytics;

import com.crashlytics.android.Crashlytics;

import il.ac.pddailycogresearch.pddailycog.Firebase.FirebaseIO;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseRetrieveLastChoreListener;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnFirebaseSaveImageListener;
import il.ac.pddailycogresearch.pddailycog.model.Chore;
import il.ac.pddailycogresearch.pddailycog.utils.Consts;

public class ConnectivityChangeReceiver extends BroadcastReceiver { //ask Tal if too much for users to call this every time...
    private static final String TAG = ConnectivityChangeReceiver.class.getSimpleName();
    /***
     * allow checking wither this is the first receiver
     */
    private static int race=0;

    @Override
    public void onReceive(Context context, Intent intent) {
        //act only if there is internet connection and if it is the first instance to react
        //needed because in airplane toggle happens few connectivity changes
        if(isNetworkAvailable(context)&&++race==1) {
            saveChoreImageInDB(context);
            Crashlytics.log("Receiver log");
            Crashlytics.logException(new Throwable("Receiver non-fatal"));
        }
        else {
            Log.d(TAG, "false receiver");
        }
    }

    private void saveChoreImageInDB(Context context) {

        final FirebaseIO firebaseIO = FirebaseIO.getInstance();
        if(!firebaseIO.isUserLogged())
            return;
        firebaseIO.retrieveLastChore(
                new IOnFirebaseRetrieveLastChoreListener() {
                    @Override
                    public void onChoreRetrieved(final Chore chore) {
                        if (chore!=null&&chore.getResultImg() != null && chore.getResultImg().split(":")[0].equals(Consts.LOCAL_URI_PREFIX))
                            firebaseIO.saveImage(Uri.parse(chore.getResultImg()),
                                    new IOnFirebaseSaveImageListener() {
                                        @Override
                                        public void onImageSaved(Uri downloadUrl) {
                                            chore.setResultImg(downloadUrl.toString());
                                            firebaseIO.saveChore(chore);
                                        }

                                        @Override
                                        public void onError(String msg) {
                                            Log.e(TAG,msg);
                                        }
                                    });
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e(TAG,msg);
                    }
                }
        );
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
