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

package com.anysoftkeyboard.ui.tutorials;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anysoftkeyboard.ui.settings.MainFragment;
import com.anysoftkeyboard.ui.settings.MainSettingsActivity;
import com.menny.android.anysoftkeyboard.R;

import net.evendanan.chauffeur.lib.FragmentChauffeurActivity;
import net.evendanan.chauffeur.lib.experiences.TransitionExperiences;

public class ChangeLogFragment extends Fragment {

    private static final String EXTRA_LOGS_TO_SHOW = "EXTRA_LOGS_TO_SHOW";

    public static final int SHOW_ALL_CHANGELOG = -1;
    public static final int SHOW_LATEST_CHANGELOG = -2;

    public static ChangeLogFragment createFragment(int logToShow) {
        ChangeLogFragment fragment = new ChangeLogFragment();
        Bundle b = createArgs(logToShow);
        fragment.setArguments(b);

        return fragment;
    }

    private static Bundle createArgs(int logToShow) {
        Bundle b = new Bundle();
        b.putInt(EXTRA_LOGS_TO_SHOW, logToShow);
        return b;
    }

    private int mLogToShow = SHOW_ALL_CHANGELOG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogToShow = getArguments().getInt(EXTRA_LOGS_TO_SHOW);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getMainLayout(), container, false);
    }

    protected int getMainLayout() {
        return R.layout.changelog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        ViewGroup logContainer = (ViewGroup) view.findViewById(getLogItemsContainerId());

        for (VersionChangeLogs.VersionChangeLog change : VersionChangeLogs.createChangeLog()) {
            View logHeader = inflater.inflate(R.layout.changelogentry_header, logContainer, false);
            TextView versionName = (TextView) logHeader.findViewById(R.id.changelog_version_title);
            versionName.setPaintFlags(versionName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            setTitleText(versionName, change.versionName);

            logContainer.addView(logHeader);
            for (String changeEntry : change.changes) {
                TextView entryView = (TextView) inflater.inflate(R.layout.changelogentry_item, logContainer, false);
                entryView.setText(getString(R.string.change_log_bullet_point, changeEntry));
                logContainer.addView(entryView);
            }
            //TODO: add milestone url
            if (mLogToShow == SHOW_LATEST_CHANGELOG) break;//in this case, one is enough.
            //adding a divider between version
            logContainer.addView(inflater.inflate(R.layout.transparent_divider, logContainer, false));
        }
    }

    protected void setTitleText(TextView titleView, String versionName) {
        titleView.setText(getString(R.string.change_log_entry_header_template_without_name, versionName));
    }

    protected int getLogItemsContainerId() {
        return R.id.change_logs_container;
    }

    @Override
    public void onStart() {
        super.onStart();
        MainSettingsActivity.setActivityTitle(this, getString(R.string.changelog));
    }

    public static class CardedChangeLogFragment extends ChangeLogFragment {
        public CardedChangeLogFragment() {
            setArguments(createArgs(ChangeLogFragment.SHOW_LATEST_CHANGELOG));
        }

        @Override
        protected int getLogItemsContainerId() {
            return R.id.card_with_read_more;
        }

        @Override
        protected int getMainLayout() {
            return R.layout.card_with_more_container;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            ViewGroup container = (ViewGroup) view.findViewById(R.id.card_with_read_more);
            MainFragment.setupLink(container, R.id.read_more_link, new ClickableSpan() {
                @Override
                public void onClick(View v) {
                    FragmentChauffeurActivity activity = (FragmentChauffeurActivity) getActivity();
                    if (activity == null) return;
                    activity.addFragmentToUi(ChangeLogFragment.createFragment(ChangeLogFragment.SHOW_ALL_CHANGELOG),
                            TransitionExperiences.DEEPER_EXPERIENCE_TRANSITION);
                }
            }, true);
        }

        @Override
        protected void setTitleText(TextView titleView, String versionName) {
            titleView.setText(getString(R.string.change_log_card_version_title_template, versionName));
        }
    }
}
