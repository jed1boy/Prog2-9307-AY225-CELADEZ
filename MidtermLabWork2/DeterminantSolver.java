/**
 * =====================================================
 * Student Name    : CELADEZ, JED CEDRIC G.
 * Course          : Math 101 - Linear Algebra
 * Assignment      : Programming Assignment 1 - 3x3 Matrix Determinant Solver
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 16, 2026
 * GitHub Repo     : https://github.com/[your-username]/uphsd-cs-celadez-jedcedric
 *
 * Description:
 *   This program computes the determinant of a hardcoded 3x3 matrix assigned
 *   to Jed Cedric G. Celadez for Math 101. The solution uses cofactor
 *   expansion along the first row and prints each major computation step.
 * =====================================================
 */
public class DeterminantSolver {

    // Assigned 3x3 matrix in row-major order.
    static int[][] matrix = {
        { 5, 3, 1 },
        { 2, 4, 6 },
        { 1, 5, 3 }
    };

    // Computes a 2x2 determinant: (a*d) - (b*c).
    static int computeMinor(int a, int b, int c, int d) {
        return (a * d) - (b * c);
    }

    // Prints the matrix in a readable 3x3 layout.
    static void printMatrix(int[][] m) {
        for (int[] row : m) {
            System.out.printf("  | %2d %2d %2d |%n", row[0], row[1], row[2]);
        }
    }

    // Solves determinant using cofactor expansion along the first row.
    static void solveDeterminant(int[][] m) {
        String line = "=".repeat(55);

        System.out.println(line);
        System.out.println("  3x3 MATRIX DETERMINANT SOLVER");
        System.out.println("  Student: CELADEZ, JED CEDRIC G.");
        System.out.println("  Assigned Matrix:");
        System.out.println(line);
        printMatrix(m);
        System.out.println(line);
        System.out.println();
        System.out.println("Expanding along Row 1 (cofactor expansion):");
        System.out.println();

        // Step 1: Minor M11 from rows/cols excluding row 1 and col 1.
        int a11 = m[1][1], b11 = m[1][2], c11m = m[2][1], d11 = m[2][2];
        int ad11 = a11 * d11;
        int bc11 = b11 * c11m;
        int minor11 = computeMinor(a11, b11, c11m, d11);
        System.out.printf(
            "  Step 1 - Minor M11: det([%d,%d],[%d,%d]) = (%d*%d) - (%d*%d) = %d - %d = %d%n",
            a11, b11, c11m, d11, a11, d11, b11, c11m, ad11, bc11, minor11
        );

        // Step 2: Minor M12 from rows/cols excluding row 1 and col 2.
        int a12 = m[1][0], b12 = m[1][2], c12m = m[2][0], d12 = m[2][2];
        int ad12 = a12 * d12;
        int bc12 = b12 * c12m;
        int minor12 = computeMinor(a12, b12, c12m, d12);
        System.out.printf(
            "  Step 2 - Minor M12: det([%d,%d],[%d,%d]) = (%d*%d) - (%d*%d) = %d - %d = %d%n",
            a12, b12, c12m, d12, a12, d12, b12, c12m, ad12, bc12, minor12
        );

        // Step 3: Minor M13 from rows/cols excluding row 1 and col 3.
        int a13 = m[1][0], b13 = m[1][1], c13m = m[2][0], d13 = m[2][1];
        int ad13 = a13 * d13;
        int bc13 = b13 * c13m;
        int minor13 = computeMinor(a13, b13, c13m, d13);
        System.out.printf(
            "  Step 3 - Minor M13: det([%d,%d],[%d,%d]) = (%d*%d) - (%d*%d) = %d - %d = %d%n",
            a13, b13, c13m, d13, a13, d13, b13, c13m, ad13, bc13, minor13
        );

        // Apply signs for first-row cofactor expansion: + - +.
        int c11 =  m[0][0] * minor11;
        int c12 = -m[0][1] * minor12;
        int c13 =  m[0][2] * minor13;

        System.out.println();
        System.out.printf("  Cofactor C11 = (+1) * %d * %d = %d%n", m[0][0], minor11, c11);
        System.out.printf("  Cofactor C12 = (-1) * %d * %d = %d%n", m[0][1], minor12, c12);
        System.out.printf("  Cofactor C13 = (+1) * %d * %d = %d%n", m[0][2], minor13, c13);

        int det = c11 + c12 + c13;
        System.out.println();
        System.out.printf("  det(M) = %d + (%d) + %d%n", c11, c12, c13);
        System.out.println(line);
        System.out.printf("  DETERMINANT = %d%n", det);

        if (det == 0) {
            System.out.println("  The matrix is SINGULAR - it has no inverse.");
        }

        System.out.println(line);
    }

    public static void main(String[] args) {
        solveDeterminant(matrix);
    }
}

