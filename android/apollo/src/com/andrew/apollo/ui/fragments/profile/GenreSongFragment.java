/*
 * Copyright (C) 2012 Andrew Neal Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.andrew.apollo.ui.fragments.profile;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import com.andrew.apollo.Config;
import com.andrew.apollo.adapters.ProfileSongAdapter;
import com.andrew.apollo.loaders.GenreSongLoader;
import com.andrew.apollo.model.Song;
import com.andrew.apollo.utils.MusicUtils;
import com.frostwire.android.R;

import java.util.List;

/**
 * This class is used to display all of the songs from a particular playlist.
 * 
 * @author Andrew Neal (andrewdneal@gmail.com)
 * @author Angel Leon (gubatron@gmail.com)
 */
public class GenreSongFragment extends ProfileFragment<ProfileSongAdapter, Song> {
    /**
     * Empty constructor as per the {@link Fragment} documentation
     */
    public GenreSongFragment() {
        super(12,0);
    }

    @Override
    ProfileSongAdapter createAdapter() {
        return new ProfileSongAdapter(getActivity(), R.layout.list_item_simple);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position,
            final long id) {
        MusicUtils.playAllFromUserItemClick(getActivity(), getAdapter(), position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Loader<List<Song>> onCreateLoader(final int id, final Bundle args) {
        return new GenreSongLoader(getActivity(), args.getLong(Config.ID));
    }

    /**
     * Restarts the loader.
     */
    public void refresh() {
        // Scroll to the stop of the list before restarting the loader.
        // Otherwise, if the user has scrolled enough to move the header, it
        // becomes misplaced and needs to be reset.
        mListView.setSelection(0);
        // Wait a moment for the preference to change.
        SystemClock.sleep(10);
        getLoaderManager().restartLoader(LOADER, getArguments(), this);
    }
}
