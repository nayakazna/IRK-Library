package com.irk.irk_library.data.model

data class HuffmanNode(
    val character: Char? = null,
    val frequency: Int,
    var left: HuffmanNode? = null,
    var right: HuffmanNode? = null
) : Comparable<HuffmanNode> {
    override fun compareTo(other: HuffmanNode): Int {
        return this.frequency.compareTo(other.frequency)
    }
}