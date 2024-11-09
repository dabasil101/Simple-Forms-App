package com.example.forms

import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract.Constants
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.forms.helpers.ApiHelper
import com.example.forms.helpers.PrefsHelper
import org.json.JSONArray
import org.json.JSONObject

class Signin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val email: EditText = findViewById(R.id.email)
        val password: EditText = findViewById(R.id.password)
        val loginButton: Button = findViewById(R.id.login)
        val registerLink: TextView = findViewById(R.id.linktiregister)

        loginButton.setOnClickListener {
            val valEmail = email.text.toString().trim()
            val valPassword = password.text.toString().trim()

            if (valEmail.isEmpty() || valPassword.isEmpty()) {
                Toast.makeText(this, "Email and Password are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = JSONObject().apply {
                put("email", valEmail)
                put("password", valPassword)
            }

                    val api = com.example.forms.constant.Constants.BASEURL + "member_signin"
            val helper = ApiHelper(this@Signin)

            helper.post(api, body, object : ApiHelper.CallBack {
                override fun onSuccess(result: JSONArray?) {}

                override fun onSuccess(result: JSONObject?) {
                    result?.let {
                        if (it.has("token")) {
                            val memberId = it.getString("member_id")
                            val token = it.getString("token")
                            Toast.makeText(applicationContext, "Login successful", Toast.LENGTH_SHORT).show()
                            PrefsHelper.savePrefs(applicationContext, "token", token)
                            getMemberData(memberId)
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                        } else {
                            Toast.makeText(applicationContext, "Wrong credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(result: String?) {
                    Toast.makeText(applicationContext, "Login failed: $result", Toast.LENGTH_SHORT).show()
                }
            })
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }
    }

    private fun getMemberData(memberId: String) {
        val body = JSONObject().apply {
            put("member_id", memberId)
        }

        val helper = ApiHelper(applicationContext)
        val api = com.example.forms.constant.Constants.BASEURL  + "member_profile"
        helper.post2(api, body, object : ApiHelper.CallBack {
            override fun onSuccess(result: JSONArray?) {}

            override fun onSuccess(result: JSONObject?) {
                result?.let {
                    if (it.has("_id")) {
                        PrefsHelper.savePrefs(applicationContext, "member_id", it.getString("_id"))
                        PrefsHelper.savePrefs(applicationContext, "surname", it.getString("surname"))
                        PrefsHelper.savePrefs(applicationContext, "email", it.getString("email"))
                    }
                }
            }

            override fun onFailure(result: String?) {
                Toast.makeText(applicationContext, "Failed to fetch data: $result", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
