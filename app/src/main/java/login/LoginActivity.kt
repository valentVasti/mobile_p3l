package login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import androidx.localbroadcastmanager.R
//import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
//import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mobile_p3l.HomeInstrukturActivity
import com.example.mobile_p3l.HomeMOActivity
import com.example.mobile_p3l.HomeMemberActivity
import com.example.mobile_p3l.InstrukturHomeActivity
import com.example.mobile_p3l.MOHomeActivity
import com.example.mobile_p3l.MemberHomeActivity
import com.example.mobile_p3l.databinding.ActivityLoginBinding
import org.json.JSONObject
import server.api.UserApi
import java.nio.charset.StandardCharsets

//import java.nio.charset.StandardCharsets

class LoginActivity : AppCompatActivity() {
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputLayoutUsername = binding.inputLayoutUsername
        val inputlayoutPassword = binding.inputLayoutPassword

        val btnLogin = binding.loginBtn

        queue = Volley.newRequestQueue(this)

        val jsonobj = JSONObject()

        btnLogin.setOnClickListener(View.OnClickListener {
            val inputEmail = inputLayoutUsername.getEditText()?.getText().toString()
            val inputPassword = inputlayoutPassword.getEditText()?.getText().toString()

//            if (inputUsername.isEmpty())
//                layoutUsername.setError("Username must be filled with text")
//
//            if (inputPassword.isEmpty())
//                layoutPassword.setError("Password must be filled with text")
            jsonobj.put("email", inputEmail)
            jsonobj.put("password", inputPassword)
                val request = JsonObjectRequest(Request.Method.POST, UserApi.LOGIN_URL,jsonobj,
                    { response ->
                      Log.d("Responsenya: ", response["message"].toString())

                        Toast.makeText(
                        this@LoginActivity,
                            response["message"].toString() + " Nama: " + response["nama"].toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                        var bundle = Bundle()
                        var move: Intent

                        if(response["id"].toString().contains('P')){
                            move = Intent(this, HomeMOActivity::class.java)
                        }else if(response["id"].toString().contains('I')){
                            move = Intent(this, HomeInstrukturActivity::class.java)
                        }else{
                            move = Intent(this, HomeMemberActivity::class.java)
                        }
                        bundle.putString("nama_user", response["nama"].toString())
                        bundle.putString("id_user", response["id"].toString())
                        move.putExtras(bundle)

                        startActivity(move)

                    }, { error->
                        try{
                            val errorData = String(error.networkResponse.data, StandardCharsets.UTF_8)
                            val errorJson = JSONObject(errorData)
                            if(error.networkResponse.statusCode != 400){
                                Toast.makeText(
                                    this@LoginActivity,
                                    errorJson["message"].toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }catch (e: Exception){
                            Log.d("Error Login", e.message.toString())
                            Toast.makeText(this@LoginActivity,"exception: " + e.message, Toast.LENGTH_SHORT).show()
                        }
                    })
                queue!!.add(request)

        })
    }
}
