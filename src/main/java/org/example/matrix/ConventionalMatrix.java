package org.example.matrix;

import org.apache.commons.math3.linear.*;

public class ConventionalMatrix {
    private final RealMatrix M;
    private final RealMatrix N;
    private final RealMatrix MN;
    private final RealMatrix NM;
    private final LUDecomposition lu;

    private final long timeLU;
    private final long timeMult;
    private final long totalTime;

    public ConventionalMatrix(double[][] matrix) {
        this(matrix, false);
    }

    public ConventionalMatrix(double[][] matrix, boolean verbose) {
        long startLU = System.nanoTime();
        this.M = MatrixUtils.createRealMatrix(matrix);
        this.lu = new LUDecomposition(M);
        this.N = lu.getSolver().getInverse();
        long endLU = System.nanoTime();

        long startMult = System.nanoTime();
        this.MN = M.multiply(N);
        this.NM = N.multiply(M);
        long endMult = System.nanoTime();

        this.timeLU = endLU - startLU;
        this.timeMult = endMult - startMult;
        this.totalTime = endMult - startLU;

        if (verbose) logDetails();
    }

    public void logDetails() {
        System.out.println("Original Matrix M:");
        MatrixLogger.print(M.getData());

        System.out.println("\n[LU Decomposition Info]");
        System.out.println("L:");
        MatrixLogger.print(lu.getL().getData());
        System.out.println("U:");
        MatrixLogger.print(lu.getU().getData());
        System.out.println("P (Permutation):");
        MatrixLogger.print(lu.getP().getData());

        System.out.println("\nInverse Matrix N:");
        MatrixLogger.print(N.getData());

        System.out.println("\nMatrix Product M * N:");
        MatrixLogger.print(MN.getData());

        System.out.println("\nMatrix Product N * M:");
        MatrixLogger.print(NM.getData());

        System.out.println("\n[Validation]");
        System.out.println("M * N ≈ I: " + isIdentity(MN));
        System.out.println("N * M ≈ I: " + isIdentity(NM));

        System.out.println("\n[Performance]");
        System.out.println("Time (LU Inversion): " + timeLU + " ns");
        System.out.println("Time (Multiplication): " + timeMult + " ns");
        System.out.println("Total Time (ConventionalMatrix): " + totalTime + " ns");
    }

    public boolean isIdentity(RealMatrix A) {
        RealMatrix identity = MatrixUtils.createRealIdentityMatrix(A.getRowDimension());
        return A.subtract(identity).getNorm() < 1e-6;
    }

    public RealMatrix getInverse() {
        return N;
    }

    public static boolean isIdentityStatic(double[][] A) {
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[0].length; j++)
                if (Math.abs(A[i][j] - (i == j ? 1 : 0)) > 1e-6)
                    return false;
        return true;
    }

    public static void main(String[] args) {
        double[][] M = {
                {1, 3, 3, 6},
                {4, 2, 8, 2},
                {3, 3, 4, 5},
                {2, 6, 3, 1}
        };

        ConventionalMatrix conv = new ConventionalMatrix(M, true);
    }
}
