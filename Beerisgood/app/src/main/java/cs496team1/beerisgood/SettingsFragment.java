package cs496team1.beerisgood;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by RohanMac on 6/3/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,String rootkey){
        addPreferencesFromResource(R.xml.prefs);
    }
}
