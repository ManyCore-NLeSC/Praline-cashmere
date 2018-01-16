package nl.esciencecenter.praline.integeralign;

public class AffineGapCost implements IGapCost{
    final int start;
    final int extend;

    public AffineGapCost(int start, int extend) {
        this.start = start;
        this.extend = extend;
    }


    @Override
    public int getGapCost(int gapSize) {
        if(gapSize == 0) return 0;
        return start + (gapSize - 1) * extend;
    }
}
