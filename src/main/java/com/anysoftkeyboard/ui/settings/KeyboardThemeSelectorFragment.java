/*
 * Copyright (c) 2013 Menny Even-Danan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anysoftkeyboard.ui.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.SharedPreferencesCompat;

import com.anysoftkeyboard.keyboards.AnyKeyboard;
import com.anysoftkeyboard.keyboards.KeyboardFactory;
import com.anysoftkeyboard.keyboards.views.DemoAnyKeyboardView;
import com.anysoftkeyboard.theme.KeyboardTheme;
import com.anysoftkeyboard.theme.KeyboardThemeFactory;
import com.sevencupsoftea.ears.R;

import net.evendanan.chauffeur.lib.FragmentChauffeurActivity;
import net.evendanan.chauffeur.lib.experiences.TransitionExperiences;

import java.util.Collections;
import java.util.List;

//import com.menny.android.anysoftkeyboard.R;

public class KeyboardThemeSelectorFragment extends AbstractKeyboardAddOnsBrowserFragment<KeyboardTheme> {

    public KeyboardThemeSelectorFragment() {
        super("KeyboardThemeSelectorFragment", R.string.keyboard_theme_list_title, true, false, true);
    }

    @Override
    protected void onTweaksOptionSelected() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof FragmentChauffeurActivity) {
            FragmentChauffeurActivity chauffeurActivity = (FragmentChauffeurActivity) activity;
            chauffeurActivity.addFragmentToUi(new KeyboardThemeTweaksFragment(), TransitionExperiences.DEEPER_EXPERIENCE_TRANSITION);
        }
    }

    @NonNull
    @Override
    protected List<KeyboardTheme> getEnabledAddOns() {
        return Collections.singletonList(KeyboardThemeFactory.getCurrentKeyboardTheme(getContext()));
    }

    @NonNull
    @Override
    protected List<KeyboardTheme> getAllAvailableAddOns() {
        return KeyboardThemeFactory.getAllAvailableThemes(getContext());
    }

    @Override
    protected int getMarketSearchTitle() {
        return R.string.search_market_for_keyboard_addons;
    }

    @Nullable
    @Override
    protected String getMarketSearchKeyword() {
        return "theme";
    }

    @Override
    protected void onEnabledAddOnsChanged(@NonNull List<String> newEnabledAddOns) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString(getString(R.string.settings_key_keyboard_theme_key), newEnabledAddOns.get(0));
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    @Override
    protected void applyAddOnToDemoKeyboardView(@NonNull KeyboardTheme addOn, @NonNull DemoAnyKeyboardView demoKeyboardView) {
        demoKeyboardView.resetKeyboardTheme(addOn);
        AnyKeyboard defaultKeyboard = KeyboardFactory.getEnabledKeyboards(getContext()).get(0).createKeyboard(getContext(), getResources().getInteger(R.integer.keyboard_mode_normal));
        defaultKeyboard.loadKeyboard(demoKeyboardView.getThemedKeyboardDimens());
        demoKeyboardView.setKeyboard(defaultKeyboard);
    }
}