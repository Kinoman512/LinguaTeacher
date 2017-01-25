package org.ling.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.ling.model.agent.CommonWordAgent;

import java.io.Serializable;

/**
 * Created by Dmitry on 30.06.2016.
 */
@Table(name = "SetWords"  )
public class SetWords extends Model implements Serializable, iType {

    static int type = 0;

        @Column(name = "name")
    String name;

    @Column(name = "natv")
    String natv;
    @Column(name = "lang")
    String lang;


    @Column(name = "backcolor")
    long backcolor;
    @Column(name = "image")
    //url
    String image;

    public SetWords() {
        super();
    }


    public SetWords(String name, String lang, String natv) {
        super();
        this.name = name;
        this.lang = lang;
        this.natv = natv;
    }






    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getType() {
        return type;
    }


    public String getNatv() {
        return natv;
    }

    public void setNatv(String natv) {
        this.natv = natv;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }



    public int getCount() {
        return CommonWordAgent.findBySetWord(getId()).size();
    }


    public long getBackcolor() {
        return backcolor;
    }

    public void setBackcolor(long backcolor) {
        this.backcolor = backcolor;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
