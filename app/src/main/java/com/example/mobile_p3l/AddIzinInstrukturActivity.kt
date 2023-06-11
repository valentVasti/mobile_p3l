package com.example.mobile_p3l

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mobile_p3l.databinding.ActivityAddIzinInstrukturBinding
import org.json.JSONObject
import server.api.IzinInstrukturApi
import server.api.UserApi
import server.model.IzinInstruktur
import java.nio.charset.StandardCharsets

class AddIzinInstrukturActivity : AppCompatActivity() {
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityAddIzinInstrukturBinding = ActivityAddIzinInstrukturBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cancelBtn = binding.cancelBtn
        val addIzinBtn = binding.addIzinBtn

        val inputLayoutIdJadwalHarian = binding.inputLayoutJadwalHarian
        val inputLayoutIdInstrukturPengganti = binding.inputLayoutIdInstrukturPengganti

        val bundle = intent.extras
        val id_instruktur = bundle?.getString("id_instruktur")

        queue = Volley.newRequestQueue(this)

        val jsonobj = JSONObject()

        cancelBtn.setOnClickListener{
            var bundle = Bundle()
            bundle.putString("id_instruktur", id_instruktur)
            val moveToIzin = Intent(this@AddIzinInstrukturActivity, HomeInstrukturActivity::class.java)
            moveToIzin.putExtras(bundle)
            startActivity(moveToIzin)
        }

        addIzinBtn.setOnClickListener{

            val inputIdJadwalHarian = inputLayoutIdJadwalHarian.getEditText()?.getText().toString()
            val inputIdInstrukturPengganti = inputLayoutIdInstrukturPengganti.getEditText()?.getText().toString()

            jsonobj.put("id_izin", "1")
            jsonobj.put("id_jadwal_harian", inputIdJadwalHarian)
            jsonobj.put("id_instruktur", id_instruktur)
            jsonobj.put("id_instruktur_pengganti", inputIdInstrukturPengganti)
            jsonobj.put("status_konfirmasi", 1)
            jsonobj.put("tgl_izin", "1")
            val request = JsonObjectRequest(
                Request.Method.POST, IzinInstrukturApi.ADD_DATA,jsonobj,
                { response ->
                    Log.d("Responsenya: ", response["message"].toString())

                    Toast.makeText(
                        this@AddIzinInstrukturActivity,
                        "Berhasil mengajukan izin",
                        Toast.LENGTH_SHORT
                    ).show()

                    var bundle = Bundle()
                    val move = Intent(this, HomeInstrukturActivity::class.java)
                    bundle.putString("id_instruktur", id_instruktur)
                    move.putExtras(bundle)
                    startActivity(move)

                }, { error->
                    try{
                        val errorData = String(error.networkResponse.data, StandardCharsets.UTF_8)
                        val errorJson = JSONObject(errorData)
                        if(error.networkResponse.statusCode != 400){
                            Toast.makeText(
                                this@AddIzinInstrukturActivity,
                                errorJson["message"].toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }catch (e: Exception){
                        Log.d("Error Login", e.message.toString())
                        Toast.makeText(this@AddIzinInstrukturActivity,"exception: " + e.message, Toast.LENGTH_SHORT).show()
                    }
                })
            queue!!.add(request)
        }
    }
}