package com.example.tisproject

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var inputTujuan: EditText
    private lateinit var btnCariTiket: Button
    private lateinit var hasilTiket: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputTujuan = findViewById(R.id.inputTujuan)
        btnCariTiket = findViewById(R.id.btnCariTiket)
        hasilTiket = findViewById(R.id.hasilTiket)

        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
        }

        hasilTiket.visibility = View.GONE
        progressBar.visibility = View.GONE

        btnCariTiket.setOnClickListener {
            val tujuan = inputTujuan.text.toString()
            val asal = findViewById<TextView>(R.id.inputKeberangkatan).text.toString()

            if (tujuan.isEmpty()) {
                Toast.makeText(this, "Silakan isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            hasilTiket.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                val tiket = SoapHelper.cariTiket(asal, tujuan)

                runOnUiThread {
                    progressBar.visibility = View.GONE
                    hasilTiket.visibility = View.VISIBLE

                    if (tiket.isNotEmpty()) {
                        val hasil = tiket.joinToString("\n\n") {
                            """
                            Bus: ${it.nama_bus}
                            Dari: ${it.rute_keberangkatan} ke ${it.rute_tujuan}
                            Waktu: ${it.waktu_berangkat}
                            Harga: Rp ${it.harga}tt
                            Fasilitas: ${it.fasilitas}
                            """.trimIndent()
                        }
                        hasilTiket.text = "Tiket ditemukan:\n\n$hasil"
                    } else {
                        hasilTiket.text = "Tiket tidak ditemukan"
                    }
                }

            }
        }
    }
}
