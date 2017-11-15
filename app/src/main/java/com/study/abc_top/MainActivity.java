package com.study.abc_top;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView newsList;

    private NewsRVCursorAdapter adapter;
    private static final String[] NEWS_SUMMARY_PROJECTION = new String[] {
            NewsProvider.NEWS_ITEM_ID,
            NewsProvider.NEWS_ITEM_TITLE,
            NewsProvider.NEWS_ITEM_PUBDATE,
            NewsProvider.NEWS_ITEM_LINK,
            NewsProvider.NEWS_ITEM_DESCRIPTION,
            NewsProvider.NEWS_ITEM_CATEGORY,
            NewsProvider.NEWS_ITEM_IMAGE_DEFAULT,
            NewsProvider.NEWS_ITEM_IMAGE_4X3_SMALL,
            NewsProvider.NEWS_ITEM_IMAGE_4X3_MEDIUM,
            NewsProvider.NEWS_ITEM_IMAGE_4X3_LARGE,
            NewsProvider.NEWS_ITEM_IMAGE_16X9_SMALL,
            NewsProvider.NEWS_ITEM_IMAGE_16X9_LARGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =   findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getNews();

        initRecyclerView();

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri news_uri = com.study.abc_top.NewsProvider.NEWS_CONTENT_URI;
        return new CursorLoader(this, news_uri, NEWS_SUMMARY_PROJECTION, null, null, NewsProvider.NEWS_ITEM_PUBDATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void getNews() {
        Intent intent = new Intent(this, DownloadIntentService.class);
        startService(intent);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new NewsRVCursorAdapter(null, this);
        newsList = findViewById(R.id.recycler_news_list);
        newsList.setLayoutManager(layoutManager);
        newsList.setAdapter(adapter);
    }
}
