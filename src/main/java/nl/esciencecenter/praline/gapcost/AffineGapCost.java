package nl.esciencecenter.praline.gapcost;

import java.io.Serializable;

public class AffineGapCost implements IGapCost, Serializable {
    public final float start;
    public final float extend;

    public AffineGapCost(float start, float extend) {
        this.start = start;
        this.extend = extend;
    }


    @Override
    public float getGapCost(int gapSize) {
        if(gapSize == 0) return 0;
        return start + (gapSize - 1) * extend;
    }
}
