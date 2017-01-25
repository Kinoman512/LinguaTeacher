package org.ling.fragment.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.plumillonforge.android.chipview.ChipViewAdapter;

import org.R;;
import org.ling.Tag;

/**
 * Created by Dmitry on 06.07.2016.
 */
public class MyChipAdapter extends ChipViewAdapter {
    public MyChipAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutRes(int position) {


        return R.layout.view_tokenword;


    }


    @Override
    public int getBackgroundColorSelected(int position) {
        return R.color.red;
    }

    @Override
    public int getBackgroundRes(int position) {
        return  R.color.orange;
    }

    @Override
    public int getBackgroundColor(int position) {
        return 0;
    }

    @Override
    public void onLayout(View view, int position) {
        Tag tag = (Tag) getChip(position);

        TextView word = (TextView) view.findViewById(R.id.word_chip);
        word.setText(tag.getmWord());

        TextView trans = (TextView) view.findViewById(R.id.trans);
        trans.setText(tag.getmTrans());

    }
}