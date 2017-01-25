package org.ling.fragment.view.adapter;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import org.R;
import org.ling.model.CommonWord;
import org.ling.model.agent.CommonWordAgent;
import org.videolan.vlc.gui.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 11.01.2017.
 */
public class MultiChoiceImpl implements AbsListView.MultiChoiceModeListener {

    private AbsListView listView;

    public MultiChoiceImpl(AbsListView listView) {
        this.listView = listView;
    }


    @Override
    //Метод вызывается при любом изменении состояния выделения рядов
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
        Log.d(MainActivity.TAG, "onItemCheckedStateChanged");
        int selectedCount = listView.getCheckedItemCount();
        //Добавим количество выделенных рядов в Context Action Bar
        setSubtitle(actionMode, selectedCount);
    }

    @Override
    //Здесь надуваем CAB из xml
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        Log.d(MainActivity.TAG, "onCreateActionMode");
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.video_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        Log.d(MainActivity.TAG, "onPrepareActionMode");
        return false;
    }

    @Override
    //Вызывается при клике на любой Item из СAB
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
       // String text = "Action - " + menuItem.getTitle() + " ; Selected items: " + getSelectedFiles();
        //Toast.makeText(listView.getContext(), text , Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        Log.d(MainActivity.TAG, "onDestroyActionMode");
    }

    private void setSubtitle(ActionMode mode, int selectedCount) {
        switch (selectedCount) {
            case 0:
                mode.setSubtitle(null);
                break;
            default:
                mode.setTitle(String.valueOf(selectedCount));
                break;
        }
    }

    private List<CommonWord> getSelectedFiles() {
        List<CommonWord> selectedWords = new ArrayList<CommonWord>();

        SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();

        for (int i = 0; i < sparseBooleanArray.size(); i++) {

            if (sparseBooleanArray.valueAt(i)) {

                CommonWord selectedItem = (CommonWord) listView.getItemAtPosition(sparseBooleanArray.keyAt(i));
                selectedItem.delete();
                //selectedFiles.add("#" + Integer.toHexString(selectedItem).replaceFirst("ff", ""));
            }

        }
        org.ling.MainActivity.resumeFragment();

        return selectedWords;
    }
}
