package com.irk.irk_library.data.model

enum class CryptoStepKind {
    CAESAR_ENCRYPTION_START,
    CAESAR_CHAR_SHIFT,
    CAESAR_DECRYPTION_START,
    RSA_KEYGEN_START,
    RSA_PRIME_SELECTION,
    RSA_N_CALC,
    RSA_M_CALC,
    RSA_E_SELECTION,
    RSA_D_CALC,
    RSA_ENCRYPTION,
    RSA_DECRYPTION,
    FINAL_RESULT
}

data class CryptoStep(
    val description: String,
    val kind: CryptoStepKind,
    val intermediateValue: String? = null
)