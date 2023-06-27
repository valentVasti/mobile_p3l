package com.example.mobile_p3l

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import server.api.BookingKelasApi
import server.model.BookingKelas
import java.nio.charset.StandardCharsets

class PresensiKelasActivity : AppCompatActivity(), PresensiKelasAdapter.addPresensiListener {
    private var srPresensiKelas: SwipeRefreshLayout? = null
    private var adapter: PresensiKelasAdapter? = null
    private var layoutLoading: LinearLayout? = null
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presensi_kelas)

        queue = Volley.newRequestQueue(this)
        layoutLoading = findViewById(R.id.layout_loading)
        srPresensiKelas = findViewById(R.id.sr_presensi_kelas)

        val bundle = intent.extras
        val id_jadwal_harian = bundle?.getString("id_jadwal_harian")

        srPresensiKelas?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { allBookingKelas(id_jadwal_harian!!) })

//        val rvProduk = findViewById<RecyclerView>(R.id.rv_presensi_member_kelas)
//        adapter = PresensiKelasAdapter(ArrayList(), this, this)
//        rvProduk.layoutManager = LinearLayoutManager(this)
//        rvProduk.adapter = adapter
        allBookingKelas(id_jadwal_harian!!)
    }

    private fun allBookingKelas(idJadwalHarian: String){
        srPresensiKelas!!.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, BookingKelasApi.GET_BY_ID_JADWAL_HARIAN + idJadwalHarian, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val memberPresensi = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(), Array<BookingKelas>::class.java
                )

                val rvProduk = findViewById<RecyclerView>(R.id.rv_presensi_member_kelas)
                adapter = PresensiKelasAdapter(memberPresensi.toList(), this, this, idJadwalHarian)
                rvProduk.layoutManager = LinearLayoutManager(this)
                rvProduk.adapter = adapter

                srPresensiKelas!!.isRefreshing = false

                if(!memberPresensi.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Data Member Berhasil Diambil",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }else {
                    Toast.makeText(this, "Tidak Ada Member!", Toast.LENGTH_SHORT)
                        .show()
                }
            }, Response.ErrorListener { error ->
                srPresensiKelas!!.isRefreshing = false
                try{
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }catch (e: Exception){
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }
        }
        queue!!.add(stringRequest)
    }

    override fun onButtonClick() {
        val bundle = intent.extras
        val id_jadwal_harian = bundle?.getString("id_jadwal_harian")
        allBookingKelas(id_jadwal_harian!!)
    }
}