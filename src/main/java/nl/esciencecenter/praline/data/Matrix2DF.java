package nl.esciencecenter.praline.data;

public class Matrix2DF {
    final double[] data;
    public final int nrRows, nrCols;

    public Matrix2DF(float[][] init){
        this.nrRows = init.length;
        this.nrCols = init[0].length;
        data = new double[nrRows * nrCols];
        for(int row = 0 ; row < nrRows; row++){
            for(int col = 0 ; col < nrCols ; col++){
                set(row,col,init[row][col]);
            }
        }
    }


    public Matrix2DF(int nrRows, int nrCols){
        data = new double[nrRows * nrCols];
        this.nrRows = nrRows;
        this.nrCols = nrCols;
    }

    public double get(int row, int col){
        return data[row * nrCols + col ];
    }

    public void set(int row, int col, double val){
        data[row * nrCols + col] = val;
    }
}
