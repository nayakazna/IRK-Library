package com.irk.irk_library.ui.crypto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.irk.irk_library.databinding.FragmentCryptoBinding

class CryptoFragment : Fragment() {

    private var _binding: FragmentCryptoBinding? = null
    private val binding get() = _binding!!

    private lateinit var cryptoViewModel: CryptoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cryptoViewModel = ViewModelProvider(this)[CryptoViewModel::class.java]
        _binding = FragmentCryptoBinding.inflate(inflater, container, false)

        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupUI() {
        // buatCaesar Cipher
        binding.btnCaesarEncrypt.setOnClickListener {
            val text = binding.etInputText.text.toString()
            val shift = binding.etShift.text.toString().toIntOrNull() ?: 0
            if (text.isNotEmpty()) {
                cryptoViewModel.caesarEncrypt(text, shift)
            } else {
                Toast.makeText(context, "Masukkan teks untuk dienkripsi", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCaesarDecrypt.setOnClickListener {
            val text = binding.etInputText.text.toString()
            val shift = binding.etShift.text.toString().toIntOrNull() ?: 0
            if (text.isNotEmpty()) {
                cryptoViewModel.caesarDecrypt(text, shift)
            } else {
                Toast.makeText(context, "Masukkan teks untuk didekripsi", Toast.LENGTH_SHORT).show()
            }
        }

        // buat RSA
        binding.btnGenerateKeys.setOnClickListener {
            val p = binding.etPrimeP.text.toString().toLongOrNull()
            val q = binding.etPrimeQ.text.toString().toLongOrNull()

            if (p != null && q != null) {
                cryptoViewModel.generateRSAKeys(p, q)
            } else {
                Toast.makeText(context, "Mohon masukkan bilangan prima yang valdi", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRsaEncrypt.setOnClickListener {
            val text = binding.etRsaText.text.toString()
            if (text.isNotEmpty()) {
                cryptoViewModel.rsaEncrypt(text)
            } else {
                Toast.makeText(context, "Masukkan teks untuk dienkripsi", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRsaDecrypt.setOnClickListener {
            cryptoViewModel.rsaDecrypt()
        }
    }

    private fun setupObservers() {
        cryptoViewModel.caesarResult.observe(viewLifecycleOwner) { result ->
            binding.tvCaesarResult.text = result
        }

        cryptoViewModel.caesarSteps.observe(viewLifecycleOwner) { steps ->
            binding.tvCaesarSteps.text = steps
        }

        cryptoViewModel.rsaKeys.observe(viewLifecycleOwner) { keys ->
            binding.tvRsaKeys.text = keys
        }

        cryptoViewModel.rsaResult.observe(viewLifecycleOwner) { result ->
            binding.tvRsaResult.text = result
        }

        cryptoViewModel.rsaSteps.observe(viewLifecycleOwner) { steps ->
            binding.tvRsaSteps.text = steps
        }

        cryptoViewModel.showRsaControls.observe(viewLifecycleOwner) { show ->
            binding.layoutRsaControls.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}