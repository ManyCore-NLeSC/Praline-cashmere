package nl.esciencecenter.praline.data;

public class Matrix2DF {
    final float[] data;
    public final int nrRows, nrCols;

    public Matrix2DF(int nrRows, int nrCols){
        data = new float[nrRows * nrCols];
        this.nrRows = nrRows;
        this.nrCols = nrCols;
    }

    public float get(int row, int col){
        return data[row * nrCols + col ];
    }

    public void set(int row, int col, float val){
        data[row * nrCols + col] = val;
    }
}
