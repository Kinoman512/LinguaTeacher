package org.ling.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by Dmitry on 30.06.2016.
 */
@Table(name = "Stage" )
public class Stage  extends Model implements Serializable {

    @Column(name = "weight")
    long weight;
    @Column(name = "delta")
    long delta;

    public Stage() {
        super();
    }
    public Stage(long weight, long delta) {
        this.weight = weight;
        this.delta = delta;
    }


    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public long getDelta() {
        return delta;
    }

    public void setDelta(long delta) {
        this.delta = delta;
    }


}
