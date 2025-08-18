package com.irk.irk_library.data.model

class CaesarCipher {

    fun encrypt(plainText: String, shift: Int): Pair<String, List<CryptoStep>> {
        val steps = mutableListOf<CryptoStep>()
        val cipherText = StringBuilder()

        steps.add(CryptoStep("Menggeser alfabet sebesar $shift karakter. ", CryptoStepKind.CAESAR_ENCRYPTION_START))

        plainText.forEach { char ->
            if (char.isLetter()) {
                val base = if (char.isUpperCase()) 'A' else 'a'
                val shiftedChar = ((char - base + shift) % 26 + base.code).toChar()
                steps.add(CryptoStep("Mengenkripsi karakter '$char' ke '$shiftedChar'.", CryptoStepKind.CAESAR_CHAR_SHIFT))
                cipherText.append(shiftedChar)
            } else {
                cipherText.append(char)
            }
        }

        steps.add(CryptoStep("Enkripsi berhasil.", CryptoStepKind.FINAL_RESULT, cipherText.toString()))
        return Pair(cipherText.toString(), steps)
    }

    fun decrypt(cipherText: String, shift: Int): Pair<String, List<CryptoStep>> {
        val steps = mutableListOf<CryptoStep>()
        val plainText = StringBuilder()

        steps.add(CryptoStep("Menggeser alfabet sebesar $shift.", CryptoStepKind.CAESAR_DECRYPTION_START))

        // Decrypting is just encrypting with a negative shift
        val (decryptedResult, decryptSteps) = encrypt(cipherText, 26 - shift)
        steps.addAll(decryptSteps)

        steps.add(CryptoStep("Dekripsi berhasil.", CryptoStepKind.FINAL_RESULT, decryptedResult))
        return Pair(decryptedResult, steps)
    }
}
