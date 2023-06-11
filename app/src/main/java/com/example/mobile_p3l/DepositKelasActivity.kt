package com.example.mobile_p3l

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
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
import server.model.DepositKelas
import java.nio.charset.StandardCharsets

class DepositKelasActivity : AppCompatActivity() {
    private var srDepositKelas: SwipeRefreshLayout? = null
    private var adapter: DepositKelasAdapter? = null
    private var svDepositKelas: SearchView? = null
    private var layoutLoading: LinearLayout? = null
    private var queue: RequestQueue? = null

    companion object{
        const val LAUNCH_ADD_ACTIVITY = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit_kelas)

        queue = Volley.newRequestQueue(this)
        layoutLoading = findViewById(R.id.layout_loading)
        srDepositKelas = findViewById(R.id.sr_deposit_kelas)
        svDepositKelas = findViewById(R.id.sv_deposit_kelas)

        val bundle = intent.extras
        val id_member = bundle?.getString("id_member")

        srDepositKelas?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { allDepositKelas(id_member!!) })
        svDepositKelas?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(s: String): Boolean{
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                adapter!!.filter.filter(s)
                return false
            }
        })

        val rvProduk = findViewById<RecyclerView>(R.id.rv_deposit_kelas)
        adapter = DepositKelasAdapter(ArrayList(), this)
        rvProduk.layoutManager = LinearLayoutManager(this)
        rvProduk.adapter = adapter
        allDepositKelas(id_member!!)
    }

    private fun allDepositKelas(id_member: String){
        srDepositKelas!!.isRefreshing = true
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, DepositKelasApi.GET_BY_ID_MEMBER + id_member, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val depositKelas = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(), Array<DepositKelas>::class.java
                )

                adapter!!.setDepositKelasList(depositKelas)
                adapter!!.filter.filter(svDepositKelas!!.query)
                srDepositKelas!!.isRefreshing = false

                if(!depositKelas.isEmpty())
                    Toast.makeText(this@DepositKelasActivity, "Data Berhasil Diambil", Toast.LENGTH_SHORT)
                        .show()
                else
                    Toast.makeText(this@DepositKelasActivity, "Data Kosong!", Toast.LENGTH_SHORT)
                        .show()
            }, Response.ErrorListener { error ->
                srDepositKelas!!.isRefreshing = false
                try{
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@DepositKelasActivity,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }catch (e: Exception){
                    Toast.makeText(this@DepositKelasActivity, e.message, Toast.LENGTH_SHORT).show()
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

    private fun setLoading(isLoading: Boolean){
        if (isLoading){
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            layoutLoading!!.visibility = View.VISIBLE
        }else{
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            layoutLoading!!.visibility = View.GONE
        }
    }
}