package nl.esciencecenter.praline.gapcost;


public class LinearGapCost implements IGapCost {

    public final int cost;

    public LinearGapCost(int cost) {
        this.cost = cost;
    }

    @Override
    public float getGapCost(int gapSize) {
        return gapSize * cost;
    }
}
