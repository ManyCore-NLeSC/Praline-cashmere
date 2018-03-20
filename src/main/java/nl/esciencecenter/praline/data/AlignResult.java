package nl.esciencecenter.praline.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlignResult {
    private final float score;
    private final List<Coordinate> steps;



    public AlignResult(float score, List<Coordinate> steps) {
        this.score = score;
        this.steps = steps;
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
