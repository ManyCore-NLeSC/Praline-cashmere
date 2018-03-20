package nl.esciencecenter.praline.integeralign;

import nl.esciencecenter.praline.data.Coordinate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlignResultSteps {
    private final float score;
    private final List<Coordinate> steps;

    public AlignResultSteps(AlignResult a){
        this.score = a.getScore();
        this.steps = toSteps(a);
    }

    public AlignResultSteps(float score, List<Coordinate> steps) {
        this.score = score;
        this.steps = steps;
    }

    List<Coordinate> toSteps(AlignResult a){
        List<Coordinate> c= new ArrayList<>();
        int coli = a.getAlign().getStart().getX();
        int rowi = a.getAlign().getStart().getY();
        c.add(new Coordinate(coli,rowi));
        for(AlignStep step : a.getAlign().getSteps()){
            switch(step){
                case ALIGN: coli++; rowi++; break;
                case GAPA: rowi++; break;
                case GAPB: coli++; break;
            }
            c.add(new Coordinate(coli,rowi));

        }
        return c;
    }

    public float getScore() {
        return this.score;
    }

    public List<Coordinate> getSteps() {
        return this.steps;
    }



    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Iterator<Coordinate> it = this.steps.iterator();
        if(it.hasNext()){
            builder.append(it.next().toString());
            while(it.hasNext()){
                builder.append(" ");
                builder.append(it.next().toString());
            }
        }

        return builder.toString();
    }
}
