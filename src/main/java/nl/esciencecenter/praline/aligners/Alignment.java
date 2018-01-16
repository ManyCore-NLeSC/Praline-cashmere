package nl.esciencecenter.praline.aligners;

import nl.esciencecenter.praline.newalign.Coordinate;

import java.util.List;

public class Alignment {
    final Coordinate start;
    final List<AlignStep> steps;

    public Alignment(Coordinate start, List<AlignStep> steps) {
        this.start = start;
        this.steps = steps;
    }
}
