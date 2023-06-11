package com.example.mobile_p3l

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobile_p3l.databinding.ActivityMemberHomeBinding
import com.example.mobile_p3l.databinding.ActivityMohomeBinding
import login.LoginActivity

class MOHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMohomeBinding = ActivityMohomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val namaMOTextView = binding.textViewNamaMO
        val logoutBtn = binding.logoutBtn

        val bundle = intent.extras
        val nama_user = bundle?.getString("nama_user")

        namaMOTextView.text = nama_user

        logoutBtn.setOnClickListener {
            val moveLogout = Intent(this@MOHomeActivity, LoginActivity::class.java)
            startActivity(moveLogout)
        }
    }
}