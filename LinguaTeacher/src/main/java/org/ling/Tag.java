package org.ling;

import com.plumillonforge.android.chipview.Chip;

/**
 * Created by Plumillon Forge on 25/09/15.
 */
public class Tag implements Chip {
    private String mWord;
    private String mTrans;
    private int  number;


    private String mgender;
    private String mclassf;
    private String mtransc;
    private int mType = 0;

    public Tag(String name, String trans,int number, int type) {
        this(name,trans );
        this.number = number;
        mType = type;
    }

    public Tag(String name, String trans) {
        mWord = name;
        mTrans = trans;

    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getMgender() {
        return mgender;
    }

    public void setMgender(String mgender) {
        this.mgender = mgender;
    }

    public String getMclassf() {
        return mclassf;
    }

    public void setMclassf(String mclassf) {
        this.mclassf = mclassf;
    }

    public String getmWord() {
        return mWord;
    }

    public void setmWord(String mWord) {
        this.mWord = mWord;
    }

    public String getmTrans() {
        return mTrans;
    }

    public void setmTrans(String mTrans) {
        this.mTrans = mTrans;
    }

    public int getType() {
        return mType;
    }

    @Override
    public String getText() {
        return mWord + " " + mTrans;
    }
}
