package nl.esciencecenter.praline.newalign;

import nl.esciencecenter.praline.aligners.AlignStep;

import java.util.List;

public class AlignResult {
    public final float score;
    public final List<AlignStep> align;

    public AlignResult(float score, List<AlignStep> align) {
        this.score = score;
        this.align = align;
    }
}
