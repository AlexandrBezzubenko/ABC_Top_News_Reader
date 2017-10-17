package com.study.testtask_01;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

class NewsRVCursorAdapter extends RecyclerView.Adapter<NewsRVCursorAdapter.NewsViewHolder> {

    private static final int NEWS_ID = 0;
    private static final int NEWS_TITLE = 1;
    private static final int NEWS_PUBDATE = 2;
    private static final int NEWS_LINK = 3;
    private static final int NEWS_DESCRIPTION = 4;
    private static final int NEWS_CATEGORY = 5;
    private static final int NEWS_IMAGE_1 = 6;
    private static final int NEWS_IMAGE_2 = 7;
    private static final int NEWS_IMAGE_3 = 8;
    private static final int NEWS_IMAGE_4 = 9;
    private static final int NEWS_IMAGE_5 = 10;
    private static final int NEWS_IMAGE_6 = 11;
    private static final int NEWS_IMAGE_7 = 12;

    private Cursor cursor;
    private Context context;

    NewsRVCursorAdapter(Cursor cursor, Context context) {
        this.cursor = cursor;
        this.context = context;
    }

    @Override
    public NewsRVCursorAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        holder.bindData(getItem(position));
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    void swapCursor(final Cursor cursor) {
        this.cursor = cursor;
        this.notifyDataSetChanged();
    }

    private Cursor getItem(final int position) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.moveToPosition(position);
        }
        return cursor;
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        CardView newsItemCard;
        ImageView image;
        TextView title;
        TextView description;
        TextView pubDate;

        private NewsViewHolder(View itemView) {
            super(itemView);

            newsItemCard = itemView.findViewById(R.id.card_news_item);
            image = itemView.findViewById(R.id.image_news_image);
            title = itemView.findViewById(R.id.text_news_title);
            description = itemView.findViewById(R.id.text_news_description);
            pubDate = itemView.findViewById(R.id.text_news_pubdate);
        }

        private void bindData(Cursor cursor) {
            int orientation = context.getResources().getConfiguration().orientation;
            if (orientation == ORIENTATION_PORTRAIT) {
                Picasso.with(context).load(cursor.getString(NEWS_IMAGE_1)).into(image);
            } else {
                Picasso.with(context).load(cursor.getString(NEWS_IMAGE_6)).into(image);
            }
            title.setText(cursor.getString(NEWS_TITLE));
            description.setText(cursor.getString(NEWS_DESCRIPTION));
            pubDate.setText(convertToLocalTime(cursor.getString(NEWS_PUBDATE)));
        }

        private String convertToLocalTime(String utcTime) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(utcTime));
            int localOffset = TimeZone.getDefault().getOffset(calendar.getTimeInMillis());
            calendar.add(Calendar.MILLISECOND, localOffset);
            DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
            return dateFormat.format(calendar.getTime());
        }
    }
}
