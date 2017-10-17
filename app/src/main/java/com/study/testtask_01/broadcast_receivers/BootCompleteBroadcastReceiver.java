package com.study.testtask_01.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.study.testtask_01.DownloadIntentService;

/**
 * Class for receiving notifications from system about boot completation.
 *
 * Depending from application preferences starts {@link DownloadIntentService} for downloading news
 * immediately after boot.
 */
public class BootCompleteBroadcastReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "BootCompleteReceiver";

    private static final String PREFERENCES = "preferences";
    private static final String DOWNLOAD_NEWS_ON_BOOT = "download_news_on_boot";

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive: " + intent.getAction());

        this.context = context;

        if (downloadNewsOnBoot()) {
            Intent i = new Intent(context, DownloadIntentService.class);
            context.startService(i);
        }
    }

    private boolean downloadNewsOnBoot() {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(DOWNLOAD_NEWS_ON_BOOT, false);
    }

}
