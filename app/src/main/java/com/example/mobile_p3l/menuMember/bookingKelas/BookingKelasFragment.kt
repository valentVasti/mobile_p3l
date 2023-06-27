package com.example.mobile_p3l.menuMember.bookingKelas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mobile_p3l.AddBookingKelasActivity
import com.example.mobile_p3l.AddIzinInstrukturActivity
import com.example.mobile_p3l.BookingKelasAdapter
import com.example.mobile_p3l.IzinInstrukturAdapter
import com.example.mobile_p3l.JadwalHarianAdapter
import com.example.mobile_p3l.databinding.FragmentBookingKelasBinding
import com.example.mobile_p3l.databinding.FragmentIzinInstrukturBinding
import com.example.mobile_p3l.databinding.FragmentPresensiInstrukturBinding
import com.google.gson.Gson
import org.json.JSONObject
import server.api.BookingKelasApi
import server.api.IzinInstrukturApi
import server.api.JadwalHarianApi
import server.model.BookingKelas
import server.model.IzinInstruktur
import server.model.JadwalHarian
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingKelasFragment : Fragment(), BookingKelasAdapter.CancelBookingKelasListener {

    private var _binding: FragmentBookingKelasBinding? = null
    private var srBookingKelas: SwipeRefreshLayout? = null
    private var adapter: BookingKelasAdapter? = null
    private var svBookingKelas: SearchView? = null
    //    private var layoutLoading: LinearLayout? = null
    private var queue: RequestQueue? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingKelasBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHistory
//        textView.setText("History Fragment")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id_user = arguments?.getString("id_user")

        queue = Volley.newRequestQueue(context)
        srBookingKelas = binding.srBookingKelas
        svBookingKelas = binding.svBookingKelas

//        val bundle = intent.extras
//        val id_instruktur = bundle?.getString("id_instruktur")

        srBookingKelas?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { allBookingKelas(id_user!!) })
        svBookingKelas?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(s: String): Boolean{
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                adapter!!.filter.filter(s)
                return false
            }
        })

        val fabAdd = binding.btnAddBookingKelas // pindah ke AddBookingKelasActivity
        fabAdd.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("id_member", id_user)
            val moveToAdd = Intent(context, AddBookingKelasActivity::class.java)
            moveToAdd.putExtras(bundle)
            startActivity(moveToAdd)
        }

        val rvProduk = binding.rvBookingKelas
        adapter = BookingKelasAdapter(ArrayList(), context, id_user!!, this)
        rvProduk.layoutManager = LinearLayoutManager(context)
        rvProduk.adapter = adapter
        allBookingKelas(id_user!!)

    }

    private fun allBookingKelas(id_member: String){
        srBookingKelas!!.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, BookingKelasApi.GET_BY_ID_MEMBER + id_member, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val bookingKelas = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(), Array<BookingKelas>::class.java
                )

                adapter!!.setBookingKelasList(bookingKelas)
                adapter!!.filter.filter(svBookingKelas!!.query)
                srBookingKelas!!.isRefreshing = false

                if(!bookingKelas.isEmpty())
                    Toast.makeText(context, "Data Berhasil Diambil", Toast.LENGTH_SHORT)
                        .show()
                else
                    Toast.makeText(context, "Data Kosong!", Toast.LENGTH_SHORT)
                        .show()
            }, Response.ErrorListener { error ->
                srBookingKelas!!.isRefreshing = false
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
        val id_user = arguments?.getString("id_user")
        allBookingKelas(id_user!!)
    }
}