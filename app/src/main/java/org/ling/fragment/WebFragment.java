package org.ling.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
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
import android.widget.Toast;

import com.bossturban.webviewmarker.TextSelectionSupport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import im.delight.android.webview.AdvancedWebView;
import odyssey.ru.linguateacher.CustomWebView;
import odyssey.ru.linguateacher.Dialog;
import odyssey.ru.linguateacher.MainActivity;
import odyssey.ru.linguateacher.Setting;

import org.ling.fragment.view.MySnackBar;
import org.ling.fragment.view.WebLoadButton;
import org.ling.model.SetWords;
import org.ling.model.agent.SetWordsAgent;
import org.ling.server.YandexServer;
import org.ling.utils.IAction;
import org.ling.utils.IActionHandler;
import org.ling.utils.MyTextSelectionSupport;
import odyssey.ru.linguateacher.R;
import org.ling.task.DownloadPage;

/**
 * Created by Dmitry on 23.06.2016.
 */
public class WebFragment extends Fragment implements AdvancedWebView.Listener {

    LayoutInflater inflater;
    Context context;
    MyTextSelectionSupport mTextSelectionSupport;
    static List<String> searchHistory = new ArrayList<>();
    int searchHistoryPos;
    //    /String mUrl;
    private static CustomWebView mWebView;


    private static EditText edtSeach;
    int lastTouchX;
    int lastTouchY;
    private LinearLayout btns_tran;
    DisplayMetrics metricsB;

    SetWords currentSet;
    String selectedtext = "";
    static String URL = "";

    String prevTextUrl;
    String TIP_TAG = "WebFragmentTip";
    String LIST_TAG = "WebFragmentList";
    static WebLoadButton webLoadBtn;
    static ProgressBar pbLoading;
    static Thread downloadTask;

    DownloadPage task;
    View rootView;
    private ViewGroup container;
    private MySnackBar mySnackBar;

