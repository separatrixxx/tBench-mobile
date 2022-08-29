package com.megapixel.trashbench

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : AppCompatActivity()
{

    var firebaseUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        back_help.setOnClickListener {
            onBackPressed()
        }


        gq1.setOnClickListener {
            val intent = Intent(this@HelpActivity, Gq1Activity::class.java)
            startActivity(intent)
            finish()
        }

        gq2.setOnClickListener {
            val intent = Intent(this@HelpActivity, Gq2Activity::class.java)
            startActivity(intent)
            finish()
        }

        gq3.setOnClickListener {
            val intent = Intent(this@HelpActivity, Gq3Activity::class.java)
            startActivity(intent)
            finish()
        }

        aq1.setOnClickListener {
            val intent = Intent(this@HelpActivity, Aq1Activity::class.java)
            startActivity(intent)
            finish()
        }

        aq2.setOnClickListener {
            val intent = Intent(this@HelpActivity, Aq2Activity::class.java)
            startActivity(intent)
            finish()
        }

        aq3.setOnClickListener {
            val intent = Intent(this@HelpActivity, Aq3Activity::class.java)
            startActivity(intent)
            finish()
        }

        aq4.setOnClickListener {
            val intent = Intent(this@HelpActivity, Aq4Activity::class.java)
            startActivity(intent)
            finish()
        }

        aq5.setOnClickListener {
            val intent = Intent(this@HelpActivity, Aq5Activity::class.java)
            startActivity(intent)
            finish()
        }

        mq1.setOnClickListener {
            val intent = Intent(this@HelpActivity, Mq1Activity::class.java)
            startActivity(intent)
            finish()
        }

        mq2.setOnClickListener {
            val intent = Intent(this@HelpActivity, Mq2Activity::class.java)
            startActivity(intent)
            finish()
        }

        mq3.setOnClickListener {
            val intent = Intent(this@HelpActivity, Mq3Activity::class.java)
            startActivity(intent)
            finish()
        }

        mq4.setOnClickListener {
            val intent = Intent(this@HelpActivity, Mq4Activity::class.java)
            startActivity(intent)
            finish()
        }

        mq5.setOnClickListener {
            val intent = Intent(this@HelpActivity, Mq5Activity::class.java)
            startActivity(intent)
            finish()
        }

        mq6.setOnClickListener {
            val intent = Intent(this@HelpActivity, Mq6Activity::class.java)
            startActivity(intent)
            finish()
        }

        pq1.setOnClickListener {
            val intent = Intent(this@HelpActivity, Pq1Activity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun updateStatus(status: String)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val hashMap = java.util.HashMap<String, Any>()
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