package org.ling.fragment.view.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsButton;
import com.plumillonforge.android.chipview.ChipViewAdapter;

import odyssey.ru.linguateacher.Dialog;
import odyssey.ru.linguateacher.MainActivity;
import odyssey.ru.linguateacher.PlayerActivity;
import odyssey.ru.linguateacher.R;
import odyssey.ru.linguateacher.WordTag;
import org.ling.fragment.view.MySnackBar;
import org.ling.model.SetWords;
import org.ling.utils.IAction;

/**
 * Created by Dmitry on 14.08.2016.
 */
public class WordChipAdapter extends ChipViewAdapter {


    Context context;
    SetWords sw;
    View pb;
    private MySnackBar mySnackBar;

    public WordChipAdapter(Context context,SetWords sw , View pb,MySnackBar mySnackBar) {
        super(context);
        this.context = context;
        this.sw = sw;
        this.pb = pb;
        this.mySnackBar = mySnackBar;
    }

    @Override
    public int getLayoutRes(int position) {
        return R.layout.view_word_video;
    }


    @Override
    public int getBackgroundColorSelected(int position) {
        return R.color.red;
    }

    @Override
    public int getBackgroundRes(int position) {

        WordTag tag = (WordTag) getChip(position);

        switch (tag.getType()) {


            case 0:
            case 4:
                return R.color.orange;

            case 2:
            case 5:
                return R.color.purple;

            case 3:
                return R.color.teal;
            default:
                return R.color.black;
        }
    }

    @Override
    public int getBackgroundColor(int position) {
        return 0;
    }

    @Override
    public void onLayout(View view, int position) {
        final WordTag tag = (WordTag) getChip(position);

        TextView word = (TextView) view.findViewById(R.id.word_chip);
        IconicsButton btn_chip_word = (IconicsButton) view.findViewById(R.id.btn_chip_word);
        word.setText(tag.getText());

        btn_chip_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v,tag.getText());
            }
        });

    }



    private void showPopupMenu(View v, final String text) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.menu_trans); // Для Android 4.0
        // для версии Android 3.0 нужно использовать длинный вариант
        // popupMenu.getMenuInflater().inflate(R.menu.popupmenu,
        // popupMenu.getMenu());

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Toast.makeText(PopupMenuDemoActivity.this,
                        // item.toString(), Toast.LENGTH_LONG).show();
                        // return true;
                        switch (item.getItemId()) {

                            case R.id.add_tran:
                                Dialog.setActivity(PlayerActivity.activity);
                                Dialog.showCreateWordDialog(text, sw, new IAction() {
                                    @Override
                                    public void onSucces(Object rs) {
                                        Toast.makeText(PlayerActivity.activity, MainActivity.getStringById(R.string.added), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return true;
                            case R.id.trans:
                                pb.setVisibility(View.VISIBLE);
                                mySnackBar.showTranslatedWord(pb, sw, text);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
        popupMenu.show();

    }
}