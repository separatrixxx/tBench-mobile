package com.megapixel.trashbench

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_gq2.*

class Gq2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gq2)

        back_answer_7.setOnClickListener {
            onBackPressed()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this@Gq2Activity, HelpActivity::class.java)
        startActivity(intent)
        finish()
    }
}