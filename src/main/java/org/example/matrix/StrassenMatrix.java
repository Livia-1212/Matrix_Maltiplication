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

        // Compute inverse via ConventionalMatrix (with verbose logging)
        ConventionalMatrix conv = new ConventionalMatrix(M, true);
        RealMatrix Nmat = conv.getInverse();
        double[][] N = Nmat.getData();

        System.out.println("\n[Strassen Recursive Algorithm Begins]");

        // ✅ Time Strassen M × N
        long startStrassenMN = System.nanoTime();
        double[][] MN = strassenMultiply(M, N);
        long endStrassenMN = System.nanoTime();

        // ✅ Time Strassen N × M
        long startStrassenNM = System.nanoTime();
        double[][] NM = strassenMultiply(N, M);
        long endStrassenNM = System.nanoTime();

        // ✅ Output matrices
        System.out.println("\nStrassen M * N:");
        MatrixLogger.print(MN);
        System.out.println("\nStrassen N * M:");
        MatrixLogger.print(NM);

        // ✅ Validation
        System.out.println("\n[Validation]");
        System.out.println("Strassen M * N ≈ I: " + ConventionalMatrix.isIdentityStatic(MN));
        System.out.println("Strassen N * M ≈ I: " + ConventionalMatrix.isIdentityStatic(NM));

        // ✅ Total time for Strassen block
        long totalStrassenTime = endStrassenNM - startStrassenMN;

        System.out.println("\n[Performance]");
        System.out.println("Strassen M × N: " + (endStrassenMN - startStrassenMN) + " ns");
        System.out.println("Strassen N × M: " + (endStrassenNM - startStrassenNM) + " ns");
        System.out.println("Total Time (StrassenMatrix): " + totalStrassenTime + " ns");

        // ✅ NEW: Time breakdown for Conventional Multiplication (without LU)
        long startConvMN = System.nanoTime();
        RealMatrix MN_conv = conv.getMatrix().multiply(conv.getInverse());
        long endConvMN = System.nanoTime();

        long startConvNM = System.nanoTime();
        RealMatrix NM_conv = conv.getInverse().multiply(conv.getMatrix());
        long endConvNM = System.nanoTime();

        long timeConventionalMN = endConvMN - startConvMN;
        long timeConventionalNM = endConvNM - startConvNM;

        System.out.println("\n[Time Breakdown: Matrix Multiplication Only]");
        System.out.println("Conventional M × N: " + timeConventionalMN + " ns");
        System.out.println("Conventional N × M: " + timeConventionalNM + " ns");
        System.out.println("Strassen     M × N: " + (endStrassenMN - startStrassenMN) + " ns");
        System.out.println("Strassen     N × M: " + (endStrassenNM - startStrassenNM) + " ns");
    }

    // Recursive Strassen utilities (unchanged)
    public static double[][] strassenMultiply(double[][] A, double[][] B) {
        int n = nextPowerOfTwo(Math.max(A.length, A[0].length));
        double[][] APrep = padMatrix(A, n);
        double[][] BPrep = padMatrix(B, n);
        double[][] CPrep = strassenRecursive(APrep, BPrep);
        return cropMatrix(CPrep, A.length, B[0].length);
    }

    public static double[][] strassenRecursive(double[][] A, double[][] B) {
        int n = A.length;
        if (n == 1) return new double[][]{{A[0][0] * B[0][0]}};

        int mid = n / 2;
        double[][][] a = split(A), b = split(B);

        double[][] M1 = strassenRecursive(add(a[0], a[3]), add(b[0], b[3]));
        double[][] M2 = strassenRecursive(add(a[2], a[3]), b[0]);
        double[][] M3 = strassenRecursive(a[0], subtract(b[1], b[3]));
        double[][] M4 = strassenRecursive(a[3], subtract(b[2], b[0]));
        double[][] M5 = strassenRecursive(add(a[0], a[1]), b[3]);
        double[][] M6 = strassenRecursive(subtract(a[2], a[0]), add(b[0], b[1]));
        double[][] M7 = strassenRecursive(subtract(a[1], a[3]), add(b[2], b[3]));

        double[][] C11 = add(subtract(add(M1, M4), M5), M7);
        double[][] C12 = add(M3, M5);
        double[][] C21 = add(M2, M4);
        double[][] C22 = add(subtract(add(M1, M3), M2), M6);

        return combine(C11, C12, C21, C22);
    }

    public static int nextPowerOfTwo(int n) {
        int pow = 1;
        while (pow < n) pow *= 2;
        return pow;
    }

    public static double[][] padMatrix(double[][] mat, int size) {
        int r = mat.length, c = mat[0].length;
        double[][] padded = new double[size][size];
        for (int i = 0; i < r; i++)
            System.arraycopy(mat[i], 0, padded[i], 0, c);
        return padded;
    }

    public static double[][] cropMatrix(double[][] mat, int r, int c) {
        double[][] result = new double[r][c];
        for (int i = 0; i < r; i++)
            System.arraycopy(mat[i], 0, result[i], 0, c);
        return result;
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
