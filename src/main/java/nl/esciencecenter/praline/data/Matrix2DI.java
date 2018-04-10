package nl.esciencecenter.praline.data;

public class Matrix2DI {
    final int[] data;
    public final int nrRows, nrCols;

    public Matrix2DI(int[][] init){
        this.nrRows = init.length;
        this.nrCols = init[0].length;
        data = new int[nrRows * nrCols];
        for(int row = 0 ; row < nrRows; row++){
            for(int col = 0 ; col < nrCols ; col++){
                set(row,col,init[row][col]);
            }
        }
    }

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

    public void printMatrix(){
        for(int i = 0 ; i < nrRows ; i++){
            for(int j = 0 ; j < nrCols ; j++){
                System.out.printf("%3d ",get(i,j));

            }
            System.out.println();
        }
    }
}


