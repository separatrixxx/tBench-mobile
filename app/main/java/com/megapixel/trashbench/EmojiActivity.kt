package com.megapixel.trashbench

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Model.User
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_emoji.*
import kotlinx.android.synthetic.main.activity_story.*

class EmojiActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emoji)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        back_emoji.setOnClickListener {
            onBackPressed()
        }

        updateMP()

        heart_emoji.setOnClickListener {
            updateHeart()
        }

        heart_emoji_set.setOnClickListener {
            updateNull()
        }

        game_emoji.setOnClickListener {
            updateGame()
        }

        game_emoji_set.setOnClickListener {
            updateNull()
        }

        gift_emoji.setOnClickListener {
            updateGift()
        }

        gift_emoji_set.setOnClickListener {
            updateNull()
        }

        face1_emoji.setOnClickListener {
            updateFace1()
        }

        face1_emoji_set.setOnClickListener {
            updateNull()
        }

        face2_emoji.setOnClickListener {
            updateFace2()
        }

        face2_emoji_set.setOnClickListener {
            updateNull()
        }

        face3_emoji.setOnClickListener {
            updateFace3()
        }

        face3_emoji_set.setOnClickListener {
            updateNull()
        }

        crown_emoji.setOnClickListener {
            updateCrown()
        }

        crown_emoji_set.setOnClickListener {
            updateNull()
        }

        burger_emoji.setOnClickListener {
            updateBurger()
        }

        burger_emoji_set.setOnClickListener {
            updateNull()
        }
    }

    private fun updateNull() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["emoji"] = ""

        usersRef.child(firebaseUser!!.uid).updateChildren(userMap)
    }

    private fun updateHeart() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["emoji"] = "heart"

        usersRef.child(firebaseUser!!.uid).updateChildren(userMap)
    }

    private fun updateGame() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["emoji"] = "game"

        usersRef.child(firebaseUser!!.uid).updateChildren(userMap)
    }

    private fun updateGift() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["emoji"] = "gift"

        usersRef.child(firebaseUser!!.uid).updateChildren(userMap)
    }

    private fun updateFace1() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["emoji"] = "face1"

        usersRef.child(firebaseUser!!.uid).updateChildren(userMap)
    }

    private fun updateFace2() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["emoji"] = "face2"

        usersRef.child(firebaseUser!!.uid).updateChildren(userMap)
    }

    private fun updateFace3() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["emoji"] = "face3"

        usersRef.child(firebaseUser!!.uid).updateChildren(userMap)
    }

    private fun updateCrown() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["emoji"] = "crown"

        usersRef.child(firebaseUser!!.uid).updateChildren(userMap)
    }

    private fun updateBurger() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["emoji"] = "burger"

        usersRef.child(firebaseUser!!.uid).updateChildren(userMap)
    }

    private fun updateMP()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    if (user!!.getEmoji() == "heart")
                    {
                        if (heart_emoji != null && heart_emoji_set != null && game_emoji != null && game_emoji_set != null)
                        {
                            heart_emoji.visibility = View.GONE
                            heart_emoji_set.visibility = View.VISIBLE
                        }
                    }

                    if (user!!.getEmoji() != "heart")
                    {
                        if (heart_emoji != null && heart_emoji_set != null && game_emoji != null && game_emoji_set != null)
                        {
                            heart_emoji.visibility = View.VISIBLE
                            heart_emoji_set.visibility = View.GONE
                        }
                    }

                    if (user!!.getEmoji() == "game")
                    {
                        if (game_emoji != null && game_emoji_set != null)
                        {
                            game_emoji.visibility = View.GONE
                            game_emoji_set.visibility = View.VISIBLE
                        }
                    }

                    if (user!!.getEmoji() != "game")
                    {
                        if (game_emoji != null && game_emoji_set != null)
                        {
                            game_emoji.visibility = View.VISIBLE
                            game_emoji_set.visibility = View.GONE
                        }
                    }

                    if (user!!.getEmoji() == "gift")
                    {
                        if (gift_emoji != null && gift_emoji_set != null)
                        {
                            gift_emoji.visibility = View.GONE
                            gift_emoji_set.visibility = View.VISIBLE
                        }
                    }

                    if (user!!.getEmoji() != "gift")
                    {
                        if (gift_emoji != null && gift_emoji_set != null)
                        {
                            gift_emoji.visibility = View.VISIBLE
                            gift_emoji_set.visibility = View.GONE
                        }
                    }

                    if (user!!.getEmoji() == "face1")
                    {
                        if (face1_emoji != null && face1_emoji_set != null)
                        {
                            face1_emoji.visibility = View.GONE
                            face1_emoji_set.visibility = View.VISIBLE
                        }
                    }

                    if (user!!.getEmoji() != "face1")
                    {
                        if (face1_emoji != null && face1_emoji_set != null)
                        {
                            face1_emoji.visibility = View.VISIBLE
                            face1_emoji_set.visibility = View.GONE
                        }
                    }

                    if (user!!.getEmoji() == "face2")
                    {
                        if (face2_emoji != null && face2_emoji_set != null)
                        {
                            face2_emoji.visibility = View.GONE
                            face2_emoji_set.visibility = View.VISIBLE
                        }
                    }

                    if (user!!.getEmoji() != "face2")
                    {
                        if (face2_emoji != null && face2_emoji_set != null)
                        {
                            face2_emoji.visibility = View.VISIBLE
                            face2_emoji_set.visibility = View.GONE
                        }
                    }

                    if (user!!.getEmoji() == "face3")
                    {
                        if (face3_emoji != null && face3_emoji_set != null)
                        {
                            face3_emoji.visibility = View.GONE
                            face3_emoji_set.visibility = View.VISIBLE
                        }
                    }

                    if (user!!.getEmoji() != "face3")
                    {
                        if (face3_emoji != null && face3_emoji_set != null)
                        {
                            face3_emoji.visibility = View.VISIBLE
                            face3_emoji_set.visibility = View.GONE
                        }
                    }

                    if (user!!.getEmoji() == "crown")
                    {
                        if (crown_emoji != null && crown_emoji_set != null)
                        {
                            crown_emoji.visibility = View.GONE
                            crown_emoji_set.visibility = View.VISIBLE
                        }
                    }

                    if (user!!.getEmoji() != "crown")
                    {
                        if (crown_emoji != null && crown_emoji_set != null)
                        {
                            crown_emoji.visibility = View.VISIBLE
                            crown_emoji_set.visibility = View.GONE
                        }
                    }

                    if (user!!.getEmoji() == "burger")
                    {
                        if (burger_emoji != null && burger_emoji_set != null)
                        {
                            burger_emoji.visibility = View.GONE
                            burger_emoji_set.visibility = View.VISIBLE
                        }
                    }

                    if (user!!.getEmoji() != "burger")
                    {
                        if (burger_emoji != null && burger_emoji_set != null)
                        {
                            burger_emoji.visibility = View.VISIBLE
                            burger_emoji_set.visibility = View.GONE
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