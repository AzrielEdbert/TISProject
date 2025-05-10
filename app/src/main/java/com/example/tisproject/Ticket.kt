package com.example.tisproject

import org.json.JSONObject

data class Ticket(
    val id_tiket: String,
    val nama_bus: String,
    val harga: Int,
    val fasilitas: String,
    val waktu_berangkat: String,
    val rute_keberangkatan: String,
    val rute_tujuan: String
) {
    companion object {
        fun fromJson(json: String): Ticket {
            val obj = JSONObject(json)
            return Ticket(
                id_tiket = obj.getString("id_tiket"),
                nama_bus = obj.getString("nama_bus"),
                harga = obj.getInt("harga"),
                fasilitas = obj.getString("fasilitas"),
                waktu_berangkat = obj.getString("waktu_berangkat"),
                rute_keberangkatan = obj.getString("rute_keberangkatan"),
                rute_tujuan = obj.getString("rute_tujuan")
            )
        }
    }
}
