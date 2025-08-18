package com.irk.irk_library.data.model
import kotlin.math.abs

data class Matrix(val rows: Int, val columns: Int, val data: Array<DoubleArray>) {
//  1. SPL (via Gauss-Jordan)
    // fungsi pembantu, cuma sampe tahap eliminasi (ga sampe penyulihan mundur)
    fun solveGaussEliminationSteps(matrix: Matrix): Pair<Matrix, List<MatrixStep>> {
        val mtx = matrix.data.map { it.copyOf() }.toTypedArray()
        val steps = mutableListOf<MatrixStep>()
        val n = matrix.rows
        val m = matrix.columns

        steps.add(MatrixStep("Memulai eliminasi Gauss untuk mengubah matriks menjadi matriks eselon baris.", StepKind.INITIAL_STATE, mtx.map { it.copyOf() }.toTypedArray()))

        for (i in 0 until n) {
            // Find pivot row
            var maxRow = i
            for (k in i + 1 until n) {
                if (abs(mtx[k][i]) > abs(mtx[maxRow][i])) {
                    maxRow = k
                }
            }

            // tukar baris kalau maxRow bukan i
            if (i != maxRow) {
                val temp = mtx[i]
                mtx[i] = mtx[maxRow]
                mtx[maxRow] = temp
                steps.add(MatrixStep("Tukarkan R${i + 1} dan R${maxRow + 1} untuk mendapatkan pivot terbesar.", StepKind.GAUSS_ROW_SWAP, mtx.map { it.copyOf() }.toTypedArray()))
            }

            // eliminasi
            for (j in i + 1 until n) {
                if (mtx[i][i] == 0.0) continue
                val factor = mtx[j][i] / mtx[i][i]
                for (k in i until m) {
                    mtx[j][k] -= factor * mtx[i][k]
                }
                steps.add(MatrixStep("R${j + 1} -> R${j + 1} - (${String.format("%.2f", factor)}) * R${i + 1}", StepKind.GAUSS_ROW_ELIMINATION, mtx.map { it.copyOf() }.toTypedArray()))
            }
        }
        steps.add(MatrixStep("Selesai!.", StepKind.FINAL_STATE, mtx.map { it.copyOf() }.toTypedArray()))
        return Pair(Matrix(n, m, mtx), steps)
    }

    // fungsi utama
    fun solveGaussJordan(matrix: Matrix): Pair<Matrix, List<MatrixStep>> {
        val mtx = matrix.data.map { it.copyOf() }.toTypedArray()
        val steps = mutableListOf<MatrixStep>()
        val n = matrix.rows
        val m = matrix.columns

        // ambil tahap eliminasi dari Gauss
        val (gaussResult, gaussSteps) = solveGaussEliminationSteps(matrix)
        steps.addAll(gaussSteps)

        // satu utama
        for (i in 0 until n) {
            if (mtx[i][i] == 0.0) continue
            val pivotValue = mtx[i][i]
            for (k in i until m) {
                mtx[i][k] /= pivotValue
            }
            steps.add(MatrixStep("Kalikan R${i + 1} dengan 1/${String.format("%.2f", pivotValue)} untuk mendapatkan pivot terbesar.", StepKind.GAUSS_ROW_SCALING, mtx.map { it.copyOf() }.toTypedArray()))
        }

        // eliminasi elemen di atas satu utama
        for (i in n - 1 downTo 0) {
            for (j in 0 until i) {
                val factor = mtx[j][i]
                for (k in i until m) {
                    mtx[j][k] -= factor * mtx[i][k]
                }
                steps.add(MatrixStep("R${j + 1} -> R${j + 1} - (${String.format("%.2f", factor)}) * R${i + 1}", StepKind.GAUSSJ_BACKWARD, mtx.map { it.copyOf() }.toTypedArray()))
            }
        }

        steps.add(MatrixStep("Selesai! Matriks sudah berada dalam bentuk matriks eselon baris tereduksi. ", StepKind.FINAL_STATE, mtx.map { it.copyOf() }.toTypedArray()))
        return Pair(Matrix(n, m, mtx), steps)
    }


//  2a. Determinan
    // pake Gauss
    fun solveDeterminant(matrix: Matrix): Pair<Double, List<MatrixStep>> {
        if (matrix.rows != matrix.columns) {
            throw IllegalArgumentException("Determinan cuma buat matriks persegi!")
        }

        val steps = mutableListOf<MatrixStep>()
        steps.add(MatrixStep("Lakukan eliminasi Gauss untuk mengubah matriks menjadi matriks eselon baris (spesifiknya segitiga atas).", StepKind.INITIAL_STATE, matrix.data.map { it.copyOf() }.toTypedArray()))

        // ambil dari tahap eliminasi Gauss
        val (gaussResult, _) = solveGaussEliminationSteps(matrix)
        val upperTriangularMatrix = gaussResult.data
        steps.add(MatrixStep("Mendapatkan matriks segitiga atas.", StepKind.NA, upperTriangularMatrix.map { it.copyOf() }.toTypedArray()))

        // det = product dari seluruh elemen diagonal
        var det = 1.0
        for (i in 0 until matrix.rows) {
            det *= upperTriangularMatrix[i][i]
        }

        steps.add(MatrixStep("Determinan adalah hasil kali seluruh elemen diagonal.", StepKind.FINAL_STATE, upperTriangularMatrix.map { it.copyOf() }.toTypedArray()))

        return Pair(det, steps)
    }

