package org.ling;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.R;
import org.ling.fragment.view.MySnackBar;
import org.ling.fragment.view.SearchWordBtn;
import org.ling.fragment.view.adapter.DialogListViewAdapter;
import org.ling.model.SetWords;
import org.ling.model.Video;
import org.ling.model.agent.SetWordsAgent;
import org.ling.model.agent.SetWordsService;
import org.ling.model.agent.WordManager;
import org.ling.utils.FlagsUtils;
import org.ling.utils.IAction;
import org.ling.utils.IActionHandler;
import org.ling.utils.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import odyssey.ru.linglibrary.Util;

/**
 * Created by Dmitry on 01.07.2016.
 */
public class Dialog {

    private static Context mActivity = MainActivity.activity;
    static LayoutInflater inflater;
    static  View rootView;

    static Lang mlang1 = Lang.findByCodeCountry("ru");
    static Lang mlang2 = Lang.findByCodeCountry("en");

    public  static void  setActivity(Context activity){
        mActivity = activity;
    }

    public static void showCreateWordDialog(final SetWords sw, LayoutInflater inflater, View rootView , final IAction act) {
        Dialog.inflater = inflater;
        Dialog.rootView = rootView;
        showCreateWordDialog("", sw, act);
    }

    public static void showCreateWordDialog( String text, final SetWords sw, final IAction act) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View promptView = layoutInflater.inflate(R.layout.dialog_newword, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.mWordInp);
        final EditText editText2 = (EditText) promptView.findViewById(R.id.mWordInp2);
        final TextView mSetWord = (TextView) promptView.findViewById(R.id.mSetWord);
        final ProgressBar pbDialog = (ProgressBar) promptView.findViewById(R.id.pbDialog);

        pbDialog.setVisibility(View.INVISIBLE);
        editText.setText(text);
        editText.setHint(sw.getLang());
        editText2.setHint(sw.getNatv());

