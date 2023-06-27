package com.example.mobile_p3l.menuMember.historyMember

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
import com.example.mobile_p3l.HistoryPresensiMemberAdapter
import com.example.mobile_p3l.HistoryTransaksiMemberAdapter
import com.example.mobile_p3l.databinding.FragmentHistoryMemberBinding
import com.google.gson.Gson
import org.json.JSONObject
import server.api.HistoryApi
import server.model.HistoryTransaksiMember
import java.nio.charset.StandardCharsets

class HistoryMemberFragment : Fragment() {

    private var _binding: FragmentHistoryMemberBinding? = null
    private var srHistoryMember: SwipeRefreshLayout? = null
    private var adapterHistoryTransaksi: HistoryTransaksiMemberAdapter? = null
    private var adapterHistoryKelas: HistoryPresensiMemberAdapter? = null
    private var svHistoryMember: SearchView? = null
    private var queue: RequestQueue? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryMemberBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id_user = arguments?.getString("id_user")

        queue = Volley.newRequestQueue(context)
        srHistoryMember = binding.srHistoryMember
        svHistoryMember = binding.svHistoryMember

//        val bundle = intent.extras
//        val id_instruktur = bundle?.getString("id_instruktur")

        srHistoryMember?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            allHistoryTransaksiMember(id_user!!)
        })

        svHistoryMember?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(s: String): Boolean{
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                adapterHistoryTransaksi!!.filter.filter(s)
                return false
            }
        })

//        val rvHistoryTransaksi = binding.rvHistoryTransaksiMember
//        rvHistoryTransaksi.layoutManager = LinearLayoutManager(context)
//        rvHistoryTransaksi.adapter = adapterHistoryTransaksi
//        adapterHistoryTransaksi = HistoryTransaksiMemberAdapter(ArrayList(), context)
//        allHistoryTransaksiMember(id_user!!)

        val rvHistoryTransaksi = binding.rvHistoryTransaksiMember
        rvHistoryTransaksi.layoutManager = LinearLayoutManager(context)
        rvHistoryTransaksi.adapter = adapterHistoryTransaksi
        adapterHistoryTransaksi = HistoryTransaksiMemberAdapter(ArrayList(), context)
        allHistoryTransaksiMember(id_user!!)
    }

    private fun allHistoryTransaksiMember(id_member: String){
        srHistoryMember!!.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, HistoryApi.GET_HISTORY_TRANSAKSI + id_member, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val historyTransaksiMember = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(), Array<HistoryTransaksiMember>::class.java
                )

                adapterHistoryTransaksi!!.setHistoryMemberList(historyTransaksiMember)
                adapterHistoryTransaksi!!.filter.filter(svHistoryMember!!.query)
                srHistoryMember!!.isRefreshing = false

                if(!historyTransaksiMember.isEmpty())
                    Toast.makeText(context, "Data Berhasil Diambil", Toast.LENGTH_SHORT)
                        .show()
                else
                    Toast.makeText(context, "Data Kosong!", Toast.LENGTH_SHORT)
                        .show()
            }, Response.ErrorListener { error ->
                srHistoryMember!!.isRefreshing = false
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

//    private fun allHistoryKelasMember(id_member: String){
//        srHistoryMember!!.isRefreshing = true
//        val stringRequest: StringRequest = object :
//            StringRequest(Method.GET, HistoryApi.GET_HISTORY_KELAS + id_member, Response.Listener { response ->
//                val gson = Gson()
//                val jsonObject = JSONObject(response)
//                val historyKelasMember = gson.fromJson(
//                    jsonObject.getJSONArray("data").toString(), Array<HistoryKelasMember>::class.java
//                )
//
//                adapterHistoryKelas!!.setHistoryMemberList(historyKelasMember)
//                adapterHistoryKelas!!.filter.filter(svHistoryMember!!.query)
//                srHistoryMember!!.isRefreshing = false
//
//                if(!historyKelasMember.isEmpty())
//                    Toast.makeText(context, "Data Berhasil Diambil", Toast.LENGTH_SHORT)
//                        .show()
//                else
//                    Toast.makeText(context, "Data Kosong!", Toast.LENGTH_SHORT)
//                        .show()
//            }, Response.ErrorListener { error ->
//                srHistoryMember!!.isRefreshing = false
//                try{
//                    val responseBody =
//                        String(error.networkResponse.data, StandardCharsets.UTF_8)
//                    val errors = JSONObject(responseBody)
//                    Toast.makeText(
//                        context,
//                        errors.getString("message"),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }catch (e: Exception){
//                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//                }
//            }) {
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                val headers = HashMap<String, String>()
//                headers["Accept"] = "application/json"
//                return headers
//            }
//        }
//        queue!!.add(stringRequest)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}