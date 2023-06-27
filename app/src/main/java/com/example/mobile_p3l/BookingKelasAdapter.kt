package com.example.mobile_p3l

import android.content.Context
import android.graphics.Color
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
import server.api.PresensiInstrukturApi
import server.model.BookingKelas
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class BookingKelasAdapter(private var bookingKelasList: List<BookingKelas>, context: Context?, id_member: String, private val listener: CancelBookingKelasListener) :
    RecyclerView.Adapter<BookingKelasAdapter.ViewHolder>(), Filterable{

    private var filteredBookingKelasList: MutableList<BookingKelas>
    private val context: Context
    private val id_member = id_member

    init{
        filteredBookingKelasList = ArrayList(bookingKelasList)
        this.context = context!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_booking_kelas, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int{
        return filteredBookingKelasList.size
    }

    fun setBookingKelasList(bookingKelasList: Array<BookingKelas>){
        this.bookingKelasList = bookingKelasList.toList()
        filteredBookingKelasList = bookingKelasList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking_kelas = filteredBookingKelasList[position]

        holder.tvNamaKelas.text = booking_kelas.jadwal_harian.kelas.nama_kelas
        holder.tvNamaInstruktrur.text = "Instruktur: " + booking_kelas.jadwal_harian.instruktur.nama
        holder.tvHariTglBookingKelas.text = booking_kelas.jadwal_harian.hari_kelas_harian + ", " + booking_kelas.tgl_booking_kelas
        holder.tvJamKelasBooking.text = booking_kelas.jadwal_harian.jam_mulai + " - " + booking_kelas.jadwal_harian.jam_selesai
        holder.tvStatusBookingKelas.text = booking_kelas.status

        val dateString = booking_kelas.created_at.substring(0, 10)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString)

        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("id", "ID"))
        val hariPengajuanBookingKelas = dayOfWeekFormat.format(date).toString()

        holder.tvHariTglPengajuanBookingKelas.text = "Tanggal Pengajuan: $hariPengajuanBookingKelas, $dateString"

        if(booking_kelas.status.equals("BATAL")){
            holder.btnCancelBookingKelas.isEnabled = false
            holder.tvStatusBookingKelas.setTextColor((Color.parseColor("#d00000")))
        }

        holder.btnCancelBookingKelas.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
            materialAlertDialogBuilder.setTitle("Konfirmasi Batal Kelas")
                .setMessage("Apakah anda yakin ingin batalkan booking ini?")
                .setNegativeButton("Tidak", null)
                .setPositiveButton("Ya"){ _, _ ->
                    cancelBooking(booking_kelas.jadwal_harian.id_jadwal_harian, id_member, booking_kelas.tgl_booking_kelas)
                }
                .show()
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<BookingKelas> = java.util.ArrayList()
                if (charSequenceString.isEmpty()){
                    filtered.addAll(bookingKelasList)
                }else{
                    for(booking_kelas in bookingKelasList){
                        if(booking_kelas.jadwal_harian.kelas.nama_kelas.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault())) ||
                            booking_kelas.jadwal_harian.hari_kelas_harian.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))
                        ) filtered.add(booking_kelas)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredBookingKelasList.clear()
                filteredBookingKelasList.addAll((filterResults.values as List<BookingKelas>))
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var tvNamaKelas: TextView
        var tvNamaInstruktrur: TextView
        var tvHariTglBookingKelas: TextView //tanggal kelasnya
        var tvJamKelasBooking: TextView
        var tvStatusBookingKelas: TextView
        var tvHariTglPengajuanBookingKelas : TextView //tanggal pengajuan kelas
        var btnCancelBookingKelas: Button

        var cvBookingKelas: CardView

        init {
            tvNamaKelas = itemView.findViewById(R.id.tv_nama_kelas)
            tvNamaInstruktrur = itemView.findViewById(R.id.tv_nama_instruktur)
            tvHariTglBookingKelas = itemView.findViewById(R.id.tv_hari_tgl_kelas_harian_booking)
            tvJamKelasBooking = itemView.findViewById(R.id.tv_jam_kelas_booking)
            tvStatusBookingKelas = itemView.findViewById(R.id.tv_status_booking)
            tvHariTglPengajuanBookingKelas = itemView.findViewById(R.id.tv_pengajuan_booking)
            btnCancelBookingKelas = itemView.findViewById(R.id.button_cancel_booking)
            cvBookingKelas = itemView.findViewById(R.id.cv_booking_kelas)
        }
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return format.format(date)
    }

    private fun cancelBooking(id_jadwal_harian: String, id_member: String, tgl_booking_kelas: String){
        var queue: RequestQueue? = Volley.newRequestQueue(context)

        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, BookingKelasApi.CANCEL_BOOKING + id_jadwal_harian + "/" + id_member + "/" + tgl_booking_kelas, Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                val message = jsonResponse.getString("message")

                if(!message.isEmpty()) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT)
                        .show()
                    listener.onButtonClick()
                }else{
                    Toast.makeText(context, "Batal booking kelas gagal!", Toast.LENGTH_SHORT)
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

    interface CancelBookingKelasListener {
        fun onButtonClick()
    }
}