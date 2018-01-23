package il.ac.pddailycogresearch.pddailycog.utils;

import android.app.Activity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import il.ac.pddailycogresearch.pddailycog.R;
import il.ac.pddailycogresearch.pddailycog.interfaces.IOnAlertDialogResultListener;

/**
 * Created by User on 17/01/2018.
 */

public final class CommonUtils {

    private CommonUtils(){

    }

    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
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

    public static void closeApp(Activity activity) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN)
            activity.finishAffinity();//ask Tal
        else
            activity.finish();//TODO
    }

    public static boolean isAirplaneMode(Context context) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            return Settings.System.getInt(context.getContentResolver(),Settings.Global.AIRPLANE_MODE_ON,0)==1;
        } else {
            return Settings.System.getInt(context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON,0)==1;

        }
    }
}
