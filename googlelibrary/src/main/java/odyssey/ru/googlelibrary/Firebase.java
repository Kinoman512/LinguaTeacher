package odyssey.ru.googlelibrary;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


/**
 * Created by Dmitry on 29.11.2016.
 */


public class Firebase {

    private static InterstitialAd mInterstitialAd;
    private static boolean stoped ;
    private static AdView mAdView;


    public static void mobileAdsInit(){

        if(stoped){
            return;
        }
        // Initialize the Mobile Ads SDK.
       // MobileAds.initialize(GoogleLibrary.getApp(), "ca-app-pub-7734400213523992~6231402263");
        MobileAds.initialize(GoogleLibrary.getApp(), "ca-app-pub-7734400213523992~7811136261");

        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(GoogleLibrary.getApp());
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId("ca-app-pub-7734400213523992/2576571864");
       // mInterstitialAd.setAdUnitId("ca-app-pub-7734400213523992/4615068261");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
//                startGame();
            }
        });
    }



    public static void showInterstitial() {

        if(stoped){
            return;
        }


        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
           // Toast.makeText(GoogleLibrary.getApp(), "Ad did not load", Toast.LENGTH_SHORT).show();
        }
    }

    public static void loadInterstitial() {
        if(stoped){
            return;
        }

        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }
    }

    public static void loadAdView(AdView adView) {

        if(stoped){
            return;
        }
        mAdView = adView;
        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }


    public static void stop() {

        stoped = true;
        if(mAdView!= null){
            mAdView.setVisibility(View.GONE);
        }
    }

    public static void start() {

        stoped = false;
        if(mAdView!= null){
            mAdView.setVisibility(View.VISIBLE);
        }
    }




}
