package org.ling.service;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import org.ling.utils.FileHelper;
import org.ling.utils.IAction;
import org.ling.utils.IActionHandler;
import org.ling.utils.Md5;

import java.io.File;
import java.io.IOException;

import odyssey.ru.linglibrary.FileHelp;

/**
 * Created by Dmitry on 04.07.2016.
 */
public class SoundService implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {

    IAction onPlayComplete;
    MediaPlayer mediaPlayer;

    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    static SoundService inst;

    public static SoundService getIst() {
        if (inst == null) {
            inst = new SoundService();
        }

        return inst;
    }

    ;

    public void stopPlay() {
        onPlayComplete = new IAction() {
            @Override
            public void onSucces(Object rs) {
            }
        };
        if(mediaPlayer!= null) mediaPlayer.stop();
    }

    void beginPlay(String path) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setOnPreparedListener(SoundService.this);
            mediaPlayer.setOnCompletionListener(SoundService.this);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(String url, String lang, final IAction onPlayComplete) {

        this.onPlayComplete = onPlayComplete;
        //то же что и imagesservice
        String sound = Md5.md5(url);
        final String path = FileHelp.getPathToWordMp3(sound, lang);
        File f = new File(path);
        if (f.exists()) {
            beginPlay(path);
            return;
        }

        FileHelper.downloadMp3(url, sound, lang, null, new IActionHandler() {
            @Override
            public void onSuccessAction(Object rs) {
                beginPlay(path);
            }

            @Override
            public void onFailAction(String s, Throwable throwable) {
                if (onPlayComplete != null)
                    onPlayComplete.onSucces("");
            }
        });

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("MediaPlayer", "MediaPlayer complite");
        if (onPlayComplete != null)
            onPlayComplete.onSucces("");
    }
}
