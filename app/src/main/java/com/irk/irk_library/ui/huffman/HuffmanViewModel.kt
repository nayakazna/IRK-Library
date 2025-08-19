package com.irk.irk_library.ui.huffman

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irk.irk_library.data.model.Huffman
import com.irk.irk_library.data.model.HuffmanStep
import com.irk.irk_library.data.model.HuffmanNode

class HuffmanViewModel : ViewModel() {
    private val _treeRoot = MutableLiveData<HuffmanNode?>()
    val treeRoot: LiveData<HuffmanNode?> = _treeRoot
    private val huffman = Huffman()

    private val _encodedResult = MutableLiveData<String>()
    val encodedResult: LiveData<String> = _encodedResult

    private val _decodedResult = MutableLiveData<String>()
    val decodedResult: LiveData<String> = _decodedResult

    private val _frequencyTable = MutableLiveData<String>()
    val frequencyTable: LiveData<String> = _frequencyTable

    private val _codeTable = MutableLiveData<String>()
    val codeTable: LiveData<String> = _codeTable

    private val _steps = MutableLiveData<String>()
    val steps: LiveData<String> = _steps

    private val _showResults = MutableLiveData<Boolean>()
    val showResults: LiveData<Boolean> = _showResults

    private val _compressionStats = MutableLiveData<String>()
    val compressionStats: LiveData<String> = _compressionStats

    fun encodeText(text: String) {
        try {
            val result = huffman.encode(text)

            _treeRoot.value = getTreeRootFromSteps(result.steps)

            _encodedResult.value = "Encoded: ${result.encodedText}"
            _decodedResult.value = "Decoded: ${result.decodedText}"

            // Format frequency table
            val freqTable = StringBuilder("Analisis Frekuensi:\n")
            result.frequencyTable.entries.sortedByDescending { it.value }.forEach { (char, freq) ->
                freqTable.append("'$char': $freq kali\n")
            }
            _frequencyTable.value = freqTable.toString()

            val codeTableText = StringBuilder("Tabel Kode Huffman:\n")
            result.codeTable.entries.sortedBy { it.key }.forEach { (char, code) ->
                codeTableText.append("'$char': $code\n")
            }
            _codeTable.value = codeTableText.toString()

            // Format steps
            _steps.value = formatHuffmanSteps(result.steps)

            // Calculate compression stats
            val originalBits = text.length * 8
            val compressedBits = result.encodedText.length
            val compressionRatio = if (originalBits > 0) {
                ((originalBits - compressedBits).toDouble() / originalBits * 100)
            } else 0.0

            _compressionStats.value = """
                Compression Statistics:
                Original size: $originalBits bits (${text.length} chars × 8 bits)
                Compressed size: $compressedBits bits
                Compression ratio: ${String.format("%.2f", compressionRatio)}%
                Space saved: ${originalBits - compressedBits} bits
            """.trimIndent()

            _showResults.value = true

        } catch (e: Exception) {
            _encodedResult.value = "Error: ${e.message}"
            _showResults.value = true
        }
    }

    private fun getTreeRootFromSteps(steps: List<HuffmanStep>): HuffmanNode? {
        for (step in steps.reversed()) {
            if (step.treeSnapshot != null && step.kind.name.contains("FINAL")) {
                return step.treeSnapshot
            }
        }
        return steps.lastOrNull { it.treeSnapshot != null }?.treeSnapshot
    }
    fun clearResults() {
        _showResults.value = false
        _treeRoot.value = null
        _encodedResult.value = ""
        _decodedResult.value = ""
        _frequencyTable.value = ""
        _codeTable.value = ""
        _steps.value = ""
        _compressionStats.value = ""
    }

    private fun formatHuffmanSteps(steps: List<HuffmanStep>): String {
        val sb = StringBuilder()
        steps.forEachIndexed { index, step ->
            sb.append("${index + 1}. ${step.description}\n")
            step.intermediateData?.let { data ->
                sb.append("   → $data\n")
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}