package nl.esciencecenter.praline.integeralign;

import nl.esciencecenter.praline.aligners.AlignStep;
import nl.esciencecenter.praline.aligners.Alignment;
import nl.esciencecenter.praline.newalign.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class AlignResultSteps {
    public final float score;
    public final List<Coordinate> steps;

    public AlignResultSteps(AlignResult a){
        this.score = a.score;
        this.steps = toSteps(a);
    }

    public AlignResultSteps(float score, List<Coordinate> steps) {
        this.score = score;
        this.steps = steps;
    }

    List<Coordinate> toSteps(AlignResult a){
        List<Coordinate> c= new ArrayList<>();
        int coli = a.align.start.x;
        int rowi = a.align.start.y;
        c.add(new Coordinate(coli,rowi));
        for(AlignStep step : a.align.steps){
            switch(step){
                case ALIGN: coli++; rowi++; break;
                case GAPA: rowi++; break;
                case GAPB: coli++; break;
            }
            c.add(new Coordinate(coli,rowi));

        }
        return c;
    }
}
