package com.irk.irk_library.ui.matrix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.irk.irk_library.R

class MatrixFragment : Fragment() {

    private lateinit var matrixViewModel: MatrixViewModel
    private var currentRows = 3
    private var currentCols = 3

    // UI Components
    private lateinit var etRows: EditText
    private lateinit var etCols: EditText
    private lateinit var btnCreateMatrix: Button
    private lateinit var spinnerOperation: Spinner
    private lateinit var gridMatrix: GridLayout
    private lateinit var layoutVectorB: LinearLayout
    private lateinit var layoutVectorInputs: LinearLayout
    private lateinit var btnSolve: Button
    private lateinit var layoutResults: LinearLayout
    private lateinit var tvResult: TextView
    private lateinit var tvSteps: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        matrixViewModel = ViewModelProvider(this)[MatrixViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_matrix, container, false)

        initViews(root)
        setupUI()
        setupObservers()
        createMatrixGrid()

        return root
    }

    private fun initViews(root: View) {
        etRows = root.findViewById(R.id.etRows)
        etCols = root.findViewById(R.id.etCols)
        btnCreateMatrix = root.findViewById(R.id.btnCreateMatrix)
        spinnerOperation = root.findViewById(R.id.spinnerOperation)
        gridMatrix = root.findViewById(R.id.gridMatrix)
        layoutVectorB = root.findViewById(R.id.layoutVectorB)
        layoutVectorInputs = root.findViewById(R.id.layoutVectorInputs)
        btnSolve = root.findViewById(R.id.btnSolve)
        layoutResults = root.findViewById(R.id.layoutResults)
        tvResult = root.findViewById(R.id.tvResult)
        tvSteps = root.findViewById(R.id.tvSteps)
    }

    private fun setupUI() {
        // Setup operation spinner
        val operations = arrayOf(
            "Gauss Elimination",
            "Gauss-Jordan",
            "Determinant",
            "Inverse",
            "Cramer's Rule"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, operations)
        spinnerOperation.adapter = adapter

        // Setup listeners
        btnCreateMatrix.setOnClickListener {
            createMatrixGrid()
        }

        btnSolve.setOnClickListener {
            solveMatrix()
        }

        spinnerOperation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val operation = operations[position]
                layoutVectorB.visibility = if (operation == "Cramer's Rule") View.VISIBLE else View.GONE

                when (operation) {
                    "Determinant", "Inverse" -> {
                        if (currentRows != currentCols) {
                            Toast.makeText(context, "Operasi ini cuma buat matriks persegi", Toast.LENGTH_SHORT).show()
                        }
                    }
                    "Cramer's Rule" -> {
                        if (currentRows != currentCols) {
                            Toast.makeText(context, "Cramer's rule cuma bisa buat matriks persegi", Toast.LENGTH_SHORT).show()
                        }
                        createVectorBInputs()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupObservers() {
        matrixViewModel.result.observe(viewLifecycleOwner) { result ->
            tvResult.text = result
        }

        matrixViewModel.steps.observe(viewLifecycleOwner) { steps ->
            tvSteps.text = steps
        }

        matrixViewModel.showResults.observe(viewLifecycleOwner) { show ->
            layoutResults.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    private fun createMatrixGrid() {
        val rowsText = etRows.text.toString()
        val colsText = etCols.text.toString()

        if (rowsText.isEmpty() || colsText.isEmpty()) {
            Toast.makeText(context, "Harap masukkan dimensi yang valid", Toast.LENGTH_SHORT).show()
            return
        }

        currentRows = rowsText.toIntOrNull() ?: 3
        currentCols = colsText.toIntOrNull() ?: 3

        if (currentRows < 1 || currentRows > 6 || currentCols < 1 || currentCols > 6) {
            Toast.makeText(context, "Ukuran matriks cuma antara 1x1 sampai 6x6", Toast.LENGTH_SHORT).show()
            return
        }

        gridMatrix.removeAllViews()
        gridMatrix.rowCount = currentRows
        gridMatrix.columnCount = currentCols

        for (i in 0 until currentRows) {
            for (j in 0 until currentCols) {
                val editText = EditText(requireContext()).apply {
                    layoutParams = ViewGroup.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setText("0")
                    inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    hint = "($i,$j)"
                }
                gridMatrix.addView(editText)
            }
        }

        matrixViewModel.resetResults()
    }

    private fun createVectorBInputs() {
        layoutVectorInputs.removeAllViews()

        for (i in 0 until currentRows) {
            val editText = EditText(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(120, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    marginEnd = 8
                }
                setText("0")
                inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                hint = "b$i"
            }
            layoutVectorInputs.addView(editText)
        }
    }

    private fun solveMatrix() {
        try {
            // Get matrix data from UI
            val matrixData = Array(currentRows) { DoubleArray(currentCols) }
            val gridChildren = gridMatrix.children.toList()

            for (i in 0 until currentRows) {
                for (j in 0 until currentCols) {
                    val editText = gridChildren[i * currentCols + j] as EditText
                    val value = editText.text.toString().toDoubleOrNull() ?: 0.0
                    matrixData[i][j] = value
                }
            }

            val operation = spinnerOperation.selectedItem.toString()

            var vectorB: DoubleArray? = null
            if (operation == "Cramer's Rule") {
                vectorB = DoubleArray(currentRows)
                val vectorInputs = layoutVectorInputs.children.toList()

                for (i in 0 until currentRows) {
                    val editText = vectorInputs[i] as EditText
                    vectorB[i] = editText.text.toString().toDoubleOrNull() ?: 0.0
                }
            }

            if ((operation == "Determinant" || operation == "Inverse" || operation == "Cramer's Rule") && currentRows != currentCols) {
                Toast.makeText(context, "Operasi '$operation' cuma buat matriks persegi", Toast.LENGTH_SHORT).show()
                return
            }

            // Solve
            matrixViewModel.solveMatrix(matrixData, operation, vectorB)

        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}