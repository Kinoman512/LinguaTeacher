package org.ling.fragment.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import org.ling.MainActivity;
import org.ling.model.Pronounce;
import org.ling.model.SimpleWord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry on 11.07.2016.
 */
public class PopupMenuBotton extends com.mikepenz.iconics.view.IconicsImageView {

    CommunityMaterial.Icon  VER_DOTS = CommunityMaterial.Icon.cmd_dots_vertical;
    List<PlaySoundBotton> listPSB = new ArrayList<>();
    SimpleWord sw;
    PlaySoundBotton psb;

    public PopupMenuBotton(Context context) {
        super(context);
    }

    public PopupMenuBotton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!this.isInEditMode()) {
            setIcon(VER_DOTS);
            setColor(Color.LTGRAY);
        }
    }

    public PopupMenuBotton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                showPopupMenu(this);
                break;

            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    public  void setSimpleWord(SimpleWord sw){
        this.sw = sw;

        final List<Pronounce> list = sw.getInstPronounces();
        if(!list.isEmpty()){
            setColor(Color.BLACK);
        }
    }

    public void setPsb(PlaySoundBotton psb) {
        this.psb = psb;
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.activity, v);

        if(sw == null || sw.getInstPronounces().size() == 0 ){
            return;
        }


        final List<Pronounce> list = sw.getInstPronounces();
        if(list.isEmpty()){
            setVisibility(INVISIBLE);
        }

        for(int i = 0; i<list.size();i++){
            Pronounce pr = list.get(i);
            popupMenu.getMenu().add(0, i, i, pr.getUserName() + " " + pr.getPlace());

        }
        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int id = item.getItemId();
                        Pronounce pr = list.get(id);
                        sw.setChoosedPronounce(id);
                        sw.save();

                        if(psb != null){
                            psb.setSound(pr, sw.getLang());
                            psb.stop();
                            psb.play();
                        }

                        for(PlaySoundBotton psb : listPSB){
                            psb.setSound(pr, sw.getLang());
                        }

                        return true;

                    }
                });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });
        popupMenu.show();
    }


    public void addPsb(PlaySoundBotton psb) {

        listPSB.add(psb);
    }
}
