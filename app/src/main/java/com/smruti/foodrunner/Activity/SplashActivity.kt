package com.smruti.foodrunner.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.smruti.foodrunner.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_splash)



        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({

           val sharedpreference =
                getSharedPreferences(getString(R.string.sharedpreferences), Context.MODE_PRIVATE)
            val islogin = sharedpreference.getBoolean("login", false)
            if (islogin) {
                val intent = Intent(this@SplashActivity,MainActivity::class.java)
                startActivity(intent)

            } else {
                val startAct = Intent(this@SplashActivity, login::class.java)
                startActivity(startAct)
            }


        }, 2000)

    }
}
