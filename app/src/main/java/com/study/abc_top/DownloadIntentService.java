package com.study.abc_top;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.study.abc_top.broadcast_receivers.DownloadNotifyBroadcastReceiver;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadIntentService extends IntentService {

    public static final String LOG_TAG = "DownloadIntentService";

    private static final String PREFERENCES = "preferences";
    private static final String LAST_UPDATE_DATE = "last_update_date";
    private static final long UPDATE_FREQ_MILLIS = 1 * 60 * 60 * 1_000; // hours * minutes * seconds * milliseconds
    private static final String DOWNLOAD_NEWS_PERIODICALLY = "download_news_periodically";
    private static final String DOWNLOAD_PERIOD = "download_period";

    private Context context;

    public DownloadIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d(LOG_TAG, "onHandleIntent: " + (intent != null ? intent.getAction() : null));

        context = getApplicationContext();

        if (isUpdateNeeds()) {
            ArrayList<Item> res = new ArrayList<>();
            try {
                URL url = new URL(context.getResources().getString(R.string.url_to_feed));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10_000 /* milliseconds */);
                connection.setConnectTimeout(15_000 /* milliseconds */);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                Parser parser = new Parser();
                res = parser.parse(input);

            } catch (IOException e) {
                Log.e(LOG_TAG, "onHandleIntent: Exception occurred while downloading xml");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.e(LOG_TAG, "onHandleIntent: Exception occurred while parsing");
                e.printStackTrace();
            }

            writeNewsToDB(res);

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    private void writeNewsToDB(ArrayList<Item> newsList) {
        for (Item i : newsList) {
            writeNewsItemToDB(i);
        }

        saveLastUpdateTime();
    }

    private void writeNewsItemToDB(Item item) {
        final String NEWS_ITEM_TITLE = "title";
        final String NEWS_ITEM_PUBDATE = "pub_date";
        final String NEWS_ITEM_LINK = "link";
        final String NEWS_ITEM_DESCRIPTION = "description";
        final String NEWS_ITEM_CATEGORY = "category";
        final String NEWS_ITEM_IMAGE_DEFAULT = "image_default";
        final String NEWS_ITEM_IMAGE_4X3_SMALL = "image_4x3_small";
        final String NEWS_ITEM_IMAGE_4X3_MEDIUM = "image_4x3_medium";
        final String NEWS_ITEM_IMAGE_4X3_LARGE = "image_4x3_large";
        final String NEWS_ITEM_IMAGE_16X9_SMALL = "image_16x9_small";
        final String NEWS_ITEM_IMAGE_16X9_LARGE = "image_16x9_large";

        final String AUTHORITY = "com.study.testtask_10.NewsReader";
        final String NEWS_PATH = "news";
        final Uri NEWS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + NEWS_PATH);

        ContentValues cv = new ContentValues();
        cv.put(NEWS_ITEM_TITLE, item.getTitle());
        cv.put(NEWS_ITEM_PUBDATE, item.getPubDate());
        cv.put(NEWS_ITEM_LINK, item.getLink());
        cv.put(NEWS_ITEM_DESCRIPTION, item.getDescription());
        cv.put(NEWS_ITEM_CATEGORY, item.getCategory());
        cv.put(NEWS_ITEM_IMAGE_DEFAULT, item.getThumbnailLinkArray().get(0));
        cv.put(NEWS_ITEM_IMAGE_4X3_SMALL, item.getThumbnailLinkArray().get(1));
        cv.put(NEWS_ITEM_IMAGE_4X3_MEDIUM, item.getThumbnailLinkArray().get(2));
        cv.put(NEWS_ITEM_IMAGE_16X9_SMALL, item.getThumbnailLinkArray().get(3));
        cv.put(NEWS_ITEM_IMAGE_4X3_LARGE, item.getThumbnailLinkArray().get(4));
        cv.put(NEWS_ITEM_IMAGE_16X9_LARGE, item.getThumbnailLinkArray().get(5));

        Uri newUri = getContentResolver().insert(NEWS_CONTENT_URI, cv);

        Log.d("writeNewsItemToDB: ", "insert, result Uri : " + newUri);
    }

    private boolean isUpdateNeeds() {
        Context context = getApplicationContext();
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        long lastUpdateDate = preferences.getLong(PREFERENCES, 0);
        return System.currentTimeMillis() - lastUpdateDate > UPDATE_FREQ_MILLIS;
    }

    private void saveLastUpdateTime() {
        Context context = getApplicationContext();
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        preferences.edit().putLong(LAST_UPDATE_DATE,System.currentTimeMillis()).apply();
    }

    private boolean downloadNewsPeriodically() {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        boolean downloadPeriodically = preferences.getBoolean(DOWNLOAD_NEWS_PERIODICALLY, true);
        long period = preferences.getLong(DOWNLOAD_PERIOD, 0);
        return downloadPeriodically && period != 0;
    }

    public void startNotifying() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DownloadNotifyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT );
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, 1_000 * 60 * 60, pendingIntent);
    }
}