    // pake dekomposisi LU (termasuk Divide and Conquer)

//  2b. Invers
    // pake Gauss-Jordan
fun solveInverse(matrix: Matrix): Pair<Matrix, List<MatrixStep>> {
    if (matrix.rows != matrix.columns) {
        throw IllegalArgumentException("Matriks bukan persegi tak punya invers.")
    }

    val steps = mutableListOf<MatrixStep>()
//    val (det, _) = solveDeterminant(matrix)
//    if (det == 0.0) {
//        steps.add(MatrixStep("Determinan adalah 0, jadi tidak punya invers.", StepKind.FINAL_STATE, matrix.data.map { it.copyOf() }.toTypedArray()))
//        return Pair(Matrix(matrix.rows, matrix.columns, emptyArray()), steps)
//    }

    val n = matrix.rows
    val augmentedData = Array(n) { DoubleArray(n * 2) }

    // bikin matriks augmented [A | I]
    for (i in 0 until n) {
        for (j in 0 until n) {
            augmentedData[i][j] = matrix.data[i][j]
            if (i == j) {
                augmentedData[i][j + n] = 1.0
            }
        }
    }
    steps.add(MatrixStep("Buat matriks augmented [A | I].", StepKind.NA, augmentedData.map { it.copyOf() }.toTypedArray()))

    val augmentedMatrix = Matrix(n, n * 2, augmentedData)

    // gauss Jordan
    val (gaussJordanResult, _) = solveGaussJordan(augmentedMatrix)
    val finalAugmentedMatrix = gaussJordanResult.data
    steps.add(MatrixStep("Lakukan eliminasi Gauss-Jordan untuk mendapatkan matriks augmented [I | A^-1].", StepKind.NA, finalAugmentedMatrix.map { it.copyOf() }.toTypedArray()))

    // cek apakah sisi kiri itu I
    for (i in 0 until n) {
        if (finalAugmentedMatrix[i][i] != 1.0) {
            steps.add(MatrixStep("Matriks tidak invertibel.", StepKind.FINAL_STATE, finalAugmentedMatrix.map { it.copyOf() }.toTypedArray()))
            throw IllegalArgumentException("The matrix is not invertible.")
        }
    }

    // ambil sisi kanan
    val inverseData = Array(n) { DoubleArray(n) }
    for (i in 0 until n) {
        for (j in 0 until n) {
            inverseData[i][j] = finalAugmentedMatrix[i][j + n]
        }
    }

    val inverseMatrix = Matrix(n, n, inverseData)
    steps.add(MatrixStep("Sisi kanan matriks tereduksi adalah inversnya!", StepKind.FINAL_STATE, inverseMatrix.data.map { it.copyOf() }.toTypedArray()))

    return Pair(inverseMatrix, steps)
}


//  3. Cramer
    // pake determinan via Gauss
fun solveCramer(matrix: Matrix, bVector: DoubleArray): Pair<DoubleArray, List<MatrixStep>> {
    if (matrix.rows != matrix.columns) {
        throw IllegalArgumentException("Matriks yang diberikan bukan matriks persegi.")
    }

    val steps = mutableListOf<MatrixStep>()
    val n = matrix.rows

    // cek ukuran bVector
    if (bVector.size != n) {
        throw IllegalArgumentException("Ukuran vektor b harus sama dengan jumlah baris dalam matriks.")
    }

    // itung determinan aseli
    val (detA, detASteps) = solveDeterminant(matrix)
    steps.addAll(detASteps)
    steps.add(MatrixStep("Determinan matriks asli: det(A) = ${String.format("%.2f", detA)}", StepKind.FINAL_STATE, matrix.data.map { it.copyOf() }.toTypedArray()))

    if (detA == 0.0) {
        throw IllegalArgumentException("Determinan nol. Aturan Cramer tidak bisa dipakai.")
    }

    val solutions = DoubleArray(n)

    // Ganti tiap kolom
    for (i in 0 until n) {
        val tempMatrixData = matrix.data.map { it.copyOf() }.toTypedArray()
        for (j in 0 until n) {
            tempMatrixData[j][i] = bVector[j]
        }
        val tempMatrix = Matrix(n, n, tempMatrixData)

        steps.add(MatrixStep("Ganti kolom ${i + 1} dengan vektor b.", StepKind.CRAMER_REPLACEMENT, tempMatrix.data.map { it.copyOf() }.toTypedArray()))

        // determinan matriks yang baru
        val (detTemp, _) = solveDeterminant(tempMatrix)

        // solusi untuk x_i
        solutions[i] = detTemp / detA
        steps.add(MatrixStep("det(A_${i+1}) / det(A) = ${String.format("%.2f", detTemp)} / ${String.format("%.2f", detA)} = ${String.format("%.2f", solutions[i])}", StepKind.FINAL_STATE, tempMatrix.data.map { it.copyOf() }.toTypedArray()))
    }

    steps.add(MatrixStep("Vektor solusi akhir.", StepKind.FINAL_STATE, Array(1) { solutions }))

    return Pair(solutions, steps)
}


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix

        if (rows != other.rows) return false
        if (columns != other.columns) return false
        if (!data.contentDeepEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + columns
        result = 31 * result + data.contentDeepHashCode()
        return result
    }
}