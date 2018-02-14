package nl.esciencecenter.praline.integeralign;



import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.aligners.Alignment;

import java.util.List;

public class AlignResult {
    private final float score;
    private final Alignment align;

    public AlignResult(float score, Alignment align) {
        this.score = score;
        this.align = align;
    }

    public float getScore() {
        return this.score;
    }

    public Alignment getAlign() {
        return this.align;
    }
}