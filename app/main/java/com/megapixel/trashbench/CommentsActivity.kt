package com.megapixel.trashbench

import android.content.pm.ActivityInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Adapter.CommentAdapter
import com.megapixel.trashbench.Model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CommentsActivity : AppCompatActivity()
{
    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter: CommentAdapter? = null
    private var commentList: MutableList<com.megapixel.trashbench.Model.Comment>? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val commentAnimRV = AnimationUtils.loadAnimation(this, R.anim.recycler_view_comments)


        val commentRV = findViewById<RecyclerView>(R.id.recycler_view_comments)


        commentRV.startAnimation(commentAnimRV)



        val intent = intent
        postId = intent.getStringExtra("postId")
        publisherId = intent.getStringExtra("publisherId")

        firebaseUser = FirebaseAuth.getInstance().currentUser


        var recyclerView: RecyclerView = findViewById(R.id.recycler_view_comments)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentAdapter(this, commentList)
        recyclerView.adapter = commentAdapter

        userInfo()
        readComments()
        getPostImage()

        post_comment.setOnClickListener(View.OnClickListener {
            if (add_comment!!.text.toString() == "")
            {
                Toast.makeText(this@CommentsActivity, R.string.write_comment_first, Toast.LENGTH_SHORT).show()
            }
            else
            {
                addComment()
            }
        })

        back_comments.setOnClickListener {
            onBackPressed()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addComment()
    {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId!!)

        val now = LocalDateTime.now()
        var formatter = DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")

        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = add_comment.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid
        commentsMap["time"] = formatter.format(now).toString()

        commentsRef.push().setValue(commentsMap)

        addNotification()

        add_comment!!.text.clear()
    }


    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Glide.with(applicationContext).load(user!!.getImage()).into(profile_image_comment)
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun getPostImage()
    {
        val postRef = FirebaseDatabase.getInstance()
            .reference.child("Posts")
            .child(postId!!).child("postimage")

        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val image = p0.value.toString()

                    Glide.with(applicationContext).load(image).into(post_image_comments)
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun readComments()
    {
        val commentsRef = FirebaseDatabase.getInstance()
            .reference.child("Comments")
            .child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    commentList!!.clear()

                    for (snapshot in p0.children)
                    {
                        val comment = snapshot.getValue(com.megapixel.trashbench.Model.Comment::class.java)
                        commentList!!.add(comment!!)
                    }

                    commentAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun addNotification()
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(publisherId!!)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "Commented your Content: " + add_comment.text.toString()
        notiMap["postid"] = postId
        notiMap["ispost"] = true

        notiRef.push().setValue(notiMap)
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