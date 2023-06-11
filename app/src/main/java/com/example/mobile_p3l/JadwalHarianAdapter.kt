package com.example.mobile_p3l

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.json.JSONObject
import server.api.IzinInstrukturApi
import server.api.PresensiInstrukturApi
import server.model.IzinInstruktur
import server.model.JadwalHarian
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class JadwalHarianAdapter(private var jadwalHarianList: List<JadwalHarian>, context: Context?, private val listener: UpdateJamSelesaiListener):
    RecyclerView.Adapter<JadwalHarianAdapter.ViewHolder>(){

    private var filteredJadwalHarianList: MutableList<JadwalHarian>
    private val context: Context

    init{
        filteredJadwalHarianList = ArrayList(jadwalHarianList)
        this.context = context!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_jadwal_harian, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int{
        return jadwalHarianList.size
    }

//        fun setJadwalHarianList(jadwalHarianList: Array<JadwalHarian>){
//            this.jadwalHarianList = jadwalHarianList.toList()
////            val filtered: MutableList<JadwalHarian> = java.util.ArrayList()
////            filtered.addAll(this.jadwalHarianList)
//            filteredJadwalHarianList = jadwalHarianList.toMutableList()
//        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jadwal_harian = jadwalHarianList[position]
        setUpdateJamMulaiButton(jadwal_harian.id_jadwal_harian, holder.btn_update_jam_mulai)

        holder.tvNamaInstruktur.text = jadwal_harian.instruktur.nama
        holder.tvNamaKelas.text = jadwal_harian.kelas.nama_kelas
        holder.tvHariKelas.text = jadwal_harian.hari_kelas_harian
        holder.tvTanggalKelas.text = jadwal_harian.tgl_kelas
        holder.tvJamKelas.text = jadwal_harian.jam_mulai + "-" + jadwal_harian.jam_selesai
        holder.tvKeterangan.text = jadwal_harian.keterangan

        holder.btn_update_jam_mulai.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin mengupdate jam mulai kelas ini?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya"){ _, _ ->
                    updateJamMulai(jadwal_harian.id_jadwal_harian)
                }
                .show()
        }

        holder.btn_update_jam_selesai.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin mengupdate jam selesai kelas ini?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya") { _, _ ->
                    updateJamSelesai(jadwal_harian.id_jadwal_harian)
                    var bundle = Bundle()
                    var move: Intent

                    listener.onButtonClick()
                }
                .show()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var tvNamaInstruktur: TextView
        var tvNamaKelas: TextView
        var tvHariKelas: TextView
        var tvTanggalKelas: TextView
        var tvJamKelas: TextView
        var tvKeterangan: TextView

        var btn_update_jam_mulai: Button
        var btn_update_jam_selesai: Button

        init {
            tvNamaInstruktur = itemView.findViewById(R.id.tv_nama_instruktur)
            tvNamaKelas = itemView.findViewById(R.id.tv_nama_kelas_harian)
            tvHariKelas = itemView.findViewById(R.id.tv_hari_kelas_harian)
            tvTanggalKelas = itemView.findViewById(R.id.tv_tgl_kelas)
            tvJamKelas = itemView.findViewById(R.id.tv_jam_kelas)
            tvKeterangan = itemView.findViewById(R.id.tv_keterangan)

            btn_update_jam_mulai = itemView.findViewById(R.id.button_update_jam_mulai)
            btn_update_jam_selesai = itemView.findViewById(R.id.button_update_jam_selesai)
        }
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return format.format(date)
    }

    private fun updateJamMulai(idJadwalHarian: String){
        var queue: RequestQueue? = Volley.newRequestQueue(context)

        val jsonobj = JSONObject()

        jsonobj.put("id_jadwal_harian", idJadwalHarian)
        val request = JsonObjectRequest(
            Request.Method.POST, PresensiInstrukturApi.UPDATE_JAM_MULAI,jsonobj,
            { response ->
                Log.d("Responsenya: ", response["message"].toString())

                Toast.makeText(
                    context,
                    "Berhasil update jam mulai",
                    Toast.LENGTH_SHORT
                ).show()

            }, { error->
                try{
                    val errorData = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errorJson = JSONObject(errorData)
                    if(error.networkResponse.statusCode != 400){
                        Toast.makeText(
                            context,
                            errorJson["message"].toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }catch (e: Exception){
                    Log.d("Error Update Jam Mulai: ", e.message.toString())
                    Toast.makeText(context,"exception: " + e.message, Toast.LENGTH_SHORT).show()
                }
            })

        queue!!.add(request)
    }

    private fun updateJamSelesai(idJadwalHarian: String){
        var queue: RequestQueue? = Volley.newRequestQueue(context)

        val stringRequest: StringRequest = object :
            StringRequest(Method.PUT, PresensiInstrukturApi.UPDATE_JAM_SELESAI + idJadwalHarian, Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                val message = jsonResponse.getString("message")

                if(!message.isEmpty())
                    Toast.makeText(context, message, Toast.LENGTH_SHORT)
                        .show()
                else
                    Toast.makeText(context, "Update jam selesai gagal!", Toast.LENGTH_SHORT)
                        .show()
            }, Response.ErrorListener { error ->
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

    private fun setUpdateJamMulaiButton(idJadwalHarian: String, button: Button){
        var queue: RequestQueue? = Volley.newRequestQueue(context)

        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, PresensiInstrukturApi.CHECK_UPDATE_JAM_MULAI + idJadwalHarian, Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                val status = jsonResponse.getString("status")

                if(status.equals('1')){
                    button.isEnabled = false
                }else{
                    button.isEnabled = true
                }

                Toast.makeText(
                    context,
                    "Statusnya:" + status,
                    Toast.LENGTH_SHORT
                ).show()

            }, Response.ErrorListener { error ->
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

    interface UpdateJamSelesaiListener {
        fun onButtonClick()
    }
}
