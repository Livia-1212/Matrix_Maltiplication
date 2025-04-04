package org.example.matrix;

public class MatrixLogger {

    public static void print(double[][] mat) {
        for (double[] row : mat) {
            for (double v : row) {
                if (Math.abs(v) < 1e-10) v = 0.0;  // cleanup -0.0000
                System.out.printf("%10.5f ", v);
            }
            System.out.println();
        }
    }
}
