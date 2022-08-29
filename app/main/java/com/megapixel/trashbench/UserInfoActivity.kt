package com.megapixel.trashbench

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Model.Post
import com.megapixel.trashbench.Model.User
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.activity_user_info.birthday
import kotlinx.android.synthetic.main.activity_user_info.birthday_profile_frag
import kotlinx.android.synthetic.main.activity_user_info.city
import kotlinx.android.synthetic.main.activity_user_info.city_profile_frag
import kotlinx.android.synthetic.main.activity_user_info.specialty
import kotlinx.android.synthetic.main.activity_user_info.specialty_profile_frag
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.android.synthetic.main.fragment_profile.info_profile_frag as info_profile_frag1

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UserInfoActivity : AppCompatActivity() {

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        overridePendingTransition(R.anim.pop_up, R.anim.pop_up)

        val intent = intent
        profileId = intent.getStringExtra("profileId")


        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        window.setLayout((width*.9).toInt(), (height*.6).toInt())

        val params = window.attributes
        params.gravity = Gravity.CENTER_HORIZONTAL
        params.verticalMargin = 10f
        params.x = 0
        params.y = 0

        window.attributes = params




        total_followers_pop.setOnClickListener {
            val intent = Intent(this, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }


        total_followings_pop.setOnClickListener {
            val intent = Intent(this, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "following")
            startActivity(intent)
        }


        getFollowers()
        getFollowings()
        userInfo()
        getTotalNumberOfPosts()
    }

    private fun getFollowers()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")


        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val totalFollowers: Int = p0.childrenCount.toInt()

                    when {
                        totalFollowers > 999 -> {
                            val a = totalFollowers/1000
                            val b = totalFollowers%1000
                            val c = b/100

                            followers_total?.text = a.toString() + "." + c.toString() + "K"
                        }
                        totalFollowers > 999999 -> {
                            val a = totalFollowers/1000000
                            val b = totalFollowers%1000000
                            val c = b/100000

                            followers_total?.text = a.toString() + "." + c.toString() + "M"
                        }
                        else -> {
                            followers_total?.text = p0.childrenCount.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun getFollowings()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")


        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val totalFollowings: Int = p0.childrenCount.toInt()

                    when {
                        totalFollowings > 999 -> {
                            val a = totalFollowings/1000
                            val b = totalFollowings%1000
                            val c = b/100

                            followings_total?.text = a.toString() + "." + c.toString() + "K"
                        }
                        totalFollowings > 999999 -> {
                            val a = totalFollowings/1000000
                            val b = totalFollowings%1000000
                            val c = b/100000

                            followings_total?.text = a.toString() + "." + c.toString() + "M"
                        }
                        else -> {
                            followings_total?.text = p0.childrenCount.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    info_profile_frag?.text = user!!.getBio()
                    city_profile_frag?.text = user!!.getCity()
                    birthday_profile_frag?.text = user!!.getBirthday()
                    specialty_profile_frag?.text = user!!.getSpecialty()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    private fun getTotalNumberOfPosts()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener( object : ValueEventListener
        {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    var postCounter = 0

                    for (snapshot in dataSnapshot.children)
                    {
                        val post = snapshot.getValue(Post::class.java)!!

                        if (post.getPublisher() == profileId)
                        {
                            postCounter++
                        }
                    }
                    content_pop.text = "$postCounter"
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
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