package com.megapixel.trashbench

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Model.Clubs
import com.megapixel.trashbench.Model.User
import kotlinx.android.synthetic.main.activity_club_info.*
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.fragment_club.view.*
import kotlinx.android.synthetic.main.activity_club_info.followings_total as followings_total1

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ClubInfoActivity : AppCompatActivity()
{

    private lateinit var clubId: String
    private lateinit var ownerId: String
    private lateinit var firebaseUser: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_info)


        overridePendingTransition(R.anim.pop_up, R.anim.pop_up)

        val intent = intent
        clubId = intent.getStringExtra("clubId")
        ownerId = intent.getStringExtra("ownerId")


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


        userInfo()
        ownerInfo()
        getJoiners()
    }


    private fun ownerInfo()
    {
        val clubsRef = FirebaseDatabase.getInstance().reference.child("Users").child(ownerId)

        clubsRef.addValueEventListener(object : ValueEventListener
        {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    owner_image.let {
                        Glide.with(applicationContext).load(user!!.getImage()).into(
                            it
                        )
                    }

                    owner_name?.text = user!!.getFullname()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun userInfo()
    {
        val clubsRef = FirebaseDatabase.getInstance().reference.child("Clubs").child(clubId)

        clubsRef.addValueEventListener(object : ValueEventListener
        {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val club = p0.getValue<Clubs>(Clubs::class.java)

                    name_club_frag?.text = club!!.getClubname()
                    description_club_frag?.text = club.getClubdescription()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun getJoiners()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Join").child(clubId)
            .child("Joined")


        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val totalJoiners: Int = p0.childrenCount.toInt()

                    when {
                        totalJoiners in 1000..999999 -> {
                            val a = totalJoiners/1000
                            val b = totalJoiners%1000
                            val c = b/100

                            followings_total.text = a.toString() + "." + c.toString() + "K"
                        }
                        totalJoiners > 999999 -> {
                            val a = totalJoiners/1000000
                            val b = totalJoiners%1000000
                            val c = b/100000

                            followings_total.text = a.toString() + "." + c.toString() + "M"
                        }
                        else -> {
                            followings_total.text = p0.childrenCount.toString()
                        }
                    }
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