package il.ac.pddailycogresearch.pddailycog.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;

/**
 * Created by User on 17/01/2018.
 */

public final class CommonUtils {
    private CommonUtils(){

    }

    public static void createAlertDialog(final Context context, final int title, final int message,
                                         final IOnAlertDialogResultListener alertDialogResultListener){
        final AlertDialog ad = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialogResultListener.onResult(true);
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialogResultListener.onResult(false);
                            }
                        })
                .create();
        ad.show();
    }

    public static void showMessage(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    public static void showMessage(Context context,@StringRes int msgId){
        showMessage(context,context.getResources().getString(msgId));
    }

    public static String getTimeStamp() {
        return new SimpleDateFormat(Consts.TIMESTAMP_FORMAT, Locale.US).format(new Date());
    }
}
