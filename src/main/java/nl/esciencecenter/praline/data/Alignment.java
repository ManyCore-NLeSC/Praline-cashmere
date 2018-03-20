package nl.esciencecenter.praline.data;

import java.util.List;

public class Alignment {
    private final Coordinate start;
    private final List<AlignStep> steps;

    public Alignment(Coordinate start, List<AlignStep> steps) {
        this.start = start;
        this.steps = steps;
    }

    public Coordinate getStart() {
        return this.start;
    }

    public List<AlignStep> getSteps() {
        return this.steps;
    }
}
