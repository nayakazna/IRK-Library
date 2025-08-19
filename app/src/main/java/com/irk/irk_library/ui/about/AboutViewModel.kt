package com.irk.irk_library.ui.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = """
            Tentang Saya
            
            👨‍💻 Z. Nayaka Athadiansyah
            📧 nayaka.zna@gmail.com
            
            🎯 Motivasi Pembuatan:
            Aplikasi ini dibuat sebagai implementasi praktis dari materi-materi yang diajarkan di Lab IRK supaya saya tidak skill issue.
            
            🚀 Harapan sebagai Asisten IRK:
            Saya berharap ke depannya saya bisa mengembangkan aplikasi ini dengan lebih baik dan tidak skill issue seperti sekarang.
            
            💫 Visi & Misi:
            Menjadi asisten yang mampu menumbuhkan kecintaan terhadap bidang compsci dan tidak mempermalukan lab IRK.

        """.trimIndent()
    }
    val text: LiveData<String> = _text
}