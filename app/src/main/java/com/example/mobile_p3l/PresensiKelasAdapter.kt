package com.example.mobile_p3l

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import server.api.PresensiKelasApi
import server.model.BookingKelas
import server.model.JadwalHarian
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class PresensiKelasAdapter(private var presensiKelasList: List<BookingKelas>, context: Context?, private val listener: addPresensiListener, idJadwalHarian: String):
    RecyclerView.Adapter<PresensiKelasAdapter.ViewHolder>(){

    private var filteredPresensiKelasList: MutableList<BookingKelas>
    private val context: Context
    private val idJadwalHarian = idJadwalHarian

    init{
        filteredPresensiKelasList = ArrayList(presensiKelasList)
        this.context = context!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_presensi_kelas, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int{
        return presensiKelasList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val presensi_kelas = presensiKelasList[position]

        holder.tvNamaMember.text = presensi_kelas.member.nama
        holder.tvIdMember.text = presensi_kelas.member.id_member

        holder.btn_hadir.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Presensi Hadir")
                .setMessage("Apakah anda yakin ingin presensi member hadir?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya"){ _, _ ->
                    addPresensiMember(idJadwalHarian, presensi_kelas.member.id_member, "HADIR")
                    listener.onButtonClick()
                }
                .show()
        }

        holder.btn_tidak_hadir.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Presensi Tidak Hadir")
                .setMessage("Apakah anda yakin ingin presensi member tidak hadir?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya") { _, _ ->
                    addPresensiMember(idJadwalHarian, presensi_kelas.member.id_member, "TIDAK HADIR")
                    listener.onButtonClick()
                }
                .show()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var tvNamaMember: TextView
        var tvIdMember: TextView

        var btn_hadir: Button
        var btn_tidak_hadir: Button

        init {
            tvNamaMember = itemView.findViewById(R.id.tv_nama_member_presensi)
            tvIdMember = itemView.findViewById(R.id.tv_id_member_presensi)

            btn_hadir = itemView.findViewById(R.id.button_hadir)
            btn_tidak_hadir = itemView.findViewById(R.id.button_tidak_hadir)
        }
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return format.format(date)
    }

    private fun addPresensiMember(idJadwalHarian: String, idMember: String, statusKehadiran: String){
        var queue: RequestQueue? = Volley.newRequestQueue(context)

        val jsonobj = JSONObject()

        jsonobj.put("id_jadwal_harian", idJadwalHarian)
        jsonobj.put("id_member", idMember)
        jsonobj.put("status_kehadiran", statusKehadiran)

        val request = JsonObjectRequest(
            Request.Method.POST, PresensiKelasApi.ADD_DATA,jsonobj,
            { response ->
                Log.d("Responsenya: ", response["message"].toString())

                Toast.makeText(
                    context,
                    "Presensi berhasil",
                    Toast.LENGTH_SHORT
                ).show()

            }, { error->
                try{
                    val errorData = String(error.networkResponse.data, StandardCharsets.UTF_8)

                    val errorJson = JSONObject(errorData)
                        Toast.makeText(
                            context,
                            errorJson["message"].toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                }catch (e: Exception){
                    Log.d("Presensi Gagal: ", e.message.toString())
                    Toast.makeText(context,"exception: " + e.message, Toast.LENGTH_SHORT).show()
                }
            })

        queue!!.add(request)
    }

    interface addPresensiListener {
        fun onButtonClick()
    }
}
