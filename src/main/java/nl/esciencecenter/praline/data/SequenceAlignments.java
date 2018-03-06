package nl.esciencecenter.praline.data;

import java.util.HashMap;

public class SequenceAlignments {
    private HashMap<Integer, HashMap<Integer, Float>> matrix;

    public void addElement(Integer sequenceOne, Integer sequenceTwo, Float score) {
        matrix.get(sequenceOne).put(sequenceTwo, score);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for ( int row = 0; row < matrix.size(); row++ ) {
            for ( int column = 0; column < matrix.size(); column++ ) {
                stringBuilder.append(matrix.get(row).get(column));
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }
}
