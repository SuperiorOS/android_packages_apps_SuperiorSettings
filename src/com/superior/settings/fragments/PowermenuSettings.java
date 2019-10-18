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
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;
import com.superior.settings.preferences.Utils;
import com.superior.settings.R;

public class PowermenuSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "PowerMenu";

    private static final String KEY_POWERMENU_TORCH = "powermenu_torch";
    private static final String KEY_POWERMENU_LOCKSCREEN = "powermenu_lockscreen";
    private static final String KEY_POWERMENU_LS_REBOOT = "powermenu_ls_reboot";
    private static final String KEY_POWERMENU_LS_ADVANCED_REBOOT = "powermenu_ls_advanced_reboot";
    private static final String KEY_POWERMENU_LS_SCREENSHOT = "powermenu_ls_screenshot";
    private static final String KEY_POWERMENU_LS_TORCH = "powermenu_ls_torch";

    private SwitchPreference mPowermenuTorch;
    private SwitchPreference mPowerMenuLockscreen;
    private SwitchPreference mPowerMenuReboot;
    private SwitchPreference mPowerMenuAdvancedReboot;
    private SwitchPreference mPowerMenuScreenshot;
    private SwitchPreference mPowerMenuLSTorch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.superior_settings_powermenu);
        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mPowermenuTorch = (SwitchPreference) findPreference(KEY_POWERMENU_TORCH);
        mPowermenuTorch.setOnPreferenceChangeListener(this);
        if (!Utils.deviceSupportsFlashLight(getActivity())) {
            prefScreen.removePreference(mPowermenuTorch);
            prefScreen.removePreference(mPowerMenuLSTorch);
        } else {
        mPowermenuTorch.setChecked((Settings.System.getInt(resolver,
                Settings.System.POWERMENU_TORCH, 0) == 1));
        }

        mPowerMenuLockscreen = (SwitchPreference) findPreference(KEY_POWERMENU_LOCKSCREEN);
        mPowerMenuLockscreen.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWERMENU_LOCKSCREEN, 1) == 1));
        mPowerMenuLockscreen.setOnPreferenceChangeListener(this);

        mPowerMenuReboot = (SwitchPreference) findPreference(KEY_POWERMENU_LS_REBOOT);
        mPowerMenuReboot.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWERMENU_LS_REBOOT, 1) == 1));
        mPowerMenuReboot.setOnPreferenceChangeListener(this);

        mPowerMenuAdvancedReboot = (SwitchPreference) findPreference(KEY_POWERMENU_LS_ADVANCED_REBOOT);
        mPowerMenuAdvancedReboot.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWERMENU_LS_ADVANCED_REBOOT, 0) == 1));
        mPowerMenuAdvancedReboot.setOnPreferenceChangeListener(this);

        mPowerMenuScreenshot = (SwitchPreference) findPreference(KEY_POWERMENU_LS_SCREENSHOT);
        mPowerMenuScreenshot.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWERMENU_LS_SCREENSHOT, 0) == 1));
        mPowerMenuScreenshot.setOnPreferenceChangeListener(this);

        mPowerMenuLSTorch = (SwitchPreference) findPreference(KEY_POWERMENU_LS_TORCH);
        mPowerMenuLSTorch.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.POWERMENU_LS_TORCH, 0) == 1));
        mPowerMenuLSTorch.setOnPreferenceChangeListener(this);

        updateLockscreen();
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

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mPowermenuTorch) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWERMENU_TORCH, value ? 1 : 0);
            return true;
        } else if (preference == mPowerMenuLockscreen) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWERMENU_LOCKSCREEN, value ? 1 : 0);
            updateLockscreen();
            return true;
        } else if (preference == mPowerMenuReboot) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWERMENU_LS_REBOOT, value ? 1 : 0);
            return true;
        } else if (preference == mPowerMenuAdvancedReboot) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWERMENU_LS_ADVANCED_REBOOT, value ? 1 : 0);
            return true;
        } else if (preference == mPowerMenuScreenshot) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWERMENU_LS_SCREENSHOT, value ? 1 : 0);
            return true;
        } else if (preference == mPowerMenuLSTorch) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWERMENU_LS_TORCH, value ? 1 : 0);
            return true;
        }
        return false;
    }

    private void updateLockscreen() {
        boolean lockscreenOptions = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.POWERMENU_LOCKSCREEN, 1) == 1;

        if (lockscreenOptions) {
            mPowerMenuReboot.setEnabled(true);
            mPowerMenuAdvancedReboot.setEnabled(true);
            mPowerMenuScreenshot.setEnabled(true);
            mPowerMenuLSTorch.setEnabled(true);
        } else {
            mPowerMenuReboot.setEnabled(false);
            mPowerMenuAdvancedReboot.setEnabled(false);
            mPowerMenuScreenshot.setEnabled(false);
            mPowerMenuLSTorch.setEnabled(false);
        }
    }
}
