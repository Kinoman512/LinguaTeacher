package org.ling.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.R;
import org.ling.Dialog;
import org.ling.MainActivity;
import org.ling.Setting;
import org.ling.model.Video;
import org.ling.model.agent.VideoAgent;
import org.ling.utils.IAction;
import org.ling.utils.IActionHandler;
import org.videolan.vlc.gui.video.VideoPlayerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import odyssey.ru.linglibrary.SubAnalyser;
import odyssey.ru.linglibrary.Util;

/**
 * Created by Dmitry on 02.11.2016.
 */
public class VideoFragment extends Fragment {

    private static final String TAG = "ling.VideoFragment";
    private static final String LIST_TAG = "LIST_VideoFragment_Tag";

    LayoutInflater inflater;
    Context context;

    View rootView;
    ImageView videoThumb;

    Button btn_play_video;
    Button btn_show;
    Button btn_nsub;
    Button btn_fsub;
    Button btn_select_video;
    Button btn_delete_vid;

    View mVideoList;


    static TextView txt_video;
    static TextView txt_fsub;
    static TextView txt_nsub;

    static int selectedField = 0;
    TextView mVideoName;

    static Video currentVideo;
    List<Video> listVideo = new ArrayList<>();


    @Override
    public void onResume() {
        start();

        if (MainActivity.activity.getCurrentTab() == 0) {
            tip();
        }
        start();
        updateStats();
        super.onResume();
    }

    private void tip() {

    }


    void start() {




//        txt_video.setText("Your Video");
//        txt_fsub.setText("Foreign Sub");
//        txt_nsub.setText("Native Sub");

//        videoThumb
        listVideo = VideoAgent.getAll();
        final List<String> listFields = new ArrayList<>();
        final List<Drawable> listDrawable = new ArrayList<>();
        final Drawable img = new IconicsDrawable(MainActivity.activity, CommunityMaterial.Icon.cmd_video).color(Color.LTGRAY);
        final Drawable img2 = new IconicsDrawable(MainActivity.activity, CommunityMaterial.Icon.cmd_plus).color(Color.LTGRAY);

        for (Video v : listVideo) {
            listFields.add(v.getName());
            listDrawable.add(img);
        }

        listFields.add("Add");
        listDrawable.add(img2);

        int pos = Setting.getInt(LIST_TAG);
        if(pos < listVideo.size()){
            currentVideo = listVideo.get(pos);
            updateStats();
        }

        mVideoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.setActivity(MainActivity.activity);
                Dialog.showListDialog(getResources().getString(R.string.choose_lang_text), listFields, listDrawable, new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        int pos = (Integer) rs;

                        if (pos < listVideo.size()) {
                            Setting.setInt(LIST_TAG, pos);
                            currentVideo = listVideo.get(pos);
                            mVideoName.setText(currentVideo.getName());
                            updateStats();
                        } else {
                            Dialog.showCreateVideo(new IAction() {
                                @Override
                                public void onSucces(Object rs) {
                                    onResume();
                                    int pos = listVideo.size() - 1;
                                    Setting.setInt(LIST_TAG, pos);
                                    currentVideo = listVideo.get(pos);
                                    mVideoName.setText(currentVideo.getName());
                                    updateStats();
                                }
                            });
                        }


                    }

                    @Override
                    public void onFailAction(String s, Throwable throwable) {
                    }
                });
            }
        });



        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubAnalyser suba = new SubAnalyser();

                String path1 =  currentVideo.getFsub();
                String path2 =  currentVideo.getNsub();

                if(path1.isEmpty() || path1 == null ){
                    Util.showMassage(Util.getStringById(R.string.need_f_sub));
                    return;
                }



                try {
                    String pathToFSUB = suba.convert(path1, false);
                    String pathToNSUB = "";

                    try{
                        pathToNSUB = suba.convert(path2, true);
                    }catch(Exception e){

                    }



                    String html ="";
                    if(pathToNSUB.isEmpty()){
                        html =  suba.getHtmlFromSubs(pathToFSUB);
                    }else{
                        html = suba.getHtmlFromSubs(pathToFSUB,pathToNSUB );
                    }



                    MainActivity.activity.switchToWebFragment(html);



                } catch (Exception e) {

                    Util.showMassage(Util.getStringById(R.string.err_read_sub_file));
                    e.printStackTrace();
                }


            }
        });

        btn_play_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubAnalyser suba = new SubAnalyser();

                String path0 =  currentVideo.getVideo();
                String path1 =  currentVideo.getFsub();
                String path2 =  currentVideo.getNsub();




                if(path1.isEmpty() || path1 == null ){
                    Util.showMassage(Util.getStringById(R.string.nedd_select_f_flie));
                    return;
                }



                try {
                    String pathToFSUB = suba.convert(path1, false);
                    String pathToNSUB = "";//suba.convert(path2, true);
                    String pathVideo = "file:///" + currentVideo.getVideo();
                    List<SubAnalyser.UnionSub> subUnions ;

                    try{
                        pathToNSUB = suba.convert(path2, true);
                    }catch(Exception e){

                    }


                    if(pathToNSUB.isEmpty()){
                        subUnions =  suba.analiseSub(pathToFSUB);
                    }else{
                        subUnions = suba.analiseSub(pathToFSUB,pathToNSUB );
                    }


                    //suba.analiseSub(pathToFSUB);



                    VideoPlayerActivity.start(context , pathVideo, subUnions, pathToFSUB, pathToNSUB);



                } catch (Exception e) {

                    Util.showMassage(Util.getStringById(R.string.err_read_sub_file));
                    e.printStackTrace();
                }


            }
        });

        btn_delete_vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (VideoAgent.getAll().size() > 1){

                    int pos = Setting.getInt(LIST_TAG);

                    currentVideo = listVideo.get(pos);
                    currentVideo.delete();
                    if(pos > 0) pos--;
                    currentVideo = listVideo.get(pos);

                    mVideoName.setText(currentVideo.getName());
                    Setting.setInt(LIST_TAG, pos);
                    onResume();
                    Util.showMassage(Util.getStringById(R.string.removed));
                }else{
                    Util.showMassage(Util.getStringById(R.string.limit_no_delete_1_video));
                }
            }
        });


    }

    private void updateStats() {
        if (currentVideo != null) {
            Video cv = currentVideo;

            mVideoName.setText(cv.getName());

            final String pathVideo = cv.getVideo();
            String pathFSUB = cv.getFsub();
            String pathNSUB = cv.getNsub();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap thumb = ThumbnailUtils.createVideoThumbnail(pathVideo, MediaStore.Video.Thumbnails.MINI_KIND);

                    MainActivity.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoThumb.setImageBitmap(thumb);
                        }
                    });
                }
            }).start();


            String videoName = "";
            String FSUBName = "";
            String NSUBoName = "";

            if (pathVideo!= null || !pathVideo.isEmpty()) {
                videoName = getNameFromPath(pathVideo);
            }else{
                videoName = "none";
            }
            if (pathFSUB!= null || !pathFSUB.isEmpty()) {
                FSUBName = getNameFromPath(pathFSUB);

            }else{
                FSUBName = "none";
            }
            if (pathNSUB!= null || !pathNSUB.isEmpty()) {
                NSUBoName = getNameFromPath(pathNSUB);
            }else{
                NSUBoName = "none";
            }



