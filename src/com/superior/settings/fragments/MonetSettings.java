/*
 * Copyright (C) 2021 Yet Another AOSP Project
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
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.search.SearchIndexable;

import com.superior.support.colorpicker.ColorPickerPreference;
import com.superior.support.preferences.CustomSeekBarPreference;

import java.lang.Math;

@SearchIndexable
public class MonetSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String WALLPAPER_KEY = "monet_engine_use_wallpaper_color";
    private static final String COLOR_KEY = "monet_engine_color_override";
    private static final String CHROMA_KEY = "monet_engine_chroma_factor";

    SwitchPreference mUseWall;
    ColorPickerPreference mColorOvr;
    CustomSeekBarPreference mChroma;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        final ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.monet_engine_settings);

        mUseWall = findPreference(WALLPAPER_KEY);
        mColorOvr = findPreference(COLOR_KEY);
        String color = Settings.Secure.getString(resolver, COLOR_KEY);
        boolean useWall = color == null || color.isEmpty();
        mUseWall.setChecked(useWall);
        mColorOvr.setEnabled(!useWall);
        if (!useWall) mColorOvr.setNewPreviewColor(
                ColorPickerPreference.convertToColorInt(color));
        mUseWall.setOnPreferenceChangeListener(this);
        mColorOvr.setOnPreferenceChangeListener(this);

        mChroma = findPreference(CHROMA_KEY);
        float chroma = Settings.Secure.getFloat(resolver, CHROMA_KEY, 1) * 100;
        mChroma.setValue(Math.round(chroma));
        mChroma.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mUseWall) {
            boolean value = (Boolean) newValue;
            mColorOvr.setEnabled(!value);
            if (value) Settings.Secure.putString(resolver, COLOR_KEY, "");
            return true;
        } else if (preference == mColorOvr) {
            int value = (Integer) newValue;
            Settings.Secure.putString(resolver, COLOR_KEY,
                    ColorPickerPreference.convertToRGB(value));
            return true;
        } else if (preference == mChroma) {
            int value = (Integer) newValue;
            Settings.Secure.putFloat(resolver, CHROMA_KEY, value / 100f);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SUPERIOR;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.monet_engine_settings);
}

