package org.ling.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by Dmitry on 23.06.2016.
 */

@Table(name = "Pronounces"  )
public class Pronounce  extends Model implements Serializable {



    @Column(name = "url" , unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    String url;
    @Column(name = "likes")
    int likes;
    @Column(name = "username")
    String userName;
    @Column(name = "place")
    String place;
    @Column(name = "gender")
    String gender;

    //refrence
    @Column(name = "simpleWord")
    long simpleWord = -1;

    public Pronounce() {
        super();
    }


    public long getSimpleWord() {
        return simpleWord;
    }

    public void setSimpleWord(long simpleWord) {
        this.simpleWord = simpleWord;
    }

    public Pronounce(String url, int likes, String userName, String place, String gender) {
        super();
        this.url = url;
        this.likes = likes;
        this.userName = userName;
        this.place = place;
        this.gender = gender;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}


