package odyssey.ru.linguateacher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.exoplayer.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import org.ling.fragment.SetWordsFragment;
import org.ling.fragment.TrainFragment;
import org.ling.fragment.TrainRoomFragment;
import org.ling.fragment.VocabularyFragment;
import org.ling.fragment.WebFragment;
import org.ling.fragment.view.CustomViewPager;
import org.ling.fragment.view.MySnackBar;
import org.ling.utils.FileHelper;
import org.ling.utils.FirstPageFragmentListener;
import org.ling.utils.Lang;

public class MainActivity extends AppCompatActivity {
    private static final String VERSION_PRODUCT = "0.0.1";
    private static final String AUTHOR_PRODUCT = "Dmitry Gribkov      ";
    private static final String VK_LINK =        "vk.com/odyssey512   ";
    private static final String EMAIL_LINK =     "kinoman512@gmail.com";

    static FragmentManager fragmentManager;
    static String filedir;
    public static MainActivity activity;
    long start;
    public static Toolbar toolbar;
    static public Menu menu;

    private AdView mAdView;


    static int tabIndex = 0;
    static int oldPos = 0;
    TabLayout tabLayout;
    static CustomViewPager viewPager;
    MyAdapter mAdapter;
    Drawable[] imageArray;

    static MyAdapter.PageListener listener;
    private Drawable[] imageArray2;

    public static void switchToNextFragment(Fragment fr) {
        listener.onSwitchToNextFragment(fr);
    }

    public int getCurrentTab() {
        return viewPager.getCurrentItem();
    }

