package com.megapixel.trashbench

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Model.Story
import com.megapixel.trashbench.Model.User
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.activity_messages_chat.*
import kotlinx.android.synthetic.main.activity_story.*
import kotlinx.android.synthetic.main.activity_story.burger
import kotlinx.android.synthetic.main.activity_story.check_mark
import kotlinx.android.synthetic.main.activity_story.crown
import kotlinx.android.synthetic.main.activity_story.face1
import kotlinx.android.synthetic.main.activity_story.face2
import kotlinx.android.synthetic.main.activity_story.face3
import kotlinx.android.synthetic.main.activity_story.mp
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.game
import kotlinx.android.synthetic.main.fragment_profile.gift
import kotlinx.android.synthetic.main.fragment_profile.heart
import java.util.HashMap
import kotlinx.android.synthetic.main.activity_story.game as game1
import kotlinx.android.synthetic.main.activity_story.gift as gift1

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener
{
    var currentUserId: String = ""
    var userId: String = ""
    var counter = 0
    var pressTime = 0L
    var limit = 500L

    var imagesList: List<String>? = null
    var storyIdsList: List<String>? = null

    var storiesProgressView: StoriesProgressView? = null

    var firebaseUser: FirebaseUser? = null
    
    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { view, motionEvent ->

        when(motionEvent.action)
        {
            MotionEvent.ACTION_DOWN ->
            {
                pressTime = System.currentTimeMillis()
                storiesProgressView!!.pause()
                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP ->
            {
                val now = System.currentTimeMillis()
                storiesProgressView!!.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }

        false
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        userId = intent.getStringExtra("userId")

        storiesProgressView = findViewById(R.id.stories_progress)


        layout_seen.visibility = View.GONE
        options_story.visibility = View.GONE

        if (userId == currentUserId)
        {
            layout_seen.visibility = View.VISIBLE
            options_story.visibility = View.VISIBLE
        }

        getStories(userId!!)
        userInfo(userId!!)


        val reverse: View = findViewById(R.id.reverse)
        reverse.setOnClickListener { storiesProgressView!!.reverse() }
        reverse.setOnTouchListener(onTouchListener)

        val skip: View = findViewById(R.id.skip)
        skip.setOnClickListener { storiesProgressView!!.skip() }
        skip.setOnTouchListener(onTouchListener)


        layout_seen.setOnClickListener{
            val intent = Intent(this@StoryActivity, ShowUsersActivity::class.java)
            intent.putExtra("id", userId)
            intent.putExtra("storyid", storyIdsList!![counter])
            intent.putExtra("title", "views")
            startActivity(intent)
        }


        options_story.setOnClickListener {
            story_delete.visibility = View.VISIBLE
            story_cancel.visibility = View.VISIBLE
            options_story.visibility = View.GONE
        }

        story_cancel.setOnClickListener {
            story_delete.visibility = View.GONE
            story_cancel.visibility = View.GONE
            options_story.visibility = View.VISIBLE
            confirm_delete.visibility = View.GONE
        }

        story_delete.setOnClickListener {
            story_delete.visibility = View.GONE
            confirm_delete.visibility = View.VISIBLE
        }

        confirm_delete.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().reference
                .child("Story")
                .child(userId!!)
                .child(storyIdsList!![counter])

            ref.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(this@StoryActivity, R.string.deleted_story, Toast.LENGTH_SHORT).show()
                }
            }
            story_delete.visibility = View.GONE
            story_cancel.visibility = View.GONE
            options_story.visibility = View.VISIBLE
            confirm_delete.visibility = View.GONE
        }

        updateMP()
    }

    private fun updateMP()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    if (user!!.getMP() == "megapixel")
                    {
                        if (mp != null)
                        {
                            mp.visibility = View.VISIBLE
                        }
                    }

                    if (user.getMP() == "check mark")
                    {
                        if (check_mark != null)
                        {
                            check_mark.visibility = View.VISIBLE
                        }
                    }

                    if (user.getEmoji() == "heart")
                    {
                        if (heart != null)
                        {
                            heart.visibility = View.VISIBLE
                        }
                    }

                    if (heart != null && user.getEmoji() == "")
                    {
                        if (heart != null)
                        {
                            heart.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "game")
                    {
                        if (game != null)
                        {
                            game.visibility = View.VISIBLE
                        }
                    }

                    if (game != null && user.getEmoji() == "")
                    {
                        if (game != null)
                        {
                            game.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "gift")
                    {
                        if (gift != null)
                        {
                            gift.visibility = View.VISIBLE
                        }
                    }

                    if (gift != null && user.getEmoji() == "")
                    {
                        if (gift != null)
                        {
                            gift.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "face1")
                    {
                        if (face1 != null)
                        {
                            face1.visibility = View.VISIBLE
                        }
                    }

                    if (face1 != null && user.getEmoji() == "")
                    {
                        if (face1 != null)
                        {
                            face1.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "face2")
                    {
                        if (face2 != null)
                        {
                            face2.visibility = View.VISIBLE
                        }
                    }

                    if (face2 != null && user.getEmoji() == "")
                    {
                        if (face2 != null)
                        {
                            face2.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "face3")
                    {
                        if (face3 != null)
                        {
                            face3.visibility = View.VISIBLE
                        }
                    }

                    if (face3 != null && user.getEmoji() == "")
                    {
                        if (face3 != null)
                        {
                            face3.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "crown")
                    {
                        if (crown != null)
                        {
                            crown.visibility = View.VISIBLE
                        }
                    }

                    if (crown != null && user.getEmoji() == "")
                    {
                        if (crown != null)
                        {
                            crown.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "burger")
                    {
                        if (burger != null)
                        {
                            burger.visibility = View.VISIBLE
                        }
                    }

                    if (burger != null && user.getEmoji() == "")
                    {
                        if (burger != null)
                        {
                            burger.visibility = View.GONE
                        }
                    }

                    if (mp != null && user.getMP() == "")
                    {
                        if (check_mark != null)
                        {
                            mp.visibility = View.GONE
                            check_mark.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun getStories(userId: String)
    {
        imagesList = ArrayList()
        storyIdsList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!)

        ref.addValueEventListener(object : ValueEventListener
        {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot)
            {
                (imagesList as ArrayList<String>).clear()
                (storyIdsList as ArrayList<String>).clear()

                for (snapshot in p0.children)
                {
                    val story: Story? = snapshot.getValue<Story>(Story::class.java)
                    val timeCurrent = System.currentTimeMillis()

                    if (timeCurrent > story!!.getTimeStart() && timeCurrent < story.getTimeEnd())
                    {
                        (imagesList as ArrayList<String>).add(story.getImageUrl())
                        (storyIdsList as ArrayList<String>).add(story.getStoryId())
                    }
                }

                storiesProgressView!!.setStoriesCount((imagesList as ArrayList<String>).size)
                storiesProgressView!!.setStoryDuration(5000L)
                storiesProgressView!!.setStoriesListener(this@StoryActivity)
                storiesProgressView!!.startStories(counter)
                Glide.with(applicationContext).load(imagesList!!.get(counter)).into(image_story)

                addViewToStory(storyIdsList!!.get(counter))
                seenNumber(storyIdsList!!.get(counter))
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun userInfo(userId: String)
    {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(userId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    if (user!!.getDeleted() != "true")
                    {
                        Glide.with(applicationContext).load(user.getImage()).into(story_profile_image)
                    }
                    else
                    {
                        Glide.with(applicationContext).load(R.drawable.profile_deleted_image).into(story_profile_image)
                    }

                    story_full_name.text = user.getFullname()
                    story_username.text = user.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun addViewToStory(storyId: String)
    {

        FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!)
            .child(storyId)
            .child("views")
            .child(currentUserId)
            .setValue(true)
    }


    private fun seenNumber(storyId: String)
    {
        val ref = FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!)
            .child(storyId)
            .child("views")

        ref.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                val totalViews: Int = p0.childrenCount.toInt()

                when {
                    totalViews > 999 -> {
                        val a = totalViews/1000
                        val b = totalViews%1000
                        val c = b/100

                        seen_number.text = a.toString() + "." + c.toString() + "K"
                    }
                    totalViews > 999999 -> {
                        val a = totalViews/1000000
                        val b = totalViews%1000000
                        val c = b/100000

                        seen_number.text = a.toString() + "." + c.toString() + "M"
                    }
                    else -> {
                        seen_number.text = p0.childrenCount.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    override fun onComplete()
    {
        finish()
    }

    override fun onPrev()
    {
        if (counter - 1 < 0) return
        Picasso.get().load(imagesList!![--counter]).fit().into(image_story)
        seenNumber(storyIdsList!![counter])
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNext()
    {
        Glide.with(applicationContext).load(imagesList!![++counter]).into(image_story)
        addViewToStory(storyIdsList!![counter])
        seenNumber(storyIdsList!![counter])
    }

    override fun onDestroy() {
        super.onDestroy()
        storiesProgressView!!.destroy()
    }

    override fun onResume() {
        super.onResume()
        storiesProgressView!!.resume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        storiesProgressView!!.pause()
        updateStatus("offline")
    }


    private fun updateStatus(status: String)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref!!.updateChildren(hashMap)
    }
}