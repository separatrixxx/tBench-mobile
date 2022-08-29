package com.megapixel.trashbench

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Adapter.PagerAdapter
import com.megapixel.trashbench.Fragments.NotificationsFragment
import com.megapixel.trashbench.Fragments.ProfileFragment
import com.megapixel.trashbench.Fragments.SearchFragment
import com.megapixel.trashbench.Model.User
import com.megapixel.trashbench.Utils.firebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {


    private lateinit var random: Random
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private var bool = ""

    private var back_pressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        semicircle?.visibility = View.VISIBLE
        circle?.visibility = View.GONE
        circle_small?.visibility = View.GONE
        circle_search?.visibility = View.GONE
        circle_add_post?.visibility = View.GONE
        circle_notifications?.visibility = View.GONE
        smooth_bg?.visibility = View.GONE


        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        initViewPager2()

        bool = "2"

        random = Random()
        handler = Handler()

        swipeRefreshLayout.setOnRefreshListener {
            runnable = Runnable {
                swipeRefreshLayout.isRefreshing = false
            }

            handler.postDelayed(
                runnable, 500.toLong()
            )

            moveToMainActivity()
        }

        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_orange_dark,
            android.R.color.holo_red_light
        )

        val srcBtn: ImageView = findViewById(R.id.search_content_btn)
        val ntfBtn: ImageView = findViewById(R.id.notifications_content_btn)

        val sc: Button = findViewById(R.id.semicircle)
        val crc: Button = findViewById(R.id.circle)
        val crc_small: Button = findViewById(R.id.circle_small)
        val crc_src: Button = findViewById(R.id.circle_search)
        val crc_ntf: Button = findViewById(R.id.circle_notifications)
        val crc_add: Button = findViewById(R.id.circle_add_post)


        val crcIn = AnimationUtils.loadAnimation(this, R.anim.circle_in)

        val circleLayout: RelativeLayout = findViewById(R.id.circle_layout)


        sc.setOnClickListener {

            sc?.visibility = View.GONE
            crc?.visibility = View.VISIBLE
            crc_small?.visibility = View.VISIBLE
            crc_src?.visibility = View.VISIBLE
            crc_ntf?.visibility = View.VISIBLE
            crc_add?.visibility = View.VISIBLE
            smooth_bg?.visibility = View.VISIBLE

            circleLayout.startAnimation(crcIn)
        }

        crc_small.setOnClickListener {

            sc?.visibility = View.VISIBLE
            crc?.visibility = View.GONE
            crc_small?.visibility = View.GONE
            crc_src?.visibility = View.GONE
            crc_ntf?.visibility = View.GONE
            crc_add?.visibility = View.GONE
            smooth_bg?.visibility = View.GONE

            sc.startAnimation(crcIn)
        }


        srcBtn.setOnClickListener {
            moveToFragmentSearch(SearchFragment())
            srcBtn.setImageResource(R.drawable.search_set)
            ntfBtn.setImageResource(R.drawable.notifications)
            profile_content_btn.visibility = View.GONE
            visibleButtons()
        }

        ntfBtn.setOnClickListener {
            moveToFragmentNotifications(NotificationsFragment())
            srcBtn.setImageResource(R.drawable.search)
            ntfBtn.setImageResource(R.drawable.notifications_set)
            profile_content_btn.visibility = View.GONE
            visibleButtons()
        }

        crc_src.setOnClickListener {
            moveToFragmentSearch(SearchFragment())
            srcBtn.setImageResource(R.drawable.search_set)
            ntfBtn.setImageResource(R.drawable.notifications)
            profile_content_btn.visibility = View.GONE
            visibleButtons()
        }

        crc_ntf.setOnClickListener {
            moveToFragmentNotifications(NotificationsFragment())
            srcBtn.setImageResource(R.drawable.search)
            ntfBtn.setImageResource(R.drawable.notifications_set)
            profile_content_btn.visibility = View.GONE
            visibleButtons()
        }

        crc_add.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
            overridePendingTransition(R.anim.bottom_in, R.anim.none)
            visibleButtons()
        }


        profile_content_btn.setOnClickListener {
            moveToFragmentProfile(ProfileFragment())
            srcBtn.setImageResource(R.drawable.search)
            ntfBtn.setImageResource(R.drawable.notifications)
            profile_content_btn.visibility = View.GONE
            visibleButtons()
        }


        userInfo()


    }



    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    if (user != null) {
                        Glide.with(applicationContext).load(user.getImage()).into(profile_content_btn)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun visibleButtons() {
        semicircle?.visibility = View.GONE
        circle?.visibility = View.GONE
        circle_small?.visibility = View.GONE
        circle_search?.visibility = View.GONE
        circle_notifications?.visibility = View.GONE
        circle_add_post?.visibility = View.GONE
        smooth_bg?.visibility = View.GONE
    }



    private fun initViewPager2() {

        var viewPager: ViewPager2 = findViewById(R.id.view_pager)
        var adapter = PagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
    }


    private fun moveToFragmentProfile(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container_profile, fragment)
        fragmentTrans.commit()

        notifications_content_btn?.visibility = View.GONE
        search_content_btn?.visibility = View.GONE
    }


    private fun moveToFragmentSearch(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container_profile, fragment)
        fragmentTrans.commit()

        bool = "3"

        notifications_content_btn?.visibility = View.VISIBLE
        search_content_btn?.visibility = View.VISIBLE
    }

    private fun moveToFragmentNotifications(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container_profile, fragment)
        fragmentTrans.commit()

        bool = "4"

        notifications_content_btn?.visibility = View.VISIBLE
        search_content_btn?.visibility = View.VISIBLE
    }

    private fun moveToMainActivity()
    {
        startActivity(intent)
        finish()
    }

    override fun onBackPressed()
    {
        if (back_pressed + 500 > System.currentTimeMillis()) finish()
        else moveToMainActivity()

        back_pressed = System.currentTimeMillis()
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

    override fun onStart() {
        super.onStart()

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    if (user!!.getDeleted() == "true")
                    {
                        val intent = Intent(this@MainActivity, RecoveryAccountActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }
}