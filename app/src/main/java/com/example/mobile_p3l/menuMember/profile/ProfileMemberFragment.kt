package com.example.mobile_p3l.menuMember.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mobile_p3l.DepositKelasActivity
import com.example.mobile_p3l.databinding.FragmentProfileMemberBinding
import com.google.gson.Gson
import org.json.JSONObject
import server.api.MemberApi
import server.model.Member
import java.nio.charset.StandardCharsets

class ProfileMemberFragment : Fragment() {

    private var _binding: FragmentProfileMemberBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var queue: RequestQueue? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileMemberBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id_user = arguments?.getString("id_user")

        queue = Volley.newRequestQueue(context)

//        val bundle = intent.extras
//        val id_instruktur = bundle?.getString("id_instruktur")

        val btnDepositKelas = binding.buttonDepositKelas
        btnDepositKelas.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("id_member", id_user)
            val move = Intent(context, DepositKelasActivity::class.java)
            move.putExtras(bundle)
            startActivity(move)
        }

        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, MemberApi.GET_BY_ID + id_user, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val member = gson.fromJson(
                    jsonObject.getJSONObject("data").toString(), Member::class.java
                )

                if(member != null) {
                    Toast.makeText(context, "Data Berhasil Diambil" + member.status, Toast.LENGTH_SHORT)
                        .show()

                    var status = member.status
                    var tgl_kadaluarsa = member.tgl_kadaluarsa

                    if(status.equals("1", true)){
                        status = "AKTIF"
                    }else{
                        status = "TIDAK AKTIF"
                        tgl_kadaluarsa = "-"
                    }

                    binding.tvStatus.setText(status)
                    binding.tvTglKadaluarsa.setText("Tanggal kadaluarsa: " + tgl_kadaluarsa)
                    binding.tvNamaMember.setText(member.nama)
                    binding.tvNoTelp.setText(member.no_telp)
                    binding.tvTglLahir.setText(member.tgl_lahir)
                    binding.tvEmail.setText(member.email)
                    binding.tvDepositUang.setText("Rp " + member.deposit_uang + ",-")

                }else{
                    Toast.makeText(context, "Member tidak ditemukan!", Toast.LENGTH_SHORT)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}