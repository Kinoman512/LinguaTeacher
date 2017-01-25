package org.ling;

import com.plumillonforge.android.chipview.Chip;

/**
 * Created by Dmitry on 14.08.2016.
 */
public class WordTag implements Chip {

    private String word;

    private int mType = 0;


    public WordTag(String name) {
        word = name;
    }


    public int getType(){
        return  mType;
    }

    public void  setType(int i){
          mType = i ;
    }
    @Override
    public String getText() {
        return word;
    }
}
