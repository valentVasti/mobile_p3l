package com.example.mobile_p3l.menuInstruktur.izinInstruktur

import android.content.Intent
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
import com.example.mobile_p3l.AddIzinInstrukturActivity
import com.example.mobile_p3l.IzinInstrukturAdapter
import com.example.mobile_p3l.databinding.FragmentIzinInstrukturBinding
import com.google.gson.Gson
import org.json.JSONObject
import server.api.IzinInstrukturApi
import server.model.IzinInstruktur
import java.nio.charset.StandardCharsets

class izinInstrukturFragment() : Fragment() {

    private var _binding: FragmentIzinInstrukturBinding? = null
    private var srIzinInstruktur: SwipeRefreshLayout? = null
    private var adapter: IzinInstrukturAdapter? = null
    private var svIzinInstruktur: SearchView? = null
//    private var layoutLoading: LinearLayout? = null
    private var queue: RequestQueue? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIzinInstrukturBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id_user = arguments?.getString("id_user")

        queue = Volley.newRequestQueue(context)
        srIzinInstruktur = binding.srIzinInstruktur
        svIzinInstruktur = binding.svIzinInstruktur

//        val bundle = intent.extras
//        val id_instruktur = bundle?.getString("id_instruktur")

        srIzinInstruktur?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { allIzinInstruktur(id_user!!) })
        svIzinInstruktur?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(s: String): Boolean{
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                adapter!!.filter.filter(s)
                return false
            }
        })

        val fabAdd = binding.fabAdd
        fabAdd.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("id_instruktur", id_user)
            val moveToAdd = Intent(context, AddIzinInstrukturActivity::class.java)
            moveToAdd.putExtras(bundle)
            startActivity(moveToAdd)
        }

        val rvProduk = binding.rvIzinInstruktur
        adapter = IzinInstrukturAdapter(ArrayList(), context)
        rvProduk.layoutManager = LinearLayoutManager(context)
        rvProduk.adapter = adapter
        allIzinInstruktur(id_user!!)

    }

    private fun allIzinInstruktur(id_instruktur: String){
        srIzinInstruktur!!.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, IzinInstrukturApi.GET_BY_ID_INSTRUKTUR + id_instruktur, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val izininstruktur = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(), Array<IzinInstruktur>::class.java
                )

                adapter!!.setIzinInstrukturList(izininstruktur)
                adapter!!.filter.filter(svIzinInstruktur!!.query)
                srIzinInstruktur!!.isRefreshing = false

                if(!izininstruktur.isEmpty())
                    Toast.makeText(context, "Data Berhasil Diambil", Toast.LENGTH_SHORT)
                        .show()
                else
                    Toast.makeText(context, "Data Kosong!", Toast.LENGTH_SHORT)
                        .show()
            }, Response.ErrorListener { error ->
                srIzinInstruktur!!.isRefreshing = false
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