    void tip() {
        if (Setting.getBool(TIP_TAG)) {
            return;
        }
        new SweetAlertDialog(MainActivity.activity)
                .setTitleText("Подсказка")
                .setContentText("Выберите из списка в правом верхнем углу словарь, куда будут запоминаться слова.\n" +
                        "Выделите иностранное слово для появиления кнопки перевода. При выборе кнопки \"перевести\" вы увидете внизу список слов. При клике на одно из них вы сохраните слово в вашем словаре")
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
    String prevURL = "";
    String prevhtml = "";

    @Override
    public void onResume() {
        if (MainActivity.activity.getCurrentTab() == 2) {

            tip();

            if (first) {
                String url = "en.wikipedia.org/wiki/Doctor_Who";
                prevURL = Setting.getString("prevURL");


                if (prevURL != "") {
                    url = prevURL;
                }

                prevhtml = Setting.getString("HTML");
                if (prevhtml.equals("") || prevURL == "") {
                    runUrlDownloadPage(url);
                } else {
                    onFinishDownloadPage(prevhtml, null, prevURL, false, "utf-8");
                }

                prevTextUrl = url;
                URL = prevTextUrl;
                first = false;
            }
        }
//        showSearchView();
        start();
        mWebView.onResume();
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
        mWebView.onDestroy();
        super.onDestroy();
    }

    public void hideSearchView() {
        ActionBar action = MainActivity.activity.getSupportActionBar(); //get the actionbar
        action.setDisplayShowCustomEnabled(false);
        action.setCustomView(null);
    }

    public void showSearchView() {
        ActionBar action = MainActivity.activity.getSupportActionBar(); //get the actionbar

        action.setDisplayShowCustomEnabled(true); //enable it to display a
        action.setCustomView(R.layout.search_bar);//add the custom view
        edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch);
        webLoadBtn = (WebLoadButton) action.getCustomView().findViewById(R.id.webLoadBtn);

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        if (displaymetrics.widthPixels > 0) {
            edtSeach.setWidth(displaymetrics.widthPixels - 210);
        }
        IAction actionStop = new IAction() {
            @Override
            public void onSucces(Object rs) {
                if (pbLoading != null) {
                    pbLoading.setVisibility(View.GONE);
                }
                if (downloadTask != null)
                    downloadTask.interrupt();
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


//        edtSeach.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(!prevTextUrl.equals(s.toString())){
//                    webLoadBtn.onload();
//                }else{
//                    webLoadBtn.onrepeat();
//                }
//                webLoadBtn.invalidate();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
        //open the keyboard focused in the edtSearch
        InputMethodManager imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


    }

    private void doSearch() {
        final String url = edtSeach.getText().toString();
        runUrlDownloadPage(url);

    }

    void start() {

//        mTextSelectionSupport = TextSelectionSupport.support(MainActivity.activity, mWebView);
        final LinearLayout btns_tran = (LinearLayout) rootView.findViewById(R.id.btns_tran);
        final Button btnAdd = (Button) rootView.findViewById(R.id.mBtnAdd);
        final Button btnTrans = (Button) rootView.findViewById(R.id.mBtnTranslate);
        final TextView textLangs = (TextView) rootView.findViewById(R.id.textLangs);

        final RelativeLayout listSetWords = (RelativeLayout) rootView.findViewById(R.id.mSetWords2);
        final TextView mSetName = (TextView) rootView.findViewById(R.id.mSetName);
        pbLoading = (ProgressBar) rootView.findViewById(R.id.pbLoading);
        final List<SetWords> listSet = SetWordsAgent.getAll();
        final List<String> list = new ArrayList<>();

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
            currentSet = listSet.get(pos);
            mSetName.setText(currentSet.getName());
            for (SetWords e : listSet) {
                list.add(e.getName());
            }
            textLangs.setText(currentSet.getLang() + " -> " + currentSet.getNatv());

        }
        listSetWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.setActivity(MainActivity.activity);
                Dialog.showListDialog(MainActivity.getStringById(R.string.select_dict), list, null, new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        int pos = (Integer) rs;
                        currentSet = listSet.get(pos);

                        Setting.setInt(LIST_TAG, pos);
                        mSetName.setText(currentSet.getName());
                        textLangs.setText(currentSet.getLang() + " -> " + currentSet.getNatv());
                    }

                    @Override
                    public void onFailAction(String s, Throwable throwable) {
                    }
                });
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.showCreateWordDialog(selectedtext, currentSet, new IAction() {
                    @Override
                    public void onSucces(Object rs) {
                        Toast.makeText(MainActivity.activity, MainActivity.getStringById(R.string.added), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btnTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbLoading.setVisibility(View.VISIBLE);
                mySnackBar = new MySnackBar(MainActivity.activity,inflater, rootView );
                mySnackBar.showTranslatedWord(pbLoading, currentSet,selectedtext);
            }
        });


        btns_tran.setVisibility(View.GONE);


        mTextSelectionSupport = new MyTextSelectionSupport(MainActivity.activity, mWebView, new IAction() {
            @Override
            public void onSucces(Object rs) {
                btns_tran.setVisibility(View.GONE);
                if (mySnackBar.getSnackbar() != null)
                    mySnackBar.getSnackbar().dismiss();
            }
        });
        Display display = MainActivity.activity.getWindowManager().getDefaultDisplay();
        metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);


        mTextSelectionSupport.setSelectionListener(new TextSelectionSupport.SelectionListener() {
            @Override
            public void startSelection() {
            }

            @Override
            public void selectionChanged(String text) {


                selectedtext = text;
                lastTouchX = (int) mTextSelectionSupport.dragX;
                lastTouchY = (int) mTextSelectionSupport.dragY;

                if (metricsB.widthPixels / 2 > lastTouchX) {
                    lastTouchX += 70 - btns_tran.getMeasuredWidth() / 2;
                } else {
                    lastTouchX -= 70 + btns_tran.getMeasuredWidth() / 2;
                }


                if (metricsB.heightPixels / 2 > lastTouchY) {
                    lastTouchY += 120 - btns_tran.getMeasuredHeight() / 2;
                } else {
                    lastTouchY -= 120 + btns_tran.getMeasuredHeight() / 2;
                }
                MainActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btns_tran.setVisibility(View.VISIBLE);
                        btns_tran.setX(lastTouchX);
                        btns_tran.setY(lastTouchY);
                    }
                });

            }

            @Override
            public void endSelection() {
                //cont.removeView(btns_tran);
            }
        });

        CookieManager.getInstance().setAcceptCookie(true);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
//        mWebView.setWebChromeClient(new WebChromeClient());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mWebView.setWebContentsDebuggingEnabled(true);
//        }
//        mWebView.setWebViewClient(new MyWebViewClient());


        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webLoadBtn.onstop(false);
                runUrlDownloadPage(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });

        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        mWebView.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && searchHistory.size() > 1) {

                    searchHistory.remove(searchHistory.size() - 1);
                    String mUrl = searchHistory.get(searchHistory.size() - 1);

                    webLoadBtn.onstop(false);
                    runUrlDownloadPage(mUrl);
                    edtSeach.setText(mUrl);
                    container.removeView(mWebView);
                    return true;
//                    return false;
                }
