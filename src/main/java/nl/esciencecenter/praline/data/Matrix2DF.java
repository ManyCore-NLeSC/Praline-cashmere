package nl.esciencecenter.praline.data;

public class Matrix2DF {
    final float[] data;
    public final int nrRows, nrCols;

    public Matrix2DF(float[][] init){
        this.nrRows = init.length;
        this.nrCols = init[0].length;
        data = new float[nrRows * nrCols];
        for(int row = 0 ; row < nrRows; row++){
            for(int col = 0 ; col < nrCols ; col++){
                set(row,col,init[row][col]);
            }
        }
    }


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

    public void printMatrix(){
        for(int i = 0 ; i < nrRows ; i++){
            for(int j = 0 ; j < nrCols ; j++){
                System.out.printf("%3.1f ",get(i,j));

            }
            System.out.println();
        }
    }
}
