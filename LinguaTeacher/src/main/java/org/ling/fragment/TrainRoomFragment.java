package org.ling.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.R;
import org.ling.Dialog;
import org.ling.MainActivity;
import org.ling.Setting;
import org.ling.model.CommonWord;
import org.ling.model.SetWords;
import org.ling.model.agent.CommonWordAgent;
import org.ling.model.agent.SetWordsAgent;
import org.ling.model.iType;
import org.ling.utils.FlagsUtils;
import org.ling.utils.IActionHandler;
import org.ling.utils.Lang;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import odyssey.ru.linglibrary.Util;

;

/**
 * Created by Dmitry on 16.07.2016.
 */
public class TrainRoomFragment extends Fragment {

    private static final String LIST_TAG = "LIST_TraimRoomLearn_Tag";
    View rootView;
    Context context;
    ListView stages;
    static iType currentSet;
    static int x = 0;
    static String TIP_TAG = "TraimRoomLearn";


    TextView tr_stat_text;
    TextView tr_new_text;
    TextView tr_wait_text;
    TextView tr_prepare_text;
    TextView tr_finish_text;
    TextView tr_nexttime_text;

    TextView tr_new_count;
    TextView tr_wait_count;
    TextView tr_prepare_count;
    TextView tr_finish_count;
    private static int LIMIT = 9;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onResume() {
        if (MainActivity.activity.getCurrentTab() == 0) {
            tip();
        }
        start();
        super.onResume();
    }

