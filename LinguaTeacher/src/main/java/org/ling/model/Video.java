package org.ling.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by Dmitry on 03.11.2016.
 */

@Table(name = "Video" )
public class Video extends Model implements Serializable {

    @Column(name = "name")
    String name;
    @Column(name = "video")
    String video = "";
    @Column(name = "fsub")
    String fsub = "";
    @Column(name = "nsub")
    String nsub = "";

    public Video() {
        super();
    }

    public Video(String name, String video, String fsub, String nsub) {
        this.name = name;
        this.video = video;
        this.fsub = fsub;
        this.nsub = nsub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getFsub() {
        return fsub;
    }

    public void setFsub(String fsub) {
        this.fsub = fsub;
    }

    public String getNsub() {
        return nsub;
    }

    public void setNsub(String nsub) {
        this.nsub = nsub;
    }
}
