package odyssey.ru.linguateacher;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import java.util.ArrayList;
import java.util.List;

import org.ling.fragment.view.adapter.DialogListViewAdapter;
import org.ling.model.SetWords;
import org.ling.model.agent.SetWordsAgent;
import org.ling.model.agent.SetWordsService;
import org.ling.model.agent.WordManager;
import org.ling.utils.FlagsUtils;
import org.ling.utils.IAction;
import org.ling.utils.IActionHandler;
import org.ling.utils.Lang;

/**
 * Created by Dmitry on 01.07.2016.
 */
public class Dialog {

    private static  AppCompatActivity mActivity = MainActivity.activity;

    static Lang mlang1 = Lang.findByCodeCountry("ru");
    static Lang mlang2 = Lang.findByCodeCountry("en");
    
    public  static void  setActivity(AppCompatActivity activity){
        mActivity = activity;
    }

    public static void showCreateWordDialog(final SetWords sw, final IAction act) {
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

        editText.setText(text);
        editText.setHint(sw.getLang());
        editText2.setHint(sw.getNatv());
        String mSet = mSetWord.getText().toString();
        mSetWord.setText(mSet + " " + sw.getName());

        final com.mikepenz.iconics.view.IconicsImageView btn = (com.mikepenz.iconics.view.IconicsImageView) promptView.findViewById(R.id.btnSearchWord);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setIcon(GoogleMaterial.Icon.gmd_pause);

            }
        });
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Создать", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        WordManager wm = new WordManager();
                        String s = String.valueOf(editText.getText());
                        String d = String.valueOf(editText2.getText());
                        String lang1 = sw.getLang();
                        String lang2 = sw.getNatv();
                        wm.createCommonWord(s, d, "", lang1, lang2, sw.getId(), act, null);


                    }
                })
                .setNegativeButton("Отмена",
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
                .setNegativeButton("Cancel",
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
                .setPositiveButton("Переименовать", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                })
                .setNegativeButton("Отмена",
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
                    Toast.makeText(mActivity, " Набор должен иметь имя ",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                List<SetWords> lsw = SetWordsAgent.getAll();
                lsw.remove(sw);

                for(SetWords e: lsw){
                    if(e.getName().equals(name)){
                        Toast.makeText(mActivity, "Набор с таким названием уже существует" ,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                sw.setName(name);
                sw.save();

                Toast.makeText(mActivity, "Набор переименован ",
                        Toast.LENGTH_SHORT).show();
                alert.dismiss();
                if(act!=  null) act.onSucces("");
            }
        });
    }


    public static void showCreateSetWordsDialog(final IActionHandler handler) {

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
                dsl.showListDialog("Выберите ваш язык", list, listDrawable, new IActionHandler() {
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

                dsl.showListDialog("Выберите язык для изучения", list, listDrawable, new IActionHandler() {
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
                .setPositiveButton("Создать", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                })
                .setNegativeButton("Отмена",
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
                if (mlang1.getCodeCountry() == mlang2.getCodeCountry()) {
                    Toast.makeText(mActivity, "Языки дожны быть разные " + nameSetText.getText(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = nameSetText.getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(mActivity, " Набор должен иметь имя ",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                SetWordsService sws = new SetWordsService();
                sws.createSetWords(name, mlang1.getCodeLingua(), mlang2.getCodeLingua());

                Toast.makeText(mActivity, "Создан набор " + nameSetText.getText(),
                        Toast.LENGTH_SHORT).show();
                handler.onSuccessAction("");
                alert.dismiss();
            }
        });
    }
}