    void calculateStats(List<CommonWord> lcw) {
        int newWord = 0;
        int prepareRepeate = 0;
        int waitRepeate = 0;
        int fineshedWord = 0;
        long nextRepeat = 0;

        for (CommonWord cw : lcw) {
            if (cw.getStage() == 0) newWord++;
            if (cw.getStage() == -1) fineshedWord++;
            if (cw.getStage() > 0) {
                if (nextRepeat == 0 || nextRepeat < cw.getNextTrain())
                    nextRepeat = cw.getNextTrain();
                if (cw.getNextTrain() < new Date().getTime()) {
                    prepareRepeate++;
                } else {
                    waitRepeate++;
                }
            }
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'   'HH:mm:ss");

        String nextTrain = getResources().getString(R.string.missing_text);

        Date date = new Date(nextRepeat);
        if (nextRepeat != 0)
            nextTrain = sf.format(date);


        tr_new_count.setText("" + newWord);
        tr_wait_count.setText("" + waitRepeate);
        tr_prepare_count.setText("" + prepareRepeate);
        tr_finish_count.setText("" + fineshedWord);

        tr_nexttime_text.setText(getResources().getString(R.string.next_repeat_text) + " " + nextTrain);


    }

    public static List<CommonWord> getListCWBySet() {
        List<CommonWord> lcw = new ArrayList<>();
        switch (currentSet.getType()) {
            case 2:
                lcw = CommonWordAgent.getAll();
                break;
            case 1:
                lcw = new ArrayList<>();
                Lang lang = (Lang) currentSet;

                List<SetWords> lsw = SetWordsAgent.findByLang(lang.getCodeLingua());
                for (SetWords e : lsw) {
                    lcw.addAll(CommonWordAgent.findBySetWord(e.getId()));
                }
                break;
            case 0:
                lcw = new ArrayList<>();
                SetWords sw = (SetWords) currentSet;

                lcw.addAll(CommonWordAgent.findBySetWord(sw.getId()));
                break;
        }
        return lcw;
    }


    void updateStats() {
        List<CommonWord> lcw = getListCWBySet();
        calculateStats(lcw);
    }


    void start() {
        final List<iType> mSet = new ArrayList<>();

//        final Button btn_train_new = (Button) rootView.findViewById(R.id.btn_train_new);
//        final Button btn_train_old = (Button) rootView.findViewById(R.id.btn_train_old);
        final Button btn_train_all = (Button) rootView.findViewById(R.id.btn_train_all);

        final RelativeLayout listSetWords = (RelativeLayout) rootView.findViewById(R.id.mSetWords3);
        final LinearLayout TrainViewLayout = (LinearLayout) rootView.findViewById(R.id.TrainViewLayout);

        final TextView mSetName = (TextView) rootView.findViewById(R.id.mSetName);

        tr_stat_text = (TextView) rootView.findViewById(R.id.tr_stat_text);
        tr_new_text = (TextView) rootView.findViewById(R.id.tr_new_text);
        tr_wait_text = (TextView) rootView.findViewById(R.id.tr_wait_text);
        tr_prepare_text = (TextView) rootView.findViewById(R.id.tr_prepare_text);
        tr_finish_text = (TextView) rootView.findViewById(R.id.tr_finish_text);
        tr_nexttime_text = (TextView) rootView.findViewById(R.id.tr_nexttime_text);

        tr_new_count = (TextView) rootView.findViewById(R.id.text_new_count);
        tr_wait_count = (TextView) rootView.findViewById(R.id.text_wait_count);
        tr_prepare_count = (TextView) rootView.findViewById(R.id.text_prepare_count);
        tr_finish_count = (TextView) rootView.findViewById(R.id.text_end_count);


        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        TrainViewLayout.measure(0, 0);
        int u = TrainViewLayout.getMeasuredWidth();
        if (displaymetrics.widthPixels < TrainViewLayout.getMeasuredWidth()) {
            TrainViewLayout.setOrientation(LinearLayout.VERTICAL);
        }


//        btn_train_new.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<CommonWord> lcw = getListCWBySet().subList(0,LIMIT);
//                List<CommonWord> lcwnews = new ArrayList<CommonWord>();
//                for (CommonWord cw : lcw) {
//                    if (cw.getStage() == 0) {
//                        lcwnews.add(cw);
//                    }
//                }
//                if (lcwnews.size() == 0) {
//                    Toast.makeText(context, R.string.no_words_for_workout_text, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Bundle args = new Bundle();
//                ArrayList<CommonWord> list1 = (ArrayList) lcwnews;
//                args.putSerializable("Words", list1);
//                TrainFragment newFragment = new TrainFragment();
//                newFragment.setArguments(args);
//                MainActivity.switchToNextFragment(newFragment);
//
//            }
//        });
//
//        btn_train_old.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<CommonWord> lcw = getListCWBySet().subList(0, LIMIT);;
//                List<CommonWord> lcwnews = new ArrayList<CommonWord>();
//                long now = new Date().getTime();
//                for (CommonWord cw : lcw) {
//                    if (cw.getStage() > 0 && cw.getNextTrain() < now) {
//                        lcwnews.add(cw);
//                    }
//                }
//                if (lcwnews.size() == 0) {
//                    Toast.makeText(context,  R.string.no_words_for_workout_text, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Bundle args = new Bundle();
//                ArrayList<CommonWord> list1 = (ArrayList) lcwnews;
//                args.putSerializable("Words", list1);
//                TrainFragment newFragment = new TrainFragment();
//                newFragment.setArguments(args);
////                firstPageListener.onSwitchToNextFragment(newFragment);
//                MainActivity.switchToNextFragment(newFragment);
//            }
//        });

        btn_train_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainWords();
            }
        });


        mSet.add(new iType() {
            @Override
            public String getName() {
                return getResources().getString(R.string.all_set_text);
            }

            @Override
            public int getType() {
                return 2;
            }
        });


        List<String> listLangs = new ArrayList<>();

        List<SetWords> lsw = SetWordsAgent.getAll();

        for (SetWords e : lsw) {
            if (!listLangs.contains(e.getLang())) {
                String lang = e.getLang();
                Lang mLang = Lang.findByCodeCountry(lang);
//                String str = mLang.findByCodeLingua();
                String name = mLang.getCodeCountry();
                listLangs.add(name);
            }
        }

        for (final String e : listLangs) {
            mSet.add(Lang.findByCodeCountry(e));
        }

        List<Drawable> listTemp = FlagsUtils.getFlagsByCode(listLangs);
        final List<Drawable> listDrawable = new ArrayList<>();
        Drawable img = new IconicsDrawable(MainActivity.activity, CommunityMaterial.Icon.cmd_animation).color(Color.LTGRAY);
        Drawable img2 = new IconicsDrawable(MainActivity.activity, CommunityMaterial.Icon.cmd_bookmark).color(Color.LTGRAY);

        listDrawable.add(img);
        listDrawable.addAll(listTemp);


        for (final SetWords e : lsw) {
            listDrawable.add(img2);
            mSet.add(e);
        }

        final List<String> listFields = new ArrayList<>();
        for (final iType e : mSet) {
            listFields.add(e.getName());
        }


        listSetWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.setActivity(MainActivity.activity);
                Dialog.showListDialog(getResources().getString(R.string.choose_lang_text), listFields, listDrawable, new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        int pos = (Integer) rs;
                        Setting.setInt(LIST_TAG, pos);
                        currentSet = mSet.get(pos);
                        mSetName.setText(currentSet.getName());
                        updateStats();
                    }

                    @Override
                    public void onFailAction(String s, Throwable throwable) {
                    }
                });
            }
        });

        int pos = Setting.getInt(LIST_TAG);
        if (pos >= mSet.size()) {
            pos = 0;
        }
        currentSet = mSet.get(pos);
        mSetName.setText(currentSet.getName());
        updateStats();
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_trainroom, container, false);
        this.context = inflater.getContext();


        return rootView;
    }

    void tip() {

        if (Setting.getBool(TIP_TAG)) {
            return;
        }


        final SweetAlertDialog sld3 = new SweetAlertDialog(MainActivity.activity)
                .setTitleText(getResources().getString(R.string.tip))
                .setContentText(getResources().getString(R.string.tip_train_room3))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        Setting.setBool(TIP_TAG, true);
                    }
                });


        final SweetAlertDialog sld2 = new SweetAlertDialog(MainActivity.activity)
                .setTitleText(getResources().getString(R.string.tip))
                .setContentText(getResources().getString(R.string.tip_train_room2))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        sld3.show();

                    }
                });
        new SweetAlertDialog(MainActivity.activity)
                .setTitleText(getResources().getString(R.string.tip))
                .setContentText(getResources().getString(R.string.tip_train_room1))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        sld2.show();
                    }
                })
                .show();
    }


    public static void trainWords() {
        List<CommonWord> temp = getListCWBySet();


        ArrayList<CommonWord> lcwnews = new ArrayList<CommonWord>();

        for (CommonWord cw : temp) {
            if (cw.getNextTrain() < new Date().getTime() && cw.getStage() >= 0) {
                //lcwnews.add(cw);
                if (lcwnews.size() > LIMIT) {
                    break;
                }
                lcwnews.add(cw);
            }
        }

        if (lcwnews.size() == 0) {


            String msg = Util.getStringById(R.string.no_words_for_workout_text);
            Util.showMassage(msg);
            return;
        }
        Bundle args = new Bundle();
        args.putSerializable("Words", lcwnews);
        TrainFragment newFragment = new TrainFragment();
        newFragment.setArguments(args);
        MainActivity.switchToNextFragment(newFragment);
    }


}