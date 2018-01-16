package nl.esciencecenter.praline.newalign;

import nl.esciencecenter.praline.aligners.AlignStep;

import java.util.List;

public class AlignResult {
    public final double score;
    public final List<AlignStep> align;

    public AlignResult(double score, List<AlignStep> align) {
        this.score = score;
        this.align = align;
    }
}
