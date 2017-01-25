package odyssey.ru.testapp;

import android.content.IntentSender;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleDriveActivity";
    private static final int REQUEST_CODE_RESOLUTION = 11;

    WebView webView;
    private GoogleApiClient mGoogleApiClient;
    private boolean fileOperation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    List<UnionSub> list = analiseSub();
//                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//                    toolbar.setVisibility(View.GONE);
//
//                    String font_text1 = "<font color=\"#F48E60\">" ;
//                    String font_text2 = "<font color=\"#6077F4\">" ;
//                    String font_text_close ="</font>";
//
//                    int y = 0;
//                    StringBuilder sb = new StringBuilder();
//                    for (UnionSub us : list) {
//
//                        List<String> ls1 = us.getListSubF();
//                        List<String> ls2 = us.getListSubN();
//                        sb.append(y + " " + font_text1 );
//                        for (String e : ls1) {
//                            sb.append(e + "<br />");
//                        }
//                        sb.append(font_text_close + " "  + font_text2);
//                        for (String e : ls2) {
//                            sb.append(e + "<br />");
//                        }
//                        sb.append(font_text_close);
//                        y++;
//                        sb.append("<br />");
//                    }
//                    String html = sb.toString();
//
//                    webView.loadDataWithBaseURL("google.com", html, "text/html", "UTF-8", null);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("DebugPony" , e.toString());
                }
            }
        });

        webView = (WebView) findViewById(R.id.webView);

        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });

        webView.loadUrl("http://browser-info.ru/");
    }

    /**
     * Called when the activity will start interacting with the user.
     * At this point your activity is at the top of the activity stack,
     * with user input going to it.
     */

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {


            /**
             * Create the API client and bind it to an instance variable.
             * We use this instance as the callback for connection and connection failures.
             * Since no account name is passed, the user is prompted to choose.
             */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {

            // disconnect Google API client connection
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed:" + result.toString());

        if (!result.hasResolution()) {

            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }

        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */

        try {

            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);

        } catch (IntentSender.SendIntentException e) {

            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    /**
     * It invoked when Google API client connected
     *
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
    }

    /**
     * It invoked when connection suspended
     *
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {

        Log.i(TAG, "GoogleApiClient connection suspended");
    }


    String path1 = "/storage/emulated/0/Android/data/org.linguateacher2/files/doc.mkv";

    String path2 = "/storage/emulated/0/Android/data/org.linguateacher2/files/doc1.srt";
    String path3 = "/storage/emulated/0/Android/data/org.linguateacher2/files/doc2.srt";

    String path4 = "/storage/emulated/0/Android/data/org.linguateacher2/files/doc3.ass";








}