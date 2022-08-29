package com.megapixel.trashbench

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_options.*
import java.util.*


class OptionsActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        overridePendingTransition(R.anim.options_up_right, R.anim.options_up_right)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val height = ViewGroup.LayoutParams.MATCH_PARENT

        window.setLayout(700, height)

        val params = window.attributes
        params.gravity = Gravity.CENTER_VERTICAL
        params.horizontalMargin = 10f
        
        params.x = 0
        params.y = 0

        window.attributes = params

        emoji_layout.setOnClickListener {
            startActivity(Intent(this, EmojiActivity::class.java))
            finish()
        }


        clubs_layout.setOnClickListener {
            startActivity(Intent(this, ClubsActivity::class.java))
            finish()
        }


        weather_layout.setOnClickListener {
            startActivity(Intent(this, WeatherActivity::class.java))
            finish()
        }


        targets_layout.setOnClickListener {
            startActivity(Intent(this, TargetsActivity::class.java))
            finish()
        }


        help_layout.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
            finish()
        }

        about_layout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
            finish()
        }
    }

    private fun updateStatus(status: String)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()

        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()

        updateStatus("offline")
    }
}