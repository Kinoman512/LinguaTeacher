package org.ling.utils;

import android.app.Activity;
import android.webkit.WebView;

import com.bossturban.webviewmarker.TextSelectionSupport;

/**
 * Created by Dmitry on 26.06.2016.
 */
public class MyTextSelectionSupport extends TextSelectionSupport {


    IAction hideInTouchOut;

    public MyTextSelectionSupport(Activity activity, WebView webview, IAction hideInTouchOut) {
       super(activity,webview);
        super.setup();
        this.hideInTouchOut = hideInTouchOut;
    }



    public void endSelectionMode() {

        hideInTouchOut.onSucces("");
        super.endSelectionMode();
    }
}
