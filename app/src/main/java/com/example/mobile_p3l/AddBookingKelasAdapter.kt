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
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import server.api.BookingKelasApi
import server.model.JadwalHarian
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class AddBookingKelasAdapter(private var jadwalHarianList: List<JadwalHarian>, context: Context?, id_member: String) :
    RecyclerView.Adapter<AddBookingKelasAdapter.ViewHolder>(), Filterable{

    private val id_member = id_member
    private var filteredJadwalHarianList: MutableList<JadwalHarian>
    private val context: Context

    init{
        filteredJadwalHarianList = ArrayList(jadwalHarianList)
        this.context = context!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_add_booking_kelas, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int{
        return filteredJadwalHarianList.size
    }

    fun setBookingKelasList(jadwalHarianList: Array<JadwalHarian>){
        this.jadwalHarianList = jadwalHarianList.toList()
        filteredJadwalHarianList = jadwalHarianList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jadwal_harian = filteredJadwalHarianList[position]

        holder.tvNamaKelas.text = jadwal_harian.kelas.nama_kelas
        holder.tvNamaInstruktrur.text = jadwal_harian.instruktur.nama
        holder.tvHariBookingKelas.text = jadwal_harian.hari_kelas_harian
        holder.tvTanggalKelasBooking.text = jadwal_harian.tgl_kelas
        holder.tvJamKelasBooking.text = jadwal_harian.jam_mulai + " - " + jadwal_harian.jam_selesai
        holder.tvStatusBookingKelas.text = jadwal_harian.keterangan

        holder.btnAddBookingKelas.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin booking kelas ini?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya"){ _, _ ->
                    addBookingKelas(jadwal_harian.id_jadwal_harian, id_member)
                }
                .show()
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<JadwalHarian> = java.util.ArrayList()
                if (charSequenceString.isEmpty()){
                    filtered.addAll(jadwalHarianList)
                }else{
                    for(jadwal_harian in jadwalHarianList){
                        if(jadwal_harian.kelas.nama_kelas.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))
                        ) filtered.add(jadwal_harian)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredJadwalHarianList.clear()
                filteredJadwalHarianList.addAll((filterResults.values as List<JadwalHarian>))
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var tvNamaKelas: TextView
        var tvNamaInstruktrur: TextView
        var tvHariBookingKelas: TextView
        var tvTanggalKelasBooking: TextView
        var tvJamKelasBooking: TextView
        var tvStatusBookingKelas: TextView

        var btnAddBookingKelas: Button
        var cvAddBookingKelas: CardView

        init {
            tvNamaKelas = itemView.findViewById(R.id.tv_nama_kelas_add_booking)
            tvNamaInstruktrur = itemView.findViewById(R.id.tv_nama_instruktur_add_booking)
            tvHariBookingKelas = itemView.findViewById(R.id.tv_hari_kelas_harian_add_booking)
            tvTanggalKelasBooking = itemView.findViewById(R.id.tv_tanggal_kelas_add_booking)
            tvJamKelasBooking = itemView.findViewById(R.id.tv_jam_kelas_add_booking)
            tvStatusBookingKelas = itemView.findViewById(R.id.tv_status_add_booking)
            btnAddBookingKelas = itemView.findViewById(R.id.button_add_booking)
            cvAddBookingKelas = itemView.findViewById(R.id.cv_add_booking_kelas)
        }
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return format.format(date)
    }

    private fun addBookingKelas(id_jadwal_harian: String, id_member: String){
        var queue: RequestQueue? = Volley.newRequestQueue(context)

        val jsonobj = JSONObject()

        jsonobj.put("id_jadwal_harian", id_jadwal_harian)
        jsonobj.put("id_member", id_member)

        val request = JsonObjectRequest(
            Request.Method.POST, BookingKelasApi.ADD_DATA,jsonobj,
            { response ->
                Log.d("Responsenya: ", response["message"].toString())

                Toast.makeText(
                    context,
                    "Berhasil mengajukan izin",
                    Toast.LENGTH_SHORT
                ).show()

                var bundle = Bundle()
                val move = Intent(context, HomeMemberActivity::class.java)
                bundle.putString("id_user", id_member)
                move.putExtras(bundle)
                context.startActivity(move)

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
                    Log.d("Error Login", e.message.toString())
                    Toast.makeText(context,"exception: " + e.message, Toast.LENGTH_SHORT).show()
                }
            })
        queue!!.add(request)
    }

}