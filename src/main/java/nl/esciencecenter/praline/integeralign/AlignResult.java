package nl.esciencecenter.praline.integeralign;



import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.aligners.Alignment;

import java.util.List;

public class AlignResult {
    public final int score;
    public final Alignment align;

    public AlignResult(int score, Alignment align) {
        this.score = score;
        this.align = align;
    }
}