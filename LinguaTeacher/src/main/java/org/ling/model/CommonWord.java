package org.ling.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.util.Date;

import org.ling.model.agent.SimpleWordAgent;
import org.ling.model.agent.StageAgent;

/**
 * Created by Dmitry on 30.06.2016.
 */
@Table(name = "CommonWord" )
public class CommonWord extends Model implements Serializable{


    @Column(name = "source")
    long source;

    @Column(name = "dest")
    long dest;

    //url
    @Column(name = "image")
    String image;

    //id stage
    @Column(name = "stage")
    long stageWeight = 0;


    @Column(name = "note")
    String note = "";


    @Column(name = "added")
    long added;
    @Column(name = "lastTrain")
    long lastTrain;
    @Column(name = "idSet")
    long idSet;

    @Column(name = "nextTrain")
    long nextTrain;

    public CommonWord(long source, long dest, long idSet) {
        super();
        this.source = source;
        this.dest = dest;
        this.idSet = idSet;
        this.added =  new Date().getTime();
    }



    public SimpleWord getInsSource(){
        return SimpleWordAgent.findById(source);
    }

    public SimpleWord getInsDest(){
        return SimpleWordAgent.findById(dest);
    }

    public Stage getInsStage(){
        return StageAgent.getCloseStageWeight(stageWeight);
    }




    public long getIdSet() {
        return idSet;
    }

    public void setIdSet(long idSet) {
        this.idSet = idSet;
    }

    public CommonWord() {
        super();
    }

    public long getSource() {
        return source;
    }

    public void setSource(long source) {
        this.source = source;
    }

    public long getDest() {
        return dest;
    }

    public void setDest(long dest) {
        this.dest = dest;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getStage() {
        return stageWeight;
    }

    public void setStage(long stage) {
        this.stageWeight = stage;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }

    public long getLastTrain() {
        return lastTrain;
    }

    public void setLastTrain(long lastTrain) {
        this.lastTrain = lastTrain;
    }

    public long getNextTrain() {
        return nextTrain;
    }

    public void setNextTrain(long nextTrain) {
        this.nextTrain = nextTrain;
    }
}
