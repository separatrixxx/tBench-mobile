package com.megapixel.trashbench

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_mq1.*

class Mq1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mq1)

        back_answer_10.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this@Mq1Activity, HelpActivity::class.java)
        startActivity(intent)
        finish()
    }
}