//!!!!!!!!!!!!! NEEED STRINGS ****************************************************************

            if (!videoName.isEmpty()) {
                txt_video.setText(videoName);
            }else{
                txt_video.setText("Video");
            }
            if (!FSUBName.isEmpty()) {
                txt_fsub.setText(FSUBName);
            }else{
                txt_fsub.setText("Foreign sub");
            }
            if (!NSUBoName.isEmpty()) {
                txt_nsub.setText(NSUBoName);
            }else{
                txt_nsub.setText("Native sub");
            }
        }

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_video, container, false);
        this.context = inflater.getContext();
        mVideoName = (TextView) rootView.findViewById(R.id.mVideoNameText);

        btn_play_video = (Button) rootView.findViewById(R.id.btn_play_video);
        btn_show = (Button) rootView.findViewById(R.id.btn_show);

        btn_select_video = (Button) rootView.findViewById(R.id.btn_select_video);
        btn_nsub = (Button) rootView.findViewById(R.id.btn_nsub);
        btn_fsub = (Button) rootView.findViewById(R.id.btn_fsub);
        btn_delete_vid = (Button) rootView.findViewById(R.id.btn_delete_vid);

        txt_video = (TextView) rootView.findViewById(R.id.txt_video);
        txt_fsub = (TextView) rootView.findViewById(R.id.txt_fsub);
        txt_nsub = (TextView) rootView.findViewById(R.id.txt_nsub);

        videoThumb = (ImageView) rootView.findViewById(R.id.videoThumb);

        mVideoList = rootView.findViewById(R.id.mVideoList);


        btn_select_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedField = 1;
                showFileChooser();
            }
        });

        btn_fsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedField = 2;
                showFileChooser();
            }
        });







        btn_nsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedField = 3;
                showFileChooser();
            }
        });





//        Bundle bundle = getArguments();
//
//        if (bundle != null && bundle.containsKey("Set")) {
//            sw = (SetWords) bundle.getSerializable("Set");
//            start();
//        } else if (bundle == null) {
////            Toast.makeText(getActivity(), "Error Нет setWords", Toast.LENGTH_LONG).show();
//        }

        return rootView;
    }

    ;

    private void showFileChooser() {

        Intent intent = new Intent("org.openintents.action.PICK_FILE");

        File file = new File(android.os.Environment.getExternalStorageDirectory().getPath());
        intent.setData(Uri.fromFile(file));

        // Set fancy title and button (optional)
        intent.putExtra("org.openintents.extra.TITLE", context.getString(R.string.subtitle_select));
        intent.putExtra("org.openintents.extra.BUTTON_TEXT", context.getString(R.string.open));

        if (context
                .getPackageManager()
                .queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
            ((Activity) context).startActivityForResult(intent, MainActivity.FILE_SELECT_CODE);
        } else {
            // OI intent not found, trying anything
            Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
            intent2.setType("*/*");
            intent2.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                ((Activity) context).startActivityForResult(intent2, MainActivity.FILE_SELECT_CODE);
            } catch (ActivityNotFoundException e) {
                Log.i(TAG, "No file picker found on system");
                Toast.makeText(context,
                        R.string.no_file_picker_found,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void replacePath(String uri) {
        String path = uri.toString();
        String filename = getNameFromPath(path);

        if(VideoFragment.currentVideo == null){
            Util.showMassage(R.string.need_choose_video_set);
            return;
        }

        switch (VideoFragment.selectedField) {
            case 1:
                VideoFragment.txt_video.setText(filename);
                VideoFragment.currentVideo.setVideo(uri);
                break;
            case 2:
                VideoFragment.txt_fsub.setText(filename);
                VideoFragment.currentVideo.setFsub(uri);
                break;
            case 3:
                VideoFragment.txt_nsub.setText(filename);
                VideoFragment.currentVideo.setNsub(uri);
                break;
        }
        VideoFragment.currentVideo.save();
        VideoFragment.selectedField = 0;
    }

    private static String getNameFromPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }


}