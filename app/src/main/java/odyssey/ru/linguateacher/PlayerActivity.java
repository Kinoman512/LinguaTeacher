/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package odyssey.ru.linguateacher;

import android.Manifest.permission;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.accessibility.CaptioningManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecTrackRenderer.DecoderInitializationException;
import com.google.android.exoplayer.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.metadata.id3.ApicFrame;
import com.google.android.exoplayer.metadata.id3.GeobFrame;
import com.google.android.exoplayer.metadata.id3.Id3Frame;
import com.google.android.exoplayer.metadata.id3.PrivFrame;
import com.google.android.exoplayer.metadata.id3.TextInformationFrame;
import com.google.android.exoplayer.metadata.id3.TxxxFrame;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.SubtitleLayout;
import com.google.android.exoplayer.util.DebugTextViewHelper;
import com.google.android.exoplayer.util.MimeTypes;
import com.google.android.exoplayer.util.Util;
import com.google.android.exoplayer.util.VerboseLogUtil;
import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipView;
import com.plumillonforge.android.chipview.OnChipClickListener;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.ling.fragment.view.MySnackBar;
import org.ling.fragment.view.adapter.WordChipAdapter;
import org.ling.model.SetWords;
import org.ling.model.agent.SetWordsAgent;
import org.ling.player.DashRendererBuilder;
import org.ling.player.DemoPlayer;
import org.ling.player.ExtractorRendererBuilder;
import org.ling.player.HlsRendererBuilder;
import org.ling.player.SmoothStreamingRendererBuilder;
import org.ling.srt.InvalidTimestampFormatException;
import org.ling.srt.Subtitle;
import org.ling.srt.SubtitleFile;
import org.ling.utils.FileHelper;
import org.ling.utils.IActionHandler;

/**
 * An activity that plays media using {@link DemoPlayer}.
 */
