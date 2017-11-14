package com.study.abc_top.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.study.abc_top.DownloadIntentService;

public class DownloadNotifyBroadcastReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "DownloadNotifyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive: " + intent.getAction());

        Intent i = new Intent(context, DownloadIntentService.class);
        context.startService(i);
    }
}
