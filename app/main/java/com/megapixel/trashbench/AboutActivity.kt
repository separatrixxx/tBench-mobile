package com.megapixel.trashbench

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)


        firebaseUser = FirebaseAuth.getInstance().currentUser


        back_about.setOnClickListener {
            onBackPressed()
        }

        city1.setOnClickListener {
            visible1()
        }

        city2.setOnClickListener {
            visible2()
        }

        city3.setOnClickListener {
            visible3()
        }

        logo11.setOnClickListener {
            visible1()
        }

        logo12.setOnClickListener {
            visible1()
        }

        logo21.setOnClickListener {
            visible2()
        }

        logo22.setOnClickListener {
            visible2()
        }

        logo31.setOnClickListener {
            visible3()
        }

        logo32.setOnClickListener {
            visible3()
        }

        about_image_1.setOnClickListener {
            visible1()
        }

        about_image_2.setOnClickListener {
            visible2()
        }

        about_image_3.setOnClickListener {
            visible3()
        }

        smolensk_1.setOnClickListener {
            visible1()
        }

        smolensk_2.setOnClickListener {
            visible2()
        }

        smolensk_3.setOnClickListener {
            visible3()
        }

    }

    private fun visible3() {
        city1.visibility = View.VISIBLE
        city2.visibility = View.GONE
        city3.visibility = View.GONE

        logo11.visibility = View.VISIBLE
        logo12.visibility = View.VISIBLE
        logo21.visibility = View.GONE
        logo22.visibility = View.GONE
        logo31.visibility = View.GONE
        logo32.visibility = View.GONE

        about_image_1.visibility = View.VISIBLE
        about_image_2.visibility = View.GONE
        about_image_3.visibility = View.GONE

        smolensk_1.visibility = View.VISIBLE
        smolensk_2.visibility = View.GONE
        smolensk_3.visibility = View.GONE
    }

    private fun visible2() {
        city1.visibility = View.GONE
        city2.visibility = View.GONE
        city3.visibility = View.VISIBLE

        logo11.visibility = View.GONE
        logo12.visibility = View.GONE
        logo21.visibility = View.GONE
        logo22.visibility = View.GONE
        logo31.visibility = View.VISIBLE
        logo32.visibility = View.VISIBLE

        about_image_1.visibility = View.GONE
        about_image_2.visibility = View.GONE
        about_image_3.visibility = View.VISIBLE

        smolensk_1.visibility = View.GONE
        smolensk_2.visibility = View.GONE
        smolensk_3.visibility = View.VISIBLE
    }

    private fun visible1() {
        city1.visibility = View.GONE
        city2.visibility = View.VISIBLE
        city3.visibility = View.GONE

        logo11.visibility = View.GONE
        logo12.visibility = View.GONE
        logo21.visibility = View.VISIBLE
        logo22.visibility = View.VISIBLE
        logo31.visibility = View.GONE
        logo32.visibility = View.GONE

        about_image_1.visibility = View.GONE
        about_image_2.visibility = View.VISIBLE
        about_image_3.visibility = View.GONE

        smolensk_1.visibility = View.GONE
        smolensk_2.visibility = View.VISIBLE
        smolensk_3.visibility = View.GONE
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
