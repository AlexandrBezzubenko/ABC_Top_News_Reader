package com.study.testtask_01;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class Parser {

    private static final String LOG_TAG = "Parser";
    private static final String ns  = null;

    ArrayList<Item> parse (InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in, null);
            return reedFeed(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<Item> reedFeed(XmlPullParser parser) {
        ArrayList<Item> items = new ArrayList<>();

        try {
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, ns, "rss");
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, ns, "channel");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();

                if (name.equals("item")) {
                    items.add(readItem(parser));
                } else {
                    skip(parser);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    private Item readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<String> thumbnailLinkArray = new ArrayList<>();
        String keywords = null;
        String title = null;
        String link = null;
        String pubDate = null;
        String description = null;
        String category = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "thumbnail":
                    thumbnailLinkArray.add(readThumbnailLink(parser));
                    break;
                case "keywords":
                    keywords = readKeywords(parser);
                    break;
                case "title":
                    title = cleanQuotes(readTitle(parser));
                    break;
                case "link":
                    link = readLink(parser);
                    break;
                case "guid":
                    skip(parser);
                    break;
                case "pubDate":
                    pubDate = parsePubDateToUTC(readPubDate(parser));
                    break;
                case "description":
                    description = cleanQuotes(readDescription(parser));
                    break;
                case "category":
                    category = readCategory(parser);
                    break;
            }
        }

        return new Item(thumbnailLinkArray, keywords, title, link, pubDate, description, category);
    }

    private String readThumbnailLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String thumbnailLink = parser.getAttributeValue(0);
        parser.next();
        return thumbnailLink.trim();
    }

    private String readKeywords(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG,ns, "keywords");
        String keywords = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns, "keywords");
        return keywords.trim();
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title.trim();
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link.trim();
    }

    private String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "pubDate");
        String pubDate = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "pubDate");
        return pubDate.trim();
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description.trim();
    }

    private String readCategory(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "category");
        String category = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "category");
        return category.trim();
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String text = "";
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.getText();
            parser.nextTag();
        }
        return text.trim();
    }

    private void skip(XmlPullParser parser) {
        try {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parsePubDateToUTC(String pubDate){
        DateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        Date date;
        try {
            date = sdf.parse(pubDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.MILLISECOND, -TimeZone.getDefault().getOffset(c.getTimeInMillis()));
            return String.valueOf(c.getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String cleanQuotes(String text) {
        String str = "&quot;";
        if (text.contains(str))
           return text.replace(str, "\"");
        return text;
    }
}
