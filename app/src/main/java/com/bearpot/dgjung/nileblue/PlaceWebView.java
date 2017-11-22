package com.bearpot.dgjung.nileblue;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bearpot.dgjung.nileblue.R;

/**
 * Created by dg.jung on 2017-11-22.
 */

public class PlaceWebView extends AppCompatActivity {

    private WebView mWebView;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_detail);

        mWebView = (WebView) findViewById(R.id.place_detail);
        mWebView.setWebViewClient(new WebViewClient());

        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

        Intent intent = new Intent(this.getIntent());
        String url = intent.getStringExtra("url");

        mWebView.loadUrl(url);
    }

}
