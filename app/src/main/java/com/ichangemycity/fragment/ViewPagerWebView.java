package com.ichangemycity.fragment;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.ichangemycity.appdata.AppConstant;
import com.ichangemycity.appdata.AppUtils;
import com.ichangemycity.swachhbharatengineer.R;

import java.util.List;

public class ViewPagerWebView extends Fragment {

    private static final String IMAGE_DATA_EXTA = "resID";
    private int position;
    private static List<String> array;
    private WebView mWebview;
    private ProgressBar progressBar;

    public static ViewPagerWebView newInstance(int position,
                                               List<String> mArray) {
        final ViewPagerWebView f = new ViewPagerWebView();
        final Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTA, position);
        f.setArguments(args);
        array = mArray;
        return f;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments() != null ? getArguments().getInt(IMAGE_DATA_EXTA)
                : -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater
                .inflate(R.layout.inflate_view_all_media_fragment_image_preview_webview, container,
                        false);
        mWebview = v.findViewById(R.id.webView);
        progressBar = v.findViewById(R.id.progress);

        registerForContextMenu(mWebview);
        mWebview.setWebViewClient(new AppWebViewClients());
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDefaultZoom(ZoomDensity.CLOSE);
        String s = "<html><body style=\"margin: 0; padding: 0\">"
                + "<P ALIGN='CENTER'><IMG  width='100%' src=\"" +
                array.get(position) +
                "\"></P>"
                + "<body><html>";

        mWebview.setBackgroundColor(getContext().getResources().getColor(R.color.primerColorBlack));
        mWebview.loadData(s, "text/html", "UTF-8");
        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                    ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

        final WebView.HitTestResult webViewHitTestResult = mWebview.getHitTestResult();

        if (webViewHitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                webViewHitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            contextMenu.setHeaderTitle("");

            contextMenu.add(0, 1, 0, "Download Image")
                    .setOnMenuItemClickListener(menuItem -> {

                        String DownloadImageURL = webViewHitTestResult.getExtra();

                        if (URLUtil.isValidUrl(DownloadImageURL)) {

                            DownloadManager.Request mRequest = new DownloadManager.Request(
                                    Uri.parse(DownloadImageURL));
                            mRequest.allowScanningByMediaScanner();
                            mRequest.setNotificationVisibility(
                                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            DownloadManager mDownloadManager = (DownloadManager) getActivity()
                                    .getSystemService(getActivity().DOWNLOAD_SERVICE);
                            mDownloadManager.enqueue(mRequest);

                            AppUtils.getInstance().showToast(getActivity(), AppConstant.TOAST_TYPE_INFO,
                                    "Downloading image...");
                        } else {
                            AppUtils.getInstance().showToast(getActivity(), AppConstant.TOAST_TYPE_INFO,
                                    "Sorry, unable to download for now");
                        }
                        return false;
                    });
        }
    }

    class AppWebViewClients extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            progressBar.setVisibility(View.GONE);
        }
    }
}