package odyssey.ru.linguateacher;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;

import org.ling.fragment.SetWordsFragment;
import org.ling.fragment.TrainRoomFragment;
import org.ling.fragment.WebFragment;

/**
 * Created by Dmitry on 18.07.2016.
 */
public class TabsPageAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"Tab1", "Tab2", "Tab3"};
    private Context context;

    public TabsPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }



    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TrainRoomFragment();
            case 1:
                return new SetWordsFragment();
            case 2:
                return new WebFragment();
            default:
                return new TrainRoomFragment();
        }
    }






    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        // return tabTitles[position];

        TextView tv = new TextView(MainActivity.activity);
        // getDrawable(int i) is deprecated, use getDrawable(int i, Theme theme) for min SDK >=21
        Drawable image =  ContextCompat.getDrawable( context, R.drawable.htmlweb)  ;
//         Drawable image = context.getResources().getDrawable( R.drawable.htmlweb, context.getTheme());
        // Drawable image = context.getResources().getDrawable(imageResId[position]);

//        Drawable image = new IconicsDrawable(context, FontAwesome.Icon.faw_android).color(Color.GREEN);
//        image.setBounds( 0, 0, 80, 80);
//        tv.setCompoundDrawables(null, null, image, null);
//        tv.setCompoundDrawablePadding(8);


        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return tv.getText();
    }

}