//                container.removeView(mWebView);
                return false;
            }

        });


//        mWebView.loadUrl("file:///android_asset/content.html");
//        downloadPage(url);
    }



    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_web, container, false);
        this.inflater = inflater;
        this.context = inflater.getContext();
        this.container = container;
        mWebView = (CustomWebView) rootView.findViewById(R.id.webView);

        return rootView;
    }


    public void runUrlDownloadPage(final String url) {
        if (pbLoading != null)
            pbLoading.setVisibility(View.VISIBLE);
        downloadTask = new Thread(new Runnable() {
            @Override
            public void run() {
                downloadPage(url);
            }
        });
        downloadTask.start();
    }

    public void onFinishDownloadPage(String html, Exception err, final String url, boolean save, final String charset) {

        try {
            if (html.equals("")) {
                String error = MainActivity.getStringById(R.string.unknown_error);
                if (err instanceof java.net.SocketTimeoutException) {
                    error = MainActivity.getStringById(R.string.time_out);
                }
                if (err instanceof MalformedURLException) {
                    error = MainActivity.getStringById(R.string.invalid_url);
                    ;
                }
                if (err instanceof IOException) {
                    error = MainActivity.getStringById(R.string.server_not_found);
                }
                if (err instanceof UnknownHostException || err instanceof FileNotFoundException) {
                    error = MainActivity.getStringById(R.string.server_not_found_check_adress);
                    ;
                }
                toError(error);
                final String finalError = error;
                MainActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.activity, finalError, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                int index = html.indexOf("</head>");  // temp is the file content return in string
                String s1 = html.substring(0, index);
                String s2 = html.substring(index, html.length());


                s1 = s1 + "<script type=\"text/javascript\" src=\"file:///android_asset/jquery-1.8.3.js\"></script> \n";
                s1 = s1 + "<script type=\"text/javascript\" src=\"file:///android_asset/jpntext.js\"></script> \n";
                s1 = s1 + "<script type=\"text/javascript\" src=\"file:///android_asset/rangy-core.js\"></script> \n";
                s1 = s1 + "<script type=\"text/javascript\" src=\"file:///android_asset/rangy-serializer.js\" ></script> \n";
                s1 = s1 + "<script type=\"text/javascript\" src=\"file:///android_asset/android.selection.js\" ></script > \n";


                html = s1 + s2;
                final String finalHtml = html;
                if (save) {
                    Setting.setString("prevURL", url);
                    Setting.setString("HTML", html);
                }

                MainActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (searchHistory.contains(url)) {
                            searchHistory.remove(url);
                        }
                        searchHistory.add(url);
                        mWebView.loadDataWithBaseURL(url, finalHtml, "text/html", "UTF-8", null);
//                        mWebView.loadHtml(finalHtml, url);
                    }
                });

            }
        } catch (Exception e) {

        }
        ;
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (webLoadBtn != null) {
                    webLoadBtn.onrepeat();
                }
                if (edtSeach != null) {
                    edtSeach.setText(URL);
                }
                CookieManager cookieManager = CookieManager.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.setAcceptThirdPartyCookies(mWebView, true);
                } else {
                    cookieManager.setAcceptCookie(true);
                }
                if (pbLoading != null)
                    pbLoading.setVisibility(View.GONE);

            }
        });

    }


    public void downloadPage(final String url) {
        if (task != null && !task.getStatus().equals(AsyncTask.Status.FINISHED)) {
            task.cancel(true);
        }
        task = new DownloadPage(new IActionWebPage() {
            @Override
            public void onSuccess(String html, Exception err, String url, String charset) {
                WebFragment.this.onFinishDownloadPage(html, err, url, true, charset);
            }
        });

        task.execute(url);
        URL = url;
    }

    public static void toError(String error) {
        final StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<link rel=stylesheet href='css/style.css'>");
        html.append("</head>");
        html.append("<body>");

        html.append("<div id =\"block\">");
        html.append("<p><img  width=\"90\" height=\"90\"  src='img/sadpony.png'  align=\"middle\" /></p>");
        html.append("<p><h3>  " + MainActivity.getStringById(R.string.error_loading_page) + "</h3> </p>");
        html.append("<p>" + error + "</p>");
        html.append("</div>");
        html.append("</body></html>");

        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.loadDataWithBaseURL("file:///android_asset/", html.toString(), "text/html", "UTF-8", "");
            }
        });
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {
        int x = 0;
    }




    public interface IActionWebPage {

        public void onSuccess(String html, Exception err, String url, String charset);

    }

}
