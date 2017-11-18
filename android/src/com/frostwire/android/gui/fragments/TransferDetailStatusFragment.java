/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml),
 *            Marcelina Knitter (@marcelinkaaa)
 * Copyright (c) 2011-2017, FrostWire(R). All rights reserved.
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

package com.frostwire.android.gui.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frostwire.android.R;
import com.frostwire.android.gui.views.AbstractTransferDetailFragment;
import com.frostwire.transfers.TransferState;

import static com.frostwire.android.gui.util.UIUtils.getBytesInHuman;

/**
 * @author gubatron
 * @author aldenml
 * @author marcelinkaaa
 */

public class TransferDetailStatusFragment extends AbstractTransferDetailFragment {

    private TextView completedTextView;
    private LinearLayout timeLeftLabelContainer;
    private TextView timeLeftTextView;
    private ImageView timeLeftDividerLine;
    private TextView downloadedTextView;
    private TextView uploadedTextView;
    private TextView shareRatioTextView;
    private TextView peersTextView;
    private TextView seedsTextView;
    private TextView activeTimeTextView;
    private TextView seedingTimeTextView;

    public TransferDetailStatusFragment() {
        super(R.layout.fragment_transfer_detail_status);
    }

    @Override
    protected void initComponents(View v, Bundle savedInstanceState) {
        super.initComponents(v, savedInstanceState);
        completedTextView.setText("");
        timeLeftTextView.setText("");
        downloadedTextView.setText("");
        uploadedTextView.setText("");
        shareRatioTextView.setText("");
        peersTextView.setText("");
        seedsTextView.setText("");
        activeTimeTextView.setText("");
        seedingTimeTextView.setText("");
    }

    @Override
    protected int getTabTitleStringId() {
        return R.string.status;
    }

    @Override
    public void ensureComponentsReferenced(View rootView) {
        completedTextView = findView(rootView, R.id.fragment_transfer_detail_status_completion);
        timeLeftLabelContainer = findView(rootView, R.id.fragment_transfer_detail_status_time_left_container);
        timeLeftTextView = findView(rootView, R.id.fragment_transfer_detail_status_time_left);
        timeLeftDividerLine = findView(rootView, R.id.fragment_transfer_detail_status_time_left_divider_line);
        downloadedTextView = findView(rootView, R.id.fragment_transfer_detail_status_downloaded);
        uploadedTextView = findView(rootView, R.id.fragment_transfer_detail_status_uploaded);
        shareRatioTextView = findView(rootView, R.id.fragment_transfer_detail_status_share_ratio);
        peersTextView = findView(rootView, R.id.fragment_transfer_detail_status_peers);
        seedsTextView = findView(rootView, R.id.fragment_transfer_detail_status_seeds);
        activeTimeTextView = findView(rootView, R.id.fragment_transfer_detail_status_active_time);
        seedingTimeTextView = findView(rootView, R.id.fragment_transfer_detail_status_seeding_time);
    }

    @Override
    protected void updateComponents() {
        if (uiBittorrentDownload == null) {
            return;
        }
        completedTextView.setText(uiBittorrentDownload.getProgress() + "%");

        TransferState state = uiBittorrentDownload.getState();

        int timeLeftVisibility = (state == TransferState.DOWNLOADING) ? View.VISIBLE : View.GONE;
        timeLeftLabelContainer.setVisibility(timeLeftVisibility);
        timeLeftTextView.setVisibility(timeLeftVisibility);
        timeLeftDividerLine.setVisibility(timeLeftVisibility);
        if (timeLeftVisibility == View.VISIBLE) {
            timeLeftTextView.setText(seconds2time(uiBittorrentDownload.getETA()));
        }
        downloadedTextView.setText(getString(R.string.m_of_n_strings,
                getBytesInHuman(uiBittorrentDownload.getDl().getTotalBytesReceived()),
                getBytesInHuman(uiBittorrentDownload.getSize())));
        uploadedTextView.setText(getBytesInHuman(uiBittorrentDownload.getBytesSent()));
        shareRatioTextView.setText(getShareRatio(uiBittorrentDownload));
        peersTextView.setText(getString(R.string.m_of_n_decimals, uiBittorrentDownload.getConnectedPeers(), uiBittorrentDownload.getTotalPeers()));
        seedsTextView.setText(getString(R.string.m_of_n_decimals, uiBittorrentDownload.getConnectedSeeds(), uiBittorrentDownload.getTotalSeeds()));
        if (torrentHandle != null) {
            activeTimeTextView.setText(seconds2time(torrentHandle.status().activeDuration()));
            seedingTimeTextView.setText(seconds2time(torrentHandle.status().seedingDuration()));
        }
    }
}
