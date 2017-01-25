package org.ling.fragment.view;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipView;
import com.plumillonforge.android.chipview.OnChipClickListener;

import org.R;
import org.ling.MainActivity;
import org.ling.Tag;
import org.ling.fragment.view.adapter.MyChipAdapter;
import org.ling.model.SetWords;
import org.ling.model.agent.WordManager;
import org.ling.server.YandexServer;
import org.ling.server.result.Tword;
import org.ling.server.result.YandexWord;
import org.ling.utils.IAction;
import org.ling.utils.IActionHandler;

import java.util.ArrayList;
import java.util.List;

;

/**
 * Created by Dmitry on 15.08.2016.
 */
public class MySnackBar {

    private static Snackbar snackbar;
    LayoutInflater inflater;
    private Snackbar.SnackbarLayout layout;
    private Context context;
    View rootView;
    private OnClickChip actionForChipClick;

//    public IAction actionForChipClick;

    public interface OnClickChip {
        public void onClick(View v);
    }

    public void setIActionForChipClick(OnClickChip actionForChipClick) {
        this.actionForChipClick = actionForChipClick;
    }


    public MySnackBar(Context context, LayoutInflater inflater, View rootView) {
        this.rootView = rootView;
        this.context = context;
        this.inflater = inflater;
        snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_INDEFINITE);
        layout = (Snackbar.SnackbarLayout) snackbar.getView();
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
    }

    public Snackbar getSnackbar() {
        return snackbar;
    }

    public Snackbar.SnackbarLayout getSnackLayout() {
        return layout;
    }

    public void showTranslatedWord(final Activity activity, final View pbLoading, final SetWords currentSet, String selectedtext) {
        showTranslatedWord(  activity,   pbLoading,   currentSet, selectedtext ,null);
    };
    public void showTranslatedWord(final Activity activity, final View pbLoading, final SetWords currentSet, String selectedtext, final IAction actPar) {
        YandexServer ys = new YandexServer(context);
        ys.setServerActionListnerTanslate(new IActionHandler() {
            @Override
            public void onSuccessAction(Object rs) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(pbLoading!= null)
                             pbLoading.setVisibility(View.GONE);
                        if(actPar!= null) actPar.onSucces("");
                    }
                });
                List<YandexWord> listYW = (List<YandexWord>) rs;

                List<Chip> mTagList1 = new ArrayList<Chip>();

                ;//rs.getTranslatedWord().getTranslate();
                String TTWord = "";//rs.getTranslatedWord().getSource();

                String Fword = "";
                String TRword = "";
//                        mTagList1.add(tag);
                String transc = "";

                final List<Tword> listTransl = new ArrayList<Tword>();

                int number = 0;
                for (YandexWord e : listYW) {
                    for (Tword tw : e.getListtword()) {


                        String Tword = "";
                        tw.getText();
                        String gender = "";
                        String classf = "";

                        if (Fword.isEmpty())
                            Fword = e.getJword();

                        listTransl.add(tw);

                        if (!e.getTscr().isEmpty()) {
                            transc = e.getTscr();
                            Tword += "[" + e.getTscr() + "] ";
                        }
                        if (!e.getClassf().isEmpty()) {
                            classf = e.getClassf();
                            Tword += " -" + e.getClassf();
                        }
                        if (!e.getGender().isEmpty()) {
                            gender = e.getGender();
                            Tword += " -" + e.getGender();
                        }
                        Tword += " " + tw.getText();

                        TTWord = tw.getMeansTxt(3);
                        Tag tag = new Tag(Tword, TTWord, number, 0);
                        number++;
                        mTagList1.add(tag);
                    }
                }

//                        snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_INDEFINITE);
//                        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

                snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_INDEFINITE);
                layout = (Snackbar.SnackbarLayout) snackbar.getView();

                TextView textView = (TextView) layout.findViewById(R.id.snackbar_text);
                textView.setVisibility(View.INVISIBLE);
                ViewGroup group = (ViewGroup) snackbar.getView();
                group.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

                View root = inflater.inflate(R.layout.snack_tokenword, null);
                final ChipView chipView = (ChipView) root.findViewById(R.id.chip_attrs);


                chipView.setAdapter(new MyChipAdapter(context));

                chipView.setChipList(mTagList1);
                final String finalFword = Fword;
                final String finalTransc = transc;
                chipView.setOnChipClickListener(new OnChipClickListener() {
                    @Override
                    public void onChipClick(Chip chip) {


                        snackbar.dismiss();
                        if (actionForChipClick != null) {
                            actionForChipClick.onClick(chipView);
                            return;
                        }
                        final Tag tag = (Tag) chip;
                        final Tword tw = listTransl.get(tag.getNumber());

                        final IAction act = new IAction() {
                            @Override
                            public void onSucces(Object rs) {

                                MainActivity.activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        pbLoading.setVisibility(View.INVISIBLE);
                                        if(actPar != null) actPar.onSucces("");
                                        Toast.makeText(context, MainActivity.getStringById(R.string.word_saved) + " " + tag.getmWord(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        };
                        final IAction err = new IAction() {
                            @Override
                            public void onSucces(Object rs) {


                                MainActivity.activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                       // pbLoading.setVisibility(View.INVISIBLE);
                                        if(actPar != null) actPar.onSucces("");
                                        Toast.makeText(context, MainActivity.getStringById(R.string.error_when_saved) + tag.getmWord(), Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                        };

//                        pbLoading.setVisibility(View.VISIBLE);
                        WordManager wm = new WordManager();
                        wm.createCommonWord(
                                finalFword,
                                tw.getText(),
                                finalTransc,
                                currentSet.getNatv(),
                                currentSet.getLang(),
                                currentSet.getId(),
                                act,
                                err);

                    }
                });
//                layout.removeAllViews();
                layout.addView(root, 0);
                snackbar.show();
            }

            public void onFailAction(String s, Throwable throwable) {

                MainActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //pbL//oading.setVisibility(View.GONE);
                        Toast.makeText(context, MainActivity.getStringById(R.string.error_when_trans), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (currentSet == null) {
                    Toast.makeText(context, MainActivity.getStringById(R.string.select_dict), Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        ys.translate(currentSet.getLang(), currentSet.getNatv(), selectedtext);

    }


    public static boolean hideIfshow() {
        if (snackbar != null) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
