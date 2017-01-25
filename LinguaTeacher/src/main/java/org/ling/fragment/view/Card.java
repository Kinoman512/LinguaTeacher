package org.ling.fragment.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import org.R;;
import org.ling.model.CommonWord;
import org.ling.utils.TrainUnit;

/**
 * Created by Dmitry on 10.07.2016.
 */
public class Card extends RelativeLayout {

    LayoutInflater mInflater;
    TrainUnit tu;

    public Card(Context context, CommonWord cw) {
        super(context);
        this.tu = new TrainUnit();
        tu.setCommonWord(cw);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public Card(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public Card(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public Card(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public TrainUnit getTu() {
        return tu;
    }

    public void setTu(TrainUnit tu) {
        this.tu = tu;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Card(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public void init() {
        View v = mInflater.inflate(R.layout.view_card, null, true);


        final View back = (View) v.findViewById(R.id.card_viewback);
        back.setVisibility(GONE);



        addView(v);
    }
}
