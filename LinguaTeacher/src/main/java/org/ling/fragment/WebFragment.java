package org.ling.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.R;
import org.ling.CustomWebView;
import org.ling.Dialog;
import org.ling.MainActivity;
import org.ling.MyApplication;
import org.ling.Setting;
import org.ling.fragment.view.MySnackBar;
import org.ling.fragment.view.WebLoadButton;
import org.ling.model.SetWords;
import org.ling.model.agent.SetWordsAgent;
import org.ling.task.DownloadPage;
import org.ling.utils.IAction;
import org.ling.utils.IActionHandler;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import odyssey.ru.linglibrary.FileHelp;
import odyssey.ru.linglibrary.Util;

;

/**
 * Created by Dmitry on 23.06.2016.
 */
public class WebFragment extends Fragment  {

    private static final String TAG = "WebFragmentTag";
    LayoutInflater inflater;
    Context context;
    //    MyTextSelectionSupport mTextSelectionSupport;
    //    /String mUrl;
    private static CustomWebView mWebView;


    private static EditText edtSeach;
    int lastTouchX;

    String selectedtext = "";
    static String URL = "";


    String TIP_TAG = "WebFragmentTip";
    String LIST_TAG = "WebFragmentList";
    static WebLoadButton webLoadBtn;
    static ProgressBar pbLoading;
//    static Thread downloadTask;

    DownloadPage task;
    View rootView;
    private ViewGroup container;
    private MySnackBar mySnackBar;
    private String prevURL;
    private DisplayMetrics metricsB;

    void tip() {
        if (Setting.getBool(TIP_TAG)) {
            return;
        }
        new SweetAlertDialog(MainActivity.activity)
                .setTitleText(Util.getStringById(R.string.tip))
                .setContentText(Util.getStringById(R.string.web_tip))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        Setting.setBool(TIP_TAG, true);
                    }
                })
                .show();
    }


    boolean first = true;


    @Override
    public void onResume() {
        if (MainActivity.activity.getCurrentTab() == 2) {

            tip();


//
//

//            }
        }
        if (MainActivity.getTabIndex() == 2) {
            showSearchView();
        }
        start();


        if (first) {
//                 URL = "http://google.com/";

            prevURL = Setting.getString("prevURL");
            if (prevURL == "") {
                URL = "https://www.google.ru/";
            }

            first = false;

        }
        downloadPage(URL);
        //mWebView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        hideSearchView();
        mWebView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        //mWebView.onDestroy();
        super.onDestroy();
    }

    public void hideSearchView() {
        ActionBar action = MainActivity.activity.getSupportActionBar(); //get the actionbar
        action.setDisplayShowCustomEnabled(false);
        action.setCustomView(null);
    }

    public void showSearchView() {

//        MyApplication.showInterstitial();
//        MyApplication.loadInterstitial();
        ActionBar action = MainActivity.activity.getSupportActionBar(); //get the actionbar

        action.setDisplayShowCustomEnabled(true); //enable it to display a
        action.setCustomView(R.layout.search_bar);//add the custom view
        edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch);
        webLoadBtn = (WebLoadButton) action.getCustomView().findViewById(R.id.webLoadBtn);


        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        if (displaymetrics.widthPixels > 0) {
            //edtSeach.setWidth(displaymetrics.widthPixels - 210);
        }
        IAction actionStop = new IAction() {
            @Override
            public void onSucces(Object rs) {
                if (pbLoading != null) {
                    pbLoading.setVisibility(View.GONE);
                }

            }
        };

        IAction actionRepeat = new IAction() {
            @Override
            public void onSucces(Object rs) {
                pbLoading.setVisibility(View.VISIBLE);
                doSearch();
            }
        };

        webLoadBtn.setHandlers(actionStop, actionRepeat);


        //this is a listener to do a search when the user clicks on search button
        edtSeach.setSelectAllOnFocus(true);
        edtSeach.setText(URL);
        edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                    return true;
                }
                return false;
            }
        });
        edtSeach.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
             //   menu.clear();
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
               // menu.clear();
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });




        edtSeach.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edtSeach.selectAll();
