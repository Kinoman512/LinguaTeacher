package org.ling.utils;

import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.R;

/**
 * Created by User on 17.01.2017.
 */

class StyleCallback implements ActionMode.Callback {

    private static final String TAG = "StyleCallback";

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        Log.d(TAG, "onCreateActionMode");
        MenuInflater inflater = mode.getMenuInflater();
        //inflater.inflate(R.menu.style, menu);
        menu.removeItem(android.R.id.selectAll);
        return true;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }



    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Log.d(TAG, String.format("onActionItemClicked item=%s/%d", item.toString(), item.getItemId()));
        CharacterStyle cs;

      //  int start = bodyView.getSelectionStart();
       // int end = bodyView.getSelectionEnd();
     //   SpannableStringBuilder ssb = new SpannableStringBuilder(bodyView.getText());


        return false;
    }

    public void onDestroyActionMode(ActionMode mode) {
    }
}