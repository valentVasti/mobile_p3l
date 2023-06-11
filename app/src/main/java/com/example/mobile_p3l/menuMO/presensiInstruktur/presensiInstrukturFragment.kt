package com.example.mobile_p3l.menuMO.presensiInstruktur

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mobile_p3l.JadwalHarianAdapter
import com.example.mobile_p3l.databinding.FragmentPresensiInstrukturBinding
import com.google.gson.Gson
import org.json.JSONObject
import server.api.JadwalHarianApi
import server.model.JadwalHarian
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class presensiInstrukturFragment : Fragment(), JadwalHarianAdapter.UpdateJamSelesaiListener {

    private var _binding: FragmentPresensiInstrukturBinding? = null
    private var srPresensiInstruktur: SwipeRefreshLayout? = null
    private var adapter: JadwalHarianAdapter? = null
    private var queue: RequestQueue? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPresensiInstrukturBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        queue = Volley.newRequestQueue(context)
        srPresensiInstruktur = binding.srJadwalHarian

//        val bundle = intent.extras
//        val id_instruktur = bundle?.getString("id_instruktur")

        srPresensiInstruktur?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { allJadwalHarian() })


        allJadwalHarian()

    }

    private fun allJadwalHarian(){
        val currentDate: Date = Date()
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate: String = dateFormat.format(currentDate)

        srPresensiInstruktur!!.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, JadwalHarianApi.GET_BY_DATE + formattedDate, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val jadwalHarian = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(), Array<JadwalHarian>::class.java
                )

                val rvProduk = binding.rvJadwalHarian
                adapter = JadwalHarianAdapter(jadwalHarian.toList(), context, this)
                rvProduk.layoutManager = LinearLayoutManager(context)
                rvProduk.adapter = adapter

                srPresensiInstruktur!!.isRefreshing = false

                if(!jadwalHarian.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Data Jadwal Harian Berhasil Diambil",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }else {
                    Toast.makeText(context, "Tidak ada kelas hari ini!", Toast.LENGTH_SHORT)
                        .show()
                }
            }, Response.ErrorListener { error ->
                srPresensiInstruktur!!.isRefreshing = false
                try{
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        context,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }catch (e: Exception){
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onButtonClick() {
        allJadwalHarian()
    }
}