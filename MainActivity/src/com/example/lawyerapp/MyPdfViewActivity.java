package com.example.lawyerapp;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

public class MyPdfViewActivity extends Activity {

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    //String path = this.getIntent().getExtras().getString("path");
	    
	    WebView mWebView=new WebView(MyPdfViewActivity.this);
	    mWebView.getSettings().setJavaScriptEnabled(true);
	    mWebView.getSettings().setPluginState(PluginState.ON);
	    mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url="+ "/storage/sdcard/Ashland Application.pdf");
	    setContentView(mWebView);
	  }
	}