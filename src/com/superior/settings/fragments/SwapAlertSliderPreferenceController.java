/*
 * Copyright (C) 2018 CypherOS
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
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v14.preference.SwitchPreference;
import android.text.TextUtils;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

import static android.provider.Settings.System.ALERT_SLIDER_ORDER;

public class SwapAlertSliderPreferenceController extends AbstractPreferenceController implements
        PreferenceControllerMixin, Preference.OnPreferenceChangeListener {

    private String mKey;

    public SwapAlertSliderPreferenceController(Context context, String key) {
        super(context);
        mKey = key;
    }

    @Override
    public boolean isAvailable() {
        return mContext.getResources().getBoolean(com.android.internal.R.bool.config_hasAlertSlider)
                && !TextUtils.isEmpty(mContext.getResources().getString(com.android.internal.R.string.alert_slider_state_path))
                && !TextUtils.isEmpty(mContext.getResources().getString(com.android.internal.R.string.alert_slider_uevent_match_path));
    }

    @Override
    public String getPreferenceKey() {
        return mKey;
    }

    @Override
    public void updateState(Preference preference) {
        int setting = Settings.System.getInt(mContext.getContentResolver(), ALERT_SLIDER_ORDER, 0);
        ((SwitchPreference) preference).setChecked(setting != 0);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean enabled = (Boolean) newValue;
        Settings.System.putInt(mContext.getContentResolver(), ALERT_SLIDER_ORDER,
                enabled ? 1 : 0);
        return true;
    }
}
