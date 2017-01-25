package odyssey.ru.linglibrary;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dmitry on 05.11.2016.
 */
public class ViewUtil {

    static Map<TextView, Boolean> mapTxt = new HashMap<>();


    public static void setTextHidded(final TextView txt){

        txt.setTextColor(Color.LTGRAY);
        txt.setBackgroundColor(Color.LTGRAY);
        txt.getRootView();
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView  v =  txt;


                if (mapTxt.get(txt) == null) mapTxt.put(  v, false);
                boolean txt_hide_sub_bol = mapTxt.get(   v);
                txt_hide_sub_bol = !txt_hide_sub_bol;
                if (txt_hide_sub_bol) {
                    v.setBackgroundColor(Color.WHITE);
                } else {
                    v.setBackgroundColor(Color.LTGRAY);
                }
                mapTxt.put(  v, txt_hide_sub_bol);

            }
        });

    }
}
