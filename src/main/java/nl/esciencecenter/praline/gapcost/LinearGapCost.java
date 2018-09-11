package nl.esciencecenter.praline.gapcost;


import java.io.Serializable;

public class LinearGapCost implements IGapCost, Serializable {

    public final int cost;

    public LinearGapCost(int cost) {
        this.cost = cost;
    }

    @Override
    public float getGapCost(int gapSize) {
        return gapSize * cost;
    }
}
