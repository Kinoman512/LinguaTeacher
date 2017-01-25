package org.ling;

import android.os.Message;
import android.widget.ScrollView;

import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipView;
import com.plumillonforge.android.chipview.OnChipClickListener;

import org.R;
import org.ling.fragment.view.MySnackBar;
import org.ling.fragment.view.adapter.WordChipAdapter;
import org.videolan.vlc.MediaDatabase;
import org.videolan.vlc.gui.video.VideoPlayerActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import odyssey.ru.linglibrary.SubAnalyser;
import odyssey.ru.linglibrary.ViewUtil;
import odyssey.ru.linglibrary.subtitle.Subtitle;

/**
 * Created by Dmitry on 05.11.2016.
 */
public class VideoSubManager {

    private final ChipView chipView;
    private final ScrollView scrollViewWords;
    VideoPlayerActivity actv;

   // private SubtitleFile subFile;

    private int maxSub;
    private  List<SubAnalyser.UnionSub> unionsList;
    private int currentSub;
    private MySnackBar snackBar;
    private int posSub;
    private String finalSubString;
    private String tempSubString;




    public VideoSubManager(VideoPlayerActivity actv){
        this.actv = actv;
        chipView = actv.getChipView();
        scrollViewWords = actv.getScrollView();
        unionsList = actv.getUnionsSubList();
    }


    public Subtitle getSubFByPos(int pos){
        Subtitle var = unionsList.get(pos).getFsub().getSub();
        return var;
    }

    public Subtitle getSubFByPos(){
        return getSubFByPos(currentSub - 1);
    }

    public List<SubAnalyser.Nsub>  getSubNByPos(int pos){
        List<SubAnalyser.Nsub> var = unionsList.get(pos).getNsubs();
        return var;
    }

    public  List<SubAnalyser.Nsub> getSubNByPos(){
        return getSubNByPos(currentSub - 1);
    }



    public Subtitle showWordChipsByPositionSub(int pos) {
        try {
//            if (subFile == null || actv.pathtoSRT != null)
//                subFile = new SubtitleFile(new File(actv.pathtoSRT));


           // Subtitle sb = subFile.getSubtitle(pos);


            Subtitle sb =  getSubFByPos(pos);
            maxSub = unionsList.size();

            actv.btn_sub_current.setText("" + pos);
            if (currentSub >= maxSub) {
                //Util.showMassage("Вы достигли конца субтиров");
                return null;
            }

            boolean isPlayed = showWordChips(sb);

            if (!isPlayed) {
                return null;
            }
            return sb;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showWordChipsByTime(int time) {
//        try {
////            if (subFile == null)
////                subFile = new SubtitleFile(new File(actv.pathtoSRT));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InvalidTimestampFormatException e) {
//            e.printStackTrace();
//        }
//        Subtitle sb = subFile.getSubtitleByTime(time);
//        showWordChips(sb);
    }


    public boolean showWordChips(Subtitle sb) {

        List<Chip> mTagList1 = new ArrayList<Chip>();
        final List<Chip> mTagListDefault = new ArrayList<Chip>();


        if (sb == null) return false;
        String subString = "";
        subString = sb.getFullLines();

        List<String> lstrs = sb.getFullWords();
        for (String e : lstrs) {
            mTagList1.add(new WordTag(e));
        }

        long seed = System.nanoTime();
        mTagListDefault.addAll(mTagList1);
        if (mTagListDefault.size() <= 1) {
            currentSub++;
            toNextSub();
            return false;
        }


        Collections.shuffle(mTagList1, new Random(seed));
         posSub = 0;
        snackBar = new MySnackBar(actv, actv.getLayoutInflater(), actv.findViewById(R.id.root_video));
        chipView.setAdapter(new WordChipAdapter(actv, MyApplication.currentSet, null, snackBar));
        chipView.setChipList(mTagList1);

        if (scrollViewWords != null)
            scrollViewWords.computeScroll();
        actv.txt_open_sub.setText("");
        finalSubString = "";
        tempSubString = subString.toLowerCase().replaceAll("\n", " ");
        actv.txt_hide_sub.setText(tempSubString);
        ViewUtil.setTextHidded(actv.txt_hide_sub);

        chipView.setOnChipClickListener(new OnChipClickListener() {
            @Override
            public void onChipClick(Chip chip) {
                final WordTag tag = (WordTag) chip;
                if (posSub <= mTagListDefault.size() && mTagListDefault.get(posSub).getText().equals(tag.getText())) {
                    posSub++;
                    chipView.remove(tag);

                    int start = tempSubString.indexOf(tag.getText());
                    String partString = tempSubString.substring(0, start + tag.getText().length() + 1);
                    finalSubString += partString;
                    if (tempSubString.length() <= tag.getText().length()) {
                        tempSubString = tempSubString.substring(tempSubString.length());
                    } else {
                        tempSubString = tempSubString.substring(partString.length());
                    }

                    actv.txt_open_sub.setText(finalSubString);
                    actv.txt_hide_sub.setText(tempSubString);
                    ViewUtil.setTextHidded(actv.txt_hide_sub);
                    if (posSub >= mTagListDefault.size()) {
                        toNextSub();
                        return;
                    }

//                    Toast.makeText(PlayerActivity.this, "Совпадает + " + posSub, Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(350);
                                org.ling.MainActivity.activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (tag == null || chipView == null) return;
                                        tag.setType(0);
                                        chipView.refresh();
                                    }
                                });


                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    tag.setType(2);
                    chipView.refresh();
                }
            }
        });

