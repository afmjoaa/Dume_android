package io.dume.dume.common.privacyPolicy;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import io.dume.dume.R;
import io.dume.dume.common.aboutUs.AboutUsActivity;
import io.dume.dume.student.pojo.CustomStuAppCompatActivity;

import static io.dume.dume.util.DumeUtils.configureAppbar;

public class PrivacyPolicyActivity extends CustomStuAppCompatActivity implements PrivacyPolicyActivityContact.View {

    private PrivacyPolicyActivityContact.Presenter mPresenter;
    private static final String TAG = "PrivacyPolicyActivity";
    private static final int fromFlag = 14;
    private WebView aboutView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common3_activity_privacy_policy);
        setActivityContext(this, fromFlag);
        mPresenter = new PrivacyPolicyPresenter(this, new PrivacyPolicyModel());
        mPresenter.privacyPolicyEnqueue();
        findLoadView();
        configureAppbar(this, "Privacy policy");

    }

    @Override
    public void findView() {
        aboutView = findViewById(R.id.aboutWebView);
        aboutView.getSettings().setJavaScriptEnabled(true);
        aboutView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Log.w(TAG, "onPageFinished: ");


            }


            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }
        });
        aboutView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
        aboutView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgress();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideProgress();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(PrivacyPolicyActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
        aboutView.loadUrl("https://dume-2d063.firebaseapp.com/terms");
    }

    @Override
    public void initPrivacyPolicy() {

    }

    @Override
    public void configPrivacyPolicy() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
