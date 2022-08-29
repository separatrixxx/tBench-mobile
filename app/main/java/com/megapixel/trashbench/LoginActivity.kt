package com.megapixel.trashbench

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import android.app.ActivityOptions as ActivityOptions1

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        login_btn.setOnClickListener {
            loginUser()

            password_login.setBackgroundResource(R.drawable.button_main)
            email_login.setBackgroundResource(R.drawable.button_main)
        }

        val welcomeAnim = AnimationUtils.loadAnimation(this, R.anim.welcome_login)

        val loginLayout = findViewById<RelativeLayout>(R.id.login_layout)
        val welcome = findViewById<RelativeLayout>(R.id.welcome)


        welcome.startAnimation(welcomeAnim)


        login_registration_btn.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                val intent = Intent(this, RegisterActivity::class.java)
                val options = ActivityOptions1.makeSceneTransitionAnimation(this, loginLayout, "loginLayoutTransition")
                startActivity(intent, options.toBundle())
            }
            else
            {
                startActivity(Intent(this, RegisterActivity::class.java))
                overridePendingTransition(R.anim.bottom_in, R.anim.none)
            }
        }



        email_login.setOnTouchListener { v, event ->
            email_login.setBackgroundResource(R.drawable.button_set)
            password_login.setBackgroundResource(R.drawable.button_main)

            v?.onTouchEvent(event) ?: true
        }

        password_login.setOnTouchListener { v, event ->
            password_login.setBackgroundResource(R.drawable.button_set)
            email_login.setBackgroundResource(R.drawable.button_main)

            v?.onTouchEvent(event) ?: true
        }




        forgot_password.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.forgot_password)
            val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
            val username = view.findViewById<EditText>(R.id.et_username)
            builder.setView(view)
            builder.setPositiveButton(R.string.reset, DialogInterface.OnClickListener { _,  _->
                forgotPassword(username)
            })
            builder.setNegativeButton(R.string.close_more_info_profile, DialogInterface.OnClickListener { _, _ ->  })
            builder.show()

            password_login.setBackgroundResource(R.drawable.button_main)
            email_login.setBackgroundResource(R.drawable.button_main)
        }
    }

    private fun forgotPassword(username: EditText)
    {
        if (username.text.toString().isEmpty())
        {
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(username.text.toString()).matches())
        {
            return
        }

        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(username.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, R.string.email_sent, Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun loginUser()
    {
        val email = email_login.text.toString()
        val password = password_login.text.toString()

        when
        {
            TextUtils.isEmpty(email) -> Toast.makeText(this, R.string.enter_email, Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show()

            else -> {
                login_progress_bar?.visibility = View.VISIBLE
                login_background?.visibility = View.VISIBLE

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        login_progress_bar?.visibility = View.GONE
                        login_background?.visibility = View.GONE

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                        overridePendingTransition(R.anim.bottom_in, R.anim.none)
                    }
                    else
                    {
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()
                        login_progress_bar?.visibility = View.GONE
                        login_background?.visibility = View.GONE
                    }
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null)
        {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }
}