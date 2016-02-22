package com.ls.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import com.ls.directoryselectordemo.R;

public class SettingsActivity extends PreferenceActivity {

    public static class MyPreferenceFragment extends PreferenceFragment {
        private AppSettings settings;
        private final OnSharedPreferenceChangeListener sharedPrefsChangeListener;

        /* renamed from: com.ls.activities.SettingsActivity.MyPreferenceFragment.1 */
        class C01041 implements OnPreferenceChangeListener {
            C01041() {
            }

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                return true;
            }
        }

        /* renamed from: com.ls.activities.SettingsActivity.MyPreferenceFragment.2 */
        class C01052 implements OnSharedPreferenceChangeListener {
            C01052() {
            }

            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                MyPreferenceFragment.this.settings.load();
            }
        }

        public MyPreferenceFragment() {
            this.sharedPrefsChangeListener = new C01052();
        }

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            findPreference("store_path").setOnPreferenceChangeListener(new C01041());
        }

        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            this.settings = AppSettings.getSettings(getActivity());
            findPreference("store_path").setSummary(this.settings.getStorePath());
        }

        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this.sharedPrefsChangeListener);
        }

        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this.sharedPrefsChangeListener);
        }
    }

    public static void startThisActivity(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    public static void startThisActivityForResult(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, SettingsActivity.class), requestCode);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(16908290, new MyPreferenceFragment()).commit();
        }
        initActionBar();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != 16908332) {
            return false;
        }
        finish();
        return true;
    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
