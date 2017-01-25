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
import android.widget.GridView;

import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import org.ling.Dialog;
import org.ling.MainActivity;
import org.ling.utils.IActionHandler;
import org.R;;
import org.ling.model.SetWords;
import org.ling.model.agent.SetWordsAgent;
import org.ling.fragment.view.adapter.SetWordsAdapter;

/**
 * Created by Dmitry on 30.06.2016.
 */
public class SetWordsFragment extends Fragment {

    LayoutInflater inflater;
    Context context;
    View rootView;





    @Override
    public void onResume() {
        start();
        super.onResume();

    }

    void start(){
        final GridView g = (GridView) rootView.findViewById(R.id.mSetWords3);
        List<SetWords> lsw = SetWordsAgent.getAll();

        FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fabCreateSet);
        fab.attachToListView(g);
        Drawable image = new IconicsDrawable(context, FontAwesome.Icon.faw_plus).color(Color.LTGRAY);
        fab.setImageBitmap(MainActivity.convertToBitmap(image, 30, 30));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.showCreateSetWordsDialog(new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        onResume();
                    }

                    @Override
                    public void onFailAction(String s, Throwable throwable) {
                    }
                });
            }
        });


        SetWordsAdapter mAdapter = new SetWordsAdapter(context,
                lsw,this );

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
       // if(displaymetrics.widthPixels < 720){
            g.setNumColumns(1);
       // }
        g.setAdapter(mAdapter);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_set, container, false);
        this.context = inflater.getContext();
        start();





//        g.setOnItemSelectedListener(this);

        return  rootView;
    };

}
