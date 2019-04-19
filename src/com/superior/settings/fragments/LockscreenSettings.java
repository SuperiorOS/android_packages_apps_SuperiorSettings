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

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;

import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.superior.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.superior.settings.preferences.Utils;
import android.hardware.fingerprint.FingerprintManager;
import com.superior.settings.preferences.SystemSettingSwitchPreference;
import com.superior.settings.preferences.SystemSettingListPreference;
import com.superior.settings.preferences.CustomSeekBarPreference;

public class LockscreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_FACE_AUTO_UNLOCK = "face_auto_unlock";
    private static final String KEY_FACE_UNLOCK_PACKAGE = "com.android.facelock";
    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String LOCK_CLOCK_FONTS = "lock_clock_fonts";
    private static final String LOCK_DATE_FONTS = "lock_date_fonts";
    private static final String FP_UNLOCK_KEYSTORE = "fp_unlock_keystore";
    private static final String FP_CAT = "fp_category";
    private static final String CLOCK_FONT_SIZE = "lockclock_font_size";
    private static final String DATE_FONT_SIZE = "lockdate_font_size";
    private static final String LOCKSCREEN_CLOCK_SELECTION  = "lockscreen_clock_selection";

    private FingerprintManager mFingerprintManager;
    private SwitchPreference mFingerprintVib;
    private SystemSettingSwitchPreference mFpKeystore;
    private SwitchPreference mFaceUnlock;
    ListPreference mLockClockFonts;
    ListPreference mLockDateFonts;
    private CustomSeekBarPreference mClockFontSize;
    private CustomSeekBarPreference mDateFontSize;
    SystemSettingListPreference mLockClockStyle;
    private ListPreference clockAlign, clockSelect;
    private PreferenceScreen preferenceScreen;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.superior_settings_lockscreen);
		
       ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        Resources resources = getResources();
		
       mFaceUnlock = (SwitchPreference) findPreference(KEY_FACE_AUTO_UNLOCK);
        if (!Utils.isPackageInstalled(getActivity(), KEY_FACE_UNLOCK_PACKAGE)){
            prefScreen.removePreference(mFaceUnlock);
        } else {
            mFaceUnlock.setChecked((Settings.Secure.getInt(getContext().getContentResolver(),
                    Settings.Secure.FACE_AUTO_UNLOCK, 0) == 1));
            mFaceUnlock.setOnPreferenceChangeListener(this);
       }

        PreferenceCategory fingerprintCategory = (PreferenceCategory) findPreference(FP_CAT);

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (SwitchPreference) findPreference(FINGERPRINT_VIB);
        mFpKeystore = (SystemSettingSwitchPreference) findPreference(FP_UNLOCK_KEYSTORE);
        if (mFingerprintManager != null && mFingerprintManager.isHardwareDetected()){
        mFingerprintVib.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.FINGERPRINT_SUCCESS_VIB, 1) == 1));
        mFingerprintVib.setOnPreferenceChangeListener(this);
        } else {
        fingerprintCategory.removePreference(mFingerprintVib);
        fingerprintCategory.removePreference(mFpKeystore);
        }

        // Lockscren Clock Fonts
        mLockClockFonts = (ListPreference) findPreference(LOCK_CLOCK_FONTS);
        mLockClockFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCK_CLOCK_FONTS, 0)));
        mLockClockFonts.setSummary(mLockClockFonts.getEntry());
        mLockClockFonts.setOnPreferenceChangeListener(this);
		
        mLockClockStyle = (SystemSettingListPreference) findPreference(LOCKSCREEN_CLOCK_SELECTION);
        mLockClockStyle.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCKSCREEN_CLOCK_SELECTION, 0)));
        mLockClockStyle.setOnPreferenceChangeListener(this);
		
        preferenceScreen = getPreferenceScreen();
        clockSelect = (ListPreference) findPreference("lockscreen_clock_selection");
        clockAlign = (ListPreference) findPreference("lockscreen_text_clock_align");
        clockSelect.setOnPreferenceChangeListener(this);
        String value = clockSelect.getValue();
        if (value.equals("17")) {
            preferenceScreen.addPreference(clockAlign);
        } else {
            preferenceScreen.removePreference(clockAlign);
        }
		
        // Lockscren Date Fonts
        mLockDateFonts = (ListPreference) findPreference(LOCK_DATE_FONTS);
        mLockDateFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCK_DATE_FONTS, 0)));
        mLockDateFonts.setSummary(mLockDateFonts.getEntry());
        mLockDateFonts.setOnPreferenceChangeListener(this);
		
        // Lock Clock Size
        mClockFontSize = (CustomSeekBarPreference) findPreference(CLOCK_FONT_SIZE);
        mClockFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKCLOCK_FONT_SIZE, 64));
        mClockFontSize.setOnPreferenceChangeListener(this);

        // Lock Date Size
        mDateFontSize = (CustomSeekBarPreference) findPreference(DATE_FONT_SIZE);
        mDateFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKDATE_FONT_SIZE,16));
        mDateFontSize.setOnPreferenceChangeListener(this);
    }
	

   @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SUPERIOR;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
		
        if (preference == mFaceUnlock) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.FACE_AUTO_UNLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mFingerprintVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FINGERPRINT_SUCCESS_VIB, value ? 1 : 0);
            return true;
        } else if (preference == mLockClockFonts) {
            Settings.System.putInt(getContentResolver(), Settings.System.LOCK_CLOCK_FONTS,
                    Integer.valueOf((String) newValue));
            mLockClockFonts.setValue(String.valueOf(newValue));
            mLockClockFonts.setSummary(mLockClockFonts.getEntry());
            return true;
		} else if (preference == mLockDateFonts) {
            Settings.System.putInt(getContentResolver(), Settings.System.LOCK_DATE_FONTS,
                    Integer.valueOf((String) newValue));
            mLockDateFonts.setValue(String.valueOf(newValue));
            mLockDateFonts.setSummary(mLockDateFonts.getEntry());
            return true;
		} else if (preference == mClockFontSize) {
			int top = (Integer) newValue;
			Settings.System.putInt(getContentResolver(),
			Settings.System.LOCKCLOCK_FONT_SIZE, top*1);
			return true;
		} else if (preference == mDateFontSize) {
			int top = (Integer) newValue;
			Settings.System.putInt(getContentResolver(),
			Settings.System.LOCKDATE_FONT_SIZE, top*1);
			return true;
        } else if (preference == mLockClockStyle) {
            int val = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_CLOCK_SELECTION, val);
            if (val == 17) {
                Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_INFO, 0);
            } else {
                Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_INFO, 1);
            }
            return true;
        } else if (preference == mLockClockStyle) {
            int val = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_CLOCK_SELECTION, val);
            if (val == 17) {
                preferenceScreen.addPreference(clockAlign);
            } else {
                preferenceScreen.removePreference(clockAlign);
            }
        return true;
        }
        return false;
    }
 }