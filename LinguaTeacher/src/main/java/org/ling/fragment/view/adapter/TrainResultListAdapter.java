package org.ling.fragment.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

import org.R;;
import org.ling.fragment.FragmentTrainResult;
import org.ling.model.CommonWord;
import org.ling.model.Pronounce;
import org.ling.service.SoundService;
import org.ling.utils.IAction;
import org.ling.utils.TrainUnit;

/**
 * Created by Dmitry on 16.07.2016.
 */
public class TrainResultListAdapter extends BaseAdapter {

    Context mContext;
    List<TrainUnit> ltu;

    int timeStage = -1;


    public TrainResultListAdapter(Context mContext, List<TrainUnit> ltu) {
        this.mContext = mContext;
        this.ltu = ltu;


    }
    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return ltu.size();
    }

    @Override
    public Object getItem(int position) {
        return ltu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    CommunityMaterial.Icon CHECK = CommunityMaterial.Icon.cmd_check;
    CommunityMaterial.Icon CLOSE = CommunityMaterial.Icon.cmd_close;

    @Override
    public View getView(int position, View someView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (someView == null) {
            someView = inflater.inflate(R.layout.item_word_trainres, parent, false);
            TrainUnit tu = ltu.get(position);
            CommonWord cw = tu.getCommonWord();

            IconicsImageView resImage = (IconicsImageView) someView.findViewById(R.id.resWord);
            TextView word = (TextView) someView.findViewById(R.id.word);
            TextView middleTime = (TextView) someView.findViewById(R.id.middleTime);

            word.setText(cw.getInsSource().getWord());
            middleTime.setText(tu.getMiddleTime() + "");

            if(tu.getMistakes() > 1){
                resImage.setIcon(CLOSE);
                resImage.setColor(Color.RED);
            }else{
                resImage.setIcon(CHECK);
                resImage.setColor(Color.GREEN);
            }

        }
        return someView;
    }
}