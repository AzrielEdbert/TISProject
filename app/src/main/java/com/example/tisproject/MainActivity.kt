package com.example.tisproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var inputTujuan: EditText
    private lateinit var btnCariTiket: Button
    private lateinit var containerHasilTiket: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var templateTiket: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputTujuan = findViewById(R.id.inputTujuan)
        btnCariTiket = findViewById(R.id.btnCariTiket)
        containerHasilTiket = findViewById(R.id.containerHasilTiket)
        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
        }

        btnCariTiket.setOnClickListener {
            val tujuan = inputTujuan.text.toString()
            val asal = findViewById<TextView>(R.id.inputKeberangkatan).text.toString()

            if (tujuan.isEmpty()) {
                Toast.makeText(this, "Silakan isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Bersihkan hasil sebelumnya
            containerHasilTiket.removeAllViews()
            containerHasilTiket.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                val tiket = SoapHelper.cariTiket(asal, tujuan)

                runOnUiThread {
                    progressBar.visibility = View.GONE

                    if (tiket.isNotEmpty()) {
                        containerHasilTiket.visibility = View.VISIBLE

                        tiket.forEach { tiketItem ->
                            tambahCardTiket(
                                tiketItem.nama_bus,
                                "Rp ${tiketItem.harga}",
                                tiketItem.rute_keberangkatan,
                                tiketItem.rute_tujuan,
                                tiketItem.waktu_berangkat,
                                tiketItem.fasilitas
                            )
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Tiket tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun tambahCardTiket(
        namaBus: String,
        harga: String,
        keberangkatan: String,
        tujuan: String,
        waktu: String,
        fasilitas: String
    ) {
        // Buat CardView baru berdasarkan template
        val newCardView = LayoutInflater.from(this).inflate(R.layout.tiket_card_item, containerHasilTiket, false) as CardView

        // Isi data ke view
        newCardView.findViewById<TextView>(R.id.tvNamaBus).text = namaBus
        newCardView.findViewById<TextView>(R.id.tvHarga).text = harga
        newCardView.findViewById<TextView>(R.id.tvKeberangkatan).text = keberangkatan
        newCardView.findViewById<TextView>(R.id.tvTujuan).text = tujuan
        newCardView.findViewById<TextView>(R.id.tvWaktu).text = waktu
        newCardView.findViewById<TextView>(R.id.tvFasilitas).text = "Fasilitas: $fasilitas"

        // Atur onClick untuk tombol pesan
        newCardView.findViewById<Button>(R.id.btnPesan).setOnClickListener {
            Toast.makeText(this, "Memesan tiket $namaBus", Toast.LENGTH_SHORT).show()
            // Tambahkan logika pemesanan di sini
        }

        // Tambahkan CardView ke container
        containerHasilTiket.addView(newCardView)
    }
}