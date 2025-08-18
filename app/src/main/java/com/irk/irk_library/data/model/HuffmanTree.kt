package com.irk.irk_library.data.model

data class HuffmanTree(
    val root: HuffmanNode,
    val frequencyTable: Map<Char, Int>,
    val codeTable: Map<Char, String>
)