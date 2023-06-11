package com.example.mobile_p3l.menuInstruktur.profile

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
import com.example.mobile_p3l.databinding.FragmentProfileInstrukturBinding
import com.google.gson.Gson
import org.json.JSONObject
import server.api.InstrukturApi
import server.model.Instruktur
import java.nio.charset.StandardCharsets

class ProfileInstrukturFragment : Fragment() {
    private var _binding: FragmentProfileInstrukturBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var queue: RequestQueue? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileInstrukturBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id_instruktur = arguments?.getString("id_user")

        queue = Volley.newRequestQueue(context)

//        val bundle = intent.extras
//        val id_instruktur = bundle?.getString("id_instruktur")

        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, InstrukturApi.GET_BY_ID + id_instruktur, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                val instruktur = gson.fromJson(
                    jsonObject.getJSONObject("data").toString(), Instruktur::class.java
                )

                if(instruktur != null) {
                    Toast.makeText(context, "Data Berhasil Diambil", Toast.LENGTH_SHORT)
                        .show()

                    binding.tvNamaInstruktur.setText(instruktur.nama)
                    binding.tvTotalKeterlambatan.setText(instruktur.keterlambatan)
                    binding.tvNoTelp.setText(instruktur.no_telp)
                    binding.tvTglLahir.setText(instruktur.tgl_lahir)
                    binding.tvEmail.setText(instruktur.email)

                }else{
                    Toast.makeText(context, "Instruktur tidak ditemukan!", Toast.LENGTH_SHORT)
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