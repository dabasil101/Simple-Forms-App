package com.example.forms

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.forms.helpers.ApiHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setupEdgeToEdge()

        findViewById<TextView>(R.id.linktologin).setOnClickListener {
            startActivity(Intent(this, Signin::class.java))
        }

        findViewById<MaterialButton>(R.id.create).setOnClickListener {
            if (validateInputs()) {
                registerUser()
            }
        }
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun validateInputs(): Boolean {
        val password = findViewById<TextInputEditText>(R.id.password)
        val confirmPassword = findViewById<TextInputEditText>(R.id.confirm)

        if (password.text.toString() != confirmPassword.text.toString()) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun registerUser() {
        val surname = findViewById<TextInputEditText>(R.id.surname).text.toString()
        val others = findViewById<TextInputEditText>(R.id.others).text.toString()
        val email = findViewById<TextInputEditText>(R.id.email).text.toString()
        val password = findViewById<TextInputEditText>(R.id.password).text.toString()

        val api = com.example.forms.constant.Constants.BASEURL + "user_signup"
        val helper = ApiHelper(applicationContext)

        val body = JSONObject().apply {
            put("surname", surname)
            put("others", others)
            put("email", email)
            put("password", password)
        }

        helper.post(api, body, object : ApiHelper.CallBack {
            override fun onSuccess(result: JSONArray?) {
            }

            override fun onSuccess(result: JSONObject?) {
                Toast.makeText(applicationContext, "Registered successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@Signup, Signin::class.java))
                finish()
            }

            override fun onFailure(result: String?) {
                Toast.makeText(applicationContext, "Registration failed", Toast.LENGTH_SHORT).show()
            }
        })


    }
}