public class PlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback, OnClickListener,
        DemoPlayer.Listener, DemoPlayer.CaptionListener, DemoPlayer.Id3MetadataListener,
        AudioCapabilitiesReceiver.Listener {


    String TIP_TAG = "PlayerFragmentTip";
    String LIST_TAG = "PlayerFragmentList";

    // For use within demo app code.
    public static final String CONTENT_ID_EXTRA = "content_id";
    public static final String CONTENT_TYPE_EXTRA = "content_type";
    public static final String PROVIDER_EXTRA = "provider";

    // For use when launching the demo app using adb.
    private static final String CONTENT_EXT_EXTRA = "type";

    private static final String TAG = "PlayerActivity";
    private static final int MENU_GROUP_TRACKS = 1;
    private static final int ID_OFFSET = 2;

    private static final CookieManager defaultCookieManager;

    static {
        defaultCookieManager = new CookieManager();
        defaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    public static PlayerActivity activity;
    private EventLogger eventLogger;
    private KeyCompatibleMediaController mediaController;
    private View debugRootView;
    private View shutterView;
    private AspectRatioFrameLayout videoFrame;
    private SurfaceView surfaceView;
    private TextView debugTextView;
    private TextView playerStateTextView;
    private SubtitleLayout subtitleLayout;
    private Button videoButton;
    private Button audioButton;
    private Button textButton;
    private Button retryButton;
    private DemoPlayer player;
    private ChipView chipView;
    private DebugTextViewHelper debugViewHelper;
    private boolean playerNeedsPrepare;

    private long playerPosition;
    private boolean enableBackgroundAudio;

    private Uri contentUri;
    private int contentType;
    private String contentId;
    private String provider;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private Button my_btn;
    private String srt1;
    private String srt2;

    SetWords currentSet;
    // Activity lifecycle
    protected static final String nl = "\\\n";
    protected static final String sp = "[ \\t]*";
    private long start;
    private ProgressBar pbLoading;
    private MySnackBar snackBar;
    private TextView text_part_sub;
    private String tempSubString;
    private String finalSubString = "";
    private SubtitleFile exampleFile;
    private int sub = 0;
    private Thread thread;
    private int maxSub;
    private Button btn_reload;
    private long startSub = 0;
    private int endSub = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        activity = this;
        srt1 = FileHelper.getPath() + "GOT.RUS.srt";
        srt2 = FileHelper.getPath() + "GOT.ENG.srt";
        List<String> listWords = new ArrayList<>();
        String str = "";
        try {
            SubtitleFile exampleFile = new SubtitleFile(new File(srt2));
            List<Subtitle> list = exampleFile.getSubtitles();


            listWords = list.get(0).getFullWords();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidTimestampFormatException e) {
            e.printStackTrace();
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        View root = findViewById(R.id.videoContainer);
        root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
                return true;
            }
        });
        root.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE
                        || keyCode == KeyEvent.KEYCODE_MENU) {
                    return false;
                }
                return mediaController.dispatchKeyEvent(event);
            }
        });


        final RelativeLayout listSetWords = (RelativeLayout) findViewById(R.id.mSetWords2);
        text_part_sub = (TextView) findViewById(R.id.text_part_sub);
        final TextView mSetName = (TextView) findViewById(R.id.mSetName);
        final TextView textLangs = (TextView) findViewById(R.id.textLangs);
        final List<String> list = new ArrayList<>();

        final List<SetWords> listSet = SetWordsAgent.getAll();

        if (listSet.size() != 0) {
            int pos = Setting.getInt(LIST_TAG);
            if (pos >= listSet.size()) {
                pos = 0;
            }
            currentSet = listSet.get(pos);
            mSetName.setText(currentSet.getName());
            for (SetWords e : listSet) {

                list.add(e.getName());
            }
            textLangs.setText(currentSet.getLang() + " -> " + currentSet.getNatv());

        }

        listSetWords.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.setActivity(PlayerActivity.this);
                Dialog.showListDialog(MainActivity.getStringById(R.string.select_dict), list, null, new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        int pos = (Integer) rs;
                        currentSet = listSet.get(pos);

                        Setting.setInt(LIST_TAG, pos);
                        mSetName.setText(currentSet.getName());
                        textLangs.setText(currentSet.getLang() + " -> " + currentSet.getNatv());
                    }

                    @Override
                    public void onFailAction(String s, Throwable throwable) {
                    }
                });
            }
        });

        shutterView = findViewById(R.id.shutter);
        debugRootView = findViewById(R.id.controls_root);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

        videoFrame = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
        debugTextView = (TextView) findViewById(R.id.debug_text_view);

        playerStateTextView = (TextView) findViewById(R.id.player_state_view);
        subtitleLayout = (SubtitleLayout) findViewById(R.id.subtitles);
        LinearLayout videoContainer = (LinearLayout) findViewById(R.id.videoContainer);

        mediaController = new KeyCompatibleMediaController(this);
        mediaController.setAnchorView(videoContainer);

//    mediaController.setAnchorView(root);
        retryButton = (Button) findViewById(R.id.retry_button);
        retryButton.setOnClickListener(this);
        videoButton = (Button) findViewById(R.id.video_controls);
        audioButton = (Button) findViewById(R.id.audio_controls);
        textButton = (Button) findViewById(R.id.text_controls);
        btn_reload = (Button) findViewById(R.id.btn_reload);
        my_btn = (Button) findViewById(R.id.my_btn);
        chipView = (ChipView) findViewById(R.id.chip_attrs);

        subtitleLayout.setApplyEmbeddedStyles(false);

        btn_reload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekTo(startSub);
                player.setPlayWhenReady(true);
                if (endSub == 0) return;

                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int tSub = sub;
                        while (true) {
                            try {
                                Thread.sleep(endSub - startSub);

                                if (tSub != sub) {
                                    return;
                                }
                                if (player != null && player.getCurrentPosition() > endSub) {

                                    PlayerActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            player.getPlayerControl().pause();
//                                        player.seekTo(start);
                                        }
                                    });
                                    return;

                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
