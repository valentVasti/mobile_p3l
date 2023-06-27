package com.example.mobile_p3l

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import server.model.HistoryTransaksiMember
import java.text.SimpleDateFormat
import java.util.*

class HistoryTransaksiMemberAdapter(private var historyTransaksiMemberList: List<HistoryTransaksiMember>, context: Context?) :
    RecyclerView.Adapter<HistoryTransaksiMemberAdapter.ViewHolder>(), Filterable{

    private var filteredHistoryTransaksiMemberList: MutableList<HistoryTransaksiMember>
    private val context: Context

    init{
        filteredHistoryTransaksiMemberList = ArrayList(historyTransaksiMemberList)
        this.context = context!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_history_transaksi_member, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int{
        return filteredHistoryTransaksiMemberList.size
    }

    fun setHistoryMemberList(historyTransaksiMemberList: Array<HistoryTransaksiMember>){
        this.historyTransaksiMemberList = historyTransaksiMemberList.toList()
        filteredHistoryTransaksiMemberList = historyTransaksiMemberList.toMutableList()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history_member = filteredHistoryTransaksiMemberList[position]

        if(history_member.jenis_transaksi.contains("P")){
            //set history presensi kelas
            holder.tvIdPresensiKelas.text = "ID Presensi Kelas: " + history_member.id_presensi_kelas
            holder.tvNamaKelasHistory.text = history_member.nama_kelas
            holder.tvStatusKehadiran.text = history_member.status_kehadiran
            holder.tvTglKelasHistory.text = history_member.tgl_history
            holder.tvJamPresensi.text = history_member.jam_history

            //set gone history transaksi
            holder.layoutHistoryKelas.visibility = View.GONE

//            holder.tvIdTransaksiHistory.visibility = View.GONE
//            holder.tvJenisTransaksiHistory.visibility = View.GONE
//            holder.tvJumlahTransaksi.visibility = View.GONE
//            holder.tvJumlahDepositKelas.visibility = View.GONE
//            holder.tvTanggalTransaksi.visibility = View.GONE
//            holder.tvJamTransaksi.visibility = View.GONE
        }else{
            //set history transaksi
            if(history_member.jumlah_deposit_kelas.isEmpty()){
                holder.tvJumlahDepositKelas.visibility = View.GONE
            }else{
                holder.tvJumlahDepositKelas.text = history_member.jumlah_deposit_kelas
            }

            holder.tvIdTransaksiHistory.text = "ID Transaksi: " + history_member.id_transaksi
            holder.tvJenisTransaksiHistory.text = history_member.jenis_transaksi
            holder.tvJumlahTransaksi.text = history_member.jumlah_transaksi
            holder.tvJumlahDepositKelas.text = history_member.jumlah_deposit_kelas

            //set gone presensi kelas
            holder.layoutHistoryTransaksi.visibility = View.GONE
//            holder.tvIdPresensiKelas.visibility = View.GONE
//            holder.tvNamaKelasHistory.visibility = View.GONE
//            holder.tvStatusKehadiran.visibility = View.GONE
//            holder.tvTglKelasHistory.visibility = View.GONE
//            holder.tvJamPresensi.visibility = View.GONE
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charSequenceString = charSequence.toString()
                val filtered: MutableList<HistoryTransaksiMember> = java.util.ArrayList()
                if (charSequenceString.isEmpty()){
                    filtered.addAll(historyTransaksiMemberList)
                }else{
                    for(historyMember in historyTransaksiMemberList){
                        if(historyMember.jenis_transaksi.lowercase(Locale.getDefault())
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
                filteredHistoryTransaksiMemberList.addAll((filterResults.values as List<HistoryTransaksiMember>))
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var tvIdTransaksiHistory: TextView
        var tvJenisTransaksiHistory: TextView
        var tvJumlahTransaksi: TextView
        var tvJumlahDepositKelas: TextView
        var tvTanggalTransaksi: TextView
        var tvJamTransaksi: TextView

        var tvIdPresensiKelas: TextView
        var tvNamaKelasHistory: TextView
        var tvStatusKehadiran: TextView
        var tvTglKelasHistory: TextView
        var tvJamPresensi: TextView

        var cvHistoryTransaksi: CardView

        var layoutHistoryTransaksi: LinearLayout
        var layoutHistoryKelas: LinearLayout

        init {
            //transaksi
            tvIdTransaksiHistory = itemView.findViewById(R.id.tv_id_transaksi)
            tvJenisTransaksiHistory = itemView.findViewById(R.id.tv_jenis_transaksi)
            tvJumlahTransaksi = itemView.findViewById(R.id.tv_jumlah_transaksi)
            tvJumlahDepositKelas = itemView.findViewById(R.id.tv_jumlah_deposit_kelas_history)
            tvTanggalTransaksi = itemView.findViewById(R.id.tv_tgl_transaksi)
            tvJamTransaksi = itemView.findViewById(R.id.tv_jam_transaksi)
            layoutHistoryKelas = itemView.findViewById(R.id.layout_history_transaksi)

            //presensi kelas
            tvIdPresensiKelas = itemView.findViewById(R.id.tv_id_presensi_kelas_history)
            tvNamaKelasHistory = itemView.findViewById(R.id.tv_nama_kelas_history)
            tvStatusKehadiran = itemView.findViewById(R.id.tv_status_kehadiran_history)
            tvTglKelasHistory = itemView.findViewById(R.id.tv_tgl_kelas_history)
            tvJamPresensi = itemView.findViewById(R.id.tv_jam_presensi)
            layoutHistoryTransaksi = itemView.findViewById(R.id.layout_history_kelas)

            cvHistoryTransaksi = itemView.findViewById(R.id.cv_history_transaksi)



        }
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return format.format(date)
    }
}