    public static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    public static String getStringById(int id){
        return  MainActivity.activity.getString(id);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new             ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            View miss = view.findViewById(R.id.stage_holder);
            int minusHeight = 0;
            if(miss!= null){
                miss.measure(0, 0);
                minusHeight = miss.getMeasuredHeight();
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight() - minusHeight;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void videoStart() {
//        Samples.Sample sample   = new Samples.Sample("Google Glass (MP4,H264)",
//                "http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?"
//                        + "as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&"
//                        + "8506521BFC350652163895D4C26DEE124209AA9E&key=ik0", Util.TYPE_DASH);

        Samples.Sample sample   = new Samples.Sample("Google Glass (MP4,H264)",
                FileHelper.getPath() + "GOT.mkv", Util.TYPE_OTHER);
//        Samples.Sample sample   = new Samples.Sample("Google Glass (MP4,H264)",
//                "file:///android_asset/12345.mp4", Util.TYPE_OTHER);
        Intent mpdIntent = new Intent(this, PlayerActivity.class)
                .setData(Uri.parse(sample.uri))
                .putExtra(PlayerActivity.CONTENT_ID_EXTRA, sample.contentId)
                .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, sample.type)
                .putExtra(PlayerActivity.PROVIDER_EXTRA, sample.provider);
        startActivity(mpdIntent);
    }

    public void onCreate(Bundle savedInstanceState) {
//        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                new YandexServer().initSID();
//            }
//        }).start();






        filedir = getFilesDir().getPath().toString();
        activity = this;
        fragmentManager = getSupportFragmentManager();
        //setCurrentFragment(new MenuFragment(), false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mAdView = (AdView) findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);


        Lang lang1 = Lang.findByCodeCountry("ly");
        Lang lang2 = Lang.findByCodeCountry("lv");
        Lang lang3 = Lang.findByCodeCountry("eo");
        Lang lang4 = Lang.findByCodeCountry("ge");

        String str1 = lang1.getCodeCountry();
        String str2 = lang2.getCodeCountry();
        String str3 = lang3.getCodeCountry();
        String str4 = lang4.getCodeCountry();

        final List<Lang> arr = Lang.getAllLocale();
        Map<String,String> map = new HashMap<>();
        for (int i = 0; i < arr.size(); i++) {
            String stra = arr.get(i).getCodeCountry();
            String strb = arr.get(i).getDescription();
            map.put(stra,strb);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Drawable icon = new IconicsDrawable(this, FontAwesome.Icon.faw_internet_explorer).color(Color.BLACK);
        Drawable imag4 = icon.getCurrent();

        //Add tabs icon with setIcon() or simple text with .setText()
        Drawable image = new IconicsDrawable(this, FontAwesome.Icon.faw_star).color(Color.BLACK);
        Drawable image2 = new IconicsDrawable(this, FontAwesome.Icon.faw_apple).color(Color.BLACK);
        Drawable image3 = new IconicsDrawable(this, FontAwesome.Icon.faw_internet_explorer).color(Color.BLACK);
        Drawable image4 = new IconicsDrawable(this, FontAwesome.Icon.faw_bed).color(Color.BLACK);
        Drawable image5 = new IconicsDrawable(this, FontAwesome.Icon.faw_bed).color(Color.BLACK);

        Drawable image1s = new IconicsDrawable(this, FontAwesome.Icon.faw_star).color(Color.WHITE);
        Drawable image2s = new IconicsDrawable(this, FontAwesome.Icon.faw_apple).color(Color.WHITE);
        Drawable image3s = new IconicsDrawable(this, FontAwesome.Icon.faw_internet_explorer).color(Color.WHITE);
        Drawable image4s = new IconicsDrawable(this, FontAwesome.Icon.faw_bed).color(Color.WHITE);
        Drawable image5s = new IconicsDrawable(this, FontAwesome.Icon.faw_bed).color(Color.WHITE);

        imageArray = new Drawable[]{
                image, image2, image3, image4, image5
        };

        imageArray2 = new Drawable[]{
                image1s, image2s, image3s, image4s, image5s
        };
        setSupportActionBar(toolbar);

//        getSupportActionBar().setIcon(R.drawable.luna_ic);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(" " + getResources().getString( R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


//        DrawPanel.prepare(this, toolbar);
//        DrawPanel.start();
        start = System.currentTimeMillis();

        super.onCreate(savedInstanceState);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (CustomViewPager) findViewById(R.id.pager);
        mAdapter = new MyAdapter(getSupportFragmentManager());

        tabLayout.addTab(tabLayout.newTab().setIcon(image1s));
        tabLayout.addTab(tabLayout.newTab().setIcon(image2));
        tabLayout.addTab(tabLayout.newTab().setIcon(image3));
//        //Setting adapter
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(" " + mAdapter.getPageTitle(position));
                TabLayout.Tab tab1 = tabLayout.getTabAt(oldPos);
                TabLayout.Tab tab2 = tabLayout.getTabAt(position);
                tab1.setIcon(imageArray[oldPos]);
                tab2.setIcon(imageArray2[position]);
                oldPos =position;
                if (position == 2) {
                    mAdapter.showSearchBar();
                } else {
                    mAdapter.hideSearchBar();
                }
                mAdapter.resumeFragment();
            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                //Toast.makeText(this,"123", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ExtraActivity.class);
                startActivity(intent);
                return true;
            case R.id.videoStart:
                //Toast.makeText(this,"123", Toast.LENGTH_SHORT).show();
                videoStart();
                return true;
            case R.id.about:
                String version = "\n " + getResources().getString(R.string.version_text) + " " + getVersion();
                String author =   getResources().getString(R.string.developer_text) +" \n\n " + getAuhor();
                String email =   getEmail();
                String vkLink =  getVKLink();
                String about = getResources().getString(R.string.about_program_text);

                new SweetAlertDialog(MainActivity.activity)
                        .setTitleText(about)
                        .setContentText(
                                version + "\n"+
                                author + ""+
                                email + "\n"+
                                vkLink + "\n"
                        )
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();

//                Toast.makeText(this,"123", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static class MyAdapter extends FragmentPagerAdapter {


        public void hideSearchBar() {
            if (mFragmentAtPos2 instanceof WebFragment) {
                ((WebFragment) mFragmentAtPos2).hideSearchView();
            }
        }

        public void showSearchBar() {
            if (mFragmentAtPos2 instanceof WebFragment) {
                ((WebFragment) mFragmentAtPos2).showSearchView();
            }
        }

        private final class PageListener implements
                FirstPageFragmentListener {
            public void onSwitchToNextFragment(Fragment fr) {

                Fragment mFragment = null;
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        mFragment = mFragmentAtPos0;
                        mFragmentAtPos0 = fr;
                        break;
                    case 1:
                        mFragment = mFragmentAtPos1;
                        mFragmentAtPos1 = fr;
                        break;
                    case 2:
                        mFragment = mFragmentAtPos2;
                        mFragmentAtPos2 = fr;
                        break;
//                    case 3:
//                        mFragment = mFragmentAtPos3;
//                        mFragmentAtPos3 = fr;
//                        break;
//                    case 4:
//                        mFragment = mFragmentAtPos4;
//                        mFragmentAtPos4 = fr;
//                        break;

                }

                mFragmentManager.beginTransaction().remove(mFragment)
                        .commit();

//                if (mFragmentAtPos0 instanceof TrainRoomFragment){
//                    //trainfragment
//                    swipePageViewOff();
//                    mFragmentAtPos0 = fr;
//                }else{ // Instance of NextFragment
//                    mFragmentAtPos0 = fr;
//                }

                notifyDataSetChanged();
            }
        }


        void resumeFragment() {
            Fragment mFragment = mFragmentAtPos0;
            switch (viewPager.getCurrentItem()) {
                case 0:
                    mFragment = mFragmentAtPos0;
                    break;
                case 1:
                    mFragment = mFragmentAtPos1;
                    break;
                case 2:
                    mFragment = mFragmentAtPos2;
                    break;
//                case 3:
//                    mFragment = mFragmentAtPos3;
//                    break;
//                case 4:
//                    mFragment = mFragmentAtPos4;
//                    break;
            }
            mFragment.onResume();
        }

        private String[] titles = {"My Train Room", "My Vocabulary", ""};

        private final FragmentManager mFragmentManager;
        public Fragment mFragmentAtPos0;
        public Fragment mFragmentAtPos1;
        public Fragment mFragmentAtPos2;
        public Fragment mFragmentAtPos3;
        public Fragment mFragmentAtPos4;
        private Context context;

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            listener = new PageListener();
            mFragmentManager = fragmentManager;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0
                    if (mFragmentAtPos0 == null) {
                        mFragmentAtPos0 = new TrainRoomFragment();

                    }
                    return mFragmentAtPos0;

                case 1: // Fragment # 1

                    if (mFragmentAtPos1 == null) {
                        mFragmentAtPos1 = new SetWordsFragment();
                    }
                    return mFragmentAtPos1;
                case 2:// Fragment # 2
                    if (mFragmentAtPos2 == null) {
                        mFragmentAtPos2 = new WebFragment();

                    }
                    return mFragmentAtPos2;
//                case 3:
//                    if (mFragmentAtPos3 == null) {
//                        mFragmentAtPos3 = new StageFragment();
//
//                    }
//                    return mFragmentAtPos3;
//                case 4:
//                    if (mFragmentAtPos4 == null) {
//                        FragmentTrainResult newFragment = new FragmentTrainResult();
//                        ArrayList<TrainUnit> list1 = new ArrayList<TrainUnit>();
//
//                        ArrayList<CommonWord> lcw = (ArrayList<CommonWord>) CommonWordAgent.getAll();
//
//                        for (CommonWord cw : lcw) {
//                            TrainUnit tu = new TrainUnit();
//                            tu.setCommonWord(cw);
//                            tu.addTime(33);
//                            tu.addTime(33);
//                            tu.addTime(311);
//                            list1.add(tu);
//                        }

//
//                        Bundle args = new Bundle();
//                        args.putSerializable("Trains", list1);
//                        newFragment.setArguments(args);
//                        mFragmentAtPos4 = newFragment;
//
//                    }
//                    return mFragmentAtPos4;
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public int getItemPosition(Object object) {
//            if (object instanceof TrainRoomFragment &&
//                    mFragmentAtPos0 instanceof TrainFragment) {
//                return POSITION_NONE;
//            }
//            if (object instanceof TrainRoomFragment &&
//                    mFragmentAtPos0 instanceof SetWordsFragment) {
//                return POSITION_NONE;
//            }
//            if (object instanceof TrainFragment &&
//                    mFragmentAtPos0 instanceof TrainRoomFragment) {
//                return POSITION_NONE;
//            }
//
//            if (object instanceof SetWordsFragment &&
//                    mFragmentAtPos0 instanceof WebFragment) {
//                return POSITION_NONE;
//            }
//            if (object instanceof WebFragment &&
//                    mFragmentAtPos0 instanceof SetWordsFragment) {
//                return POSITION_NONE;
//            }
            return POSITION_NONE;
//            return POSITION_UNCHANGED;
        }

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

//        if (isSearchOpened) {
//            handleMenuSearch();
//            return false;
//        }

//        if (DrawPanel.getDrawer().isDrawerOpen()) {
//            DrawPanel.getDrawer().closeDrawer();
//            return false;
//        }
//


        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return false;
        }

        if (MySnackBar.hideIfshow()) {
            return false;
        }

        int item = viewPager.getCurrentItem();
        switch (item) {
            case 0:
                if (mAdapter.getItem(item) instanceof TrainFragment) {
                    ((TrainFragment) mAdapter.getItem(item)).backPressed();
                    return false;
                } else if (mAdapter.getItem(item) instanceof TrainRoomFragment) {
                    finish();
                }
                break;
            case 1:
                if (mAdapter.getItem(item) instanceof VocabularyFragment) {
                    ((VocabularyFragment) mAdapter.getItem(item)).backPressed();
                    return false;
                } else if (mAdapter.getItem(item) instanceof SetWordsFragment) {
                    finish();
                }
                break;
//            case 2:
//                if (mAdapter.getItem(item) instanceof TrainFragment) {
//                    ((TrainFragment) mAdapter.getItem(item)).backPressed();
//                    return false;
//                } else if (mAdapter.getItem(item) instanceof TrainRoomFragment) {
//                    finish();
//                }
//                break;
        }


        if ((System.currentTimeMillis() - start) > 3000) {
            Toast.makeText(getApplicationContext(), R.string.press_again_text, Toast.LENGTH_SHORT).show();
            start = System.currentTimeMillis();
            return false;
        }


        return super.onKeyDown(keyCode, event);
    }


    private static void setCurrentFragment(Fragment fragment, boolean addToBackStack) {

        int idView = R.id.MainBox2;

        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(idView, fragment);
        if (addToBackStack) transaction.addToBackStack("test");
        transaction.commit();
    }

    public static void setFragment(Fragment fragment, boolean addToBackStack) {
        setCurrentFragment(fragment, addToBackStack);
    }

    public String getVersion(){
        return VERSION_PRODUCT;
    }

    public String getAuhor(){
        return AUTHOR_PRODUCT;
    }
    public String getEmail(){
        return EMAIL_LINK;
    }
    public String getVKLink(){
        return VK_LINK;
    }


    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}
