package org.ling;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.R;
import org.ling.fragment.SetWordsFragment;
import org.ling.fragment.TrainFragment;
import org.ling.fragment.TrainRoomFragment;
import org.ling.fragment.VideoFragment;
import org.ling.fragment.VocabularyFragment;
import org.ling.fragment.WebFragment;
import org.ling.fragment.view.CustomViewPager;
import org.ling.fragment.view.MySnackBar;
import org.ling.utils.FirstPageFragmentListener;
import org.ling.utils.IAction;
import org.videolan.vlc.gui.video.VideoPlayerActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import odyssey.ru.googlelibrary.Firebase;
import odyssey.ru.linglibrary.FileHelp;
import odyssey.ru.linglibrary.Util;

public class MainActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {
    private static final String VERSION_PRODUCT = "0.3.2";
    private static final String AUTHOR_PRODUCT = "Dmitry Gribkov      ";
    private static final String VK_LINK = "vk.com/odyssey512   ";
    private static final String EMAIL_LINK = "kinoman512@gmail.com";

    private static final String TAG = "Ling.MainActivity";
    public static final int FILE_SELECT_CODE =  11;

    static FragmentManager fragmentManager;
    static String filedir;
    public static MainActivity activity;
    private static MySnackBar mySnackBar;

    long start;
    public static Toolbar toolbar;
    static public Menu menu;
    int about_count = 0;

//    private AdView mAdView;


    static public int tabIndex = 0;
    TabLayout tabLayout;
    static CustomViewPager viewPager;
    public static MyAdapter mAdapter;
    Drawable[] imageArray;
    private View appBar;
    private AdView mAdView;

    public static int getTabIndex() {
        return tabIndex;
    }

    static MyAdapter.PageListener listener;
    private Drawable[] imageArray2;

    public static void switchToNextFragment(Fragment fr) {
        listener.onSwitchToNextFragment(fr);
    }

