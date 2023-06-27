package com.example.mobile_p3l

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import server.model.HistoryKelasMember
import server.model.HistoryTransaksiMember
import java.text.SimpleDateFormat
import java.util.*

class HistoryPresensiMemberAdapter(private var historyTransaksiMemberList: List<HistoryKelasMember>, context: Context?) :
    RecyclerView.Adapter<HistoryPresensiMemberAdapter.ViewHolder>(), Filterable{

    private var filteredHistoryTransaksiMemberList: MutableList<HistoryKelasMember>
    private val context: Context

    init{
        filteredHistoryTransaksiMemberList = ArrayList(historyTransaksiMemberList)
        this.context = context!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_history_kelas_member, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int{
        return filteredHistoryTransaksiMemberList.size
    }

    fun setHistoryMemberList(historyTransaksiMemberList: Array<HistoryKelasMember>){
        this.historyTransaksiMemberList = historyTransaksiMemberList.toList()
        filteredHistoryTransaksiMemberList = historyTransaksiMemberList.toMutableList()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val history_member = filteredHistoryTransaksiMemberList[position]
//
//        holder.tvIdPresensiKelas.text = "ID Presensi: " + history_member.id_presensi_kelas
//        holder.tvNamaKelasHistory.text = history_member.nama_kelas
//        holder.tvStatusKehadiran.text = history_member.status_kehadiran
//        holder.tvTglKelasHistory.text = history_member.tgl_kelas
//        holder.tvJamPresensi.text = history_member.jam_presensi

    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<HistoryKelasMember> = java.util.ArrayList()
                if (charSequenceString.isEmpty()){
                    filtered.addAll(historyTransaksiMemberList)
                }else{
                    for(historyMember in historyTransaksiMemberList){
                        if("Presensi".lowercase(Locale.getDefault())
                                .contains(charSequenceString.lowercase(Locale.getDefault()))
                        ) filtered.add(historyMember)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filtered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredHistoryTransaksiMemberList.clear()
                filteredHistoryTransaksiMemberList.addAll((filterResults.values as List<HistoryKelasMember>))
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//
//        var tvIdPresensiKelas: TextView
//        var tvNamaKelasHistory: TextView
//        var tvStatusKehadiran: TextView
//        var tvTglKelasHistory: TextView
//        var tvJamPresensi: TextView
//
//        init {
//            tvIdPresensiKelas = itemView.findViewById(R.id.tv_id_presensi_kelas)
//            tvNamaKelasHistory = itemView.findViewById(R.id.tv_nama_kelas_history)
//            tvStatusKehadiran = itemView.findViewById(R.id.tv_status_kehadiran_history)
//            tvTglKelasHistory = itemView.findViewById(R.id.tv_tgl_kelas_history)
//            tvJamPresensi = itemView.findViewById(R.id.tv_jam_presensi)
//        }
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return format.format(date)
    }
}