package nl.esciencecenter.praline.integeralign;

public class AffineGapCost implements IGapCost{
    final float start;
    final float extend;

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
