package nl.esciencecenter.praline.data;

public class Matrix {

    int[] dimensions;
    int[] othersize;
    float[] arr;

    public Matrix(int ... dimensions){
        int size = 1;
        for(int i = dimensions.length - 1; i >= 0 ; i--){
            othersize[i] = size;
            size *= dimensions[i];
        }
        this.dimensions = dimensions;
        this.arr = new float[size];
    }

    int computeIndex(int ... index){
        if(index.length != dimensions.length){
            throw new Error("Incorrect dimension of index");
        }
        int res = 0;
        for(int i = 0 ; i < index.length ; i++){
            res+= index[i] * othersize[i];
        }
        return res;
    }



    public void set(float v,int ... index){
        arr[computeIndex(index)] = v;
    }

    public float get(int ... index){
        return arr[computeIndex(index)];
    }
}
