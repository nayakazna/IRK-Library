package com.irk.irk_library.data.model

enum class StepKind {
    INITIAL_STATE,
    GAUSS_ROW_SWAP,
    GAUSS_ROW_SCALING,
    GAUSS_ROW_ELIMINATION,
    GAUSS_BACKWARD,
    GAUSSJ_BACKWARD,
    CRAMER_REPLACEMENT,
    FINAL_STATE,
    NA
}

data class MatrixStep(
    val description: String,
    val kind: StepKind,
    val matrixSnapshot: Array<DoubleArray>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MatrixStep

        if (description != other.description) return false
        if (!matrixSnapshot.contentDeepEquals(other.matrixSnapshot)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = description.hashCode()
        result = 31 * result + matrixSnapshot.contentDeepHashCode()
        return result
    }
}