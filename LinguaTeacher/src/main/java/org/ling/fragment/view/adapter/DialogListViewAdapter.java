package org.ling.fragment.view.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import org.R;;

/**
 * Created by Dmitry on 06.08.2016.
 */
public class DialogListViewAdapter extends BaseAdapter {

    Context mContext;
    List<String> list;
    List<Drawable> listBmp;


    public DialogListViewAdapter(Context mContext, List<String> list , List<Drawable> listBmp) {
        this.mContext = mContext;
        this.list = list;
        if(listBmp == null){
            this.listBmp =  new ArrayList<>();
        }else{
            this.listBmp = listBmp;
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View someView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (someView == null) {
            someView = inflater.inflate(R.layout.item_listdialog, parent, false);
            ImageView icon = (ImageView) someView.findViewById(R.id.iconItem);
            TextView text = (TextView) someView.findViewById(R.id.textItem);

            text.setText(list.get(position));

            if(listBmp.size() > position){
                Drawable bmp = listBmp.get(position);
                if(bmp != null){
                    icon.setImageDrawable(bmp);
                }else{
                    icon.setVisibility(View.INVISIBLE);
                }
            }
        }
        return someView;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}