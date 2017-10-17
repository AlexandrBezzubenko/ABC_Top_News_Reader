package com.study.testtask_01;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Item implements Parcelable {

    private ArrayList<String> mThumbnailLinkArray;
    private String mKeywords;
    private String mTitle;
    private String mLink;
    private String mPubDate;
    private String mDescription;
    private String mCategory;

    public Item(ArrayList<String> thumbnailLinkArray, String keyword, String title, String link, String pubDate, String description, String category) {
        this.mThumbnailLinkArray = thumbnailLinkArray;
        this.mKeywords = keyword;
        this.mTitle = title;
        this.mLink = link;
        this.mPubDate = pubDate;
        this.mDescription = description;
        this.mCategory = category;
    }

    protected Item(Parcel in) {
        mThumbnailLinkArray = in.createStringArrayList();
        mKeywords = in.readString();
        mTitle = in.readString();
        mLink = in.readString();
        mPubDate = in.readString();
        mDescription = in.readString();
        mCategory = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(mThumbnailLinkArray);
        dest.writeString(mKeywords);
        dest.writeString(mTitle);
        dest.writeString(mLink);
        dest.writeString(mPubDate);
        dest.writeString(mDescription);
        dest.writeString(mCategory);
    }

    public ArrayList<String> getThumbnailLinkArray() {
        return mThumbnailLinkArray;
    }

    public String getKeywords() {
        return mKeywords;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLink() {
        return mLink;
    }

    public String getPubDate() {
        return mPubDate;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getCategory() {
        return mCategory;
    }
}
