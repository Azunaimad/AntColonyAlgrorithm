package matrix;

import java.util.Random;

/**
 * Created by Azunai on 29.11.14.
 */
public class Matrix {
    public double[][] matrix;

    /**
     * Create n-by-m matrix
     * @param n - number of rows
     * @param m - number of columns
     */
    public Matrix(int n, int m){
        matrix = new double[n][m];
    }

    /**
     * Create matrix using 2D double array
     * @param array - 2D double array
     */
    public Matrix(double[][] array){
        this.matrix = array;
    }

    /**
     * Create n-by-n matrix
     * @param n - n
     */
    public Matrix(int n){
        this.matrix = new double[n][n];
    }

    public Matrix(){}

    /**
     * Get matrix(i,j) value
     * @param i - row index
     * @param j - column index
     * @return matrix(i,j) value
     */
    public double getValue(int i, int j) {
        return matrix[i][j];
    }

    /**
     * Get matrix(i,j) value
     * @param i - row index
     * @param j - column index
     * @param value - wanted value
     */
    public void setValue(int i, int j, double value) {
        this.matrix[i][j]=value;
    }

    /**
     * Get number of rows in matrix
     * @return number of rows
     */
    public int getNRows(){
        return matrix.length;
    }

    /**
     * Get number of column in matrix
     * @return number of columns
     */
    public int getNCols(){
        return matrix[0].length;
    }

    public Matrix divideMatrixElements(double n, Matrix matrix){
        Matrix result = new Matrix(matrix.getNRows(),matrix.getNCols());
        for(int i=0; i<result.getNRows();i++)
            for(int j=0; j<result.getNCols();j++)
                result.setValue(i, j, 1.0 / matrix.getValue(i,j));
        return result;
    }

    /**
     * Divide matrix elements by number
     * @param matrix - matrix, which elements will be divided
     * @param n - number
     * @return new matrix
     */
    public Matrix divideMatrixElements(Matrix matrix, double n){
        Matrix result = new Matrix(matrix.getNRows(),matrix.getNCols());
        for(int i=0; i<matrix.getNRows();i++)
            for(int j=0; j<matrix.getNCols();j++)
                result.setValue(i,j,matrix.getValue(i,j) / n);
        return result;
    }

    /**
     * Divide matrix_1 NxM elements by matrix_2 elements
     * @param matrix1 - matrix, which elements will be divided
     * @param matrix2 - matrix, which elements are divisors
     * @return new matrix
     * @throws NullPointerException
     */
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

    /**
     * Power all matrix elements by n
     * @param n - power
     * @param matrix - - matrix, which elements will be powered
     * @return matrix
     */
    public Matrix powerMatrixElements(double n, Matrix matrix){
        Matrix result = new Matrix(matrix.getNRows(),matrix.getNCols());
        for(int i=0; i<result.getNRows();i++)
            for(int j=0; j<result.getNCols();j++)
                result.setValue(i,j,Math.pow(matrix.getValue(i,j),n));
        return result;
    }

    /**
     * Multiply matrix elements by n
     * @param n - factor
     * @param matrix - matrix, which elements will be multiplied
     * @return matrix
     */
    public Matrix multiplyMatrixElements(double n, Matrix matrix){
        Matrix result = new Matrix(matrix.getNRows(),matrix.getNCols());
        for(int i=0; i<result.getNRows();i++)
            for(int j=0; j<result.getNCols();j++)
                result.setValue(i,j,matrix.getValue(i,j)*n);
        return result;
    }

    /**
     * Multiply matrix_1 NxM elements by matrix_2 elements
     * @param matrix1 - matrix, which elements will be multiplied
     * @param matrix2 - matrix, which elements are divisors
     * @return matrix
     * @throws NullPointerException
     */
    public Matrix multiplyMatrixElements(Matrix matrix1, Matrix matrix2)throws NullPointerException{
        if(matrix1.getNRows() == matrix2.getNRows() &&
                matrix1.getNCols() == matrix2.getNCols())
        {
            Matrix result = new Matrix(matrix1.getNRows(),matrix1.getNCols());
            for(int i=0; i<result.getNRows();i++)
                for (int j = 0; j < result.getNCols(); j++)
                    result.setValue(i, j, matrix1.getValue(i, j) * matrix2.getValue(i, j));
            return result;
        }
        else
            return null;
    }

    /**
     * Summ all matrix elements
     * @param matrix - matrix, which elements will be summirised
     * @return sum
     */
    public double sumMatrixElements(Matrix matrix){
        double sum=0;
        for(int i=0; i<matrix.getNRows(); i++)
            for(int j=0; j<matrix.getNCols();j++)
                sum+=matrix.getValue(i,j);
        return sum;
    }

    /**
     * Sum matrix elements in range (i1:i2,j1:j2)
     * @param matrix - matrix, which elements will be summirised
     * @param i1 - start row
     * @param j1 - start column
     * @param i2 - end row
     * @param j2 - end column
     * @return sum
     */
    public double sumMatrixElements(Matrix matrix, int i1, int j1, int i2, int j2){
        double sum=0;
        for(int i=i1; i<=i2; i++)
            for(int j=j1; j<=j2;j++)
                sum+=matrix.getValue(i,j);
        return sum;
    }

    /**
     * Sum 2 matrices
     * @param matrix1 - first matrix
     * @param matrix2 - second matrix
     * @return summed matrix
     * @throws NullPointerException
     */
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

    /**
     * Resize matrix form (n,m) into (newN, newM).
     * If newN or newM greater than n or m, new elements will be field as 0
     * If newN or newM less than n or m, you will get matrix(0:newN,0:newM)
     * @param matrix - matrix, that will be resized
     * @param newN - new number of rows
     * @param newM - new number of columns
     * @return resized matrix
     */
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
     * Get random permutation of m in n matrix rows
     * @param n - number of matrix rows
     * @param m - permutation number
     * @return matrix with random permutation of m in n rows
     */
    public Matrix getRandPermutation(int n, int m){
        Matrix randomPermMatix = new Matrix(n,m);
        Random random = new Random();
        for(int i=0; i<n;i++)
        {
            for(int j=0; j<m; j++) randomPermMatix.setValue(i, j, j);

            for(int j=0; j<m; j++){
                int ran = j + random.nextInt (m-j);
                double temp = randomPermMatix.getValue(i,j);
                randomPermMatix.setValue(i, j, randomPermMatix.getValue(i, ran));
                randomPermMatix.setValue(i, ran, temp);
            }
        }
        return randomPermMatix;
    }

    /**
     * Print matrix in command line
     * @param matrix - printed matrix
     */
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
