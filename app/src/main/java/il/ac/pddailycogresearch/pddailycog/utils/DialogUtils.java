package il.ac.pddailycogresearch.pddailycog.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;

/**
 * Created by User on 21/01/2018.
 */

public class DialogUtils {
    private static ProgressDialog mProgressDialog;

    private DialogUtils(){

    }

    private static ProgressDialog createLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static void showLoading(Context context) {
        hideLoading();
        mProgressDialog = createLoadingDialog(context);
    }

    public static void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    public static void createAlertDialog(final Context context, @StringRes final int title, @StringRes final int message,
                                         @StringRes final int positiveButton, @StringRes final int negativeButton,
                                         final IOnAlertDialogResultListener alertDialogResultListener){
        final AlertDialog ad = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialogResultListener.onResult(true);
                            }
                        })
                .setNegativeButton(negativeButton,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialogResultListener.onResult(false);
                            }
                        })
                .create();
        ad.show();
    }

    public static void createAlertDialog(final Context context, @StringRes final int title, @StringRes final int message,
                                         @StringRes final int singleButton,
                                         final IOnAlertDialogResultListener alertDialogResultListener){
        final AlertDialog ad = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(singleButton,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialogResultListener.onResult(true);
                            }
                        })
                .create();
        ad.show();
    }

    public static void createTurnOffAirplaneModeAlertDialog(final Activity activity){
        createAlertDialog(activity, R.string.reminder, R.string.turn_off_airplane_mode_alert_msg,
                R.string.open_settings, android.R.string.cancel,
                new IOnAlertDialogResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        if(result)
                            activity.startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
                        CommonUtils.closeApp(activity);
                    }
                });
    }

    public static void createGoodbyeDialog(final Activity activity) {
        createAlertDialog(activity, R.string.goodbye_title, R.string.goodbye_msg, android.R.string.ok,
                new IOnAlertDialogResultListener() {
                    @Override
                    public void onResult(boolean result) {
                        createTurnOffAirplaneModeAlertDialog(activity);
                    }
                });
    }

}
