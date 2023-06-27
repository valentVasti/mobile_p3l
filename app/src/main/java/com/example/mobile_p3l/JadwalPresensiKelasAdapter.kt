package com.example.mobile_p3l

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import server.api.BookingKelasApi
import server.api.PresensiKelasApi
import server.model.IzinInstruktur
import server.model.JadwalHarian
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class JadwalPresensiKelasAdapter(private var jadwalPresensiKelasList: List<JadwalHarian>, context: Context?) :
    RecyclerView.Adapter<JadwalPresensiKelasAdapter.ViewHolder>(), Filterable{

    private var filteredJadwalPresensiKelasList: MutableList<JadwalHarian>
    private val context: Context

    init{
        filteredJadwalPresensiKelasList = ArrayList(jadwalPresensiKelasList)
        this.context = context!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_kelas_instruktur, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int{
        return filteredJadwalPresensiKelasList.size
    }

    fun setJadwalPresensiKelasList(jadwalPresensiKelasList: Array<JadwalHarian>){
        this.jadwalPresensiKelasList = jadwalPresensiKelasList.toList()
        filteredJadwalPresensiKelasList = jadwalPresensiKelasList.toMutableList()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jadwal_instruktur = filteredJadwalPresensiKelasList[position]
        holder.tvNamaKelas.text = jadwal_instruktur.kelas.nama_kelas

        val dateString = jadwal_instruktur.tgl_kelas
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString)

        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("id", "ID"))
        val hariPengajuanBookingKelas = dayOfWeekFormat.format(date).toString()

        holder.tvHariTanggalKelasInstruktur.text = "$hariPengajuanBookingKelas, $dateString"
        holder.tvJamKelasInstruktur.text = "${jadwal_instruktur.jam_mulai} - ${jadwal_instruktur.jam_selesai}"
        holder.tvKeterangan.text = jadwal_instruktur.keterangan

        holder.btnPresensiKelas.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi Presensi Kelas")
                .setMessage("Deposit member akan terpotong. Anda yakin akan presensi sekarang?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya"){ _, _ ->
                    updateDepositMember(jadwal_instruktur.id_jadwal_harian)

                    //pindah ke presensi kelas activity
                    val bundle = Bundle()
                    bundle.putString("id_jadwal_harian", jadwal_instruktur.id_jadwal_harian)

                    val move = Intent(context, PresensiKelasActivity::class.java)
                    move.putExtras(bundle)
                    context.startActivity(move)
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
                    filtered.addAll(jadwalPresensiKelasList)
                }else{
                    for(kelas_instruktur in jadwalPresensiKelasList){
                        if(kelas_instruktur.kelas.nama_kelas.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))
                        ) filtered.add(kelas_instruktur)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredJadwalPresensiKelasList.clear()
                filteredJadwalPresensiKelasList.addAll((filterResults.values as List<JadwalHarian>))
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var tvNamaKelas: TextView
        var tvHariTanggalKelasInstruktur: TextView
        var tvJamKelasInstruktur: TextView
        var tvKeterangan: TextView
        var cvKelasInstruktur: CardView
        var btnPresensiKelas: Button


        init {
            tvNamaKelas = itemView.findViewById(R.id.tv_nama_kelas_instruktur)
            tvHariTanggalKelasInstruktur = itemView.findViewById(R.id.tv_hari_tanggal_kelas_instruktur)
            tvJamKelasInstruktur = itemView.findViewById(R.id.tv_jam_kelas_instruktur)
            tvKeterangan = itemView.findViewById(R.id.tv_keterangan)
            btnPresensiKelas = itemView.findViewById(R.id.button_presensi_kelas)
            cvKelasInstruktur = itemView.findViewById(R.id.cv_kelas_instruktur)
        }
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return format.format(date)
    }

    private fun updateDepositMember(id_jadwal_harian: String){
        var queue: RequestQueue? = Volley.newRequestQueue(context)

        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, PresensiKelasApi.UPDATE_DEPOSIT_MEMBER + id_jadwal_harian, Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                val message = jsonResponse.getString("message")
                val jumlahMember = jsonResponse.getInt("jumlah_member")

                if( jumlahMember > 0) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT)
                        .show()
//                    listener.onButtonClick()
                }else{
                    Toast.makeText(context, "Tidak ada member yang depositnya terpotong!", Toast.LENGTH_SHORT)
                        .show()
                }
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
}