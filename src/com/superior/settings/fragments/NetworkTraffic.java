/*
 * Copyright (C) 2020 Havoc-OS
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

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.SettingsPreferenceFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.superior.support.preferences.SystemSettingListPreference;
import com.superior.support.preferences.SystemSettingSeekBarPreference;

public class NetworkTraffic extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String NETWORK_TRAFFIC_LOCATION = "network_traffic_location";
    private static final String NETWORK_TRAFFIC_MODE = "network_traffic_mode";
    private static final String NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD = "network_traffic_autohide_threshold";
    private static final String NETWORK_TRAFFIC_REFRESH_INTERVAL = "network_traffic_refresh_interval";


    private SystemSettingListPreference mNetTrafficLocation;
    private SystemSettingListPreference mNetTrafficMode;
    private SystemSettingSeekBarPreference mThreshold;
    private SystemSettingSeekBarPreference mNetTrafficRefreshInterval;


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.network_traffic);
        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

        mNetTrafficLocation = (SystemSettingListPreference) findPreference(NETWORK_TRAFFIC_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_LOCATION, 0, UserHandle.USER_CURRENT);
        mNetTrafficLocation.setOnPreferenceChangeListener(this);
        mNetTrafficLocation.setValue(String.valueOf(location));
        mNetTrafficLocation.setSummary(mNetTrafficLocation.getEntry());

        mNetTrafficMode = (SystemSettingListPreference) findPreference(NETWORK_TRAFFIC_MODE);
        int mode = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_MODE, 0, UserHandle.USER_CURRENT);
        mNetTrafficMode.setOnPreferenceChangeListener(this);
        mNetTrafficMode.setValue(String.valueOf(mode));
        mNetTrafficMode.setSummary(mNetTrafficMode.getEntry());

        mThreshold = (SystemSettingSeekBarPreference) findPreference(NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD);
        int value = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, 1, UserHandle.USER_CURRENT);
        mThreshold.setValue(value);
        mThreshold.setOnPreferenceChangeListener(this);

        mNetTrafficRefreshInterval = (SystemSettingSeekBarPreference) findPreference(NETWORK_TRAFFIC_REFRESH_INTERVAL);
        int refresh = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_REFRESH_INTERVAL, 1, UserHandle.USER_CURRENT);
        mNetTrafficRefreshInterval.setValue(refresh);
        mNetTrafficRefreshInterval.setOnPreferenceChangeListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNetTrafficLocation) {
            int location = Integer.valueOf((String) newValue);
            // 0=sb; 1=expanded sb
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_LOCATION, location, UserHandle.USER_CURRENT);
            int index = mNetTrafficLocation.findIndexOfValue((String) newValue);
            mNetTrafficLocation.setSummary(mNetTrafficLocation.getEntries()[index]);
            mNetTrafficLocation.setValue(String.valueOf(location));
            return true;
        } else if (preference == mNetTrafficMode) {
                int mode = Integer.valueOf((String) newValue);
                // 0=sb; 1=expanded sb
                Settings.System.putIntForUser(getActivity().getContentResolver(),
                        Settings.System.NETWORK_TRAFFIC_MODE, mode, UserHandle.USER_CURRENT);
                int index = mNetTrafficMode.findIndexOfValue((String) newValue);
                mNetTrafficMode.setSummary(mNetTrafficMode.getEntries()[index]);
                mNetTrafficMode.setValue(String.valueOf(mode));
                return true;
        } else if (preference == mThreshold) {
            int val = (Integer) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, val,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mNetTrafficRefreshInterval) {
                int val = (Integer) newValue;
                Settings.System.putIntForUser(getContentResolver(),
                        Settings.System.NETWORK_TRAFFIC_REFRESH_INTERVAL, val,
                        UserHandle.USER_CURRENT);
                return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SUPERIOR;
    }
}
