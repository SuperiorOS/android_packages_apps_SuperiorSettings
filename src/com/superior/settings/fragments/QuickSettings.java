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

import android.app.Activity;		
import android.app.AlertDialog;		
import android.app.Dialog;		
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.SystemProperties;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
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

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import com.superior.settings.R;

import java.util.Arrays;
import java.util.HashSet;

import com.superior.settings.preferences.CustomSeekBarPreference;
import com.superior.settings.preferences.Utils;

public class QuickSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String OMNI_QS_PANEL_BG_ALPHA = "qs_panel_bg_alpha";
    private static final String QS_HEADER_STYLE = "qs_header_style";
    private static final String QS_TILE_STYLE = "qs_tile_style";

    private CustomSeekBarPreference mQsPanelAlpha;
    private ListPreference mQsHeaderStyle;
    private ListPreference mQsTileStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.superior_settings_quicksettings);
        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mQsPanelAlpha = (CustomSeekBarPreference) findPreference(OMNI_QS_PANEL_BG_ALPHA);
        int qsPanelAlpha = Settings.System.getIntForUser(resolver,
                Settings.System.OMNI_QS_PANEL_BG_ALPHA, 255, UserHandle.USER_CURRENT);
        mQsPanelAlpha.setValue(qsPanelAlpha);
        mQsPanelAlpha.setOnPreferenceChangeListener(this);

        mQsHeaderStyle = (ListPreference) findPreference(QS_HEADER_STYLE);
       int qsHeaderStyle = Settings.System.getInt(resolver,
               Settings.System.QS_HEADER_STYLE, 0);
       int newIndex = mQsHeaderStyle.findIndexOfValue(String.valueOf(qsHeaderStyle));
       mQsHeaderStyle.setValueIndex(newIndex >= 0 ? newIndex : 0);
       mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntry());
       mQsHeaderStyle.setOnPreferenceChangeListener(this);

       mQsTileStyle = (ListPreference) findPreference(QS_TILE_STYLE);
       int qsTileStyle = Settings.System.getIntForUser(resolver,
               Settings.System.QS_TILE_STYLE, 0,
	       UserHandle.USER_CURRENT);
       int valueIndex = mQsTileStyle.findIndexOfValue(String.valueOf(qsTileStyle));
       mQsTileStyle.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
       mQsTileStyle.setSummary(mQsTileStyle.getEntry());
       mQsTileStyle.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mQsPanelAlpha) {
            int bgAlpha = (Integer) newValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.OMNI_QS_PANEL_BG_ALPHA, bgAlpha,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mQsHeaderStyle) {
            String value = (String) newValue;
            Settings.System.putInt(resolver, Settings.System.QS_HEADER_STYLE,
                   Integer.valueOf(value));
            int newIndex = mQsHeaderStyle.findIndexOfValue(value);
            mQsHeaderStyle.setSummary(mQsHeaderStyle.getEntries()[newIndex]);
            return true;
        } else if (preference == mQsTileStyle) {
            int qsTileStyleValue = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(resolver, Settings.System.QS_TILE_STYLE,
                    qsTileStyleValue, UserHandle.USER_CURRENT);
            mQsTileStyle.setSummary(mQsTileStyle.getEntries()[qsTileStyleValue]);
        }
        return true;
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

}
