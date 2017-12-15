package nl.esciencecenter.praline.data;

public class Matrix2DI {
    final int[] data;
    public final int nrRows, nrCols;

    public Matrix2DI(int nrRows, int nrCols){
        data = new int[nrRows * nrCols];
        this.nrRows = nrRows;
        this.nrCols = nrCols;
    }

    public int get(int row, int col){
        return data[row * nrCols + col ];
    }

    public void set(int row, int col, int val){
        data[row * nrCols + col] = val;
    }
}


