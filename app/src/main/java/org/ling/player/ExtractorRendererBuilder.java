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
package org.ling.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.DecoderInfo;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.SingleSampleSource;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.extractor.Extractor;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.text.TextTrackRenderer;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.AssetDataSource;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.MimeTypes;

import cn.pedant.SweetAlert.SweetAlertDialog;
import odyssey.ru.linguateacher.R;

/**
 * A {@link DemoPlayer.RendererBuilder} for streams that can be read using an {@link Extractor}.
 */
public class ExtractorRendererBuilder implements DemoPlayer.RendererBuilder {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    private final Context context;
    private final String userAgent;
    private final Uri uri;
    private String srt1 = "";
    private String srt2 = "";
    private AppCompatActivity activity;
    private boolean isShowTip = false;

    public ExtractorRendererBuilder(Context context, String userAgent, Uri uri, String srt1, String srt2, AppCompatActivity activity) {
        this.context = context;
        this.userAgent = userAgent;
        this.uri = uri;
        this.srt1 = srt1;
        this.srt2 = srt2;
        this.activity = activity;
    }

    @Override
    public void buildRenderers(DemoPlayer player) {
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        Handler mainHandler = player.getMainHandler();

        // Build the video and audio renderers.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(mainHandler, null);


        DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);

        ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
                BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE, mainHandler, player, 0);

        MediaFormat mediaFormat = MediaFormat.createTextFormat("123", MimeTypes.APPLICATION_SUBRIP, MediaFormat.NO_VALUE, C.MATCH_LONGEST_US, null);


        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(context,
                sampleSource, new MediaCodecSelector() {
            @Override
            public DecoderInfo getDecoderInfo(String mimeType, boolean requiresSecureDecoder)
                    throws MediaCodecUtil.DecoderQueryException {
                DecoderInfo info = MediaCodecUtil.getDecoderInfo(mimeType, requiresSecureDecoder);
                if (info == null) {
                    tip("Видео кодек " + mimeType + " не поддерживается. Пожалуйста, выберите другой фаил");
                }
                return info;
            }

            @Override
            public DecoderInfo getPassthroughDecoderInfo() throws MediaCodecUtil.DecoderQueryException {
                Log.d("1234Android", MediaCodecUtil.getPassthroughDecoderInfo() + "");
                return MediaCodecUtil.getPassthroughDecoderInfo();
            }
        }, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING, 9000,
                mainHandler, player, 50);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                new MediaCodecSelector() {
                    @Override
                    public DecoderInfo getDecoderInfo(String mimeType, boolean requiresSecureDecoder)
                            throws MediaCodecUtil.DecoderQueryException {
                        DecoderInfo info = MediaCodecUtil.getDecoderInfo(mimeType, requiresSecureDecoder);
                        if (info == null) {
                            tip("Аудио кодек " + mimeType + " не поддерживается. Пожалуйста, выберите другой фаил");
                        }
                        return info;
                    }

                    @Override
                    public DecoderInfo getPassthroughDecoderInfo() throws MediaCodecUtil.DecoderQueryException {
                        Log.d("1234Android", MediaCodecUtil.getPassthroughDecoderInfo() + "");
                        return MediaCodecUtil.getPassthroughDecoderInfo();
                    }
                }, null, true, mainHandler, player,
                AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);


//        DataSource dataSource = new FileDataSource();//DefaultUriDataSource(context, bandwidthMeter, userAgent);
        Uri textUri = Uri.parse(srt1);
        Uri textUri2 = Uri.parse(srt2);


        SingleSampleSource textSampleSource4 = new SingleSampleSource(textUri, new AssetDataSource(context), mediaFormat);
        SingleSampleSource textSampleSource2 = new SingleSampleSource(textUri2, new AssetDataSource(context), mediaFormat);
//        textSampleSource.disable(0);
//        textSampleSource2.disable(0);
//        SampleSource sampleArr[] = new SampleSource[2];
        SampleSource sampleArr[] = new SampleSource[2];

        sampleArr[1] = textSampleSource2;
        sampleArr[0] = textSampleSource4;


        TrackRenderer textRenderer = new TextTrackRenderer(
                sampleArr,
//                textSampleSource4,
                player,
                mainHandler.getLooper());


//    MediaFormat mediaFormat = MediaFormat.createTextFormat("0", MimeTypes.APPLICATION_SUBRIP, MediaFormat.NO_VALUE, C.MATCH_LONGEST_US, null);
//
//
//    SingleSampleSource textSampleSource = new SingleSampleSource(uri, new AssetDataSource(context), mediaFormat);
//    TrackRenderer textRenderer = new TextTrackRenderer(textSampleSource, player,
//            mainHandler.getLooper());

//    TrackRenderer textRenderer;
//    boolean haveSubtitles = true;
//
//
//
//    DataSource textDataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
//    SingleSampleSource textSampleSource = new SingleSampleSource(textUri, textDataSource,
//            MediaFormat.createTextFormat("0", MimeTypes.APPLICATION_SUBRIP, MediaFormat.NO_VALUE, C.MATCH_LONGEST_US, null));


//      textRenderer = new TextTrackRenderer(textSampleSource, player,
//            player.getMainHandler().getLooper());


        // Invoke the callback.
        TrackRenderer[] renderers = new TrackRenderer[DemoPlayer.RENDERER_COUNT];
        renderers[DemoPlayer.TYPE_VIDEO] = videoRenderer;
        renderers[DemoPlayer.TYPE_AUDIO] = audioRenderer;
        renderers[DemoPlayer.TYPE_TEXT] = textRenderer;
        player.onRenderers(renderers, bandwidthMeter);
    }

    @Override
    public void cancel() {
        // Do nothing.
    }

    SweetAlertDialog sld2;

    void tip(String tip) {
        if(!isShowTip){
            return;
        }

        if (sld2 != null) return;
        sld2 = new SweetAlertDialog(activity)
                .setTitleText(activity.getResources().getString(R.string.tip))
                .setContentText(tip)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        sld2 = null;
                        isShowTip = true;
                    }
                });
        sld2.show();
    }

}
