package nl.esciencecenter.praline.data;

import java.util.ArrayList;

public abstract class AlignmentMatrix {
    private final String id;
    private final Sequence seqA;
    private final Sequence seqB;
    private float [][] scores;
    private Move [][] pointers;

    protected AlignmentMatrix(String id, Sequence seqA, Sequence seqB) {
        this.id = id;
        this.seqA = seqA;
        this.seqB = seqB;

    }

    public void allocate(){
        this.scores = new float[seqA.getLength() + 1][];
        this.pointers = new Move[seqA.getLength() + 1][];
        for(int i = 0; i < seqA.getLength() +1; i++){
            scores[i] = new float[seqB.getLength() + 1];
            pointers[i] = new Move[seqB.getLength() + 1];
            for(int j = 0; j < seqB.getLength() + 1; j++){
                scores[i][j] = 0.0f;
                pointers[i][j] = Move.NIL;
            }
        }
    }

    public String getId() {
        return id;
    }

    public boolean isInside(int col, int row){
        return col >= 0  && col < seqA.getLength() +1 &&
                row >= 0 && row < seqB.getLength() + 1;
    }

    public void setScore(int col, int row, float value) {
        if(isInside(col,row))  {scores[col][row] = value; }
        else throw new IndexOutOfBoundsException("Out of range: column" + col + " row " + row);
    }

    public float getScore(int col, int row) {
       if(isInside(col,row)){
            return scores[col][row];
        }
        return Float.MIN_VALUE;
    }

    public void setMove(int col, int row, Move move) {
        if(isInside(col,row)){
            pointers[col][row] = move;
        }
    }

    public Move getMove(int col, int row) {
        if(isInside(col,row)){
            return pointers[col][row];
        }
        throw new IndexOutOfBoundsException("Out of range: column" + col + " row " + row);
    }

    public Sequence getSeqA() {
        return seqA;
    }

    public Sequence getSeqB() {
        return seqB;
    }

    @Override
    public String toString() {
        StringBuilder stringMatrix = new StringBuilder();
        for(int i = 0 ; i < scores.length ; i++){
            for ( float item : scores[i] ) {
                stringMatrix.append(Float.toString(item));
                stringMatrix.append(" ");
            }
            stringMatrix.append("\n");
        }

        return stringMatrix.toString();
    }


    public ArrayList<String> getAlignment( int column , int row) {
        ArrayList<String> alignment = new ArrayList<>();

        while ( getMove(column,row) != Move.NIL ) {
            Move move = getMove(column,row);
            alignment.add(String.valueOf(row) + " " + String.valueOf(column));
            if ( move == Move.TOP ) {
                row -= 1;
            } else if ( move == Move.LEFT ) {
                column -= 1;
            } else {
                row -= 1;
                column -= 1;
            }
        }
        alignment.add(String.valueOf(row) + " " + String.valueOf(column));
        return alignment;
    }

    abstract ArrayList<String> getAlignment();

}
