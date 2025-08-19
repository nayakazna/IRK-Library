package com.irk.irk_library.ui.matrix

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irk.irk_library.data.model.Matrix
import com.irk.irk_library.data.model.MatrixStep

class MatrixViewModel : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _steps = MutableLiveData<String>()
    val steps: LiveData<String> = _steps

    private val _showResults = MutableLiveData<Boolean>()
    val showResults: LiveData<Boolean> = _showResults

    fun solveMatrix(
        matrixData: Array<DoubleArray>,
        operation: String,
        vectorB: DoubleArray? = null
    ) {
        try {
            val matrix = Matrix(matrixData.size, matrixData[0].size, matrixData)

            when (operation) {
                "Gauss Elimination" -> {
                    val (resultMatrix, stepsList) = matrix.solveGaussEliminationSteps(matrix)
                    _result.value = formatMatrix(resultMatrix.data)
                    _steps.value = formatSteps(stepsList)
                }

                "Gauss-Jordan" -> {
                    val (resultMatrix, stepsList) = matrix.solveGaussJordan(matrix)
                    _result.value = formatMatrix(resultMatrix.data)
                    _steps.value = formatSteps(stepsList)
                }

                "Determinant" -> {
                    val (det, stepsList) = matrix.solveDeterminant(matrix)
                    _result.value = "Determinant = ${String.format("%.4f", det)}"
                    _steps.value = formatSteps(stepsList)
                }

                "Inverse" -> {
                    val (inverseMatrix, stepsList) = matrix.solveInverse(matrix)
                    _result.value = "Inverse Matrix:\n" + formatMatrix(inverseMatrix.data)
                    _steps.value = formatSteps(stepsList)
                }

                "Cramer's Rule" -> {
                    if (vectorB != null) {
                        val (solutions, stepsList) = matrix.solveCramer(matrix, vectorB)
                        _result.value = "Solution Vector:\n" + formatVector(solutions)
                        _steps.value = formatSteps(stepsList)
                    } else {
                        _result.value = "Error: Vector b is required for Cramer's rule"
                        _steps.value = ""
                    }
                }

                else -> {
                    _result.value = "Unknown operation"
                    _steps.value = ""
                }
            }

            _showResults.value = true

        } catch (e: Exception) {
            _result.value = "Error: ${e.message}"
            _steps.value = ""
            _showResults.value = true
        }
    }

    private fun formatMatrix(matrix: Array<DoubleArray>): String {
        val sb = StringBuilder()
        for (row in matrix) {
            sb.append("[ ")
            for (value in row) {
                sb.append(String.format("%8.3f ", value))
            }
            sb.append("]\n")
        }
        return sb.toString()
    }

    private fun formatVector(vector: DoubleArray): String {
        val sb = StringBuilder()
        vector.forEachIndexed { index, value ->
            sb.append("x${index + 1} = ${String.format("%.4f", value)}\n")
        }
        return sb.toString()
    }

    private fun formatSteps(steps: List<MatrixStep>): String {
        val sb = StringBuilder()
        steps.forEachIndexed { index, step ->
            sb.append("${index + 1}. ${step.description}\n")
            if (step.matrixSnapshot.isNotEmpty()) {
                sb.append(formatMatrix(step.matrixSnapshot))
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    fun resetResults() {
        _showResults.value = false
        _result.value = ""
        _steps.value = ""
    }
}