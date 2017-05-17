package cs496team1.beerisgood;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


/**
 * Created by Zach on 4/28/2017.
 */


public class Helper
{

    //Formatters
    static DateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    static DateFormat dateFormat = new SimpleDateFormat("MMM dd, YYYY - hh:mm a", Locale.ENGLISH);
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String convertDateTime(Context c, String input){
        try {
            Date d = simpleDateFormat.parse(input);

            return dateFormat.format(d);
        } catch (ParseException ex){
            return c.getString(R.string.error_datetime_invalid);
        }
    }


    // Weather icons
    static HashMap<String, String> weather_icons = new HashMap<>();

    static {
        // Clear sky
        weather_icons.put("01", "ic_weather_sunny");

        // Small clouds
        weather_icons.put("02", "ic_weather_partly_cloudy");
        weather_icons.put("03", "ic_weather_partly_cloudy");

        // Clouds
        weather_icons.put("04", "ic_weather_cloudy");

        // Rain
        weather_icons.put("09", "ic_weather_pouring");
        weather_icons.put("10", "ic_weather_rainy");

        // Thunderstorm
        weather_icons.put("11", "ic_weather_lightning");

        // Snow
        weather_icons.put("13", "ic_weather_snowy");
    }

    //Universal print
    public static void Print(Context c, String msg){ if (c!=null && isDebugMode(c)) { Toast.makeText(c, msg, Toast.LENGTH_SHORT).show(); } }
    public static void PrintLong(Context c, String msg){ if (c!=null && isDebugMode(c)) { Toast.makeText(c, msg, Toast.LENGTH_LONG).show(); } }
    public static void PrintUser(Context c, String msg){ if (c!=null){ Toast.makeText(c, msg, Toast.LENGTH_SHORT).show(); } }
    public static void PrintUserLong(Context c, String msg){ if (c!=null){ Toast.makeText(c, msg, Toast.LENGTH_LONG).show(); } }

    public static void Log(Context c, String cat, String msg){ if (c!=null && isDebugMode(c)) { Log.e(cat, msg); } }


    //Dialog fragment manager
    public static void OpenDialogFragment(Activity caller, DialogFragment fragment, boolean openAsDialogFragment){
        if (fragment != null) {
            if (openAsDialogFragment) { // The device is using a large layout, so show the fragment as a dialog
                FragmentTransaction ft = caller.getFragmentManager().beginTransaction();
                Fragment prev = caller.getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                fragment.show(caller.getFragmentManager(), "dialog");
                //FragmentTransaction ft = fragmentManager.beginTransaction();
                //ft.add(fragment, null);
                //ft.commitAllowingStateLoss();
            }
            else { // The device is smaller, so show the fragment fullscreen
                FragmentTransaction transaction = caller.getFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, fragment).addToBackStack(null).commit();
            }
        }
    }


    //Hide Soft Keyboard
    public static void hideSoftKeyboard(Activity act, View v)
    {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
    //Show Soft Keyboard
    public static void showSoftKeyboard(Activity act, View v)
    {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    //Color parsing
    public static int ColorFromString(String str){
        //Hash the string
        str = String.format("%X", str.hashCode());
        //Minimum string length = 6
        while (str.length() < 6) { str = '0' + str; }

        //Convert Hex string into int
        int coli = (int)Long.parseLong(str, 16);
        int r = (coli >> 16) & 0xFF;
        int g = (coli >> 8) & 0xFF;
        int b = (coli) & 0xFF;

        return Color.argb(255,r,g,b);
    }


    //Permissions
    public static boolean isPermissionGranted(Context context, String permission) {
        return Build.VERSION.SDK_INT < 23 || PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    //DEBUG
    public static boolean isDebugMode(Context ac){
        return (0 != (ac.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

}
