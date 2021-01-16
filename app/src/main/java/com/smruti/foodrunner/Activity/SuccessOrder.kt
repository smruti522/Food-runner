package com.smruti.foodrunner.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import com.smruti.foodrunner.R

class SuccessOrder : AppCompatActivity() {
    lateinit var buttonOkay: Button
    lateinit var orderSuccessfullyPlaced: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_order)

        orderSuccessfullyPlaced=findViewById(R.id.orderSuccessfullyPlaced)
        buttonOkay=findViewById(R.id.buttonOkay)

        buttonOkay.setOnClickListener(View.OnClickListener {

            val intent= Intent(this,MainActivity::class.java)

            startActivity(intent)

            finishAffinity()
        })
    }

    override fun onBackPressed() {

    }

}
