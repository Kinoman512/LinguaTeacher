package org.ling.fragment.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import org.ling.MainActivity;
import org.ling.model.SetWords;
import org.ling.utils.IAction;

/**
 * Created by Dmitry on 25.10.2016.
 */
public class SearchWordBtn extends com.mikepenz.iconics.view.IconicsImageView {

    GoogleMaterial.Icon PLAY = GoogleMaterial.Icon.gmd_search;

    LayoutInflater inflater;
    View rootView;
    SetWords currentSet;
    ProgressBar pbLoading;
    EditText editWord;

    public void search() {
        search(String.valueOf(editWord.getText().toString()));
    }

    public void init(LayoutInflater inflater, View rootView, SetWords currentSet, ProgressBar pbLoading) {
        this.inflater = inflater;
        this.rootView = rootView;
        this.currentSet = currentSet;
        this.pbLoading = pbLoading;
    }


    public void search(String word) {


        if(word.isEmpty() || this.inflater == null){
            return;
        }
        setColor(Color.YELLOW);
//        pbLoading.setVisibility(View.VISIBLE);
        MySnackBar mySnackBar = new MySnackBar(MainActivity.activity, inflater, rootView);
        mySnackBar.showTranslatedWord(MainActivity.activity, pbLoading, currentSet, word, new IAction() {
            @Override
            public void onSucces(Object rs) {

//

                MainActivity.activity.hideKeyboard();

                SearchWordBtn.this.setColor(Color.RED);
            }
        });

    }


    public SearchWordBtn(Context context) {
        super(context);
    }

    public SearchWordBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!this.isInEditMode()) {
            setIcon(PLAY);
            setColor(Color.RED);
        }
    }

    public SearchWordBtn(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                search();
                break;

            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public void setEditWord(EditText editWord) {
        this.editWord = editWord;
    }
}