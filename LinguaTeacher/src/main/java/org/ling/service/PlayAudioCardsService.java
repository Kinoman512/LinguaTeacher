package org.ling.service;

/**
 * Created by Dmitry on 12.09.2016.
 */


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import org.R;
import org.ling.fragment.view.CardGroup;
import org.ling.fragment.view.PlaySoundBotton;
import org.ling.utils.IAction;

import odyssey.ru.linglibrary.Util;

public class PlayAudioCardsService extends Service {
    MediaPlayer mPlayer;
    private CardGroup cardGroup;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        Toast.makeText(this, "Служба создана",
//                Toast.LENGTH_SHORT).show();
//        mPlayer = MediaPlayer.create(this, R.raw.sample);
//        mPlayer.setLooping(false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start(intent, startId);
        return START_NOT_STICKY;
    }

    public static void stopAll(){
        View v = CardGroup.istance.getCurrentView();
        final PlaySoundBotton btn_play_back = (PlaySoundBotton) v.findViewById(R.id.btn_play_back);
        final PlaySoundBotton btn_play_front = (PlaySoundBotton) v.findViewById(R.id.btn_play_front);

        btn_play_back.stop();;
        btn_play_front.stop();;
    }


    public void start(Intent intent, int startid) {
//        super.onStart(intent, startid);
//        Bundle extras = intent.getExtras();
        Util.showMassage(R.string.service_is_begin);

        cardGroup = (CardGroup) CardGroup.istance;
        if (cardGroup == null || cardGroup.getCards().isEmpty()) {
            Util.showMassage(R.string.err_in_determinate_crads);
        } else {
            playAudio();
        }

    }

    boolean isBothPSBHaveSounds() {
        View v = cardGroup.getCurrentView();

        final PlaySoundBotton btn_play_back = (PlaySoundBotton) v.findViewById(R.id.btn_play_back);
        final PlaySoundBotton btn_play_front = (PlaySoundBotton) v.findViewById(R.id.btn_play_front);
        if (!btn_play_back.haveSound() || !btn_play_front.haveSound())
            return false;

        return true;
    }


    int countSkeep = 0;
    private void playAudio() {
        if(!cardGroup.isPlayAudioCardsServiceRun()){
            return;
        }


        if (!isBothPSBHaveSounds()) {
            countSkeep++;
            if(countSkeep >=  cardGroup.getCards().size()){
                Util.showMassage(R.string.no_cards_with_voice);
                return;
            }
            nextCard();
        }else{
            playLangSound();
        }



    }

    public void nextCard() {
        if(!cardGroup.isPlayAudioCardsServiceRun()){
            return;
        }

        View v = cardGroup.getCards().get(cardGroup.getCurCard());
        cardGroup.onUp(v, false);
        PlayAudioCardsService.this.playAudio();
    }

    public void playLangSound() {
        if(!cardGroup.isPlayAudioCardsServiceRun()){
            return;
        }

        View v = cardGroup.getCurrentView();

//        final PlaySoundBotton btn_play_back = (PlaySoundBotton) v.findViewById(R.id.btn_play_back);
        final PlaySoundBotton btn_play_front = (PlaySoundBotton) v.findViewById(R.id.btn_play_front);
        btn_play_front.setActionWhenStopPlay(new IAction() {
            @Override
            public void onSucces(Object rs) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(3000);
//                            playNatvSound();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
                try {
                    Thread.sleep(800);
                    playNatvSound();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_play_front.play();
    }

    public void playNatvSound() {
        if(!cardGroup.isPlayAudioCardsServiceRun()){
            return;
        }
        View v = cardGroup.getCurrentView();
        final PlaySoundBotton btn_play_back = (PlaySoundBotton) v.findViewById(R.id.btn_play_back);

        btn_play_back.setActionWhenStopPlay(new IAction() {
            @Override
            public void onSucces(Object rs) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(3000);
//                            playNatvSound();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
                try {
                    Thread.sleep(1100);
                    nextCard();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        btn_play_back.play();
    }


    @Override
    public void onDestroy() {
        Util.showMassage(R.string.service_is_stop);
//        mPlayer.stop();
    }
}
