package org.example.matrix;

import org.apache.commons.math3.linear.RealMatrix;

public class StrassenMatrix {
    public static void main(String[] args) {
        double[][] M = {
                {1, 3, 3, 6},
                {4, 2, 8, 2},
                {3, 3, 4, 5},
                {2, 6, 3, 1}
        };

        ConventionalMatrix conv = new ConventionalMatrix(M, true);  // logs automatically

        RealMatrix Nmat = conv.getInverse();
        double[][] N = Nmat.getData();

        System.out.println("\n[Strassen's Algorithm Begins]");
        long start = System.nanoTime();
        double[][] MN = strassenMultiply(M, N);
        double[][] NM = strassenMultiply(N, M);
        long end = System.nanoTime();

        System.out.println("\nStrassen M * N:");
        MatrixLogger.print(MN);
        System.out.println("\nStrassen N * M:");
        MatrixLogger.print(NM);

        System.out.println("\n[Validation]");
        System.out.println("Strassen M * N ≈ I: " + ConventionalMatrix.isIdentityStatic(MN));
        System.out.println("Strassen N * M ≈ I: " + ConventionalMatrix.isIdentityStatic(NM));

        System.out.println("\n[Performance]");
        System.out.println("Total Time (Strassen Multiplication): " + (end - start) + " ns");
    }

    public static double[][] strassenMultiply(double[][] A, double[][] B) {
        double[][][] a = split(A), b = split(B);
        double[][] M1 = multiply(add(a[0], a[3]), add(b[0], b[3]));
        double[][] M2 = multiply(add(a[2], a[3]), b[0]);
        double[][] M3 = multiply(a[0], subtract(b[1], b[3]));
        double[][] M4 = multiply(a[3], subtract(b[2], b[0]));
        double[][] M5 = multiply(add(a[0], a[1]), b[3]);
        double[][] M6 = multiply(subtract(a[2], a[0]), add(b[0], b[1]));
        double[][] M7 = multiply(subtract(a[1], a[3]), add(b[2], b[3]));
        double[][] C11 = add(subtract(add(M1, M4), M5), M7);
        double[][] C12 = add(M3, M5);
        double[][] C21 = add(M2, M4);
        double[][] C22 = add(subtract(add(M1, M3), M2), M6);
        return combine(C11, C12, C21, C22);
    }

    public static double[][] multiply(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    C[i][j] += A[i][k] * B[k][j];
        return C;
    }

    public static boolean isIdentity(double[][] A) {
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[0].length; j++)
                if (Math.abs(A[i][j] - (i == j ? 1 : 0)) > 1e-6)
                    return false;
        return true;
    }

    public static double[][][] split(double[][] M) {
        int mid = M.length / 2;
        double[][] a11 = new double[mid][mid];
        double[][] a12 = new double[mid][mid];
        double[][] a21 = new double[mid][mid];
        double[][] a22 = new double[mid][mid];
        for (int i = 0; i < mid; i++)
            for (int j = 0; j < mid; j++) {
                a11[i][j] = M[i][j];
                a12[i][j] = M[i][j + mid];
                a21[i][j] = M[i + mid][j];
                a22[i][j] = M[i + mid][j + mid];
            }
        return new double[][][]{a11, a12, a21, a22};
    }

    public static double[][] combine(double[][] c11, double[][] c12, double[][] c21, double[][] c22) {
        int mid = c11.length;
        double[][] C = new double[mid * 2][mid * 2];
        for (int i = 0; i < mid; i++)
            for (int j = 0; j < mid; j++) {
                C[i][j] = c11[i][j];
                C[i][j + mid] = c12[i][j];
                C[i + mid][j] = c21[i][j];
                C[i + mid][j + mid] = c22[i][j];
            }
        return C;
    }

    public static double[][] add(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] + B[i][j];
        return C;
    }

    public static double[][] subtract(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] - B[i][j];
        return C;
    }
}
