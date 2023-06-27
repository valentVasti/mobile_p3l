package com.example.mobile_p3l.menuInstruktur.jadwalPresensiKelas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mobile_p3l.BookingKelasAdapter
import com.example.mobile_p3l.JadwalHarianAdapter
import com.example.mobile_p3l.JadwalPresensiKelasAdapter
import com.example.mobile_p3l.databinding.FragmentJadwalPresensiKelasBinding
import com.example.mobile_p3l.databinding.FragmentPresensiInstrukturBinding
import com.google.gson.Gson
import org.json.JSONObject
import server.api.JadwalHarianApi
import server.model.JadwalHarian
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JadwalPresensiKelasFragment : Fragment() {

    private var srKelasInstruktur: SwipeRefreshLayout? = null
    private var svKelasInstruktur: SearchView? = null
    private var adapter: JadwalPresensiKelasAdapter? = null
    private var queue: RequestQueue? = null
    private var _binding: FragmentJadwalPresensiKelasBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJadwalPresensiKelasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        queue = Volley.newRequestQueue(context)
        srKelasInstruktur = binding.srKelasInstruktur
        svKelasInstruktur = binding.svJadwalPresensiKelas

        val id_instruktur = arguments?.getString("id_user")

        srKelasInstruktur?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { allJadwalKelasInstruktur(id_instruktur!!) })
        svKelasInstruktur?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(s: String): Boolean{
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                adapter!!.filter.filter(s)
                return false
            }
        })

        val rvProduk = binding.rvKelasInstruktur
        adapter = JadwalPresensiKelasAdapter(ArrayList(), context)
        rvProduk.layoutManager = LinearLayoutManager(context)
        rvProduk.adapter = adapter
        allJadwalKelasInstruktur(id_instruktur!!)

    }

    private fun allJadwalKelasInstruktur(id_instruktur: String){
        val currentDate: Date = Date()
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate: String = dateFormat.format(currentDate)

        srKelasInstruktur!!.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, JadwalHarianApi.GET_KELAS_INSTRUKTUR_TODAY + id_instruktur + "/" + formattedDate, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val jadwalKelasInstruktur = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(), Array<JadwalHarian>::class.java
                )

                adapter!!.setJadwalPresensiKelasList(jadwalKelasInstruktur)
                adapter!!.filter.filter(svKelasInstruktur!!.query)
                srKelasInstruktur!!.isRefreshing = false

                if(!jadwalKelasInstruktur.isEmpty()) {
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
                srKelasInstruktur!!.isRefreshing = false
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
}