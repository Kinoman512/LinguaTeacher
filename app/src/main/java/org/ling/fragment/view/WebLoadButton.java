package org.ling.fragment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import org.ling.utils.IAction;

/**
 * Created by Dmitry on 20.07.2016.
 */
public class WebLoadButton extends com.mikepenz.iconics.view.IconicsImageView {

    final CommunityMaterial.Icon  Stop = CommunityMaterial.Icon.cmd_close;
    final CommunityMaterial.Icon  Repeat = CommunityMaterial.Icon.cmd_replay;

    CommunityMaterial.Icon state;
    CommunityMaterial.Icon prevState;


    IAction StopHandler;
    IAction RepeatHandler;

    public void setHandlers(IAction stopHandler,  IAction repeatHandler ){
        StopHandler = stopHandler;
        RepeatHandler = repeatHandler;
    }



    public void onstop(boolean bln){
        if(bln){
            if(RepeatHandler != null) RepeatHandler.onSucces("");
        }

        state  = Stop;
        setIcon(Stop);

    }

    public void onrepeat(){
        if(StopHandler != null) StopHandler.onSucces("");
        state = Repeat;
        setIcon(state);

    }

    void changeState(){
         if(state ==  Stop){
             onrepeat();
         }else  {
             onstop(true);
         }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                changeState();
                break;

        }
        return false;
    }

    public WebLoadButton(Context context) {
        super(context);
    }

    public WebLoadButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!this.isInEditMode()) {
            state  = Repeat;
            setIcon(Repeat);
        }
    }

    public WebLoadButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
