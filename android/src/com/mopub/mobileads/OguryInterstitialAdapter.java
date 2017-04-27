/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
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

package com.mopub.mobileads;

import android.content.Context;

import com.frostwire.android.core.ConfigurationManager;
import com.frostwire.android.core.Constants;
import com.frostwire.util.Logger;

import java.util.Map;
import java.util.Random;

import io.presage.IADHandler;
import io.presage.Presage;

/**
 * @author aldenml
 * @author gubatron
 * @author marcelinkaaa
 *         Created on 4/10/2017 - ogury 2.0.5
 *         Updated 04/26/2017 - ogury 2.1.1
 */
public final class OguryInterstitialAdapter extends CustomEventInterstitial {

    private static final Logger LOG = Logger.getLogger(OguryInterstitialAdapter.class);

    private static boolean OGURY_STARTED = false;
    private static boolean OGURY_ENABLED = false;

    private CustomEventInterstitialListener interstitialListener;

    public OguryInterstitialAdapter() {
        // this class should be created only once by the mopub framework
        // both OGURY_STARTED and OGURY_ENABLED are static to minimize the
        // risks in case of getting in a multithreaded environment
        OGURY_ENABLED = diceRoll();
    }

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> map, Map<String, String> map1) {
        if (!OGURY_ENABLED) {
            LOG.info("OguryInterstitialAdapter.loadInterstitial() aborted, ogury disabled.");
            return;
        }
        if (customEventInterstitialListener == null) {
            LOG.error("OguryInterstitialAdapter.loadInterstitial() aborted. CustomEventInterstitialListener was null.");
            return;
        }
        startOgury(context); // starts only once
        interstitialListener = customEventInterstitialListener;
        presage().load(new OguryIADHandler(interstitialListener));
    }

    @Override
    protected void showInterstitial() {
        if (OGURY_ENABLED && presage().canShow()) {
            presage().adToServe(new OguryIADHandler(interstitialListener));
            LOG.info("Showing Ogury-Mopub interstitial");
        } else {
            LOG.info("Ogury-Mopub show interstitial failed, ad not loaded yet");
        }
    }

    @Override
    protected void onInvalidate() {
        interstitialListener = null;
    }

    private static Presage presage() {
        return Presage.getInstance();
    }

    private static boolean diceRoll() {
        int oguryThreshold = ConfigurationManager.instance().getInt(Constants.PREF_KEY_GUI_OGURY_THRESHOLD);
        int diceRoll = new Random().nextInt(100) + 1;
        boolean enabled = diceRoll < oguryThreshold;
        LOG.info("OGURY_ENABLED -> " + enabled + " (dice roll: " + diceRoll + " < threshold: " + oguryThreshold + ")");
        return enabled;
    }

    private static void startOgury(Context context) {
        if (OGURY_STARTED || !OGURY_ENABLED) {
            return;
        }
        try {
            OGURY_STARTED = true;
            // presage internally picks the application context
            presage().setContext(context);
            presage().start();
            LOG.info("Ogury started from Mopub-Ogury adapter");
        } catch (Throwable e) {
            OGURY_STARTED = false;
            LOG.error("Could not start Ogury from Mopub-Ogury adapter", e);
        }
    }

    private static final class OguryIADHandler implements IADHandler {

        private final CustomEventInterstitialListener mopubListener;

        private OguryIADHandler(CustomEventInterstitialListener mopubListener) {
            this.mopubListener = mopubListener;
        }

        @Override
        public void onAdAvailable() {
        }

        @Override
        public void onAdNotAvailable() {
            mopubListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
        }

        @Override
        public void onAdLoaded() {
            mopubListener.onInterstitialLoaded();
        }

        @Override
        public void onAdClosed() {
            mopubListener.onInterstitialDismissed();
        }

        @Override
        public void onAdError(int code) {
            mopubListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
        }

        @Override
        public void onAdDisplayed() {
            mopubListener.onInterstitialShown();
        }
    }
}
