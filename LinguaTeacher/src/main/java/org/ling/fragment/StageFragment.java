package org.ling.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.R;
import org.ling.MyApplication;
import org.ling.fragment.view.adapter.StageListAdapter;
import org.ling.model.Stage;
import org.ling.model.agent.StageAgent;

import java.util.List;

;

/**
 * Created by Dmitry on 15.07.2016.
 */
public class StageFragment extends Fragment {

    View rootView;
    Context context;
    ListView stages;

    String TIP_TAG = "StageFragmentTip";



    public void onResume() {

        super.onResume();
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stage, container, false);
        this.context = inflater.getContext();

        stages = (ListView) rootView.findViewById(R.id.stageList);
        Button btn_save = (Button) rootView.findViewById(R.id.btn_save);


        final List<Stage> ls = StageAgent.getAll();
            int dheight = 47;
        stages.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dheight * ls.size()));


        final StageListAdapter sla = new StageListAdapter(context,ls,stages);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sla.save();
//                MainActivity.setListViewHeightBasedOnChildren(stages,30);

            }
        });
        stages.setAdapter(sla);

       int heightList =   Integer.valueOf((int) (MyApplication.getDisplayHeight() * 0.7));
        ViewGroup.LayoutParams params = stages.getLayoutParams();
        params.height = heightList;
        stages.setLayoutParams(params);

//        stages.setOnTouchListener(new View.OnTouchListener() {
//            // Setting on Touch Listener for handling the touch inside ScrollView
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // Disallow the touch request for parent scroll on touch of child view
////                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
//        MainActivity.activity.setListViewHeightBasedOnChildren(stages,30);
        return  rootView;
    };




}
