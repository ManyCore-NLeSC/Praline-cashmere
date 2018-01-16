package nl.esciencecenter.praline.newalign;

public class AffineGapCost implements IGapCost{
    final double start;
    final double extend;

    public AffineGapCost(double start, double extend) {
        this.start = start;
        this.extend = extend;
    }

    @Override
    public double getGapCost(int gapSize) {
        if (gapSize == 0) {
            return 0;
        } else {
            return  start + (gapSize - 1) * extend;
        }
    }
}
