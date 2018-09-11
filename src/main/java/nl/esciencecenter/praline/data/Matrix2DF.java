package nl.esciencecenter.praline.data;

import java.io.Serializable;

public class Matrix2DF implements Serializable {
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
        if(row >= nrRows){
            System.err.printf("Row out of bound %d bound %d\n",row, nrRows);
            throw new Error("Row out of bound");
        }
        if(col >= nrCols){
            System.err.printf("Col out of bound %d bound %d\n",col, nrCols);
            throw new Error("Col out of bound");
        }
        return data[row * nrCols + col ];
    }


    public boolean sameShape(Matrix2DF other) {
        return nrCols == other.nrCols() && nrRows == other.nrRows();
    }


    public int nrCols() {
        return nrCols;
    }

    public int nrRows() {
        return nrRows;
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
