package com.example.expressblog.Activities;


import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.example.expressblog.R;


public class settings extends PreferenceActivity{
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.set);
        loadSetting();

    }
    private void loadSetting(){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        boolean chk_night=sp.getBoolean("NIGHT",false);
        if(chk_night){
            getListView().setBackgroundColor(Color.parseColor("#222222"));
        }
        else{
            getListView().setBackgroundColor(Color.parseColor("#ffffff"));
        }

        CheckBoxPreference chk_night_instant=(CheckBoxPreference)findPreference("NIGHT");
        chk_night_instant.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference prefs, Object obj) {
                boolean yes=(boolean)obj;
                if(yes){
                    getListView().setBackgroundColor(Color.parseColor("#222222"));
                }
                else{
                    getListView().setBackgroundColor(Color.parseColor("#ffffff"));
                }


                return true;
            }
        });
        ListPreference lp=(ListPreference)findPreference("ORIENTATION");
        String orien=sp.getString("ORIENTATION","false");
        if("1".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
            lp.setSummary(lp.getEntry());
        }
        else if("2".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            lp.setSummary(lp.getEntry());
        }
        else if("3".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            lp.setSummary(lp.getEntry());
        }
        lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference prefs, Object obj) {
                String items=(String)obj;
                if(prefs.getKey().equals("ORIENTATION")){
                    switch (items){
                        case "1":
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
                            break;

                        case "2":
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            break;

                        case "3":
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            break;
                    }
                    ListPreference LPP=(ListPreference)prefs;
                    LPP.setSummary(LPP.getEntries()[LPP.findIndexOfValue(items)]);
                }
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        loadSetting();
        super.onResume();
    }
}
