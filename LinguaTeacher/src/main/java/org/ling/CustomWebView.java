package org.ling;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by Dmitry on 20.07.2016.
 */
public class CustomWebView extends WebView {
    private Context context;



    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CustomWebView(Context context) {

        super(context);
        init(context);

    }
    protected void init(Context context){
        this.context = context;
        if(!this.isInEditMode()) {
            WebSettings webviewSettings = getSettings();
            webviewSettings.setJavaScriptEnabled(true);
            // add JavaScript interface for copy
            addJavascriptInterface(new WebAppInterface(context), "JSInterface");
        }


    }


    public  void getSelectedData(){

        String js= "(function getSelectedText() {"+
                "var txt;"+
                "if (window.getSelection) {"+
                "txt = window.getSelection().toString();"+
                "} else if (window.document.getSelection) {"+
                "txt = window.document.getSelection().toString();"+
                "} else if (window.document.selection) {"+
                "txt = window.document.selection.createRange().text;"+
                "}"+
                "JSInterface.getText(txt);"+
                "})()";
        // calling the js function
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:"+js, null);
        }else{
            loadUrl("javascript:"+js);
        }
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void getText(final String text) {
            // put selected text into clipdata
            ClipboardManager clipboard = (ClipboardManager)
                    mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("simple text", text);
            clipboard.setPrimaryClip(clip);
            // gives the toast for selected text
            MainActivity.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.onSelectedText(text);
                }
            });

        }
    }
    //        CustomActionModeCallback mActionModeCallback = new CustomActionModeCallback();
//        return parent.startActionModeForChild(this, mActionModeCallback);
//    }
//
//    private class CustomActionModeCallback implements ActionMode.Callback {
//
//
//        @Override
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            return false;
//        }
//
//        @Override
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            return false;
//        }
//
//        @Override
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            return false;
//        }
//
//        @Override
//        public void onDestroyActionMode(ActionMode mode) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                clearFocus();
//            } else {
//                if (mSelectActionModeCallback != null) {
//                    mSelectActionModeCallback.onDestroyActionMode(mode);
//                }
//                mActionMode = null;
//            }
//
//        }
//    }
//
//    private void getSelectedData() {
//
//        String js = "(function getSelectedText() {" +
//                "var txt;" +
//                "if (window.getSelection) {" +
//                "txt = window.getSelection().toString();" +
//                "} else if (window.document.getSelection) {" +
//                "txt = window.document.getSelection().toString();" +
//                "} else if (window.document.selection) {" +
//                "txt = window.document.selection.createRange().text;" +
//                "}" +
//                "JSInterface.getText(txt);" +
//                "})()";
//        // calling the js function
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            evaluateJavascript("javascript:" + js, null);
//        } else {
//            loadUrl("javascript:" + js);
//        }
//    }
//
//    private class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {
//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//            if (mActionMode != null) {
//                mActionMode.finish();
//                return true;
//            }
//            return false;
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // Send the event to our gesture detector
//        // If it is implemented, there will be a return value
////        if (mDetector != null)
////            mDetector.onTouchEvent(event);
////        switch (event.getAction()) {
////            case MotionEvent.ACTION_UP:
//////                getSelectedData();
////                break;
////        }
//
//        // If the detected gesture is unimplemented, send it to the superclass
//        return super.onTouchEvent(event);
////        return false;
//    }
//
//    private class WebAppInterface {
//        Context mContext;
//
//        WebAppInterface(Context c) {
//            mContext = c;
//        }
//
//        @JavascriptInterface
//        public void getText(String text) {
//            // put selected text into clipdata
//            ClipboardManager clipboard = (ClipboardManager)
//                    mContext.getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData clip = ClipData.newPlainText("simple text", text);
//            clipboard.setPrimaryClip(clip);
////             gives the toast for selected text
//            Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
//        }
//    }
}
