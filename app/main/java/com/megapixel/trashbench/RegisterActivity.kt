package com.megapixel.trashbench

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_second)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val loginLayout = findViewById<RelativeLayout>(R.id.register_layout)


        val welcomeAnim = AnimationUtils.loadAnimation(this, R.anim.welcome_login)
        val getStarted = findViewById<TextView>(R.id.get_started)

        getStarted.startAnimation(welcomeAnim)


        register_btn.setOnClickListener {
            CreateAccount()

            full_name_register.setBackgroundResource(R.drawable.button_main)
            username_register.setBackgroundResource(R.drawable.button_main)
            email_register.setBackgroundResource(R.drawable.button_main)
            password_register.setBackgroundResource(R.drawable.button_main)
            confirm_password_register.setBackgroundResource(R.drawable.button_main)
        }

        registration_login_btn.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                val intent = Intent(this, LoginActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(this, loginLayout, "loginLayoutTransition")
                startActivity(intent, options.toBundle())
            }
            else
            {
                startActivity(Intent(this, LoginActivity::class.java))
                overridePendingTransition(R.anim.top_out, R.anim.none)
            }
        }



        full_name_register.setOnTouchListener { v, event ->
            full_name_register.setBackgroundResource(R.drawable.button_set)
            username_register.setBackgroundResource(R.drawable.button_main)
            email_register.setBackgroundResource(R.drawable.button_main)
            password_register.setBackgroundResource(R.drawable.button_main)
            confirm_password_register.setBackgroundResource(R.drawable.button_main)

            v?.onTouchEvent(event) ?: true
        }

        username_register.setOnTouchListener { v, event ->
            full_name_register.setBackgroundResource(R.drawable.button_main)
            username_register.setBackgroundResource(R.drawable.button_set)
            email_register.setBackgroundResource(R.drawable.button_main)
            password_register.setBackgroundResource(R.drawable.button_main)
            confirm_password_register.setBackgroundResource(R.drawable.button_main)

            v?.onTouchEvent(event) ?: true
        }

        email_register.setOnTouchListener { v, event ->
            full_name_register.setBackgroundResource(R.drawable.button_main)
            username_register.setBackgroundResource(R.drawable.button_main)
            email_register.setBackgroundResource(R.drawable.button_set)
            password_register.setBackgroundResource(R.drawable.button_main)
            confirm_password_register.setBackgroundResource(R.drawable.button_main)

            v?.onTouchEvent(event) ?: true
        }

        password_register.setOnTouchListener { v, event ->
            full_name_register.setBackgroundResource(R.drawable.button_main)
            username_register.setBackgroundResource(R.drawable.button_main)
            email_register.setBackgroundResource(R.drawable.button_main)
            password_register.setBackgroundResource(R.drawable.button_set)
            confirm_password_register.setBackgroundResource(R.drawable.button_main)

            v?.onTouchEvent(event) ?: true
        }

        confirm_password_register.setOnTouchListener { v, event ->
            full_name_register.setBackgroundResource(R.drawable.button_main)
            username_register.setBackgroundResource(R.drawable.button_main)
            email_register.setBackgroundResource(R.drawable.button_main)
            password_register.setBackgroundResource(R.drawable.button_main)
            confirm_password_register.setBackgroundResource(R.drawable.button_set)

            v?.onTouchEvent(event) ?: true
        }



    }

    private fun CreateAccount()
    {
        val fullName = full_name_register.text.toString()
        val userName = username_register.text.toString()
        val email = email_register.text.toString()
        val password = password_register.text.toString()
        val confirmPassword = confirm_password_register.text.toString()

        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this, R.string.write_full_name, Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this, R.string.write_user_name, Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, R.string.enter_email, Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(confirmPassword) -> Toast.makeText(this, R.string.confirm_password, Toast.LENGTH_SHORT).show()

            else -> {
                registration_progress_bar?.visibility = View.VISIBLE
                registration_background?.visibility = View.VISIBLE

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (password == confirmPassword)
                        {
                            if (task.isSuccessful)
                            {
                                saveUserInfo(fullName, userName, email, password)

                            }
                            else
                            {
                                val message = task.exception!!.toString()
                                Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                                mAuth.signOut()
                                registration_progress_bar?.visibility = View.GONE
                                registration_background?.visibility = View.GONE
                            }
                        }
                        else
                        {
                            registration_progress_bar?.visibility = View.GONE
                            registration_background?.visibility = View.GONE
                            Toast.makeText(this@RegisterActivity, R.string.password_mismatch, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }

    private fun saveUserInfo(fullName: String, userName: String, email: String, password: String)
    {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val password = password_register.text.toString()
        val confirmPassword = confirm_password_register.text.toString()

        if (password == confirmPassword) {
            val userMap = HashMap<String, Any>()
            userMap["uid"] = currentUserID
            userMap["fullname"] = fullName
            userMap["username"] = userName
            userMap["email"] = email
            userMap["password"] = password
            userMap["bio"] = "Hey, I'm using TrashBench!"
            userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/trashbench.appspot.com/o/Default%20Images%2Fprofile_image.png?alt=media&token=4c6c5fc9-36db-4af4-9bfd-18fc8ba8e9b0"
            userMap["mp"] = ""
            userMap["emoji"] = ""
            userMap["deleted"] = ""


            usersRef.child(currentUserID).setValue(userMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (password == confirmPassword) {
                            registration_progress_bar?.visibility = View.GONE
                            registration_background?.visibility = View.GONE
                            Toast.makeText(
                                this,
                                R.string.account_created_successfully,
                                Toast.LENGTH_SHORT
                            ).show()


                            FirebaseDatabase.getInstance().reference
                                .child("Follow").child(currentUserID)
                                .child("Following").child(currentUserID)
                                .setValue(true)


                            val intent = Intent(this@RegisterActivity, RegisterSettingsActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()

                            overridePendingTransition(R.anim.bottom_in, R.anim.none)
                        } else {
                            registration_progress_bar?.visibility = View.GONE
                            registration_background?.visibility = View.GONE
                            Toast.makeText(
                                this@RegisterActivity,
                                R.string.password_mismatch,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()
                        registration_progress_bar?.visibility = View.GONE
                        registration_background?.visibility = View.GONE
                    }
                }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(R.anim.top_out, R.anim.none)
    }
}