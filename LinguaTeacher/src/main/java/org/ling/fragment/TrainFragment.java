package org.ling.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.R;
import org.ling.MainActivity;
import org.ling.MyApplication;
import org.ling.Setting;
import org.ling.fragment.view.Card;
import org.ling.fragment.view.CardGroup;
import org.ling.model.CommonWord;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

;

/**
 * Created by Dmitry on 09.07.2016.
 */
public class TrainFragment extends Fragment {
    View rootView;
    Context context;
    List<CommonWord> lcw = new ArrayList<>();
    static String TIP_TAG = "TrainFragmentTip2";
    public CardGroup cardGroup;
    private Button tw_btn;


    @Override
    public void onResume() {
        if (MainActivity.activity.getCurrentTab() == 0) {
            tip();
        }
        super.onResume();
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cardtrain, container, false);
        this.context = inflater.getContext();
        MyApplication.loadInterstitial();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("Words")) {
            lcw = (List<CommonWord>) bundle.getSerializable("Words");
        } else if (bundle == null) {
//            Toast.makeText(getActivity(), "Error Нет Words", Toast.LENGTH_LONG).show();
        }
        cardGroup = (CardGroup) rootView.findViewById(R.id.tagLayout);
        tw_btn = (Button) rootView.findViewById(R.id.tw_btn);
        tw_btn.setOnClickListener(viewClickListener);

        for (int i = 0; i < lcw.size(); i++) {
            Card card = new Card(context, lcw.get(i));
            cardGroup.addView(card);
        }

         /*
            0 цикл
            1) скрыть картинку
            2) Перевернуть
            3) Пропуск

             */

        final boolean bl1 = Setting.getBool("poptw1");
        final boolean bl2 = Setting.getBool("poptw2");
        final boolean bl3 = Setting.getBool("poptw3");

        if(bl2 ){
            cardGroup.changeFaceCards(bl2);
        }


        cardGroup.goneImages(bl1);

        cardGroup.ignoreMistakes(bl3);


        return rootView;
    }

    ;


    void tip() {

        if (Setting.getBool(TIP_TAG)) {
            return;
        }
        final SweetAlertDialog sld2 = new SweetAlertDialog(MainActivity.activity)
                .setTitleText(MainActivity.activity.getResources().getString(R.string.tip))
                .setContentText(MainActivity.activity.getResources().getString(R.string.tip_train1))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        Setting.setBool(TIP_TAG, true);

                    }
                });
        new SweetAlertDialog(MainActivity.activity)
                .setTitleText(MainActivity.activity.getResources().getString(R.string.tip))
                .setContentText(MainActivity.activity.getResources().getString(R.string.tip_train2))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        sld2.show();
                    }
                })
                .show();
    }

    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }
    };

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), v);
        popupMenu.inflate(R.menu.train_words_menu); // Для Android 4.0


        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            final boolean bl = Setting.getBool("poptw" + i);
            popupMenu.getMenu().getItem(i).setChecked(bl);
            /*
            1) цикл
            2) скрыть картинку
            3) Перевернуть
            4) Пропуск

             */

            final int finalI = i;
            popupMenu.getMenu().getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    boolean bl = !Setting.getBool("poptw" + finalI);
                    switch (finalI) {
                        case 0:
                            cardGroup.playAudioAtCircle(bl);
                            Setting.setBool("poptw" + finalI, bl);
                            break;
                        case 1:
                            cardGroup.goneImages(bl);
                            Setting.setBool("poptw" + finalI, bl);
                            break;
                        case 2:
                            cardGroup.changeFaceCards(bl);
                            Setting.setBool("poptw" + finalI, bl);
                            break;
                        case 3:
                            cardGroup.ignoreMistakes(bl);
                            Setting.setBool("poptw" + finalI, bl);
                            break;


                    }
                    return false;
                }
            });


        }



        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
                // Toast.makeText(TrainFragment.this.getContext(), "onDismiss",
                // Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.show();
    }


    public void backPressed() {
        MainActivity.switchToNextFragment(new TrainRoomFragment());
    }
}
