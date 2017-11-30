package nl.esciencecenter.praline.data;

public class Matrix {

    int[] dimensions;
    int[] othersize;
    float[] arr;

    public Matrix(float[] arr, int ... dimensions){
        int size = computeSizes(dimensions);
        if(arr.length == size){
            this.arr =arr;
        } else {
            throw new Error("Incorrect size!");
        }

    }

    int computeSizes(int ... dimensions){
        int size = 1;
        othersize = new int[dimensions.length];
        for(int i = dimensions.length - 1; i >= 0 ; i--){
            othersize[i] = size;
            size *= dimensions[i];
        }
        this.dimensions = dimensions;
        return size;
    }

    public Matrix(int ... dimensions){
        int size = computeSizes(dimensions);
        this.arr = new float[size];
    }

    public void setMatrix(float[] arr){
        if(this.arr.length == arr.length){
            this.arr = arr;
        } else {
            throw new Error("Incorrect size!");
        }
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
