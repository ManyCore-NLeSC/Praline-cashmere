package nl.esciencecenter.praline.data;

public class ScoreResult {
    public final Move move;
    public final float score;

    public ScoreResult(float score, Move move ) {
        this.move = move;
        this.score = score;
    }
}
