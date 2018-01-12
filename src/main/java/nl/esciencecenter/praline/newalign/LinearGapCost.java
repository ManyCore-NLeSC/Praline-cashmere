package nl.esciencecenter.praline.newalign;

import nl.esciencecenter.praline.newalign.IGapCost;

public class LinearGapCost implements IGapCost {

    final float cost;

    public LinearGapCost(float cost) {
        this.cost = cost;
    }

    @Override
    public float getGapCost(int gapSize) {
        return gapSize * cost;
    }
}
