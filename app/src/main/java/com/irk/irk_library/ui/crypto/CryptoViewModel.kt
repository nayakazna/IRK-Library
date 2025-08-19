package com.irk.irk_library.ui.crypto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irk.irk_library.data.model.CaesarCipher
import com.irk.irk_library.data.model.Rsa
import com.irk.irk_library.data.model.RsaKey
import com.irk.irk_library.data.model.CryptoStep
import java.math.BigInteger

class CryptoViewModel : ViewModel() {

    private val caesarCipher = CaesarCipher()
    private val rsa = Rsa()
    private var currentRsaKeys: RsaKey? = null
    private var lastEncryptedData: List<BigInteger>? = null

    // Caesar Cipher LiveData
    private val _caesarResult = MutableLiveData<String>()
    val caesarResult: LiveData<String> = _caesarResult

    private val _caesarSteps = MutableLiveData<String>()
    val caesarSteps: LiveData<String> = _caesarSteps

    // RSA LiveData
    private val _rsaKeys = MutableLiveData<String>()
    val rsaKeys: LiveData<String> = _rsaKeys

    private val _rsaResult = MutableLiveData<String>()
    val rsaResult: LiveData<String> = _rsaResult

    private val _rsaSteps = MutableLiveData<String>()
    val rsaSteps: LiveData<String> = _rsaSteps

    private val _showRsaControls = MutableLiveData<Boolean>()
    val showRsaControls: LiveData<Boolean> = _showRsaControls

    fun caesarEncrypt(text: String, shift: Int) {
        val (result, steps) = caesarCipher.encrypt(text, shift)
        _caesarResult.value = "Dienkripsi: $result"
        _caesarSteps.value = formatCryptoSteps(steps)
    }

    fun caesarDecrypt(text: String, shift: Int) {
        val (result, steps) = caesarCipher.decrypt(text, shift)
        _caesarResult.value = "Terenkripsi: $result"
        _caesarSteps.value = formatCryptoSteps(steps)
    }

    fun generateRSAKeys(p: Long, q: Long) {
        try {
            val (keys, steps) = rsa.generateKeys(BigInteger.valueOf(p), BigInteger.valueOf(q))
            currentRsaKeys = keys

            _rsaKeys.value = """
                Keys berhasil dibuat!
                
                Public Key (e, n):
                e = ${keys.e}
                n = ${keys.n}
                
                Private Key (d, n):
                d = ${keys.d}
                n = ${keys.n}
            """.trimIndent()

            _rsaSteps.value = formatCryptoSteps(steps)
            _showRsaControls.value = true

        } catch (e: Exception) {
            _rsaKeys.value = "Error: ${e.message}"
            _rsaSteps.value = ""
        }
    }

    fun rsaEncrypt(text: String) {
        currentRsaKeys?.let { keys ->
            try {
                val (encrypted, steps) = rsa.encrypt(text, Pair(keys.e, keys.n))
                lastEncryptedData = encrypted

                _rsaResult.value = "Encrypted: ${encrypted.joinToString(", ")}"
                _rsaSteps.value = formatCryptoSteps(steps)

            } catch (e: Exception) {
                _rsaResult.value = "Encryption Error: ${e.message}"
            }
        } ?: run {
            _rsaResult.value = "Error: Mohon generate keysnya dulu"
        }
    }

    fun rsaDecrypt() {
        if (currentRsaKeys != null && lastEncryptedData != null) {
            try {
                val (decrypted, steps) = rsa.decrypt(lastEncryptedData!!, currentRsaKeys!!)

                _rsaResult.value = "Decrypted: $decrypted"
                _rsaSteps.value = formatCryptoSteps(steps)

            } catch (e: Exception) {
                _rsaResult.value = "Decryption Error: ${e.message}"
            }
        } else {
            _rsaResult.value = "Error: Tidak ada data/keys yang tersedia"
        }
    }

    private fun formatCryptoSteps(steps: List<CryptoStep>): String {
        val sb = StringBuilder()
        steps.forEachIndexed { index, step ->
            sb.append("${index + 1}. ${step.description}")
            step.intermediateValue?.let { value ->
                sb.append("\n   → $value")
            }
            sb.append("\n\n")
        }
        return sb.toString()
    }
}