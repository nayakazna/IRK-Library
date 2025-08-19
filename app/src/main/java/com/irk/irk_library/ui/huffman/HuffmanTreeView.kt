package com.irk.irk_library.ui.huffman

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.irk.irk_library.data.model.HuffmanNode
import kotlin.math.max
import kotlin.math.min

class HuffmanTreeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var rootNode: HuffmanNode? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var nodeRadius = 40f
    private var levelHeight = 120f

    init {
        setupPaints()
    }

    private fun setupPaints() {
        paint.color = Color.BLUE
        paint.style = Paint.Style.FILL

        textPaint.color = Color.WHITE
        textPaint.textSize = 28f
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.DEFAULT_BOLD

        linePaint.color = Color.BLACK
        linePaint.strokeWidth = 4f
        linePaint.style = Paint.Style.STROKE
    }

    fun setHuffmanTree(root: HuffmanNode?) {
        rootNode = root
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        rootNode?.let { root ->
            val centerX = width / 2f
            val startY = nodeRadius + 20f

            drawNode(canvas, root, centerX, startY, width / 4f, 0)
        }
    }

    private fun drawNode(
        canvas: Canvas,
        node: HuffmanNode,
        x: Float,
        y: Float,
        horizontalSpacing: Float,
        level: Int
    ) {
        val nodeColor = if (node.character != null) Color.GREEN else Color.BLUE
        paint.color = nodeColor
        canvas.drawCircle(x, y, nodeRadius, paint)

        val text = if (node.character != null) {
            "${node.character}\n${node.frequency}"
        } else {
            "${node.frequency}"
        }

        val lines = text.split('\n')
        val textHeight = textPaint.textSize
        val startY = y - (lines.size - 1) * textHeight / 2

        lines.forEachIndexed { index, line ->
            canvas.drawText(line, x, startY + index * textHeight, textPaint)
        }

        val childY = y + levelHeight
        val childSpacing = max(horizontalSpacing / 2, nodeRadius * 3)

        node.left?.let { leftChild ->
            val leftX = x - childSpacing
            canvas.drawLine(x, y + nodeRadius, leftX, childY - nodeRadius, linePaint)
            canvas.drawText("0", (x + leftX) / 2 - 20, (y + childY) / 2, textPaint.apply {
                color = Color.RED
                textSize = 20f
            })
            textPaint.color = Color.WHITE
            textPaint.textSize = 28f
            drawNode(canvas, leftChild, leftX, childY, childSpacing, level + 1)
        }

        node.right?.let { rightChild ->
            val rightX = x + childSpacing
            canvas.drawLine(x, y + nodeRadius, rightX, childY - nodeRadius, linePaint)
            canvas.drawText("1", (x + rightX) / 2 + 20, (y + childY) / 2, textPaint.apply {
                color = Color.RED
                textSize = 20f
            })
            textPaint.color = Color.WHITE
            textPaint.textSize = 28f
            drawNode(canvas, rightChild, rightX, childY, childSpacing, level + 1)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = calculateTreeHeight() + 100
        val height = resolveSize(desiredHeight.toInt(), heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun calculateTreeHeight(): Float {
        return if (rootNode != null) {
            getTreeDepth(rootNode!!) * levelHeight + nodeRadius * 2
        } else {
            200f
        }
    }

    private fun getTreeDepth(node: HuffmanNode?): Int {
        if (node == null) return 0
        return 1 + max(getTreeDepth(node.left), getTreeDepth(node.right))
    }
}