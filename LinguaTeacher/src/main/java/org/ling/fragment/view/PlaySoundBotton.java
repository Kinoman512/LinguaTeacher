package org.ling.fragment.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import org.ling.model.Pronounce;
import org.ling.service.SoundService;
import org.ling.utils.IAction;

/**
 * Created by Dmitry on 10.07.2016.
 */
public class PlaySoundBotton extends com.mikepenz.iconics.view.IconicsImageView {

    GoogleMaterial.Icon PLAY = GoogleMaterial.Icon.gmd_volume_up;
    GoogleMaterial.Icon STOP = GoogleMaterial.Icon.gmd_pause;
    boolean isPlaed = false;
    Pronounce mp3;
    String lang;
    SoundService soundService;
    IAction actionWhenStopPlay;

    public void setActionWhenStopPlay(IAction actionWhenStopPlay) {
        this.actionWhenStopPlay = actionWhenStopPlay;
    }

    public void setSound(Pronounce mp3, String lang) {
        setColor(Color.BLACK);
        this.mp3 = mp3;
        this.lang = lang;
    }

    public boolean haveSound(){
        if(mp3 == null || lang == null)return false;

        return  true;
    }

    public void stop() {
        if (soundService != null)
            soundService.stopPlay();
        isPlaed = true;
        if (!this.isInEditMode()) {
            setIcon(PLAY);
        }
    }
    public void play() {
        play(true);
    }
    public void play(boolean force) {
        if (isPlaed||force) {
            setIcon(STOP);
            if (mp3 != null && lang != null)
                soundService = new SoundService();

            if (mp3 == null) {
                return;
            }
            isPlaed = true;
            soundService.stopPlay();
            soundService.play(
                    mp3.getUrl(),
                    lang,
                    new IAction() {
                        @Override
                        public void onSucces(Object rs) {

                            setIcon(PLAY);
                            if(actionWhenStopPlay != null) actionWhenStopPlay.onSucces("");
                        }
                    });

        } else {
            setIcon(PLAY);
            if (soundService != null)
                soundService.stopPlay();
        }
        isPlaed = !isPlaed;
    }


    public PlaySoundBotton(Context context) {
        super(context);
    }

    public PlaySoundBotton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!this.isInEditMode()) {
            setIcon(PLAY);
            setColor(Color.LTGRAY);
        }
    }

    public PlaySoundBotton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                play();
                break;

            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
}
