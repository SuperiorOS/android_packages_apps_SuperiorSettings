/*
 * Copyright (C) 2018 The Superior OS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.superior.settings.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Handler;
import android.os.UserHandle;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;

import com.android.settingslib.widget.FooterPreference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.superior.SuperiorUtils;

import com.superior.settings.R;

public class RecentsSettings extends SettingsPreferenceFragment implements
       Preference.OnPreferenceChangeListener {

    private ListPreference mRecentsComponentType;
    private SwitchPreference mSlimToggle;
    private Preference mSlimSettings;
    private ListPreference mRecentsClearAllLocation;
    private SwitchPreference mRecentsClearAll;
    private PreferenceCategory mStockCat;
    private PreferenceCategory mSlimCat;
    private static final String KEY_CATEGORY_STOCK = "stock_recents";
    private static final String KEY_CATEGORY_SLIM = "slim_recent";
    private static final String RECENTS_COMPONENT_TYPE = "recents_component";
    private static final String PREF_SLIM_RECENTS_SETTINGS = "slim_recents_settings";
    private static final String PREF_SLIM_RECENTS = "use_slim_recents";
    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.superior_settings_recents);
        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
		
        // recents component type
        mRecentsComponentType = (ListPreference) findPreference(RECENTS_COMPONENT_TYPE);
        int type = Settings.System.getInt(resolver,
                Settings.System.RECENTS_COMPONENT, 0);
        mRecentsComponentType.setValue(String.valueOf(type));
        mRecentsComponentType.setSummary(mRecentsComponentType.getEntry());
        mRecentsComponentType.setOnPreferenceChangeListener(this);
		
        // clear all recents
        mRecentsClearAllLocation = (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3, UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry());
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);

        // Slim Recents
        mSlimSettings = (Preference) findPreference(PREF_SLIM_RECENTS_SETTINGS);
        mSlimToggle = (SwitchPreference) findPreference(PREF_SLIM_RECENTS);
        mSlimToggle.setOnPreferenceChangeListener(this);

        mStockCat = (PreferenceCategory) findPreference(KEY_CATEGORY_STOCK);
        mSlimCat = (PreferenceCategory) findPreference(KEY_CATEGORY_SLIM);
        updateRecentsState(type); 
        updateRecentsPreferences();
    }

    private void updateRecentsPreferences() {
        boolean slimEnabled = Settings.System.getIntForUser(
                getActivity().getContentResolver(), Settings.System.USE_SLIM_RECENTS, 0,
                UserHandle.USER_CURRENT) == 1;
        // Either Stock or Slim Recents can be active at a time
        mRecentsComponentType.setEnabled(!slimEnabled);
        mSlimToggle.setChecked(slimEnabled);
    }

    public void updateRecentsState(int type) {
        if (type == 0) {
           mStockCat.setEnabled(false);
           mSlimCat.setEnabled(true);
        } else {
           mStockCat.setEnabled(true);
           mSlimCat.setEnabled(true);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SUPERIOR;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRecentsComponentType) {
            int type = Integer.valueOf((String) newValue);
            int index = mRecentsComponentType.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_COMPONENT, type);
            mRecentsComponentType.setSummary(mRecentsComponentType.getEntries()[index]);
            updateRecentsState(type);
            if (type == 1) { // Disable swipe up gesture, if oreo type selected
               Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED, 0);
            }
            SuperiorUtils.showSystemUiRestartDialog(getContext());
        return true;
        } else if (preference == mSlimToggle) {
            boolean value = (Boolean) newValue;
            int type = Settings.System.getInt(
                getActivity().getContentResolver(), Settings.System.RECENTS_COMPONENT, 0);
            if (value && (type == 0)) { // change recents type to oreo when we are about to switch to slimrecents
               Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_COMPONENT, 1);
            SuperiorUtils.showSystemUiRestartDialog(getContext());
            }
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.USE_SLIM_RECENTS, value ? 1 : 0,
                    UserHandle.USER_CURRENT);
            updateRecentsPreferences();
            return true;
        } else if (preference == mRecentsClearAllLocation) {
            int location = Integer.valueOf((String) newValue);
            int index = mRecentsClearAllLocation.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location, UserHandle.USER_CURRENT);
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]);
        return true;
        }
        return false;
    }
}
