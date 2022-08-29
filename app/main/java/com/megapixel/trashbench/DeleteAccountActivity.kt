package com.megapixel.trashbench

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.megapixel.trashbench.Utils.firebaseUser
import kotlinx.android.synthetic.main.activity_delete_account.*
import java.util.HashMap

class DeleteAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        overridePendingTransition(R.anim.pop_up, R.anim.pop_up)


        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        window.setLayout((width*.9).toInt(), (height*.3).toInt())

        val params = window.attributes
        params.gravity = Gravity.CENTER_HORIZONTAL
        params.verticalMargin = 10f
        params.x = 0
        params.y = 0

        window.attributes = params

        cancel_delete_account.setOnClickListener {
            finish()
        }

        delete_account.setOnClickListener {
            deleteUser()
        }
    }

    private fun deleteUser() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["deleted"] = "true"

        usersRef.child(firebaseUser.uid).updateChildren(userMap)
    }

    private fun updateStatus(status: String)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref.updateChildren(hashMap)
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