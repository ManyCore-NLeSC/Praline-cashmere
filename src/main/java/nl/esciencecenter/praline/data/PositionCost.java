package nl.esciencecenter.praline.data;

import nl.esciencecenter.praline.data.*;

public class PositionCost  {

    final Sequence a;
    final Sequence b;
    final Matrix[] pairs;

    public PositionCost(Sequence a, Sequence b, Matrix ... pairs) {
        if(a.getNrTracks() != b.getNrTracks()){
            throw new Error("Number of tracks not equal");
        }
        if(a.getNrTracks() != pairs.length){
            throw new Error("Wrong number of scorematrices!");
        }
        this.a = a;
        this.b = b;
        this.pairs = pairs;
    }


    public float cost(int posA, int posB) {
        float cost = 0;
        for(int track = 0; track < a.getNrTracks() ; track++){
            int symbolA = a.getTrack(track).getValue(posA);
            int symbolB = b.getTrack(track).getValue(posB);
            cost += pairs[track].get(symbolA,symbolB);
        }
        return cost;
    }
}
