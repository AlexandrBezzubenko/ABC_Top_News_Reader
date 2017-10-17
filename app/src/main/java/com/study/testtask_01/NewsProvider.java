package com.study.testtask_01;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

public class NewsProvider extends ContentProvider {

    private static final String LOG_TAG = "News Provider";

    private static final String DATA_BASE = "news_db";

    private static final int DATA_BASE_VERSION = 1;
    private static final String NEWS_TABLE = "news";

    static final String NEWS_ITEM_ID = "_id";
    static final String NEWS_ITEM_TITLE = "title";
    static final String NEWS_ITEM_PUBDATE = "pub_date";
    static final String NEWS_ITEM_LINK = "link";
    static final String NEWS_ITEM_DESCRIPTION = "description";
    static final String NEWS_ITEM_CATEGORY = "category";
    static final String NEWS_ITEM_IMAGE_DEFAULT = "image_default";
    static final String NEWS_ITEM_IMAGE_4X3_SMALL = "image_4x3_small";
    static final String NEWS_ITEM_IMAGE_4X3_MEDIUM = "image_4x3_medium";
    static final String NEWS_ITEM_IMAGE_4X3_LARGE = "image_4x3_large";
    static final String NEWS_ITEM_IMAGE_16X9_SMALL = "image_16x9_small";
    static final String NEWS_ITEM_IMAGE_16X9_LARGE = "image_16x9_large";

    private static final String CREATE_NEWS_TABLE = "create table " + NEWS_TABLE + "("
            + NEWS_ITEM_ID + " integer primary key autoincrement, "
            + NEWS_ITEM_TITLE + " text, "
            + NEWS_ITEM_PUBDATE + " integer, "
            + NEWS_ITEM_LINK + " text, "
            + NEWS_ITEM_DESCRIPTION + " text, "
            + NEWS_ITEM_CATEGORY + " text,"
            + NEWS_ITEM_IMAGE_DEFAULT + " text, "
            + NEWS_ITEM_IMAGE_4X3_SMALL + " text, "
            + NEWS_ITEM_IMAGE_4X3_MEDIUM + " text, "
            + NEWS_ITEM_IMAGE_4X3_LARGE + " text, "
            + NEWS_ITEM_IMAGE_16X9_SMALL + " text, "
            + NEWS_ITEM_IMAGE_16X9_LARGE + " text" + ");";

    private static final String AUTHORITY = "com.study.testtask_10.NewsReader";
    private static final String NEWS_PATH = "news";
    public static final Uri NEWS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + NEWS_PATH);
    private static final String NEWS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + NEWS_PATH;
    private static final String NEWS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + NEWS_PATH;
    private static final int URI_NEWS = 1;
    private static final int URI_NEWS_ID = 2;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,NEWS_PATH, URI_NEWS);
        uriMatcher.addURI(AUTHORITY,NEWS_PATH + "/#", URI_NEWS_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");

        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(LOG_TAG, "query: " + uri);

        switch (uriMatcher.match(uri)) {
            case URI_NEWS:
                Log.d(LOG_TAG, "URI_NEWS");

                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = NEWS_ITEM_PUBDATE + " ASC";
                }
                break;
            case URI_NEWS_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_NEWS_ID " + id);

                if (TextUtils.isEmpty(selection)) {
                    selection = NEWS_ITEM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + NEWS_ITEM_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(NEWS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), NEWS_CONTENT_URI);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(LOG_TAG, "getType: " + uri);

        switch (uriMatcher.match(uri)) {
            case URI_NEWS:
                return NEWS_CONTENT_TYPE;
            case URI_NEWS_ID:
                return NEWS_CONTENT_ITEM_TYPE;
        }

        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(LOG_TAG, "insert: " + uri);

        if (uriMatcher.match(uri) != URI_NEWS)
            throw new IllegalArgumentException("Wrong URI:" + uri);
        Uri resultUri = null;
        db = dbHelper.getWritableDatabase();
        if (!isNewsExistsInDB(db, values)) {
            long rowId = db.insert(NEWS_TABLE, null, values);
            resultUri = ContentUris.withAppendedId(NEWS_CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(resultUri, null);
        } else {
            Log.d(LOG_TAG, "insert: the news \"" + values.getAsString(NEWS_ITEM_TITLE) + "\" already exists in database");
        }
        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(LOG_TAG, "delete: " + uri);

        switch (uriMatcher.match(uri)) {
            case URI_NEWS:
                Log.d(LOG_TAG, "URI_NEWS");

                break;
            case URI_NEWS_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_NEWS_ID " + id);

                if (TextUtils.isEmpty(selection)) {
                    selection = NEWS_ITEM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + NEWS_ITEM_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(NEWS_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return cnt;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(LOG_TAG, "update: " + uri);

        switch (uriMatcher.match(uri)) {
            case URI_NEWS:
                Log.d(LOG_TAG, "URI_NEWS");

                break;
            case URI_NEWS_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_NEWS_ID " + id);

                if (TextUtils.isEmpty(selection)) {
                    selection = NEWS_ITEM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + NEWS_ITEM_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        int cnt = db.update(NEWS_TABLE, values,selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return cnt;
    }

    private boolean isNewsExistsInDB(SQLiteDatabase db, ContentValues vc) {
        String sql = "SELECT * FROM " + NEWS_TABLE + " WHERE " + NEWS_ITEM_TITLE + " = ? ";
        String[] selectionArgs = {vc.getAsString(NEWS_ITEM_TITLE)};
        return db.rawQuery(sql, selectionArgs).getCount() != 0;
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) {
            super(context, DATA_BASE, null, DATA_BASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_NEWS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
