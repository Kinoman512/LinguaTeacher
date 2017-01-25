package org.ling.fragment.view;

import android.content.Context;
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

    public void setSound(Pronounce mp3, String lang) {
        this.mp3 = mp3;
        this.lang = lang;
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
        if (isPlaed) {
            setIcon(STOP);
            if (mp3 != null && lang != null)
                soundService = new SoundService();

            if (mp3 == null) {
                return;
            }
            soundService.play(
                    mp3.getUrl(),
                    lang,
                    new IAction() {
                        @Override
                        public void onSucces(Object rs) {
                            isPlaed = true;
                            setIcon(PLAY);
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
