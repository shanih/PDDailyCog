package il.ac.pddailycogresearch.pddailycog.interfaces;

import android.net.Uri;

/**
 * Created by User on 16/01/2018.
 */

public interface IOnFirebaseSaveImageListener {
    void onImageSaved(Uri downloadUrl);
    void onError(String msg);
}
