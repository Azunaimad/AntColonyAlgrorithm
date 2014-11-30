package matrix;

import java.util.Random;

/**
 * Created by Azunai on 29.11.14.
 */
public class Matrix {
    public double[][] matrix;
    public Matrix(int n, int m){
        matrix = new double[n][m];
    }
    public Matrix(double[][] matrix){
        this.matrix = matrix;
    }
    public Matrix(int n){
        this.matrix = new double[n][n];
    }
    public Matrix(){}

    public double getValue(int i, int j) {
        return matrix[i][j];
    }

    public void setValue(int i, int j, double value) {
        this.matrix[i][j]=value;
    }

    public int getNRows(){
        return matrix.length;
    }

    public int getNCols(){
        return matrix[0].length;
    }

    public Matrix divideMatrixElements(double n, Matrix matrix){
        Matrix result = new Matrix(matrix.getNRows(),matrix.getNCols());
        for(int i=0; i<result.getNRows();i++)
            for(int j=0; j<result.getNCols();j++)
                result.setValue(i, j, n / matrix.getValue(i,j));
        return result;
    }
    public Matrix divideMatrixElements( Matrix matrix, double n){
        Matrix result = new Matrix(matrix.getNRows(),matrix.getNCols());
        for(int i=0; i<matrix.getNRows();i++)
            for(int j=0; j<matrix.getNCols();j++)
                result.setValue(i,j,matrix.getValue(i,j) / n);
        return result;
    }
    public Matrix divideMatrixElements(Matrix matrix1, Matrix matrix2) throws NullPointerException {
        if(matrix1.getNRows() == matrix2.getNRows() &&
                matrix1.getNCols() == matrix2.getNCols())
        {
            Matrix result = new Matrix(matrix1.getNRows(),matrix1.getNCols());
            for(int i=0; i<result.getNRows();i++)
            {
                for(int j=0; j<result.getNCols();j++)
                {
                    result.setValue(i,j,matrix1.getValue(i,j) / matrix2.getValue(i,j));
                }
            }
            return result;
        }
        else
            return null;
    }
    public Matrix powerMatrixElements(double n, Matrix matrix){
        Matrix result = new Matrix(matrix.getNRows(),matrix.getNCols());
        for(int i=0; i<result.getNRows();i++)
            for(int j=0; j<result.getNCols();j++)
                result.setValue(i,j,Math.pow(matrix.getValue(i,j),n));
        return result;
    }
    public Matrix multiplyMatrixElements(double n, Matrix matrix){
        Matrix result = new Matrix(matrix.getNRows(),matrix.getNCols());
        for(int i=0; i<result.getNRows();i++)
            for(int j=0; j<result.getNCols();j++)
                result.setValue(i,j,matrix.getValue(i,j)*n);
        return result;
    }
    public Matrix multiplyMatrixElements(Matrix matrix1, Matrix matrix2)throws NullPointerException{
        if(matrix1.getNRows() == matrix2.getNRows() &&
                matrix1.getNCols() == matrix2.getNCols())
        {
            Matrix result = new Matrix(matrix1.getNRows(),matrix1.getNCols());
            for(int i=0; i<result.getNRows();i++)
            {
                for(int j=0; j<result.getNCols();j++)
                {
                    result.setValue(i,j,matrix1.getValue(i,j) * matrix2.getValue(i,j));
                }
            }
            return result;
        }
        else
            return null;
    }
    public double sumMatrixElements(Matrix matrix){
        double res=0;
        for(int i=0; i<matrix.getNRows(); i++)
            for(int j=0; j<matrix.getNCols();j++)
                res+=matrix.getValue(i,j);
        return res;
    }
    public double sumMatrixElements(Matrix matrix, int i1, int j1, int i2, int j2){
        double res=0;
        for(int i=i1; i<=i2; i++)
            for(int j=j1; j<=j2;j++)
                res+=matrix.getValue(i,j);
        return res;
    }
    public Matrix sumMatrix(Matrix matrix1, Matrix matrix2) throws NullPointerException{
        if(matrix1.getNRows() == matrix2.getNRows() &&
                matrix1.getNCols() == matrix2.getNCols())
        {
            Matrix result = new Matrix(matrix1.getNRows(),matrix1.getNCols());
            for(int i=0; i<result.getNRows();i++)
            {
                for(int j=0; j<result.getNCols();j++)
                {
                    result.setValue(i,j,matrix1.getValue(i,j) + matrix2.getValue(i,j));
                }
            }
            return result;
        }
        else
            return null;
    }

    public Matrix resizeMatrix(Matrix matrix,int newN, int newM){
        Matrix newMatrix = new Matrix(newN,newM);
        for(int i=0; i<newN; i++)
            for(int j=0; j<newM; j++)
                if(i<matrix.getNRows() && j< matrix.getNCols())
                    newMatrix.setValue(i,j,matrix.getValue(i,j));
                else
                    newMatrix.setValue(i,j,0);
        return newMatrix;
    }

    /**
     * Only for integer matrices
     * @param n
     * @param m
     * @return
     */
    public Matrix getRandPermutation(int n, int m){
        Matrix rp = new Matrix(n,m);
        Random r = new Random();
        for(int i=0; i<n;i++)
        {
            for(int j=0; j<m; j++){
                rp.setValue(i,j,j);
            }

            for(int j=0; j<m; j++){
                int ran = j + r.nextInt (m-j);
                double temp = rp.getValue(i,j);
                rp.setValue(i,j,rp.getValue(i,ran));
                rp.setValue(i,ran,temp);
            }
        }
        return rp;
    }

    public void printMatrix(Matrix matrix){
        for(int i=0; i<matrix.getNRows(); i++){
            for(int j=0; j<matrix.getNCols(); j++)
                System.out.print(matrix.getValue(i, j) + " ");
            System.out.println();
        }
    }

    public double getMinValue(){
        double minValue = matrix[0][0];
        for (double[] aMatrix : matrix)
            for (int j = 0; j < matrix[0].length; j++)
                if (aMatrix[j] < minValue)
                    minValue = aMatrix[j];
        return minValue;
    }
    public int[] getMinValueIndexes(){
        int[] indexes = {0,0};
        double minValue = matrix[0][0];
        for(int i=0;i<matrix.length;i++)
            for(int j=0; j<matrix[0].length;j++)
                if(matrix[i][j]<minValue)
                {
                    minValue = matrix[i][j];
                    indexes[0]=i;
                    indexes[1]=j;
                }
        return indexes;
    }

}
