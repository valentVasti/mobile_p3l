package com.example.mobile_p3l

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import server.api.IzinInstrukturApi
import server.model.IzinInstruktur
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class IzinInstrukturActivity : AppCompatActivity() {
    private var srIzinInstruktur: SwipeRefreshLayout? = null
    private var adapter: IzinInstrukturAdapter? = null
    private var svIzinInstruktur: SearchView? = null
    private var layoutLoading: LinearLayout? = null
    private var queue: RequestQueue? = null

    companion object{
        const val LAUNCH_ADD_ACTIVITY = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin_instruktur)

        queue = Volley.newRequestQueue(this)
        layoutLoading = findViewById(R.id.layout_loading)
        srIzinInstruktur = findViewById(R.id.sr_izin_instruktur)
        svIzinInstruktur = findViewById(R.id.sv_izin_instruktur)

        val bundle = intent.extras
        val id_instruktur = bundle?.getString("id_instruktur")

        srIzinInstruktur?.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener { allIzinInstruktur(id_instruktur!!) })
        svIzinInstruktur?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(s: String): Boolean{
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                adapter!!.filter.filter(s)
                return false
            }
        })

        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)
        fabAdd.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("id_instruktur", id_instruktur)
            val moveToAdd = Intent(this@IzinInstrukturActivity, AddIzinInstrukturActivity::class.java)
            moveToAdd.putExtras(bundle)
            startActivity(moveToAdd)
        }
        val rvProduk = findViewById<RecyclerView>(R.id.rv_izin_instruktur)
        adapter = IzinInstrukturAdapter(ArrayList(), this)
        rvProduk.layoutManager = LinearLayoutManager(this)
        rvProduk.adapter = adapter
        allIzinInstruktur(id_instruktur!!)
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
                    Toast.makeText(this@IzinInstrukturActivity, "Data Berhasil Diambil", Toast.LENGTH_SHORT)
                        .show()
                else
                    Toast.makeText(this@IzinInstrukturActivity, "Data Kosong!", Toast.LENGTH_SHORT)
                        .show()
            }, Response.ErrorListener { error ->
                srIzinInstruktur!!.isRefreshing = false
                try{
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@IzinInstrukturActivity,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                }catch (e: Exception){
                    Toast.makeText(this@IzinInstrukturActivity, e.message, Toast.LENGTH_SHORT).show()
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

//    fun deleteIzinInstruktur(id: Long){
//        setLoading(true)
//        val stringRequest: StringRequest = object :
//            StringRequest(Method.DELETE, IzinInstrukturApi.DELETE_URL + id, Response.Listener { response ->
//                setLoading(false)
//
//                val jsonObject = JSONObject(response)
//                val gson = Gson()
////                var izin_instruktur = gson.fromJson(response, IzinInstruktur::class.java)
//                val izininstruktur = gson.fromJson(
//                    jsonObject.getJSONArray("data").toString(), Array<IzinInstruktur>::class.java
//                )
//                if(izininstruktur != null)
//                    Toast.makeText(this@MainActivity, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show()
//                allIzinInstruktur()
//            }, Response.ErrorListener { error ->
//                setLoading(false)
//                try{
//                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
//                    val errors = JSONObject(responseBody)
//                    Toast.makeText(
//                        this@MainActivity,
//                        errors.getString("messsage"),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }catch (e: java.lang.Exception){
//                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
//                }
//            }){
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String>{
//                val headers = java.util.HashMap<String, String>()
//                headers["Accept"] = "application/json"
//                return headers
//            }
//        }
//        queue!!.add(stringRequest)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == LAUNCH_ADD_ACTIVITY && resultCode == RESULT_OK) allIzinInstruktur(id_instruktur)
//    }

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