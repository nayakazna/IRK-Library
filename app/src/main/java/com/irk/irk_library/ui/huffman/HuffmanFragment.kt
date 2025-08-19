package com.irk.irk_library.ui.huffman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.irk.irk_library.databinding.FragmentHuffmanBinding

class HuffmanFragment : Fragment() {

    private var _binding: FragmentHuffmanBinding? = null
    private val binding get() = _binding!!

    private lateinit var huffmanViewModel: HuffmanViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        huffmanViewModel = ViewModelProvider(this)[HuffmanViewModel::class.java]
        _binding = FragmentHuffmanBinding.inflate(inflater, container, false)

        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupUI() {
        binding.btnEncode.setOnClickListener {
            val text = binding.etInputText.text.toString()
            if (text.isNotEmpty()) {
                huffmanViewModel.encodeText(text)
            } else {
                Toast.makeText(context, "Masukkan teks untuk di-encode", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnClear.setOnClickListener {
            huffmanViewModel.clearResults()
            binding.etInputText.setText("")
        }
    }

    private fun setupObservers() {
        huffmanViewModel.treeRoot.observe(viewLifecycleOwner) { treeRoot ->
            binding.treeView.setHuffmanTree(treeRoot)
        }
        huffmanViewModel.encodedResult.observe(viewLifecycleOwner) { result ->
            binding.tvEncodedResult.text = result
        }

        huffmanViewModel.decodedResult.observe(viewLifecycleOwner) { result ->
            binding.tvDecodedResult.text = result
        }

        huffmanViewModel.frequencyTable.observe(viewLifecycleOwner) { table ->
            binding.tvFrequencyTable.text = table
        }

        huffmanViewModel.codeTable.observe(viewLifecycleOwner) { table ->
            binding.tvCodeTable.text = table
        }

        huffmanViewModel.steps.observe(viewLifecycleOwner) { steps ->
            binding.tvSteps.text = steps
        }

        huffmanViewModel.showResults.observe(viewLifecycleOwner) { show ->
            binding.layoutResults.visibility = if (show) View.VISIBLE else View.GONE
        }

        huffmanViewModel.compressionStats.observe(viewLifecycleOwner) { stats ->
            binding.tvCompressionStats.text = stats
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}