        return true;
    }


    public void toNextSub() {
        toSub(currentSub);
    }

    public void toPrevSub() {
        if(currentSub > 1){
            currentSub--;
            //currentSub--;
        }
        if(currentSub != 0){
            currentSub--;
            //currentSub--;
        }
        toSub(currentSub );
    }

    public void toSub(int num) {

        currentSub = num;
        Subtitle sb = showWordChipsByPositionSub(currentSub);
        if (sb != null) {
            actv.startSub = sb.getStartTime().getAllMilliseconds() - 150;
            actv.endSub = sb.getEndTime().getAllMilliseconds();
//                    player.getPlayerControl().seekTo(start);
//                    player.setPlayWhenReady(true);
//            player.seekTo(start);
//            pause();
//            seekToPosition((int) startSub);
            actv.play();
            currentSub++;

        }

    }
    public void playBefore(int playCount) {
        actv.delta = actv.endSub - actv.startSub;
        float speed = actv.mLibVLC.getRate();
        if (actv.delta <= 0) return;
        if (actv.delta <= 1000) actv.delta = 1000  ;

        actv.delta*= speed;
        actv.delta/= 3;
        actv.seekToPosition( actv.startSub);
        Message msg = actv.mHandler.obtainMessage(actv.CHANGE_PLAY, playCount, 0);
        if (actv.delta != 0) {
            actv.mHandler.removeMessages(actv.CHANGE_PLAY);
            actv.mHandler.sendMessageDelayed(msg, actv.delta);
        }



    }

    public void continuePlay(int code,int playCount){

        Message msg = actv.mHandler.obtainMessage(code,playCount,0);
        if (actv.delta != 0) {
            actv.mHandler.removeMessages(code);
            actv.mHandler.sendMessageDelayed(msg, actv.delta);
        }
    }

    public void changeAudioAndPlay( boolean playSecondAudioTrack,int playCount) {
        if (playSecondAudioTrack && actv.mLibVLC.getAudioTracksCount() > 1){
            actv.delta = actv.endSub - actv.startSub;
            if (actv.delta <= 0) return;
            if (actv.delta <= 1000) actv.delta = 1000 ;
            float speed = actv.mLibVLC.getRate();
            actv.delta*= speed;
            actv.delta/= 3;
            Message msg = actv.mHandler.obtainMessage(actv.STOP_PLAY, playCount,0);
            if (actv.delta != 0) {
                actv.mHandler.removeMessages(actv.STOP_PLAY);
                actv.mHandler.sendMessageDelayed(msg, actv.delta );
            }


            actv.seekToPosition( actv.startSub);
            MediaDatabase.getInstance().updateMedia(
                    actv.mLocation,
                    MediaDatabase.mediaColumn.MEDIA_AUDIOTRACK,
                    actv.audio2);
            actv.mLibVLC.setAudioTrack(actv.audio2);
//                                    startPlayback();
//            playBefore(start, end, false);
        }else{
            actv.pause();
        }
    }

    public void stopPlay() {

        MediaDatabase.getInstance().updateMedia(
                actv.mLocation,
                MediaDatabase.mediaColumn.MEDIA_AUDIOTRACK,
                actv.audio1);
        actv.mLibVLC.setAudioTrack(actv.audio1);


        actv.pause();
    }


    public void setSubByTime(int time) {

         int pos = 0;

         for( SubAnalyser.UnionSub elem : unionsList){
             int end  = elem.getFsub().getSub().getEndTime().getAllMilliseconds();

             if(time > end ){
                 pos++;
                 continue;
             }
              currentSub = pos;
             toSub(currentSub);
             return;
         }

        //Util.showMassage("Время не найдено!");

        //boolean isPlayed = showWordChips(sb);

//        if (!isPlayed) {
//           // return null;
//        }
        //return sb;
    }
}
