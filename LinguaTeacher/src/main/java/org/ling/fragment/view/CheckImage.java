package org.ling.fragment.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;

/**
 * Created by Dmitry on 04.08.2016.
 */
public class CheckImage extends com.mikepenz.iconics.view.IconicsImageView {
    CommunityMaterial.Icon  OK = CommunityMaterial.Icon.cmd_check;
    CommunityMaterial.Icon  BAD = CommunityMaterial.Icon.cmd_close;

    public CheckImage(Context context) {
        super(context);
    }

    public CheckImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setOk(){
        setVisibility(VISIBLE);
        setIcon(OK);
        setColor(Color.GREEN);
    }
    public void setBad(){
        setVisibility(VISIBLE);
        setIcon(BAD);
        setColor(Color.RED);
    }

}
