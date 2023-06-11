package com.example.mobile_p3l

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import server.model.BookingKelas
import server.model.IzinInstruktur
import java.text.SimpleDateFormat
import java.util.*

class BookingKelasAdapter(private var bookingKelasList: List<BookingKelas>, context: Context?) :
    RecyclerView.Adapter<IzinInstrukturAdapter.ViewHolder>(), Filterable{

    private var filteredIzinInstrukturList: MutableList<IzinInstruktur>
    private val context: Context

    init{
        filteredIzinInstrukturList = ArrayList(izinInstrukturList)
        this.context = context!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_izin_instruktur, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int{
        return filteredIzinInstrukturList.size
    }

    fun setIzinInstrukturList(izinInstrukturList: Array<IzinInstruktur>){
        this.izinInstrukturList = izinInstrukturList.toList()
        filteredIzinInstrukturList = izinInstrukturList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val izin_instruktur = filteredIzinInstrukturList[position]
        holder.tvIdIzin.text = "ID Izin: " + izin_instruktur.id_izin
        holder.tvIdInstruktur.text = izin_instruktur.id_instruktur
        holder.tvIdJadwalHarian.text = "ID Jadwal Harian: " + izin_instruktur.id_jadwal_harian
        holder.tvIdInstrukturPengganti.text = "ID Instruktur Pengganti: " + izin_instruktur.id_instruktur_pengganti
        holder.tvTanggal.text = "Tanggal Izin Kelas: " + izin_instruktur.tgl_izin
//        holder.tvStatus.text = izin_instruktur.status_konfirmasi

        if(izin_instruktur.status_konfirmasi == "0"){
            holder.tvStatus.text = "BELUM DIKONFIRMASI"
        }else{
            holder.tvStatus.text = "SUDAH DIKONFIRMASI"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<IzinInstruktur> = java.util.ArrayList()
                if (charSequenceString.isEmpty()){
                    filtered.addAll(izinInstrukturList)
                }else{
                    for(izin_instruktur in izinInstrukturList){
                        if(izin_instruktur.id_izin.lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))
                        ) filtered.add(izin_instruktur)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredIzinInstrukturList.clear()
                filteredIzinInstrukturList.addAll((filterResults.values as List<IzinInstruktur>))
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var tvIdIzin: TextView
        var tvIdInstruktur: TextView
        var tvIdJadwalHarian: TextView
        var tvIdInstrukturPengganti: TextView
        var tvTanggal: TextView
        var tvStatus: TextView
        var cvIzinInstruktur: CardView


        init {
            tvIdIzin = itemView.findViewById(R.id.tv_id_izin_instruktur)
            tvIdInstruktur = itemView.findViewById(R.id.tv_id_instruktur)
            tvIdJadwalHarian = itemView.findViewById(R.id.tv_id_jadwal_harian)
            tvIdInstrukturPengganti = itemView.findViewById(R.id.tv_id_instruktur_pengganti)
            tvTanggal = itemView.findViewById(R.id.tv_tanggal)
            tvStatus = itemView.findViewById(R.id.tv_status)
            cvIzinInstruktur = itemView.findViewById(R.id.cv_izin_instruktur)
        }
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return format.format(date)
    }

}