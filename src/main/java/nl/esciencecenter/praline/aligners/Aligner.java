package nl.esciencecenter.praline.aligners;

class Aligner {
    private float gapScore;

    Aligner() {
        gapScore = -2.0f;
    }

    Aligner(float gapScore) {
        this.gapScore = gapScore;
    }

    float getGapScore() {
        return gapScore;
    }

    void setGapScore(float gapScore) {
        this.gapScore = gapScore;
    }
}
