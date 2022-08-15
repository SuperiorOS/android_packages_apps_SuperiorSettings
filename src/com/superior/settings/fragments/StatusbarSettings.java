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

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.superior.support.preferences.SystemSettingMasterSwitchPreference;
import com.superior.support.preferences.SecureSettingSwitchPreference;

public class StatusbarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String NETWORK_TRAFFIC_STATE = "network_traffic_state";
    private static final String SYSTEMUI_PACKAGE = "com.android.systemui";
    private static final String CONFIG_RESOURCE_NAME = "flag_combined_status_bar_signal_icons";
    private static final String COBINED_STATUSBAR_ICONS = "show_combined_status_bar_signal_icons";

    private SystemSettingMasterSwitchPreference mNetTrafficState;
    private SecureSettingSwitchPreference mCombinedIcons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.superior_settings_statusbar);
        final PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mNetTrafficState = (SystemSettingMasterSwitchPreference) findPreference(NETWORK_TRAFFIC_STATE);
        mNetTrafficState.setChecked(Settings.System.getInt(resolver,
                Settings.System.NETWORK_TRAFFIC_STATE, 0) == 1);
        mNetTrafficState.setOnPreferenceChangeListener(this);

        mCombinedIcons = (SecureSettingSwitchPreference)
        findPreference(COBINED_STATUSBAR_ICONS);
        Resources sysUIRes = null;
        boolean def = false;
        int resId = 0;
        try {
            sysUIRes = getActivity().getPackageManager()
                    .getResourcesForApplication(SYSTEMUI_PACKAGE);
        } catch (Exception ignored) {
            // If you don't have system UI you have bigger issues
        }
        if (sysUIRes != null) {
            resId = sysUIRes.getIdentifier(
                    CONFIG_RESOURCE_NAME, "bool", SYSTEMUI_PACKAGE);
            if (resId != 0) def = sysUIRes.getBoolean(resId);
        }
        boolean enabled = Settings.Secure.getInt(resolver,
                COBINED_STATUSBAR_ICONS, def ? 1 : 0) == 1;
        mCombinedIcons.setChecked(enabled);
        mCombinedIcons.setOnPreferenceChangeListener(this);
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

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNetTrafficState) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver, Settings.System.NETWORK_TRAFFIC_STATE, value ? 1 : 0);
            return true;
        } else if (preference == mCombinedIcons) {
            boolean enabled = (boolean) newValue;
            Settings.Secure.putInt(resolver,
                    COBINED_STATUSBAR_ICONS, enabled ? 1 : 0);
            return true;
        }
        return false;
    }
}
