package com.irk.irk_library.data.model

import java.math.BigInteger

data class RsaKey(
    val n: BigInteger,
    val e: BigInteger,
    val d: BigInteger
)

class Rsa {

    // fungsi pembantu
    private fun gcd(a: BigInteger, b: BigInteger): BigInteger = a.gcd(b)

    private fun modInverse(a: BigInteger, m: BigInteger): BigInteger {
        var aVar = a
        var mVar = m
        val m0 = mVar
        var x0 = BigInteger.ZERO
        var x1 = BigInteger.ONE

        if (mVar == BigInteger.ONE) return BigInteger.ZERO

        while (aVar > BigInteger.ONE) {
            val q = aVar / mVar
            val t = mVar
            mVar = aVar % mVar
            aVar = t
            val temp = x0
            x0 = x1 - q * x0
            x1 = temp
        }

        if (x1 < BigInteger.ZERO) x1 += m0
        return x1
    }


    fun generateKeys(p: BigInteger, q: BigInteger): Pair<RsaKey, List<CryptoStep>> {
        val steps = mutableListOf<CryptoStep>()

        // Step 1: Prime selection
        steps.add(CryptoStep("Selected prime numbers p=$p and q=$q.", CryptoStepKind.RSA_PRIME_SELECTION))

        // Step 2: Calculate n = p * q
        val n = p * q
        steps.add(CryptoStep("Calculated n = p * q = $n.", CryptoStepKind.RSA_N_CALC, n.toString()))

        // Step 3: Calculate m (phi) = (p-1)(q-1)
        val m = (p - BigInteger.ONE) * (q - BigInteger.ONE)
        steps.add(CryptoStep("Calculated m = (p-1)(q-1) = $m.", CryptoStepKind.RSA_M_CALC, m.toString()))

        // Step 4: Choose e, relatively prime to m
        var e = BigInteger("65537") // Common choice for e
        if (gcd(e, m) != BigInteger.ONE) {
            // Find a valid e if the common one doesn't work
            e = BigInteger("2")
            while (e < m) {
                if (gcd(e, m) == BigInteger.ONE) break
                e++
            }
        }
        steps.add(CryptoStep("Selected a public key e that is relatively prime to m: e=$e.", CryptoStepKind.RSA_E_SELECTION, e.toString()))

        // Step 5: Calculate d, the modular multiplicative inverse of e mod m
        val d = modInverse(e, m)
        steps.add(CryptoStep("Calculated the private key d, where (e * d) mod m = 1. d=$d.", CryptoStepKind.RSA_D_CALC, d.toString()))

        val keys = RsaKey(n, e, d)
        steps.add(CryptoStep("Key pair generated. Public Key (e, n) = ($e, $n). Private Key (d, n) = ($d, $n).", CryptoStepKind.FINAL_RESULT))

        return Pair(keys, steps)
    }

    fun encrypt(message: String, publicKey: Pair<BigInteger, BigInteger>): Pair<List<BigInteger>, List<CryptoStep>> {
        val steps = mutableListOf<CryptoStep>()
        val (e, n) = publicKey
        val encryptedList = mutableListOf<BigInteger>()

        steps.add(CryptoStep("Starting RSA encryption with public key (e, n) = ($e, $n).", CryptoStepKind.RSA_ENCRYPTION))

        val bigIntMessage = BigInteger(message.toByteArray())
        val encrypted = bigIntMessage.modPow(e, n)

        encryptedList.add(encrypted)

        steps.add(CryptoStep("Encrypted message using c = m^e mod n. Result: $encrypted", CryptoStepKind.FINAL_RESULT))

        return Pair(encryptedList, steps)
    }

    fun decrypt(encryptedList: List<BigInteger>, privateKey: RsaKey): Pair<String, List<CryptoStep>> {
        val steps = mutableListOf<CryptoStep>()
        val (n, e, d) = privateKey

        steps.add(CryptoStep("Starting RSA decryption with private key (d, n) = ($d, $n).", CryptoStepKind.RSA_DECRYPTION))

        val decryptedList = mutableListOf<BigInteger>()

        encryptedList.forEach { encrypted ->
            val decrypted = encrypted.modPow(d, n)
            decryptedList.add(decrypted)
        }

        val decryptedBigInt = decryptedList[0]
        val originalMessage = String(decryptedBigInt.toByteArray())

        steps.add(CryptoStep("Decrypted message using m = c^d mod n. Result: $originalMessage", CryptoStepKind.FINAL_RESULT))

        return Pair(originalMessage, steps)
    }
}
