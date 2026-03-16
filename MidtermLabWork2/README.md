# Programming Assignment 1 - 3x3 Matrix Determinant Solver

## Student Information
- Student Name: CELADEZ, JED CEDRIC G.
- Course: Math 101 - Linear Algebra
- School: University of Perpetual Help System DALTA, Molino Campus
- Date Completed: March 16, 2026

## Assigned Matrix
\[
M = \begin{bmatrix}
5 & 3 & 1 \\
2 & 4 & 6 \\
1 & 5 & 3
\end{bmatrix}
\]

## Files Included
- DeterminantSolver.java
- determinant_solver.js

## How to Run
### Java
```bash
javac DeterminantSolver.java
java DeterminantSolver
```

### JavaScript (Node.js)
```bash
node determinant_solver.js
```

## Final Determinant Value
- det(M) = -84

## Sample Output (Java and JavaScript)
```text
=======================================================
  3x3 MATRIX DETERMINANT SOLVER
  Student: CELADEZ, JED CEDRIC G.
  Assigned Matrix:
=======================================================
  |  5  3  1 |
  |  2  4  6 |
  |  1  5  3 |
=======================================================

Expanding along Row 1 (cofactor expansion):

  Step 1 - Minor M11: det([4,6],[5,3]) = (4*3) - (6*5) = 12 - 30 = -18
  Step 2 - Minor M12: det([2,6],[1,3]) = (2*3) - (6*1) = 6 - 6 = 0
  Step 3 - Minor M13: det([2,4],[1,5]) = (2*5) - (4*1) = 10 - 4 = 6

  Cofactor C11 = (+1) * 5 * -18 = -90
  Cofactor C12 = (-1) * 3 * 0 = 0
  Cofactor C13 = (+1) * 1 * 6 = 6

  det(M) = -90 + (0) + 6
=======================================================
  DETERMINANT = -84
=======================================================
```
