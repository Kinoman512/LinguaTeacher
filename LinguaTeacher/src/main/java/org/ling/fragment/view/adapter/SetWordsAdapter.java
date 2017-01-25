package org.ling.fragment.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import org.ling.MainActivity;
import org.R;;
import org.ling.fragment.VocabularyFragment;
import org.ling.fragment.WebFragment;
import org.ling.model.CommonWord;
import org.ling.model.SetWords;
import org.ling.model.agent.CommonWordAgent;
import org.ling.model.agent.SetWordsAgent;
import org.ling.service.ImageService;
import org.ling.utils.FirstPageFragmentListener;
import org.ling.utils.IAction;
import org.ling.utils.IActionHandler;

/**
 * Created by Dmitry on 30.06.2016.
 */
public class SetWordsAdapter extends BaseAdapter {

    Context mContext;
    List<SetWords> lsw;
    Fragment setWordsFragment;


    public SetWordsAdapter(Context mContext, List<SetWords> lsw, Fragment setWordsFragment) {
        this.mContext = mContext;
        this.lsw = lsw;
        this.setWordsFragment = setWordsFragment;
    }

    @Override
    public int getCount() {
        return lsw.size();
    }

    @Override
    public Object getItem(int position) {
        return lsw.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lsw.get(position).getId();
    }

    @Override
    public View getView(int position, View someView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);


        if (someView == null) {
            someView = inflater.inflate(R.layout.cell_set, parent, false);
        }

        final SetWords sw = lsw.get(position);

        final ImageView icon = (ImageView) someView.findViewById(R.id.mSetWordsIcon);

        if (sw.getImage() == null) {
            List<CommonWord> lcw = CommonWordAgent.findBySetWord(sw.getId());
            if( lcw.size() != 0){
                Random rd = new Random();

                int u1 = rd.nextInt(lcw.size());
                int u2 = rd.nextInt(lcw.size());
                int u3 = rd.nextInt(lcw.size());
                CommonWord cw1 = lcw.get(u1);
                ImageService.getImage(cw1.getImage(), new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        Bitmap bmp = (Bitmap) rs;
                        icon.setImageBitmap(bmp);
                        icon.invalidate();
                        icon.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailAction(String s, Throwable throwable) {
                    }
                });


            }
        }

        LinearLayout back1 = (LinearLayout) someView.findViewById(R.id.mBackSetWords1);
        LinearLayout back2 = (LinearLayout) someView.findViewById(R.id.mBackSetWords2);

        TextView mLang1 = (TextView) someView.findViewById(R.id.mLang1);
        TextView mLang2 = (TextView) someView.findViewById(R.id.mLang2);

        mLang1.setText(sw.getLang());
        mLang2.setText(sw.getNatv());


        TextView name = (TextView) someView.findViewById(R.id.mNameSetWords);
        TextView count = (TextView) someView.findViewById(R.id.mCountSetWords);
        View sep = (View) someView.findViewById(R.id.separatorSet);


        if (sw.getImage() != null) {
//            icon =
        }//setting button's click and implementing the onClick method

        final com.mikepenz.iconics.view.IconicsImageView button = (com.mikepenz.iconics.view.IconicsImageView) someView.findViewById(R.id.mBtnSettingSet);

        if(sw.getBackcolor() > 4){
            button.setColor(Color.WHITE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //переименовать
                //удалить
                //переместить
                //тренировать
                //словарь


                final String[] items = {
                        MainActivity.activity.getResources().getString(R.string.rename),
                        MainActivity.activity.getResources().getString(R.string.remove),
                        MainActivity.activity.getResources().getString(R.string.vocabulary)
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity);
                builder.setTitle(MainActivity.activity.getResources().getString(R.string.select_action) );
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                rename(sw);
                                break;
                            case 1:
                                remove(sw);
                                break;
                            case 2:
                                vocabulary(sw);

                        }
                       // Toast.makeText(MainActivity.activity, items[item],
                        //        Toast.LENGTH_SHORT).show();


                    }
                });
                builder
                        .setCancelable(false)
                        .setNegativeButton( MainActivity.getStringById(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        back1.setBackgroundColor((int) sw.getBackcolor());
        //показать словарь
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putSerializable("Set", sw);
                VocabularyFragment newFragment = new VocabularyFragment();
                newFragment.setArguments(args);
//                MainActivity.activity.startPage(MainActivity.SECOND_PAGE, newFragment);
                MainActivity.switchToNextFragment(newFragment);

//                MainActivity.switchToNextFragment(new WebFragment());


            }
        });

        name.setText(sw.getName());
        count.setText("" + sw.getCount());
        return someView;
    }


    void vocabulary(SetWords sw) {
        Bundle args = new Bundle();
        args.putSerializable("Set", sw);
        VocabularyFragment newFragment = new VocabularyFragment();
        newFragment.setArguments(args);
//                MainActivity.activity.startPage(MainActivity.SECOND_PAGE, newFragment);
        MainActivity.switchToNextFragment(newFragment);
    }

    void remove(final SetWords sw) {
        final List<SetWords> lsw = SetWordsAgent.getAll();

        if(lsw.size() <= 1){
            Toast.makeText(MainActivity.activity,  MainActivity.getStringById(R.string.cant_remove_last_dict) ,
                    Toast.LENGTH_SHORT).show();
        }else{
            new SweetAlertDialog(MainActivity.activity, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(MainActivity.getStringById(R.string.remove_dict))
                    .setContentText(MainActivity.getStringById(R.string.remove_dict2))
                    .setCancelText(MainActivity.getStringById(R.string.no))
                    .setConfirmText(MainActivity.getStringById(R.string.yes))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            SetWordsAgent.remove(sw);
                            lsw.remove(sw);
                            notifyDataSetChanged();
                            setWordsFragment.onResume();
                            Toast.makeText(MainActivity.activity, MainActivity.getStringById(R.string.vocabularyUp)  + " " + sw.getName() + " " + MainActivity.getStringById(R.string.removed) ,
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();



        }

    }

    void rename(SetWords sw) {
        //переименовать
        //показать диалог с 1 полем.
        org.ling.Dialog.showRenameSetWordsDialog(sw, new IAction() {
            @Override
            public void onSucces(Object rs) {
                notifyDataSetChanged();
                setWordsFragment.onResume();
            }
        });
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
