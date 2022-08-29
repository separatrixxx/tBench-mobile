package com.megapixel.trashbench

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_target.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddTargetActivity : AppCompatActivity()
{

    var firebaseUser: FirebaseUser? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_target)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        close_add_target_btn.setOnClickListener {
            val intent = Intent(this@AddTargetActivity, TargetsActivity::class.java)
            startActivity(intent)
            finish()
        }

        save_new_target_btn.setOnClickListener {
            addTarget()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addTarget() {
        when (target_text.text) {
            null -> Toast.makeText(this, R.string.write_target, Toast.LENGTH_SHORT).show()
            else -> {


                val ref = FirebaseDatabase.getInstance().reference.child("Targets")
                val targetId = ref.push().key

                val now = LocalDateTime.now()
                var formatter = DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")

                val targetMap = HashMap<String, Any>()
                targetMap["targetId"] = targetId!!
                targetMap["target"] = target_text.text.toString()
                targetMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                targetMap["time"] = formatter.format(now).toString()

                ref.child(targetId).updateChildren(targetMap)

                Toast.makeText(this, R.string.target_added_successfully, Toast.LENGTH_SHORT).show()

                val intent = Intent(this@AddTargetActivity, TargetsActivity::class.java)
                startActivity(intent)
                finish()
            }
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