package org.ling;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.R;
import org.ling.fragment.StageFragment;

/**
 * Created by Dmitry on 09.08.2016.
 */
public class ExtraActivity  extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_extra);
        setCurrentFragment(new StageFragment(),false);


    }


    private  void setCurrentFragment(Fragment fragment, boolean addToBackStack) {

        int idView = R.id.MainBox3;

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(idView, fragment);
        if (addToBackStack) transaction.addToBackStack("test");
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
