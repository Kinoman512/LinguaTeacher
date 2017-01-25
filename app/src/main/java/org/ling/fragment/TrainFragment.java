package org.ling.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import odyssey.ru.linguateacher.MainActivity;
import odyssey.ru.linguateacher.R;
import odyssey.ru.linguateacher.Setting;
import org.ling.fragment.view.Card;
import org.ling.fragment.view.CardGroup;
import org.ling.model.CommonWord;
import org.ling.model.SetWords;
import org.ling.utils.FirstPageFragmentListener;

/**
 * Created by Dmitry on 09.07.2016.
 */
public class TrainFragment extends Fragment {
    View rootView;
    Context context;
    List<CommonWord>  lcw = new ArrayList<>();
    static String TIP_TAG = "TrainFragmentTip2";

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

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("Words")) {
            lcw = (List<CommonWord>)  bundle.getSerializable("Words");
        } else if (bundle == null) {
//            Toast.makeText(getActivity(), "Error Нет Words", Toast.LENGTH_LONG).show();
        }


        CardGroup tagLayout = (CardGroup) rootView.findViewById(R.id.tagLayout);
        for (int i = 0; i < lcw.size(); i++) {
            Card card =  new Card(context, lcw.get(i));
            tagLayout.addView(card);
        }


        return  rootView;
    };


    void tip(){

        if(Setting.getBool(TIP_TAG)){
            return;
        }
        final SweetAlertDialog sld2 = new SweetAlertDialog(MainActivity.activity)
                .setTitleText( MainActivity.activity.getResources().getString(R.string.tip))
                .setContentText(MainActivity.activity.getResources().getString(R.string.tip_train1) )
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





    public void backPressed() {
        MainActivity.switchToNextFragment( new TrainRoomFragment());
    }
}
