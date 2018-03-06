package nl.esciencecenter.praline.data;

import java.util.HashMap;

public class SequenceAlignments {
    private HashMap<Integer, HashMap<Integer, Float>> matrix;

    public SequenceAlignments() {
        this.matrix = new HashMap<>();
    }

    public void addElement(Integer sequenceOne, Integer sequenceTwo, Float score) {
        if ( matrix.containsKey(sequenceOne) ) {
            matrix.get(sequenceOne).put(sequenceTwo, score);
        } else {
            HashMap<Integer, Float> pair = new HashMap<>();
            pair.put(sequenceTwo, score);
            matrix.put(sequenceOne, pair);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for ( int row = 0; row < matrix.size(); row++ ) {
            for ( int column = 0; column < matrix.size(); column++ ) {
                stringBuilder.append(matrix.get(row).get(column));
                if(!(row == matrix.size() - 1 && column == matrix.size() - 1)) stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }
}
