package org.ling.fragment.view.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageView;

import org.R;
import org.ling.MainActivity;
import org.ling.fragment.view.PlaySoundBotton;
import org.ling.fragment.view.PopupMenuBotton;
import org.ling.model.CommonWord;
import org.ling.model.SimpleWord;
import org.ling.model.Stage;
import org.ling.model.agent.StageAgent;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

;

/**
 * Created by Dmitry on 04.07.2016.
 */
public class VocabularyListAdapter extends BaseAdapter {

    Context mContext;
    List<CommonWord> lcw;

    int timeStage = -1;

    public VocabularyListAdapter(Context mContext, List<CommonWord> lcw) {
        this.mContext = mContext;
        Collections.sort(lcw, new Comparator<CommonWord>() {
            @Override
            public int compare(CommonWord o1, CommonWord o2) {
                //The dates are currently sorted in Ascending order

                return (int) (o2.getAdded() - o1.getAdded());

                //To have them in Descending order just switch the DateTimes around
            }
        });
        this.lcw = lcw;


    }


    @Override
    public int getCount() {
        return lcw.size();
    }

    @Override
    public Object getItem(int position) {
        return lcw.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lcw.get(position).getId();
    }

    @Override
    public View getView(int position, View someView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (someView == null) {
            someView = inflater.inflate(R.layout.item_word, parent, false);
            final CommonWord cw = lcw.get(position);


            LinearLayout mCommonWordLayout = (LinearLayout) someView.findViewById(R.id.mCommonWord);
            TextView wordSource = (TextView) someView.findViewById(R.id.wordSource);
            TextView wordDest = (TextView) someView.findViewById(R.id.wordDest);
            TextView mDate = (TextView) someView.findViewById(R.id.mDate);

            TextView dateTxt = (TextView) someView.findViewById(R.id.dateTxt);

            TextView statusStage = (TextView) someView.findViewById(R.id.txt_stage);
            IconicsImageView checkYes = (IconicsImageView) someView.findViewById(R.id.checkYes);

            long nextRepeat = 0;
            if (cw.getStage() > 0) {
                nextRepeat = cw.getNextTrain();
            }
            SimpleDateFormat sf = new SimpleDateFormat("yy-MM-dd'  'HH:mm");
//            SimpleDateFormat sf = new SimpleDateFormat("dd-MM''HH' h ':mm' m'" );
            Date date = new Date(nextRepeat);

            if (nextRepeat != 0) {
                dateTxt.setText(sf.format(date));
            } else {
                dateTxt.setText("");
            }

            List<Stage> list = StageAgent.getAll();
            Stage st = cw.getInsStage();
            int pos = list.indexOf(st);

            if (cw.getStage() > 0) {
                pos += 1;
            } else {
                pos = 0;
            }


            if (pos == list.size()) {
                checkYes.setVisibility(View.VISIBLE);
                statusStage.setVisibility(View.GONE);
            } else {
                statusStage.setText(pos + "/" + list.size());
                checkYes.setVisibility(View.GONE);
                statusStage.setVisibility(View.VISIBLE);
            }


            PopupMenuBotton btn_popup_sound = (org.ling.fragment.view.PopupMenuBotton) someView.findViewById(R.id.btn_popup_sound_front);
            PopupMenuBotton btn_popup_sound2 = (org.ling.fragment.view.PopupMenuBotton) someView.findViewById(R.id.btn_popup_sound_back);
            final PlaySoundBotton psb = (PlaySoundBotton) someView.findViewById(R.id.btn_play_front);
            final PlaySoundBotton psb2 = (PlaySoundBotton) someView.findViewById(R.id.btn_play_back);


            SimpleWord sw1 = cw.getInsSource();
            SimpleWord sw2 = cw.getInsDest();

            btn_popup_sound.setSimpleWord(sw1);
            btn_popup_sound2.setSimpleWord(sw2);

            btn_popup_sound.setPsb(psb);
            btn_popup_sound2.setPsb(psb2);

            psb.setSound(sw1.getCurrentPronounce(), sw1.getLang());
            psb2.setSound(sw2.getCurrentPronounce(), sw2.getLang());


            LinearLayout sectionWord1 = (LinearLayout) someView.findViewById(R.id.sectionWord1);

            //хедер
            LinearLayout sectionHead = (LinearLayout) someView.findViewById(R.id.sectionWord);
            //sectionHead.setVisibility(View.GONE);

            wordSource.setText(cw.getInsSource().getWord());
            wordDest.setText(cw.getInsDest().getWord());


            //создать функцию определяющую разность по времени, если элемент превышает какой-то порог выводится хедер

            int stage = 0;
            String textDate = "";
            long delta = new Date().getTime() - cw.getAdded();
            if (delta < DateUtils.MINUTE_IN_MILLIS * 20) {
                stage = 1;//сейчас
                textDate = MainActivity.activity.getResources().getString(R.string.now);
            } else if (delta < DateUtils.HOUR_IN_MILLIS) {
                stage = 2;// больше 20 минут назад
                textDate = MainActivity.activity.getResources().getString(R.string.m20minutes);
            } else if (delta < DateUtils.DAY_IN_MILLIS) {
                stage = 3; // больше часа назад
                textDate = MainActivity.activity.getResources().getString(R.string.m1hour);
            } else if (delta < DateUtils.WEEK_IN_MILLIS) {
                stage = 4; // больше дня назад
                textDate = MainActivity.activity.getResources().getString(R.string.m1day);
            } else if (delta < DateUtils.DAY_IN_MILLIS * 30) {
                stage = 5;// больше недели назад
                textDate = MainActivity.activity.getResources().getString(R.string.m1week);
            } else if (delta < DateUtils.YEAR_IN_MILLIS) {
                stage = 6;// больше месяца назад/
                textDate = MainActivity.activity.getResources().getString(R.string.m1month);
            } else {
                stage = 7;// больше года/
                textDate = MainActivity.activity.getResources().getString(R.string.m1year);
            }

            if (timeStage < stage) {
                //добавить хедер
                timeStage = stage;
                sectionHead.setVisibility(View.VISIBLE);
                mDate.setText(textDate);
            } else {
                sectionHead.setVisibility(View.GONE);
            }
        }


        return someView;


    }


    @Override
    public int getViewTypeCount() {
        if (getCount() == 0) return 1;
        else {
            return getCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else {
            return position;
        }
    }
}

