package org.ling.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import org.R;;
import org.ling.Setting;
import org.ling.model.CommonWord;
import org.ling.model.Pronounce;
import org.ling.model.SetWords;
import org.ling.model.SimpleWord;
import org.ling.model.Stage;
import org.ling.model.agent.SimpleWordAgent;
import org.ling.server.YandexServer;
import org.ling.utils.TrainUnit;


/**
 * Created by Dmitry on 12.06.2016.
 */
public class MenuFragment extends Fragment implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {


    LayoutInflater inflater;
    Context context;
    MediaPlayer mediaPlayer;


    private void dosmt() {


    }

    ;

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        this.inflater = inflater;
        this.context = MenuFragment.this.inflater.getContext();
        Setting.init(inflater.getContext());


        final Button btnStart = (Button) rootView.findViewById(R.id.btn_start);
        final Button btnStop = (Button) rootView.findViewById(R.id.btn_stop);
        final Button btnTest = (Button) rootView.findViewById(R.id.btn_test);
        final Button btnJ = (Button) rootView.findViewById(R.id.btn_j);
        final Button btnCont = (Button) rootView.findViewById(R.id.btnCont);


        btnCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                StageFragment newFragment = new StageFragment();
                TrainRoomFragment newFragment = new TrainRoomFragment();


//                FragmentTrainResult newFragment = new FragmentTrainResult();
//                ArrayList<TrainUnit> list1 = new ArrayList<TrainUnit>();
//
//                ArrayList<CommonWord> lcw = (ArrayList<CommonWord>) CommonWordAgent.getAll();
//
//                for(CommonWord cw: lcw){
//                    TrainUnit tu = new TrainUnit();
//                    tu.setCommonWord(cw);
//                    tu.addTime(33);
//                    tu.addTime(33);
//                    tu.addTime(311);
//                    list1.add(tu);
//                }
//
//
//                Bundle args = new Bundle();
//                args.putSerializable("Trains",   list1);
//                newFragment.setArguments(args);
               // MainActivity.setFragment(newFragment, true);


                int x = 3;
            }
        });




        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
//                new ForvoWordsTask(context, new WordsHandler()).execute("ru", "en", "red", "big");
//                MainActivity.setFragment( new SetWordsFragment(),true);

//                showInputDialog2();
                //showWordDialog();

//                SimpleWordAgent.deleteAll();
//                CommonWordAgent.deleteAll();
////                SetWordsAgent.deleteAll();
//                PronounceAgent.deleteAll();
//                StageAgent.deleteAll();

                bd();

                List<SimpleWord> list1 = SimpleWordAgent.getAll();

                Log.d("123","123");
                Log.d("123","123");

//                List<Pronounce> list2 =  SimpleWordAgent.find("кошка","ru").getInstPronounces();
                Log.d("123","123");



                int x = 3;
                Toast.makeText(context, "Start1233",
                        Toast.LENGTH_SHORT).show();


//                YandexServer ys = new YandexServer();
//                ys.translate("en","ru","castle");



//                dosmt();
//                Dialog.showCreateSetWordsDialog();

//                WordManager wm = new WordManager();
//                wm.createCommonWord("Собака","Dog","ru","en",-1);


            }
        });


        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
//                MainActivity.setFragment(new WebFragment(), true);
                Toast.makeText(context, "Start3",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnJ.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
//                Bundle args = new Bundle();
//                ArrayList<CommonWord> list1 =(ArrayList) CommonWordAgent.getAll();
//                args.putSerializable("Words",   list1);
//                TrainFragment newFragment = new TrainFragment();
//                newFragment.setArguments(args);
                StageFragment newFragment = new StageFragment();
//                MainActivity.setFragment(newFragment, true);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
//                MainActivity.setFragment(new SetWordsFragment(), true);
//                WordManager wm = new WordManager();
//                wm.createCommonWord("Cat","Кот","en","ru",-1);
//                MainActivity.setFragment(new VocabularyFragment(),true);


//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }

        });


        return rootView;
    }


    public void bd() {
        Pronounce p1 = new Pronounce("qwewqezz", 3, "dima", "123", "male");
        Pronounce p2 = new Pronounce("qwewqe2zz2", 3, "dima", "123", "male");
        Pronounce p3 = new Pronounce("qwewqe333zzz", 3, "dima", "123", "male");

//                List<Pronounce> lpr = new ArrayList<Pronounce>();
        List<Pronounce> lpr = new ArrayList<Pronounce>();
        List<Pronounce> lpr2 = new ArrayList<Pronounce>();
        lpr.add(p1);
        lpr2.add(p2);
        lpr.add(p3);

        SimpleWord sw = new SimpleWord("Любовь", "ru"   );
        SimpleWord sw2 = new SimpleWord("Love", "en" );

        sw = SimpleWordAgent.create(sw, lpr);
        sw2 = SimpleWordAgent.create(sw2, lpr2);





    }




    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
