package com.smruti.foodrunner.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smruti.foodrunner.R
import com.smruti.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class Registration : AppCompatActivity() {
    lateinit var etname:EditText
    lateinit var etemail:EditText
    lateinit var etmobile:EditText
    lateinit var etaddress:EditText
    lateinit var etpassword:EditText
    lateinit var etconfirmpassword:EditText
    lateinit var btnregister:Button
    lateinit var sharedpreference:SharedPreferences
 lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        sharedpreference =
            getSharedPreferences(getString(R.string.sharedpreferences), Context.MODE_PRIVATE)

        etname=findViewById(R.id.etName)
        etaddress=findViewById(R.id.etAddress)
        etemail=findViewById(R.id.etEmail)
        etmobile=findViewById(R.id.etPhoneNumber)
        etpassword=findViewById(R.id.etPassword)
        etconfirmpassword=findViewById(R.id.etConfirmPassword)
        btnregister=findViewById(R.id.btnRegister)
        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Registration"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnregister.setOnClickListener {
            if(etname.text.isBlank()||etaddress.text.isBlank()||etmobile.text.isBlank()||etpassword.text.isBlank()||etconfirmpassword.text.isBlank()){
                if(etname.text.isBlank())
                    etname.setError("name missing")
                    if(etaddress.text.isBlank())
                        etaddress.setError("Address Missing")
                        if(etmobile.text.isBlank())
                            etmobile.setError("Number missing")
                            if(etpassword.text.isBlank())
                                etpassword.setError("password missing")
                                if(etconfirmpassword.text.isBlank())
                                    etconfirmpassword.setError("confirm your password")
            }
            else{
            if ((etpassword.text.toString()) == (etconfirmpassword.text.toString()) && (etmobile.text.toString()).length == 10) {
                val queue = Volley.newRequestQueue(this@Registration)
                val url = "http://13.235.250.119/v2/register/fetch_result"
                if (ConnectionManager().checkConnectivity(this@Registration)) {
                    val jsonParams = JSONObject()
                    jsonParams.put("name", etname.text.toString())
                    jsonParams.put("mobile_number", etmobile.text.toString())
                    jsonParams.put("password", etpassword.text.toString())
                    jsonParams.put("address", etaddress.text.toString())
                    jsonParams.put("email", etemail.text.toString())

                    val jsonRequest =
                        object : JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            jsonParams,
                            Response.Listener {
                                try {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")



                                    if (success) {
                                        val resobject = data.getJSONObject("data")
                                        val user_id = resobject.getString("user_id")
                                        val name = resobject.getString("name")
                                        val email = resobject.getString("email")
                                        val mobile_number = resobject.getString("mobile_number")
                                        val address = resobject.getString("address")

                                            sharedpreference.edit().putString("userid", user_id).apply()
                                            sharedpreference.edit().putString("name", name).apply()
                                            sharedpreference.edit().putString("email", email).apply()
                                            sharedpreference.edit()
                                                .putString("mobile", mobile_number).apply()
                                            sharedpreference.edit().putString("address", address).apply()

                                        sharedpreference.edit().putBoolean("login", true).apply()
                                        startActivity(
                                            Intent(
                                                this@Registration,
                                                MainActivity::class.java
                                            )
                                        )

                                    } else {
                                        Toast.makeText(
                                            this@Registration,
                                            "this number is already used",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@Registration,
                                        "some exception  occurred ",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }

                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@Registration,
                                    " volley error occured ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "ccf52a609cc3d2"
                                return headers
                            }
                        }
                    queue.add(jsonRequest)
                } else {
                    Toast.makeText(this@Registration,"No internet Connection",Toast.LENGTH_SHORT).show()



                }
            }
            else{
                Toast.makeText(this@Registration,"passwords donot match",Toast.LENGTH_SHORT).show()
            }
        }}
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            android.R.id.home -> {
                finish()
                onBackPressed()
            }
        }
       return super.onOptionsItemSelected(item)
        }

    }