        String mSet = mSetWord.getText().toString();
        mSetWord.setText(mSet + " " + sw.getName());
        final SearchWordBtn btn = (SearchWordBtn) promptView.findViewById(R.id.btnSearchWord);
        btn.init(layoutInflater, promptView, sw, pbDialog);
        btn.setEditWord(editText);
        alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if ((keyCode != KeyEvent.KEYCODE_BACK)) {
                    return false;
                }
                if (MySnackBar.hideIfshow()) {
                    return false;
                }
                return  true;
            }
        });


        alertDialogBuilder.setCancelable(false)
                .setPositiveButton( Util.getStringById(R.string.create), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        WordManager wm = new WordManager();
                        String s = String.valueOf(editText.getText());
                        String d = String.valueOf(editText2.getText());
                        String lang1 = sw.getLang();
                        String lang2 = sw.getNatv();
                        wm.createCommonWord(s, d, "", lang1, lang2, sw.getId(), act, null);


                    }
                })
                .setNegativeButton(Util.getStringById(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    public static void showListDialog(String header, List<String> list, List<Drawable> listBmp, final IActionHandler handler) {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View promptView = layoutInflater.inflate(R.layout.dialog_selectlist, null);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptView);


        final TextView headTV = (TextView) promptView.findViewById(R.id.mHeader);
        final ListView lv = (ListView) promptView.findViewById(R.id.mDialogList);

        headTV.setText(header);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.activity, android.R.layout.simple_list_item_1, list);

        DialogListViewAdapter adapter = new DialogListViewAdapter(mActivity,list,listBmp);

        alertDialogBuilder.setCancelable(false)
                .setNegativeButton( Util.getStringById(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        final AlertDialog alert = alertDialogBuilder.create();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                handler.onSuccessAction(position);
                alert.cancel();
            }

        });


        alert.show();
    }


    public static void showRenameSetWordsDialog(final SetWords sw, final IAction act) {

        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View promptView = layoutInflater.inflate(R.layout.dialog_renameset, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptView);
        final EditText nameSetText = (EditText) promptView.findViewById(R.id.etNameSet);



        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(Util.getStringById(R.string.rename) , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                })
                .setNegativeButton(Util.getStringById(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();

        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name = nameSetText.getText().toString();

                if (name.isEmpty()) {
                    Util.showMassage(R.string.vocabulary_should_have_name);

                    return;
                }

                List<SetWords> lsw = SetWordsAgent.getAll();
                lsw.remove(sw);

                for (SetWords e : lsw) {
                    if (e.getName().equals(name)) {

                        Util.showMassage(R.string.voc_with_exists_name);
                        return;
                    }
                }


                sw.setName(name);
                sw.save();
                Util.showMassage(R.string.voc_is_renamed);
                alert.dismiss();
                if (act != null) act.onSucces("");
            }
        });
    }


    public static void showCreateSetWordsDialog(final IActionHandler handler) {


        Locale natv = Locale.getDefault();
        Lang natvLang = null;


        Lang.getDefault();
        for (Lang lang : Lang.getAllLocale()) {

            String str5 = natv.getDisplayName();
            String[] arr = str5.split(" ");
            String lang1 = arr[0];
            String lang2 = lang.getLocale().getDisplayName();

            if (lang1.equals( lang2) && !lang1.isEmpty()&& !lang2.isEmpty()) {
                natvLang = lang;
            }
        }

        if (natv.getDisplayLanguage().equals(Lang.stringToLocale("en").getDisplayLanguage()) || natvLang == null) {


            mlang1 = Lang.findByCodeCountry("en");
            mlang2 = Lang.findByCodeCountry("es");
        } else {
            mlang1 = Lang.findByCodeCountry(natvLang.getCodeLingua());
            mlang2 = Lang.findByCodeCountry("en");
        }





        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View promptView = layoutInflater.inflate(R.layout.dialog_newsetwords, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptView);

        final EditText nameSetText = (EditText) promptView.findViewById(R.id.etNameSet);

        final RelativeLayout lang1 = (RelativeLayout) promptView.findViewById(R.id.groupSetWords2);
        final RelativeLayout lang2 = (RelativeLayout) promptView.findViewById(R.id.groupSetWords3);

        final TextView tvGroup2 = (TextView) promptView.findViewById(R.id.tvGroup2);
        final TextView tvGroup3 = (TextView) promptView.findViewById(R.id.tvGroup3);


        tvGroup2.setText(mlang1.getDescription());
        tvGroup3.setText(mlang2.getDescription());

        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> listLangCode = new ArrayList<String>();

        final List<Lang> arr = Lang.getAllLocale();
        for (int i = 0; i < arr.size(); i++) {
            String str = arr.get(i).getCodeCountry();
            String str2 = arr.get(i).getDescription();
            listLangCode.add(str);
            list.add(str2);

        }
        final List<Drawable> listDrawable  = FlagsUtils.getFlagsByCode(listLangCode);


        lang1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dsl = new Dialog();
                dsl.showListDialog(Util.getStringById(R.string.choose_yor_ling), list, listDrawable, new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        int pos = (int) rs;
                        mlang1 = arr.get(pos);
                        tvGroup2.setText(list.get(pos));
                    }
                    @Override
                    public void onFailAction(String s, Throwable throwable) {
                    }
                });
            }
        });

        lang2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dsl = new Dialog();

                dsl.showListDialog(Util.getStringById(R.string.choose_learning_ling) , list, listDrawable, new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        int pos = (int) rs;
                        mlang2 = arr.get(pos);
                        tvGroup3.setText(list.get(pos));
                    }

                    @Override
                    public void onFailAction(String s, Throwable throwable) {
                    }
                });
            }
        });


        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(Util.getStringById(R.string.create), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                })
                .setNegativeButton(Util.getStringById(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();

        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mlang1.getCodeCountry() == mlang2.getCodeCountry()) {
//                    Toast.makeText(mActivity, "Языки дожны быть разные " + nameSetText.getText(),
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }

                String name = nameSetText.getText().toString();

                if (name.isEmpty()) {
                    Util.showMassage(R.string.vocabulary_should_have_name);

                    return;
                }

                SetWordsService sws = new SetWordsService();
                sws.createSetWords(name, mlang1.getCodeLingua(), mlang2.getCodeLingua());


                Util.showMassage(Util.getStringById(R.string.create_vocabulary) + " " +  nameSetText.getText());


                handler.onSuccessAction("");
                alert.dismiss();
            }
        });
    }

    public static void showCreateVideo(final IAction act) {

        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View promptView = layoutInflater.inflate(R.layout.dialog_create_video, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptView);
        final EditText nameSetText = (EditText) promptView.findViewById(R.id.etNameSet);



        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(Util.getStringById(R.string.create) , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                })
                .setNegativeButton( Util.getStringById(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();

        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name = nameSetText.getText().toString();

                if (name.isEmpty()) {
                    Util.showMassage(R.string.video_should_have_name);

                    return;
                }

                Video vid = new Video();
                vid.setName(name);
                vid.save();

                Util.showMassage(R.string.video_rec_saved);


                alert.dismiss();
                if (act != null) act.onSucces("");
            }
        });
    }
}
