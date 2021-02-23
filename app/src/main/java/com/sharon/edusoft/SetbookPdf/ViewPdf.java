package com.sharon.edusoft.SetbookPdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.sharon.edusoft.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ViewPdf extends AppCompatActivity {
    WebView pdfview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpdf);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        pdfview = findViewById(R.id.pdfview);
        pdfview.requestFocus();
        pdfview.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT>=19){
            pdfview.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        }else{
        pdfview.setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
        String name = getIntent().getStringExtra("name");
        String uri = getIntent().getStringExtra("url");

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle(name);
        pd.setMessage("Opening!!!");
        pd.setCancelable(false);
        pdfview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicom) {
                super.onPageStarted(view, url, favicom);
                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pd.dismiss();
            }
        });
        String url="";
        try {
           url= URLEncoder.encode(uri,"UTF-8");

        } catch (Exception e) {

        }
        pdfview.loadUrl("https://docs.google.com/gview?embedded=true&url="+url);
    }

    @Override
    public void onBackPressed(){
     if (pdfview.canGoBack()){
         pdfview.goBack();
     }else{
         super.onBackPressed();
     }
    }
}
