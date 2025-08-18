package com.irk.irk_library.data.model

data class HuffmanResult(
    val encodedText: String,
    val decodedText: String,
    val frequencyTable: Map<Char, Int>,
    val codeTable: Map<Char, String>,
    val steps: List<HuffmanStep>
)