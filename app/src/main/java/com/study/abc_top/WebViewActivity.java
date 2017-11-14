package com.study.abc_top;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String link = i.getExtras().getString("LINK");
        setContentView(R.layout.activity_web_view);

        WebView mWebView = findViewById(R.id.webView);
//        mWebView.getSettings().setJavaScriptEnabled(true);
        if (link != null )
            mWebView.loadUrl(link);
    }
}
