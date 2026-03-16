/**
 * =====================================================
 * Student Name    : CELADEZ, JED CEDRIC G.
 * Course          : Math 101 - Linear Algebra
 * Assignment      : Programming Assignment 1 - 3x3 Matrix Determinant Solver
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 16, 2026
 * GitHub Repo     : https://github.com/[your-username]/uphsd-cs-celadez-jedcedric
 * Runtime         : Node.js (run with: node determinant_solver.js)
 *
 * Description:
 *   JavaScript implementation of a 3x3 determinant solver using
 *   cofactor expansion along the first row with step-by-step output.
 * =====================================================
 */

// Assigned 3x3 matrix in row-major order.
const matrix = [
    [5, 3, 1],
    [2, 4, 6],
    [1, 5, 3]
];

// Prints the matrix in a readable 3x3 layout.
function printMatrix(m) {
    m.forEach((row) => {
        const fmt = row.map((v) => v.toString().padStart(2)).join(" ");
        console.log(`  | ${fmt} |`);
    });
}

// Computes a 2x2 determinant: (a*d) - (b*c).
function computeMinor(a, b, c, d) {
    return (a * d) - (b * c);
}

// Solves determinant using cofactor expansion along the first row.
function solveDeterminant(m) {
    const line = "=".repeat(55);

    console.log(line);
    console.log("  3x3 MATRIX DETERMINANT SOLVER");
    console.log("  Student: CELADEZ, JED CEDRIC G.");
    console.log("  Assigned Matrix:");
    console.log(line);
    printMatrix(m);
    console.log(line);
    console.log();
    console.log("Expanding along Row 1 (cofactor expansion):");
    console.log();

    // Step 1: Minor M11 from rows/cols excluding row 1 and col 1.
    const a11 = m[1][1], b11 = m[1][2], c11m = m[2][1], d11 = m[2][2];
    const ad11 = a11 * d11;
    const bc11 = b11 * c11m;
    const minor11 = computeMinor(a11, b11, c11m, d11);
    console.log(
        `  Step 1 - Minor M11: det([${a11},${b11}],[${c11m},${d11}]) = (${a11}*${d11}) - (${b11}*${c11m}) = ${ad11} - ${bc11} = ${minor11}`
    );

    // Step 2: Minor M12 from rows/cols excluding row 1 and col 2.
    const a12 = m[1][0], b12 = m[1][2], c12m = m[2][0], d12 = m[2][2];
    const ad12 = a12 * d12;
    const bc12 = b12 * c12m;
    const minor12 = computeMinor(a12, b12, c12m, d12);
    console.log(
        `  Step 2 - Minor M12: det([${a12},${b12}],[${c12m},${d12}]) = (${a12}*${d12}) - (${b12}*${c12m}) = ${ad12} - ${bc12} = ${minor12}`
    );

    // Step 3: Minor M13 from rows/cols excluding row 1 and col 3.
    const a13 = m[1][0], b13 = m[1][1], c13m = m[2][0], d13 = m[2][1];
    const ad13 = a13 * d13;
    const bc13 = b13 * c13m;
    const minor13 = computeMinor(a13, b13, c13m, d13);
    console.log(
        `  Step 3 - Minor M13: det([${a13},${b13}],[${c13m},${d13}]) = (${a13}*${d13}) - (${b13}*${c13m}) = ${ad13} - ${bc13} = ${minor13}`
    );

    // Apply signs for first-row cofactor expansion: + - +.
    const c11 =  m[0][0] * minor11;
    const c12 = -m[0][1] * minor12;
    const c13 =  m[0][2] * minor13;

    console.log();
    console.log(`  Cofactor C11 = (+1) * ${m[0][0]} * ${minor11} = ${c11}`);
    console.log(`  Cofactor C12 = (-1) * ${m[0][1]} * ${minor12} = ${c12}`);
    console.log(`  Cofactor C13 = (+1) * ${m[0][2]} * ${minor13} = ${c13}`);

    const det = c11 + c12 + c13;
    console.log();
    console.log(`  det(M) = ${c11} + (${c12}) + ${c13}`);
    console.log(line);
    console.log(`  DETERMINANT = ${det}`);

    if (det === 0) {
        console.log("  The matrix is SINGULAR - it has no inverse.");
    }

    console.log(line);
}

solveDeterminant(matrix);