    public int getCurrentTab() {
        return viewPager.getCurrentItem();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    public static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    public static String getStringById(int id) {
        return MainActivity.activity.getString(id);
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        setListViewHeightBasedOnChildren(listView, 0);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView, int addHeight) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            View miss = view.findViewById(R.id.stage_holder);
            int minusHeight = 0;
            if (miss != null) {
                miss.measure(0, 0);
                minusHeight = miss.getMeasuredHeight();
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight() - minusHeight;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight  +  (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
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


        // ****************************** adv1
//        mAdView = (AdView) findViewById(R.id.ad_view);


        appBar = (View) findViewById(R.id.appBar);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."

//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .build();
//
//        // Start loading the ad in the background.



        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Drawable icon = new IconicsDrawable(this, FontAwesome.Icon.faw_internet_explorer).color(Color.BLACK);
        Drawable imag4 = icon.getCurrent();

        //Add tabs icon with setIcon() or simple text with .setText()
        Drawable image = new IconicsDrawable(this, FontAwesome.Icon.faw_star).color(Color.BLACK);
//        Drawable image2 = new IconicsDrawable(this, FontAwesome.Icon.faw).color(Color.BLACK);
        Drawable image2 = new IconicsDrawable(this, GoogleMaterial.Icon.gmd_collections_bookmark).color(Color.BLACK);
        Drawable image3 = new IconicsDrawable(this, FontAwesome.Icon.faw_internet_explorer).color(Color.BLACK);
        Drawable image4 = new IconicsDrawable(this, FontAwesome.Icon.faw_video_camera).color(Color.BLACK);
        Drawable image5 = new IconicsDrawable(this, FontAwesome.Icon.faw_bed).color(Color.BLACK);

        Drawable image1s = new IconicsDrawable(this, FontAwesome.Icon.faw_star).color(Color.WHITE);
        Drawable image2s = new IconicsDrawable(this, GoogleMaterial.Icon.gmd_collections_bookmark).color(Color.WHITE);
        Drawable image3s = new IconicsDrawable(this, FontAwesome.Icon.faw_internet_explorer).color(Color.WHITE);
        Drawable image4s = new IconicsDrawable(this, FontAwesome.Icon.faw_video_camera).color(Color.WHITE);
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
        getSupportActionBar().setTitle(" " + getResources().getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


//        DrawPanel.prepare(this, toolbar);
//        DrawPanel.start();

        start = System.currentTimeMillis();

        super.onCreate(savedInstanceState);
         mAdView = (AdView) findViewById(R.id.ad_view);
        Firebase.loadAdView(mAdView);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (CustomViewPager) findViewById(R.id.pager);
//        viewPager.setWight

        //Firebase.stop();
       // viewPager.
//        CustomViewPager.LayoutParams parms = new CustomViewPager.LayoutParams( ,height);
//        layout.setLayoutParams(parms);

        viewPager.setOffscreenPageLimit(1);

        mAdapter = new MyAdapter(getSupportFragmentManager());

        tabLayout.addTab(tabLayout.newTab().setIcon(image1s));
        tabLayout.addTab(tabLayout.newTab().setIcon(image2));
        tabLayout.addTab(tabLayout.newTab().setIcon(image3));
        tabLayout.addTab(tabLayout.newTab().setIcon(image4));
//        //Setting adapter
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(" " + mAdapter.getPageTitle(position));
                TabLayout.Tab tab1 = tabLayout.getTabAt(tabIndex);
                TabLayout.Tab tab2 = tabLayout.getTabAt(position);
                tab1.setIcon(imageArray[tabIndex]);
                tab2.setIcon(imageArray2[position]);
                tabIndex = position;
                if (position == 2) {
                    mAdapter.showSearchBar();
                } else {
                    mAdapter.hideSearchBar();
                }

                if (position == 3) {

                    mAdView.setVisibility(View.GONE);
                } else {
                    mAdView.setVisibility(View.VISIBLE);
                }

                mAdapter.resumeFragment();
            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

//        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        p.weight = 1;
//
//        viewPager.setLayoutParams(p);

        if( Setting.getBool("FirebaseStop")){
            Firebase.stop();



           // viewPager.setLayoutParams(params);
        }

        MyApplication.loadInterstitial();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        return true;
    }

    public void  switchToWebFragment(String html){


        viewPager.setCurrentItem(2);
        mAdapter.setHtmlToWeb(html);
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

            case R.id.about:

                about_count++;
                if( about_count > 3){
                    Setting.setBool("FirebaseStop", true);
                    Firebase.stop();
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                            ViewPager.LayoutParams.MATCH_PARENT,
//                            ViewPager.LayoutParams.MATCH_PARENT, 1.0f);
//                    viewPager.setLayoutParams(params);
                    Util.showMassage(R.string.its_magic);
                }

//                Firebase.start();
                String version = "\n " + getResources().getString(R.string.version_text) + " " + getVersion();
                String author = getResources().getString(R.string.developer_text) + " \n\n " + getAuhor();
                String email = getEmail();
                String vkLink = getVKLink();
                String about = getResources().getString(R.string.about_program_text);

                new SweetAlertDialog(MainActivity.activity)
                        .setTitleText(about)
                        .setContentText(
                                version + "\n" +
                                        author + "" +
                                        email + "\n" +
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

    public static void resumeFragment(){
        mAdapter.resumeFragment();
    }



    private  class MyAdapter extends FragmentPagerAdapter {

        public String getSelectedText(){
            if (mFragmentAtPos2 instanceof WebFragment) {
                ((WebFragment) mFragmentAtPos2).getSelectedTextByWebView();
            }
            return "";
        }
        public String setHtmlToWeb(String html){
            if (mFragmentAtPos2 instanceof WebFragment) {
                ((WebFragment) mFragmentAtPos2).setHtml(html);
            }
            return "";
        }

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

        public int  getCurrentTab() {
            return viewPager.getCurrentItem();
        }


        public void showShackBarTranslateWord(String text) {
            if (mFragmentAtPos2 instanceof WebFragment) {
                ((WebFragment) mFragmentAtPos2).showShackBarTranslateWord(text);
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
                    case 3:
                        mFragment = mFragmentAtPos3;
                        mFragmentAtPos3 = fr;
                        break;
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


        public void resumeFragment() {
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
                case 3:
                    mFragment = mFragmentAtPos3;
                    break;
//                case 4:
//                    mFragment = mFragmentAtPos4;
//                    break;
            }
            if (mFragment != null)
                mFragment.onResume();
        }

        private String[] titles = {"My Train Room", "My Vocabulary", "", "Video Train"};

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

                        mFragmentAtPos2 =  new WebFragment();;

                    }
                    return mFragmentAtPos2;
                case 3:
                    if (mFragmentAtPos3 == null) {
                        mFragmentAtPos3 = new VideoFragment();

                    }
                    return mFragmentAtPos3;
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

    public String getVersion() {
        return VERSION_PRODUCT;
    }

    public String getAuhor() {
        return AUTHOR_PRODUCT;
    }

    public String getEmail() {
        return EMAIL_LINK;
    }

    public String getVKLink() {
        return VK_LINK;
    }


    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        // ****************************** adv3
//        if (mAdView != null) {
//            mAdView.pause();
//        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        // ****************************** adv2
//        if (mAdView != null) {
//            mAdView.resume();
//        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        // ****************************** adv2
//        if (mAdView != null) {
//            mAdView.destroy();
//        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;

        if (data.getDataString() == null) {
            Log.d(TAG, "Subtitle selection dialog was cancelled");
        }
        if (data.getData() == null) return;

        String uri = data.getData().getPath();
        if (requestCode ==  MainActivity.FILE_SELECT_CODE) {
            Log.d(TAG, "Selected file: " + uri);

            VideoFragment.replacePath(uri.toString());
        }
    }



    private static ActionMode mActionMode = null;

    @Override
    public void onActionModeStarted(ActionMode mode) {
//        boolean bl =  WebFragment.isFocusedSearchText();
//        if(bl){
//            View v = new View(this);
//            v.setVisibility(View.GONE);
//            mode.setCustomView(v);
//            //mode.finish();
//            super.onActionModeStarted(mode);
//            return;
//        }

        if (mActionMode == null ) {
            mActionMode = mode;
            Menu menu = mode.getMenu();
            // Remove the default menu items (select all, copy, paste, search)
            menu.clear();
            // Inflate your own menu items



            switch( mAdapter.getCurrentTab()){
                case 2: mode.getMenuInflater().inflate(R.menu.translate, menu);  break;
                case 1: mode.getMenuInflater().inflate(R.menu.vocabulary_menu, menu);  break;

            }






            List<MenuItem> menuItems = new ArrayList<>();
            // get custom menu item
            for (int i = 0; i < menu.size(); i++) {
                //int icon = R.drawable.bench;
                //menu.getItem(i).setIcon(icon);

                menuItems.add(menu.getItem(i));
            }
            menu.clear();
            // reset menu item order
            int size = menuItems.size();
            for (int i = 0; i < size; i++) {
                addMenuItem(menu, menuItems.get(i), i, true);
            }

            //appBar.setVisibility(View.GONE);
            // If you want to keep any of the defaults,
            // remove the items you don't want individually:
            // menu.removeItem(android.R.id.[id_of_item_to_remove])
            //mActionMode.finish();
//            mActionMode.
//            mActionMode.hide(10000);
            //menu.clear();
        }

        super.onActionModeStarted(mode);
    }

    private void addMenuItem(Menu menu, MenuItem item, int order, boolean isClick){
        MenuItem menuItem = menu.add(item.getGroupId(),
                item.getItemId(),
                order,
                item.getTitle());
         menuItem.setIcon(item.getIcon());
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if (isClick)
            // set custom menu item click
            menuItem.setOnMenuItemClickListener(this);
    }

    public static MenuItem translateItem ;

    public static void onSelectedText(String text){

        switch (translateItem.getItemId()) {
            case R.id.add:
                //Util.showMassage(text);
                Dialog.showCreateWordDialog(text, MyApplication.currentSet, new IAction() {
                    @Override
                    public void onSucces(Object rs) {
                        Toast.makeText(MainActivity.activity, MainActivity.getStringById(R.string.added), Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case R.id.translate:
                // do some different stuff
                 mAdapter.showShackBarTranslateWord(text);

                break;
            case R.id.copy:
                //mAdapter.showShackBarTranslateWord(text);

                ClipboardManager clipboard = (ClipboardManager) MainActivity.activity.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", text);
                clipboard.setPrimaryClip(clip);

                break;
            default:
                // ...
                break;
        }
        if (mActionMode != null) {
            MainActivity.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActionMode.finish();
                }
            });

        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch( mAdapter.getCurrentTab()){
            case 2:
                translateItem = item;
                mAdapter.getSelectedText();  break;

           // case 1: mode.getMenuInflater().inflate(R.menu.vocabulary_menu, menu);  break;

        }


        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){}
                catch(Exception e){}
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onActionModeFinished(ActionMode mode) {
        mActionMode = null;
        //mode.getCustomView().setVisibility(View.GONE       sads);
        super.onActionModeFinished(mode);
        //appBar.setVisibility(View.VISIBLE);


    }




}
