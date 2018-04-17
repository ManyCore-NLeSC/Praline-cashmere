package nl.esciencecenter.praline.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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

    public List<AlignStep> getAlignSteps() {
        Iterator<Coordinate> it = this.steps.iterator();
        List<AlignStep> res = new Vector<>();
        Coordinate prev = it.next();
        while(it.hasNext()){
            Coordinate c = it.next();
            int xdiff = c.getX() - prev.getX();
            int ydiff = c.getY() - prev.getY();
            if(xdiff == 1 && ydiff == 1){
                res.add(AlignStep.ALIGN);
            } else if(xdiff == 0 && ydiff == 1){
                res.add(AlignStep.GAPA);
            } else if(xdiff == 1 && ydiff == 0){
                res.add(AlignStep.GAPB);
            } else {
                throw new Error("Not an alignstep!");
            }
            prev = c;
        }
        return res;
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
