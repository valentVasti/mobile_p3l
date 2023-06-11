package com.example.mobile_p3l

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mobile_p3l.databinding.ActivityInstrukturHomeBinding
import com.example.mobile_p3l.databinding.ActivityLoginBinding
import login.LoginActivity

class InstrukturHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityInstrukturHomeBinding = ActivityInstrukturHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val namaInstrukturTextView = binding.textViewNamaInstruktur

        val logoutBtn = binding.logoutBtn
        val izinInstrukturBtn = binding.izinInstrukturBtn

        val bundle = intent.extras
        val nama_user = bundle?.getString("nama_user")
        val id_user = bundle?.getString("id_user")

        namaInstrukturTextView.text = nama_user

        logoutBtn.setOnClickListener {
            val moveLogout = Intent(this@InstrukturHomeActivity, LoginActivity::class.java)
            startActivity(moveLogout)
        }

        izinInstrukturBtn.setOnClickListener{
            var bundle = Bundle()
            bundle.putString("id_instruktur", id_user)
            val moveToIzin = Intent(this@InstrukturHomeActivity, IzinInstrukturActivity::class.java)
            moveToIzin.putExtras(bundle)
            startActivity(moveToIzin)
        }
    }
}