//

                    }
                });
                thread.start();


            }
        });

        my_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toNextSub();
            }
        });

        CookieHandler currentHandler = CookieHandler.getDefault();
        if (currentHandler != defaultCookieManager) {
            CookieHandler.setDefault(defaultCookieManager);
        }

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(this, this);
        audioCapabilitiesReceiver.register();


    }


    public Subtitle showWordChipsByPositionSub(int pos) {
        try {
            if (exampleFile == null)
                exampleFile = new SubtitleFile(new File(srt2));

            Subtitle sb = exampleFile.getSubtitle(pos);
            maxSub = exampleFile.getSubtitles().size();

            if (sub >= maxSub) {
                Toast.makeText(this, "Вы достигли конца субтиров", Toast.LENGTH_SHORT).show();
                return null;
            }

            showWordChips(sb);
            return sb;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidTimestampFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showWordChipsByTime(int time) {
        try {
            if (exampleFile == null)
                exampleFile = new SubtitleFile(new File(srt2));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidTimestampFormatException e) {
            e.printStackTrace();
        }
        Subtitle sb = exampleFile.getSubtitleByTime(time);
        showWordChips(sb);
    }


    public void showWordChips(Subtitle sb) {

        List<Chip> mTagList1 = new ArrayList<Chip>();
        final List<Chip> mTagListDefault = new ArrayList<Chip>();


        if (sb == null) return;
        String subString = "";
        subString = sb.getFullLines();

        List<String> lstrs = sb.getFullWords();
        for (String e : lstrs) {
            mTagList1.add(new WordTag(e));
        }

        long seed = System.nanoTime();
        mTagListDefault.addAll(mTagList1);
        if (mTagListDefault.size() <= 1) {
            toNextSub();
            return;
        }


        Collections.shuffle(mTagList1, new Random(seed));
        posSub = 0;
        snackBar = new MySnackBar(this, getLayoutInflater(), findViewById(R.id.root));
        chipView.setAdapter(new WordChipAdapter(this, currentSet, pbLoading, snackBar));
        chipView.setChipList(mTagList1);


        text_part_sub.setText("");
        finalSubString = "";
        tempSubString = subString.toLowerCase().replaceAll("\n", " ");
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

                    text_part_sub.setText(finalSubString);
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
                                MainActivity.activity.runOnUiThread(new Runnable() {
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


    }

    private void toNextSub() {
        toNextSub(sub);
    }

    private void toNextSub(int num) {

        sub = num;
        sub++;
        Subtitle sb = showWordChipsByPositionSub(sub);
        if (sb != null) {
            startSub = sb.getStartTime().getAllMilliseconds();
            endSub = sb.getEndTime().getAllMilliseconds();

//                    player.getPlayerControl().seekTo(start);
//                    player.setPlayWhenReady(true);
//            player.seekTo(start);
            player.setPlayWhenReady(true);


            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int tSub = sub;
                    while (true) {
                        try {
                            Thread.sleep(endSub - startSub);

                            if (tSub != sub) {
                                return;
                            }
                            if (player != null && player.getCurrentPosition() > endSub) {

                                PlayerActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        player.getPlayerControl().pause();
//                                        player.seekTo(start);
                                    }
                                });
                                return;

                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
//

                }
            });
            thread.start();

        }

    }

    @Override
    public void onNewIntent(Intent intent) {
        releasePlayer();
        playerPosition = 0;
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            onShown();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            onShown();
        }
    }

    private void onShown() {
        Intent intent = getIntent();
        contentUri = intent.getData();
        contentType = intent.getIntExtra(CONTENT_TYPE_EXTRA,
                inferContentType(contentUri, intent.getStringExtra(CONTENT_EXT_EXTRA)));
        contentId = intent.getStringExtra(CONTENT_ID_EXTRA);
        provider = intent.getStringExtra(PROVIDER_EXTRA);
        configureSubtitleView();
        if (player == null) {
            if (!maybeRequestPermission()) {
                preparePlayer(true);
            }
        } else {
            player.setBackgrounded(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            onHidden();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            onHidden();
        }
    }

    private void onHidden() {
        if (!enableBackgroundAudio) {
            releasePlayer();
        } else {
            player.setBackgrounded(true);
        }
        shutterView.setVisibility(View.GONE);
//    shutterView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
        releasePlayer();
    }

    // OnClickListener methods

    @Override
    public void onClick(View view) {
        if (view == retryButton) {
            preparePlayer(true);
        }
    }

    // AudioCapabilitiesReceiver.Listener methods

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        boolean backgrounded = player.getBackgrounded();
        boolean playWhenReady = player.getPlayWhenReady();
        releasePlayer();
        preparePlayer(playWhenReady);
        player.setBackgrounded(backgrounded);
    }

    // Permission request listener method

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            preparePlayer(true);
        } else {
            Toast.makeText(getApplicationContext(), "storage_permission_denied",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // Permission management methods

    /**
     * Checks whether it is necessary to ask for permission to read storage. If necessary, it also
     * requests permission.
     *
     * @return true if a permission request is made. False if it is not necessary.
     */
    @TargetApi(23)
    private boolean maybeRequestPermission() {
        if (requiresPermission(contentUri)) {
            requestPermissions(new String[]{permission.READ_EXTERNAL_STORAGE}, 0);
            return true;
        } else {
            return false;
        }
    }

    @TargetApi(23)
    private boolean requiresPermission(Uri uri) {
        return Util.SDK_INT >= 23
                && Util.isLocalFileUri(uri)
                && checkSelfPermission(permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    // Internal methods

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        switch (contentType) {
            case Util.TYPE_SS:
                return new SmoothStreamingRendererBuilder(this, userAgent, contentUri.toString(),
                        new SmoothStreamingTestMediaDrmCallback());
            case Util.TYPE_DASH:
                return new DashRendererBuilder(this, userAgent, contentUri.toString(),
                        new WidevineTestMediaDrmCallback(contentId, provider));
            case Util.TYPE_HLS:
                return new HlsRendererBuilder(this, userAgent, contentUri.toString());
            case Util.TYPE_OTHER: //
                return new ExtractorRendererBuilder(this, userAgent, contentUri, srt1, srt2, this);
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }
    }

    private void preparePlayer(boolean playWhenReady) {
        if (player == null) {
            player = new DemoPlayer(getRendererBuilder());
            player.addListener(this);
            player.setCaptionListener(this);
            player.setMetadataListener(this);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;
            mediaController.setMediaPlayer(player.getPlayerControl());
            mediaController.setEnabled(true);
//      mediaController.setEnabled(false);
            eventLogger = new EventLogger();
            eventLogger.startSession();
            player.addListener(eventLogger);
            player.setInfoListener(eventLogger);
            player.setInternalErrorListener(eventLogger);
            debugViewHelper = new DebugTextViewHelper(player, debugTextView);
            debugViewHelper.start();
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
            updateButtonVisibilities();
        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(playWhenReady);


    }

    private void releasePlayer() {
        if (player != null) {
            debugViewHelper.stop();
            debugViewHelper = null;
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
            eventLogger.endSession();
            eventLogger = null;
        }
    }

    // DemoPlayer.Listener implementation

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            showControls();
        }
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                break;
            default:
                text += "unknown";
                break;
        }
        playerStateTextView.setText(text);
        updateButtonVisibilities();
    }

    @Override
    public void onError(Exception e) {
        String errorString = null;
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            errorString = "error_drm";//getString(Util.SDK_INT < 18 ? "R.string.error_drm_not_supported"
//          : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
//          ? "R.string.error_drm_unsupported_scheme" : "R.string.error_drm_unknown");
        } else if (e instanceof ExoPlaybackException
                && e.getCause() instanceof DecoderInitializationException) {
            // Special case for decoder initialization failures.
            DecoderInitializationException decoderInitializationException =
                    (DecoderInitializationException) e.getCause();
            if (decoderInitializationException.decoderName == null) {
                if (decoderInitializationException.getCause() instanceof DecoderQueryException) {
                    errorString = getString(R.string.error_querying_decoders);
                } else if (decoderInitializationException.secureDecoderRequired) {
                    errorString = getString(R.string.error_no_secure_decoder,
                            decoderInitializationException.mimeType);
                } else {
                    errorString = getString(R.string.error_no_decoder,
                            decoderInitializationException.mimeType);
                }
            } else {
                errorString = getString(R.string.error_instantiating_decoder,
                        decoderInitializationException.decoderName);
            }
        }
        if (errorString != null) {
            Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
        }
        playerNeedsPrepare = true;
        updateButtonVisibilities();
        showControls();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthAspectRatio) {
        shutterView.setVisibility(View.GONE);
        videoFrame.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }

    // User controls

    private void updateButtonVisibilities() {
        retryButton.setVisibility(playerNeedsPrepare ? View.VISIBLE : View.GONE);
        videoButton.setVisibility(haveTracks(DemoPlayer.TYPE_VIDEO) ? View.VISIBLE : View.GONE);
        audioButton.setVisibility(haveTracks(DemoPlayer.TYPE_AUDIO) ? View.VISIBLE : View.GONE);
        textButton.setVisibility(haveTracks(DemoPlayer.TYPE_TEXT) ? View.VISIBLE : View.GONE);
    }

    private boolean haveTracks(int type) {
        return player != null && player.getTrackCount(type) > 0;
    }

    public void showVideoPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        configurePopupWithTracks(popup, null, DemoPlayer.TYPE_VIDEO);
        popup.show();
    }

    public void showAudioPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        Menu menu = popup.getMenu();
        menu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.enable_background_audio);
        final MenuItem backgroundAudioItem = menu.findItem(0);
        backgroundAudioItem.setCheckable(true);
        backgroundAudioItem.setChecked(enableBackgroundAudio);
        OnMenuItemClickListener clickListener = new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item == backgroundAudioItem) {
                    enableBackgroundAudio = !item.isChecked();
                    return true;
                }
                return false;
            }
        };
        configurePopupWithTracks(popup, clickListener, DemoPlayer.TYPE_AUDIO);
        popup.show();
    }

    public void showTextPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        configurePopupWithTracks(popup, null, DemoPlayer.TYPE_TEXT);
        popup.show();
    }

    public void showVerboseLogPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        Menu menu = popup.getMenu();
        menu.add(Menu.NONE, 0, Menu.NONE, R.string.logging_normal);
        menu.add(Menu.NONE, 1, Menu.NONE, R.string.logging_verbose);
        menu.setGroupCheckable(Menu.NONE, true, true);
        menu.findItem((VerboseLogUtil.areAllTagsEnabled()) ? 1 : 0).setChecked(true);
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == 0) {
                    VerboseLogUtil.setEnableAllTags(false);
                } else {
                    VerboseLogUtil.setEnableAllTags(true);
                }
                return true;
            }
        });
        popup.show();
    }

    private void configurePopupWithTracks(PopupMenu popup,
                                          final OnMenuItemClickListener customActionClickListener,
                                          final int trackType) {
        if (player == null) {
            return;
        }
        int trackCount = player.getTrackCount(trackType);
        if (trackCount == 0) {
            return;
        }
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return (customActionClickListener != null
                        && customActionClickListener.onMenuItemClick(item))
                        || onTrackItemClick(item, trackType);
            }
        });
        Menu menu = popup.getMenu();
        // ID_OFFSET ensures we avoid clashing with Menu.NONE (which equals 0).
        menu.add(MENU_GROUP_TRACKS, DemoPlayer.TRACK_DISABLED + ID_OFFSET, Menu.NONE, R.string.off);
        for (int i = 0; i < trackCount; i++) {
            menu.add(MENU_GROUP_TRACKS, i + ID_OFFSET, Menu.NONE,
                    buildTrackName(player.getTrackFormat(trackType, i)));
        }
        menu.setGroupCheckable(MENU_GROUP_TRACKS, true, true);
        menu.findItem(player.getSelectedTrack(trackType) + ID_OFFSET).setChecked(true);
    }

    private static String buildTrackName(MediaFormat format) {
        if (format.adaptive) {
            return "auto";
        }
        String trackName;
        if (MimeTypes.isVideo(format.mimeType)) {
            trackName = joinWithSeparator(joinWithSeparator(buildResolutionString(format),
                    buildBitrateString(format)), buildTrackIdString(format));
        } else if (MimeTypes.isAudio(format.mimeType)) {
            trackName = joinWithSeparator(joinWithSeparator(joinWithSeparator(buildLanguageString(format),
                            buildAudioPropertyString(format)), buildBitrateString(format)),
                    buildTrackIdString(format));
        } else {
            trackName = joinWithSeparator(joinWithSeparator(buildLanguageString(format),
                    buildBitrateString(format)), buildTrackIdString(format));
        }
        return trackName.length() == 0 ? "unknown" : trackName;
    }

    private static String buildResolutionString(MediaFormat format) {
        return format.width == MediaFormat.NO_VALUE || format.height == MediaFormat.NO_VALUE
                ? "" : format.width + "x" + format.height;
    }

    private static String buildAudioPropertyString(MediaFormat format) {
        return format.channelCount == MediaFormat.NO_VALUE || format.sampleRate == MediaFormat.NO_VALUE
                ? "" : format.channelCount + "ch, " + format.sampleRate + "Hz";
    }

    private static String buildLanguageString(MediaFormat format) {
        return TextUtils.isEmpty(format.language) || "und".equals(format.language) ? ""
                : format.language;
    }

    private static String buildBitrateString(MediaFormat format) {
        return format.bitrate == MediaFormat.NO_VALUE ? ""
                : String.format(Locale.US, "%.2fMbit", format.bitrate / 1000000f);
    }

    private static String joinWithSeparator(String first, String second) {
        return first.length() == 0 ? second : (second.length() == 0 ? first : first + ", " + second);
    }

    private static String buildTrackIdString(MediaFormat format) {
        return format.trackId == null ? "" : " (" + format.trackId + ")";
    }

    private boolean onTrackItemClick(MenuItem item, int type) {
        if (player == null || item.getGroupId() != MENU_GROUP_TRACKS) {
            return false;
        }
        player.setSelectedTrack(type, item.getItemId() - ID_OFFSET);
        return true;
    }

    private void toggleControlsVisibility() {
        if (mediaController.isShowing()) {
            mediaController.hide();
            debugRootView.setVisibility(View.GONE);
        } else {
            showControls();
        }
    }

    private void showControls() {
        mediaController.show(0);
        debugRootView.setVisibility(View.VISIBLE);
    }

    // DemoPlayer.CaptionListener implementation

    int posSub = 0;

    @Override
    public void onCues(List<Cue> cues) {
        subtitleLayout.setCues(cues);


    }

    // DemoPlayer.MetadataListener implementation

    @Override
    public void onId3Metadata(List<Id3Frame> id3Frames) {
        for (Id3Frame id3Frame : id3Frames) {
            if (id3Frame instanceof TxxxFrame) {
                TxxxFrame txxxFrame = (TxxxFrame) id3Frame;
                Log.i(TAG, String.format("ID3 TimedMetadata %s: description=%s, value=%s", txxxFrame.id,
                        txxxFrame.description, txxxFrame.value));
            } else if (id3Frame instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) id3Frame;
                Log.i(TAG, String.format("ID3 TimedMetadata %s: owner=%s", privFrame.id, privFrame.owner));
            } else if (id3Frame instanceof GeobFrame) {
                GeobFrame geobFrame = (GeobFrame) id3Frame;
                Log.i(TAG, String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s",
                        geobFrame.id, geobFrame.mimeType, geobFrame.filename, geobFrame.description));
            } else if (id3Frame instanceof ApicFrame) {
                ApicFrame apicFrame = (ApicFrame) id3Frame;
                Log.i(TAG, String.format("ID3 TimedMetadata %s: mimeType=%s, description=%s",
                        apicFrame.id, apicFrame.mimeType, apicFrame.description));
            } else if (id3Frame instanceof TextInformationFrame) {
                TextInformationFrame textInformationFrame = (TextInformationFrame) id3Frame;
                Log.i(TAG, String.format("ID3 TimedMetadata %s: description=%s", textInformationFrame.id,
                        textInformationFrame.description));
            } else {
                Log.i(TAG, String.format("ID3 TimedMetadata %s", id3Frame.id));
            }
        }
    }

    // SurfaceHolder.Callback implementation

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }

    private void configureSubtitleView() {
        CaptionStyleCompat style;
        float fontScale;
        if (Util.SDK_INT >= 19) {
            style = getUserCaptionStyleV19();
            fontScale = getUserCaptionFontScaleV19();
        } else {
            style = CaptionStyleCompat.DEFAULT;
            fontScale = 1.0f;
        }
        subtitleLayout.setStyle(style);
        subtitleLayout.setFractionalTextSize(SubtitleLayout.DEFAULT_TEXT_SIZE_FRACTION * fontScale);
    }

    @TargetApi(19)
    private float getUserCaptionFontScaleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
        return captioningManager.getFontScale();
    }

    @TargetApi(19)
    private CaptionStyleCompat getUserCaptionStyleV19() {
        CaptioningManager captioningManager =
                (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
        return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
    }

    /**
     * Makes a best guess to infer the type from a media {@link Uri} and an optional overriding file
     * extension.
     *
     * @param uri           The {@link Uri} of the media.
     * @param fileExtension An overriding file extension.
     * @return The inferred type.
     */
    private static int inferContentType(Uri uri, String fileExtension) {
        String lastPathSegment = !TextUtils.isEmpty(fileExtension) ? "." + fileExtension
                : uri.getLastPathSegment();
        return Util.inferContentType(lastPathSegment);
    }

    private static final class KeyCompatibleMediaController extends MediaController {

        private MediaPlayerControl playerControl;

        public KeyCompatibleMediaController(Context context) {
            super(context);
        }

        @Override
        public void setMediaPlayer(MediaPlayerControl playerControl) {
            super.setMediaPlayer(playerControl);
            this.playerControl = playerControl;
        }

        public void seekTo(int time) {
            playerControl.seekTo(time);
            show();
            playerControl.start();
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (playerControl.canSeekForward() && (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                    || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    playerControl.seekTo(playerControl.getCurrentPosition() + 20000); // milliseconds
                    show();
                }
                return true;
            } else if (playerControl.canSeekBackward() && (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                    || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    playerControl.seekTo(playerControl.getCurrentPosition() - 5000); // milliseconds
                    show();
                }
                return true;
            }
            return super.dispatchKeyEvent(event);
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return false;
        }

        if (MySnackBar.hideIfshow()) {
            return false;
        }


//        if ((System.currentTimeMillis() - start) > 3000) {
//            Toast.makeText(getApplicationContext(), R.string.press_again_text, Toast.LENGTH_SHORT).show();
//            start = System.currentTimeMillis();
//            return false;
//        }


        return super.onKeyDown(keyCode, event);
    }


}
