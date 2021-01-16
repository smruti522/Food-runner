package com.smruti.foodrunner.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smruti.foodrunner.R
import com.smruti.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPassword2 : AppCompatActivity() {

    lateinit var etotp: EditText
    lateinit var etforgotpassword: EditText
    lateinit var etconfirmpassword: EditText
    lateinit var btnsubmit: Button
    lateinit var toolbar: Toolbar
    var number = "1234567890"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password2)
        if (intent != null) {
            number = intent.getStringExtra("mobilenumber")
        }
        etotp = findViewById(R.id.etEnterotp)
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "   Enter the recieved otp below"
        etforgotpassword = findViewById(R.id.etForgotpassword)
        etconfirmpassword = findViewById(R.id.etfConfirmPassword)
        btnsubmit = findViewById(R.id.btnsubmit)
        btnsubmit.setOnClickListener {
            if(etforgotpassword.text.isBlank()||etconfirmpassword.text.isBlank()||etotp.text.isBlank())
            {
                if(etotp.text.isBlank())
                {etotp.setError("Otp Missing")}
                if(etforgotpassword.text.isBlank())
                {etforgotpassword.setError("password Missing")}
                if(etconfirmpassword.text.isBlank())
                {
                    etconfirmpassword.setError("password Missing")
                }
            }
            else{
            if ((etforgotpassword.text.toString()) == (etconfirmpassword.text.toString())) {
                val queue = Volley.newRequestQueue(this@ForgotPassword2)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                if (ConnectionManager().checkConnectivity(this@ForgotPassword2)) {
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", number)
                    jsonParams.put("password", etforgotpassword.text.toString())
                    jsonParams.put("otp", etotp.text.toString())

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

                                        startActivity(
                                            Intent(
                                                this@ForgotPassword2,
                                                login::class.java
                                            )
                                        )

                                    } else {
                                        Toast.makeText(
                                            this@ForgotPassword2,
                                            "this number doesnot exist",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@ForgotPassword2,
                                        "some exception  occurred ",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }

                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@ForgotPassword2,
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

                    Toast.makeText(this@ForgotPassword2, "No internet", Toast.LENGTH_SHORT).show()
                }

            }
        }}

    }

    override fun onResume() {

        if (!ConnectionManager().checkConnectivity(this@ForgotPassword2)) {

            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(this@ForgotPassword2)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                ActivityCompat.finishAffinity(this)//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

        super.onResume()
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}
