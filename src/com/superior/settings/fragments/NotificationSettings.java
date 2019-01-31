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
import com.superior.settings.preferences.Utils;
import android.os.UserHandle;
import android.provider.Settings;

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import com.superior.settings.R;

public class NotificationSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";
    private static final String FLASHLIGHT_ON_CALL = "flashlight_on_call";

    private ListPreference mFlashlightOnCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.superior_settings_notifications);
        PreferenceScreen prefScreen = getPreferenceScreen();
        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!Utils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(incallVibCategory);
        }
	mFlashlightOnCall = (ListPreference) findPreference(FLASHLIGHT_ON_CALL);
	Preference FlashOnCall = findPreference("flashlight_on_call");        
	int flashlightValue = Settings.System.getInt(getContentResolver(),
		Settings.System.FLASHLIGHT_ON_CALL, 0);
	mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
	mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
	mFlashlightOnCall.setOnPreferenceChangeListener(this);	
	if (!Utils.deviceSupportsFlashLight(getActivity())) {
	    prefScreen.removePreference(FlashOnCall);
	}
	ContentResolver resolver = getActivity().getContentResolver();
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
	ContentResolver resolver = getActivity().getContentResolver();

	    if (preference == mFlashlightOnCall) {
	        int flashlightValue = Integer.parseInt(((String) newValue).toString());
	        Settings.System.putInt(resolver,
			Settings.System.FLASHLIGHT_ON_CALL, flashlightValue);
		mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
		mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
		return true;
	   }

        return false;
    }
}
