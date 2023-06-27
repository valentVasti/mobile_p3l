package com.example.mobile_p3l

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.SearchView
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
import server.api.DepositKelasApi
import server.api.JadwalHarianApi
import server.model.DepositKelas
import server.model.JadwalHarian
import java.nio.charset.StandardCharsets

class AddBookingKelasActivity : AppCompatActivity() {

    private var srAddBookingKelas: SwipeRefreshLayout? = null
    private var adapter: AddBookingKelasAdapter? = null
    private var svAddBookingKelas: SearchView? = null
    private var layoutLoading: LinearLayout? = null
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_booking_kelas)

        queue = Volley.newRequestQueue(this)
        layoutLoading = findViewById(R.id.layout_loading)
        srAddBookingKelas = findViewById(R.id.sr_add_booking_kelas)
        svAddBookingKelas = findViewById(R.id.sv_add_booking_kelas)

        val bundle = intent.extras
        val id_member = bundle?.getString("id_member")

        srAddBookingKelas?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { allJadwalHarian() })
        svAddBookingKelas?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(s: String): Boolean{
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                adapter!!.filter.filter(s)
                return false
            }
        })

        val rvProduk = findViewById<RecyclerView>(R.id.rv_add_booking_kelas)
        adapter = AddBookingKelasAdapter(ArrayList(), this, id_member!!)
        rvProduk.layoutManager = LinearLayoutManager(this)
        rvProduk.adapter = adapter
        allJadwalHarian()
    }

    private fun allJadwalHarian(){
        srAddBookingKelas!!.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, JadwalHarianApi.GET_ALL, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val jadwalHarian = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(), Array<JadwalHarian>::class.java
                )

                adapter!!.setBookingKelasList(jadwalHarian)
                adapter!!.filter.filter(svAddBookingKelas!!.query)
                srAddBookingKelas!!.isRefreshing = false

                if(!jadwalHarian.isEmpty())
                    Toast.makeText(this@AddBookingKelasActivity, "Data Berhasil Diambil", Toast.LENGTH_SHORT)
                        .show()
                else
                    Toast.makeText(this@AddBookingKelasActivity, "Data Kosong!", Toast.LENGTH_SHORT)
                        .show()
            }, Response.ErrorListener { error ->
                srAddBookingKelas!!.isRefreshing = false
                try{
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@AddBookingKelasActivity,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }catch (e: Exception){
                    Toast.makeText(this@AddBookingKelasActivity, e.message, Toast.LENGTH_SHORT).show()
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
}