//                edtSeach.showContextMenu();
                return false;
            }
        });
        edtSeach.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    edtSeach.setText(URL);
                }
            }
        });


        InputMethodManager imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


    }

    private void doSearch() {
        final String url = edtSeach.getText().toString();
        webLoadBtn.onstop(false);
        downloadPage(url);

    }

    void start() {




//        mTextSelectionSupport = TextSelectionSupport.support(MainActivity.activity, mWebView);
        final LinearLayout btns_tran = (LinearLayout) rootView.findViewById(R.id.btns_tran);
        final Button btnAdd = (Button) rootView.findViewById(R.id.mBtnAdd);
        final Button btnTrans = (Button) rootView.findViewById(R.id.mBtnTranslate);
        final TextView textLangs = (TextView) rootView.findViewById(R.id.textLangs);
        final Button btnOpenDoc = (Button) rootView.findViewById(R.id.btnOpenDoc);

        final RelativeLayout listSetWords = (RelativeLayout) rootView.findViewById(R.id.mSetWords3);
        final TextView mSetName = (TextView) rootView.findViewById(R.id.mSetName);
        pbLoading = (ProgressBar) rootView.findViewById(R.id.pbLoading);
        final List<SetWords> listSet = SetWordsAgent.getAll();
        final List<String> list = new ArrayList<>();


        btnOpenDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = "file:///" + FileHelp.getPath() + "2.docx";
                String url = "http://docs.google.com/gview?embedded=true&url="
                        + path;
                webLoadBtn.onstop(false);
                //runUrlDownloadPage(url);

//                GoogleDriveServer.insertFile(path);


            }
        });


        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        if (listSet.size() != 0) {
            int pos = Setting.getInt(LIST_TAG);
            if (pos >= listSet.size()) {
                pos = 0;
            }
            MyApplication.currentSet = listSet.get(pos);
            mSetName.setText(MyApplication.currentSet.getName());
            for (SetWords e : listSet) {
                list.add(e.getName());
            }
            textLangs.setText(MyApplication.currentSet.getLang() + " -> " + MyApplication.currentSet.getNatv());

        }
        listSetWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.setActivity(MainActivity.activity);
                Dialog.showListDialog(MainActivity.getStringById(R.string.select_dict), list, null, new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        int pos = (Integer) rs;
                        MyApplication.currentSet = listSet.get(pos);

                        Setting.setInt(LIST_TAG, pos);
                        mSetName.setText(MyApplication.currentSet.getName());
                        textLangs.setText(MyApplication.currentSet.getLang() + " -> " + MyApplication.currentSet.getNatv());
                    }

                    @Override
                    public void onFailAction(String s, Throwable throwable) {
                    }
                });
            }
        });

//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Dialog.showCreateWordDialog(selectedtext, MyApplication.currentSet, new IAction() {
//                    @Override
//                    public void onSucces(Object rs) {
//                        Toast.makeText(MainActivity.activity, MainActivity.getStringById(R.string.added), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//        btnTrans.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pbLoading.setVisibility(View.VISIBLE);
//                mySnackBar = new MySnackBar(MainActivity.activity, inflater, rootView);
//                mySnackBar.showTranslatedWord(MainActivity.activity, pbLoading, MyApplication.currentSet, selectedtext);
//            }
//        });


        btns_tran.setVisibility(View.GONE);


        Display display = MainActivity.activity.getWindowManager().getDefaultDisplay();
        metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);


//

        CookieManager.getInstance().setAcceptCookie(true);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }


        //mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

        // mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setUseWideViewPort(false);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                Log.v(TAG, "onPageStarted:");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (pbLoading != null) {
                    pbLoading.setVisibility(View.GONE);
                    //webLoadBtn.onstop(true);

                }

                if(webLoadBtn!= null){
                    webLoadBtn.onrepeat();
                }
                Log.v(TAG, "onPageFinished:");
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.v(TAG, "shouldOverrideUrlLoading");

                if (pbLoading != null) {
                    pbLoading.setVisibility(View.VISIBLE);
                }

                if (webLoadBtn != null) {
                    webLoadBtn.onstop(false);
                }
                //URL = url;

                //prevURL = URL;
                if (edtSeach != null) {
                    edtSeach.setText(URL);
                }


                DownloadPage.Request req = DownloadPage.findRequestIfHave(url);
                String url2;

                if (req.isRequest) {
                    url2 = req.url;
                } else {
                    url2 = DownloadPage.toCurrectURL(url);
                }

                URL = url2;
                view.loadUrl(URL);

                Setting.setString("prevURL",URL);


                //downloadPage(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript("(function(){return window.getSelection().toString()})()",
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.v(TAG, "SELECTION:" + value);
                        }
                    });
        }




        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        mWebView.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {


                if ((keyCode != KeyEvent.KEYCODE_BACK)) {
                    return false;
                }
                if (MySnackBar.hideIfshow()) {
                    return false;
                }


                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }


//                container.removeView(mWebView);
                return true;
            }

        });


//        mWebView.loadUrl("file:///android_asset/content.html");
//        downloadPage(url);
    }

    private void downloadPage(String url) {

        DownloadPage.Request req = DownloadPage.findRequestIfHave(url);
        String url2;

        if (req.isRequest) {
            url2 = req.url;
        } else {
            url2 = DownloadPage.toCurrectURL(url);
        }

        URL = url2;

        mWebView.loadUrl(URL);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_web, container, false);
        this.inflater = inflater;
        this.context = inflater.getContext();
        this.container = container;
//        if (mWebView == null)

        CookieManager.getInstance().setAcceptCookie(true);
        //if (mWebView == null) {
        mWebView = (CustomWebView) rootView.findViewById(R.id.webView);
        // }

        return rootView;
    }



    public void setHtml(String html) {
        mWebView.loadDataWithBaseURL("google123.com", html, "text/html", "UTF-8", null);
    }


    public interface IActionWebPage {

        public void onSuccess(String html, Exception err, String url, String charset);

    }



    public void getSelectedTextByWebView()
    {
        mWebView.getSelectedData();
    }
    public void showShackBarTranslateWord(String text)
    {
        mySnackBar = new MySnackBar(MainActivity.activity, inflater, rootView);
        mySnackBar.showTranslatedWord(MainActivity.activity, pbLoading, MyApplication.currentSet, text);
    }

    public static boolean isFocusedSearchText(){
       return  edtSeach.isFocused();
    }


}
