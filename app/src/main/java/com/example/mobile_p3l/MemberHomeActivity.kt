package com.example.mobile_p3l

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mobile_p3l.databinding.ActivityInstrukturHomeBinding
import com.example.mobile_p3l.databinding.ActivityMemberHomeBinding
import login.LoginActivity

class MemberHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMemberHomeBinding = ActivityMemberHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val namaMemberTextView = binding.textViewNamaMember
        val logoutBtn = binding.logoutBtn

        val bundle = intent.extras
        val nama_user = bundle?.getString("nama_user")

        namaMemberTextView.text = nama_user

        logoutBtn.setOnClickListener {
            val moveLogout = Intent(this@MemberHomeActivity, LoginActivity::class.java)
            startActivity(moveLogout)
        }
    }
}