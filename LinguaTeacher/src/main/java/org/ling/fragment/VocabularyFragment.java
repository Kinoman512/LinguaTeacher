package org.ling.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.R;
import org.ling.Dialog;
import org.ling.MainActivity;
import org.ling.fragment.view.adapter.MultiChoiceImpl;
import org.ling.fragment.view.adapter.VocabularyListAdapter;
import org.ling.model.CommonWord;
import org.ling.model.SetWords;
import org.ling.model.agent.CommonWordAgent;
import org.ling.utils.IAction;

import java.util.List;

;

/**
 * Created by Dmitry on 03.07.2016.
 */
public class VocabularyFragment extends Fragment {

    LayoutInflater inflater;
    Context context;
    List<CommonWord> lcw;
    SetWords sw;
    View rootView;

    public void backPressed() {
        MainActivity.switchToNextFragment( new SetWordsFragment());
    }

    @Override
    public void onResume() {
        start();
        super.onResume();
    }

    void start(){
        lcw = CommonWordAgent.findBySetWord(sw.getId());
        ListView lv = (ListView) rootView.findViewById(R.id.wordList);

        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new MultiChoiceImpl(lv));


        com.melnykov.fab.FloatingActionButton fab = (com.melnykov.fab.FloatingActionButton) rootView.findViewById(R.id.fabCreateWord);

        Drawable image = new IconicsDrawable(context, FontAwesome.Icon.faw_plus).color(Color.LTGRAY);
        fab.setImageBitmap(MainActivity.convertToBitmap(image, 30, 30));

        fab.attachToListView(lv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.setActivity(MainActivity.activity);
                Dialog.showCreateWordDialog(sw,inflater, rootView , new IAction() {
                    @Override
                    public void onSucces(Object rs) {
                        onResume();
                    }
                });
            }
        });

        VocabularyListAdapter mAdapter = new VocabularyListAdapter(context,
                lcw);
        lv.setAdapter(mAdapter);
        MainActivity.setListViewHeightBasedOnChildren(lv);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_vocabulary, container, false);
        this.context = inflater.getContext();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("Set")) {
            sw = (SetWords) bundle.getSerializable("Set");
            start();
        } else if (bundle == null) {
//            Toast.makeText(getActivity(), "Error Нет setWords", Toast.LENGTH_LONG).show();
        }

        return  rootView;
    };



}