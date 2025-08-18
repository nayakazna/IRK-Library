package com.irk.irk_library.data.model
import java.util.PriorityQueue

class Huffman {
    fun encode(text: String): HuffmanResult {
        val steps = mutableListOf<HuffmanStep>()

        // itung frekuensi
        val frequencyMap = text.groupingBy { it }.eachCount()
        steps.add(HuffmanStep("Hitung frekuensi kemunculan tiap karakter.", HuffmanStepKind.INITIAL_FREQUENCY_ANALYSIS, null, frequencyMap.toString()))

        // bikin tree-nya
        val priorityQueue = PriorityQueue<HuffmanNode>()
        frequencyMap.forEach { (char, freq) ->
            priorityQueue.add(HuffmanNode(char, freq))
        }
        steps.add(HuffmanStep("Mengurutkan simpul berdasarkan frekuensi.", HuffmanStepKind.CONSTRUCTING_NODE_LIST, null, priorityQueue.toString()))

        var root: HuffmanNode? = null
        while (priorityQueue.size > 1) {
            val left = priorityQueue.poll()
            val right = priorityQueue.poll()
            val combinedNode = HuffmanNode(null, left.frequency + right.frequency, left, right)
            priorityQueue.add(combinedNode)

            steps.add(HuffmanStep("Menggabungkan simpul dengan frekuensi terendah: '${left.character ?: 'N'}'(${left.frequency}) dan '${right.character ?: 'N'}'(${right.frequency}).", HuffmanStepKind.COMBINING_NODES, combinedNode, null))

            root = combinedNode
        }
        root = priorityQueue.poll()
        steps.add(HuffmanStep("Pohon Huffman sudah jadi.", HuffmanStepKind.BUILDING_FINAL_TREE, root, null))

        // kode biner
        val codeTable = mutableMapOf<Char, String>()
        generateCodes(root, "", codeTable, steps)
        steps.add(HuffmanStep("Membuat kode biner untuk tiap karakter.", HuffmanStepKind.GENERATING_CODES, root, codeTable.toString()))

        // encode
        val encodedText = StringBuilder()
        text.forEach { char ->
            encodedText.append(codeTable[char])
        }
        steps.add(HuffmanStep("Encoding ke karakter biner.", HuffmanStepKind.ENCODING_TEXT, root, encodedText.toString()))

        // decode (perlukah?)
        val decodedText = decode(encodedText.toString(), root!!)
        steps.add(HuffmanStep("Decoding ke karakter semula.", HuffmanStepKind.DECODING_TEXT, root, decodedText))

        return HuffmanResult(encodedText.toString(), decodedText, frequencyMap, codeTable, steps)
    }

    private fun generateCodes(
        node: HuffmanNode?,
        code: String,
        codeTable: MutableMap<Char, String>,
        steps: MutableList<HuffmanStep>
    ) {
        if (node == null) return
        if (node.character != null) {
            codeTable[node.character] = code
            return
        }
        generateCodes(node.left, code + "0", codeTable, steps)
        generateCodes(node.right, code + "1", codeTable, steps)
    }

    private fun decode(encodedText: String, root: HuffmanNode): String {
        val decodedText = StringBuilder()
        var currentNode = root
        encodedText.forEach { bit ->
            if (bit == '0') {
                currentNode = currentNode.left!!
            } else {
                currentNode = currentNode.right!!
            }
            if (currentNode.character != null) {
                decodedText.append(currentNode.character)
                currentNode = root
            }
        }
        return decodedText.toString()
    }
}