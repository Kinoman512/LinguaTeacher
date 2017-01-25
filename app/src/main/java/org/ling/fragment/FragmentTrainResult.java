package org.ling.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import odyssey.ru.linguateacher.MainActivity;
import odyssey.ru.linguateacher.R;

import org.ling.fragment.view.adapter.TrainResultListAdapter;
import org.ling.model.CommonWord;
import org.ling.model.Stage;
import org.ling.model.agent.StageAgent;
import org.ling.utils.TrainUnit;

/**
 * Created by Dmitry on 16.07.2016.
 */
public class FragmentTrainResult extends Fragment {


    View rootView;
    Context context;
    List<TrainUnit> ltu = new ArrayList<>();


     void changeStateCW(CommonWord cw  ,boolean succes ){
        boolean directUp = succes;
        List<Stage> list = StageAgent.getAll();
        if(cw.getStage() < 0) return;

        if(succes == false && cw.getStage() <= StageAgent.findMin()){
            cw.setStage(0);

        }
        if(succes == true && cw.getStage() >= StageAgent.findMax()){
            cw.setStage(-1);
            cw.setNextTrain(0);
            cw.save();
            return;
        }
        if(cw.getStage() == 0){
            directUp = true;
        }

        Stage ss = StageAgent.getCloseStageWeight(cw.getStage(), directUp);

        if(ss == null){
            cw.setStage(-1);
            cw.setNextTrain(0);
            cw.save();
            return;
        }
        cw.setStage(ss.getWeight());
        cw.setLastTrain(cw.getNextTrain());
        cw.setNextTrain(new Date().getTime() + ss.getDelta());
        cw.save();
    }



    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_result_train, container, false);
        this.context = inflater.getContext();
        boolean forSave = false;
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("Trains")) {
            ltu = (List<TrainUnit>)  bundle.getSerializable("Trains");
        } else if (bundle == null) {
            Toast.makeText(getActivity(), "Error Нет TrainUnit", Toast.LENGTH_LONG).show();
        }

        if (bundle != null && bundle.containsKey("save")) {
            forSave =   bundle.getBoolean("save");
        }
        TextView succesWords = (TextView) rootView.findViewById(R.id.succesWords);
        TextView losedWords = (TextView) rootView.findViewById(R.id.losedWords);

        Button btn_train_new = (Button) rootView.findViewById(R.id.btn_train_new);
        Button btn_train_old = (Button) rootView.findViewById(R.id.btn_train_old);
        Button btn_train_all = (Button) rootView.findViewById(R.id.btn_train_all);



        Button btn_trainroom = (Button) rootView.findViewById(R.id.btn_trainroom);

        btn_trainroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.switchToNextFragment(new TrainRoomFragment());
            }
        });




        int losed  = 0;
        int succes  = 0;
        if(forSave){
            for(TrainUnit tu : ltu){
                CommonWord cw = tu.getCommonWord();
                if(tu.getMistakes() > 1){
//                StageAgent.getCloseStageWeight();
//                cw.setStage();
                    losed++;
                    changeStateCW(cw, false);
                }
                else {
                    succes++;
                    changeStateCW(cw, true);
                }
            }
        }

        succesWords.setText("" + succes);
        losedWords.setText("" + losed);

        ListView resList = (ListView) rootView.findViewById(R.id.listResults);
        TrainResultListAdapter adapter = new TrainResultListAdapter(context, ltu);
        resList.setAdapter(adapter);
        MainActivity.activity.setListViewHeightBasedOnChildren(resList);




        return  rootView;
    };

    public void backPressed() {
        MainActivity.switchToNextFragment(new TrainRoomFragment());
    }
}
