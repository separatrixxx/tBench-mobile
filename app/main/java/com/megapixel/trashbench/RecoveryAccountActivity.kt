package com.megapixel.trashbench

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.megapixel.trashbench.Utils.firebaseUser
import kotlinx.android.synthetic.main.activity_recovery_acount.*
import java.util.HashMap

class RecoveryAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery_acount)

        restore_btn.setOnClickListener {
            val intent = Intent(this@RecoveryAccountActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

            recoveryUser()
        }

        logout_btn.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this@RecoveryAccountActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()

        overridePendingTransition(R.anim.top_out, R.anim.none)
    }

    private fun recoveryUser() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["deleted"] = ""

        usersRef.child(firebaseUser.uid).updateChildren(userMap)
    }
}