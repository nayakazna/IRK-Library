package com.irk.irk_library.data.model

enum class HuffmanStepKind {
    INITIAL_FREQUENCY_ANALYSIS,
    CONSTRUCTING_NODE_LIST,
    COMBINING_NODES,
    BUILDING_FINAL_TREE,
    GENERATING_CODES,
    ENCODING_TEXT,
    DECODING_TEXT,
    FINAL_STATE
}

data class HuffmanStep(
    val description: String,
    val kind: HuffmanStepKind,
    val treeSnapshot: HuffmanNode?,
    val intermediateData: String? = null
)