package cz.tul.android.tracker.app;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        List<Preference> prefs=new LinkedList<Preference>();
        prefs.add(findPreference("edittext_preference_acc"));
        prefs.add(findPreference("edittext_preference_time"));
        for(Preference pref:prefs){
            if(pref!=null){
                pref.setOnPreferenceChangeListener(
                        new Preference.OnPreferenceChangeListener() {

                            @Override
                            public boolean onPreferenceChange(Preference preference, Object newValue) {

                                return isLong((String) newValue);
                            }

                        });
            }
        }

    }
    public boolean isLong(String str){
        try{
           Long.parseLong(str);
        }catch (NumberFormatException e){
            return false;
        }
        return true;
    }

}