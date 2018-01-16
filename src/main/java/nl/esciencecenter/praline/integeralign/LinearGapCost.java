package nl.esciencecenter.praline.integeralign;


public class LinearGapCost implements IGapCost {

    final int cost;

    public LinearGapCost(int cost) {
        this.cost = cost;
    }

    @Override
    public int getGapCost(int gapSize) {
        return gapSize * cost;
    }
}
