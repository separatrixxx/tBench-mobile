package com.megapixel.trashbench.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.CommentsActivity
import com.megapixel.trashbench.Fragments.OtherPeopleProfileFragment
import com.megapixel.trashbench.Model.Post
import com.megapixel.trashbench.Model.User
import com.megapixel.trashbench.R
import com.megapixel.trashbench.ShowUsersActivity
import com.megapixel.trashbench.Utils.firebaseUser
import com.megapixel.trashbench.Utils.isInCurrentYear
import com.megapixel.trashbench.Utils.isToday
import com.megapixel.trashbench.ViewFullImageActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class PostAdapter(
    private val mContext: Context,
    private val mPost: List<Post>
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    var currentUserId: String = ""

    var x: String = ""
    var y: String = ""
    var r: String = ""
    var o: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        val post = mPost[position]

        Glide.with(mContext.applicationContext).load(post.getPostimage()).into(holder.postImage)

        if (post.getDescription() == "") {
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = post.getDescription()
        }

        publisherInfo(holder.profileImage, holder.userName, holder.publisher, post.getPublisher())
        isLikes(post.getPostid(), holder.likeButton, holder.likes)
        numberOfLikes(holder.likes, post.getPostid())
        getTotalComments(holder.comments, post.getPostid())
        checkSavedStatus(post.getPostid(), holder.saveButton)

        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Like") {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .setValue(true)

                addNotification(post.getPublisher(), post.getPostid())
            } else {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .removeValue()

                deleteOfLikes(holder.likes)
            }
        }


        holder.linear1.setOnClickListener {

            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("profileId", post.getPublisher())
            editor.apply()

            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_profile, OtherPeopleProfileFragment()).commit()
        }


        holder.likes.setOnClickListener {
            val intent = Intent(mContext, ShowUsersActivity::class.java)
            intent.putExtra("id", post.getPostid())
            intent.putExtra("title", "likes")
            mContext.startActivity(intent)
        }


        holder.commentButton.setOnClickListener {
            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.postImage.setOnClickListener {
            val intent = Intent(mContext, ViewFullImageActivity::class.java)
            intent.putExtra("url", post.getPostimage())
            mContext.startActivity(intent)
        }

        holder.saveButton.setOnClickListener {
            if (holder.saveButton.tag == "Save") {
                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser!!.uid)
                    .child(post.getPostid())
                    .setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser!!.uid)
                    .child(post.getPostid())
                    .removeValue()
            }
        }


        holder.optionsPostButton.visibility = View.GONE

        if (post.getPublisher() == currentUserId) {
            holder.optionsPostButton.visibility = View.VISIBLE
        }

        if (post.getPublisher() != currentUserId) {
            holder.optionsOtherPostButton.visibility = View.VISIBLE
        }


        holder.optionsOtherPostButton.setOnClickListener {
            holder.optionsOtherPostButton.visibility = View.GONE
            holder.postProfile.visibility = View.VISIBLE
            holder.postSave.visibility = View.VISIBLE
            holder.postComments.visibility = View.VISIBLE
            holder.postLike.visibility = View.VISIBLE
            holder.cancelOtherBtn.visibility = View.VISIBLE
        }

        holder.cancelOtherBtn.setOnClickListener {
            holder.optionsOtherPostButton.visibility = View.VISIBLE
            holder.postProfile.visibility = View.GONE
            holder.postSave.visibility = View.GONE
            holder.postComments.visibility = View.GONE
            holder.postLike.visibility = View.GONE
            holder.cancelOtherBtn.visibility = View.GONE
        }


        holder.postProfile.setOnClickListener {
            holder.optionsOtherPostButton.visibility = View.VISIBLE
            holder.postProfile.visibility = View.GONE
            holder.postSave.visibility = View.GONE
            holder.postComments.visibility = View.GONE
            holder.postLike.visibility = View.GONE
            holder.cancelOtherBtn.visibility = View.GONE

            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("profileId", post.getPublisher())
            editor.apply()

            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, OtherPeopleProfileFragment()).commit()
        }

        holder.postSave.setOnClickListener {
            holder.optionsOtherPostButton.visibility = View.VISIBLE
            holder.postProfile.visibility = View.GONE
            holder.postSave.visibility = View.GONE
            holder.postComments.visibility = View.GONE
            holder.postLike.visibility = View.GONE
            holder.cancelOtherBtn.visibility = View.GONE

            if (holder.saveButton.tag == "Save") {
                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser!!.uid)
                    .child(post.getPostid())
                    .setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference
                    .child("Saves")
                    .child(firebaseUser!!.uid)
                    .child(post.getPostid())
                    .removeValue()
            }
        }

        holder.postComments.setOnClickListener {
            holder.optionsOtherPostButton.visibility = View.VISIBLE
            holder.postProfile.visibility = View.GONE
            holder.postSave.visibility = View.GONE
            holder.postComments.visibility = View.GONE
            holder.postLike.visibility = View.GONE
            holder.cancelOtherBtn.visibility = View.GONE

            val intentComment = Intent(mContext, CommentsActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.postLike.setOnClickListener {
            holder.optionsOtherPostButton.visibility = View.VISIBLE
            holder.postProfile.visibility = View.GONE
            holder.postSave.visibility = View.GONE
            holder.postComments.visibility = View.GONE
            holder.postLike.visibility = View.GONE
            holder.cancelOtherBtn.visibility = View.GONE

            if (holder.likeButton.tag == "Like") {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .setValue(true)

                addNotification(post.getPublisher(), post.getPostid())
            } else {
                FirebaseDatabase.getInstance().reference
                    .child("Likes")
                    .child(post.getPostid())
                    .child(firebaseUser!!.uid)
                    .removeValue()

                deleteOfLikes(holder.likes)
            }
        }


        val formatter = SimpleDateFormat("hh:mm a dd-MM-yyyy", Locale.getDefault())

        val date = formatter.parse(post.getTime())

        if (date.isToday()) {
            formatter.applyPattern("HH:mm")
        } else if (!date.isToday() && date.isInCurrentYear()
        ) {
            formatter.applyPattern("d MMMM,  HH:mm")
        } else {
            formatter.applyPattern("d MMMM yyyy,  HH:mm")
        }


        holder.time.text = formatter.format(date)


        holder.optionsPostButton.setOnClickListener {

            holder.optionsPostButton.visibility = View.GONE
            holder.deleteBtn.visibility = View.VISIBLE
            holder.cancelBtn.visibility = View.VISIBLE
        }

        holder.deleteBtn.setOnClickListener {
            holder.deleteBtn.visibility = View.GONE
            holder.confirmDeleteBtn.visibility = View.VISIBLE
        }

        holder.confirmDeleteBtn.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().reference
                .child("Posts")
                .child(post.getPostid())

            ref.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(mContext, R.string.deleted_content, Toast.LENGTH_SHORT).show()
                }
            }

            holder.optionsPostButton.visibility = View.VISIBLE
            holder.deleteBtn.visibility = View.GONE
            holder.cancelBtn.visibility = View.GONE
            holder.confirmDeleteBtn.visibility = View.GONE
        }

        holder.cancelBtn.setOnClickListener {
            holder.optionsPostButton.visibility = View.VISIBLE
            holder.deleteBtn.visibility = View.GONE
            holder.cancelBtn.visibility = View.GONE
            holder.confirmDeleteBtn.visibility = View.GONE
        }

        updateMP(holder.mp, holder.checkMark, holder.heart, holder.game, holder.gift, holder.face1, holder.face2, holder.face3, holder.crown, holder.burger, post.getPublisher())
    }


    private fun deleteOfLikes(likes: TextView) {
        likes.text = ""
    }


    private fun numberOfLikes(likes: TextView, postid: String) {
        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val totalLikes: Int = p0.childrenCount.toInt()

                    when {
                        totalLikes > 999 -> {
                            val a = totalLikes / 1000
                            val b = totalLikes % 1000
                            val c = b / 100

                            likes.text = a.toString() + "." + c.toString() + "K"
                        }
                        totalLikes > 999999 -> {
                            val a = totalLikes / 1000000
                            val b = totalLikes % 1000000
                            val c = b / 100000

                            likes.text = a.toString() + "." + c.toString() + "M"
                        }
                        else -> {
                            likes.text = p0.childrenCount.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun getTotalComments(comments: TextView, postid: String) {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postid)

        commentsRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val totalComments: Int = p0.childrenCount.toInt()

                    when {
                        totalComments in 1000..999999 -> {
                            val a = totalComments / 1000
                            val b = totalComments % 1000
                            val c = b / 100

                            comments.text = a.toString() + "." + c.toString() + "K"
                        }
                        totalComments > 999999 -> {
                            val a = totalComments / 1000000
                            val b = totalComments % 1000000
                            val c = b / 100000

                            comments.text = a.toString() + "." + c.toString() + "M"
                        }
                        else -> {
                            comments.text = p0.childrenCount.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun isLikes(postid: String, likeButton: ImageView, likes: TextView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val LikesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        LikesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()) {
                    likeButton.setImageResource(R.drawable.like_clicked)
                    likeButton.animate().scaleX(1.2f).scaleY(1.2f).duration
                    likeButton.tag = "Liked"
                    likes.setTextColor(Color.parseColor("#EE4444"))
                } else {
                    likeButton.setImageResource(R.drawable.like_not_clicked)
                    likeButton.animate().scaleX(1f).scaleY(1f).duration
                    likeButton.tag = "Like"
                    likes.setTextColor(Color.parseColor("#1C1C1C"))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var saveButton: ImageView
        var userName: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView
        var optionsPostButton: ImageView
        var optionsOtherPostButton: ImageView
        var linear1: LinearLayout
        var deleteBtn: ImageView
        var cancelBtn: ImageView
        var confirmDeleteBtn: ImageView
        var postProfile: ImageView
        var postSave: ImageView
        var postComments: ImageView
        var postLike: ImageView
        var cancelOtherBtn: ImageView
        var time: TextView
        var mp: ImageView
        var checkMark: ImageView
        var heart: ImageView
        var game: ImageView
        var gift: ImageView
        var face1: ImageView
        var face2: ImageView
        var face3: ImageView
        var crown: ImageView
        var burger: ImageView

        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage = itemView.findViewById(R.id.post_image_content)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)
            saveButton = itemView.findViewById(R.id.post_save_comment_btn)
            userName = itemView.findViewById(R.id.username_post)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)
            optionsPostButton = itemView.findViewById(R.id.options_post)
            optionsOtherPostButton = itemView.findViewById(R.id.options_post_other)
            linear1 = itemView.findViewById(R.id.linear1)
            deleteBtn = itemView.findViewById(R.id.post_delete)
            cancelBtn = itemView.findViewById(R.id.post_cancel)
            confirmDeleteBtn = itemView.findViewById(R.id.confirm_delete)
            postProfile = itemView.findViewById(R.id.post_profile)
            postSave = itemView.findViewById(R.id.post_save)
            postComments = itemView.findViewById(R.id.post_comments)
            postLike = itemView.findViewById(R.id.post_like)
            cancelOtherBtn = itemView.findViewById(R.id.post_cancel_other)
            time = itemView.findViewById(R.id.time)
            mp = itemView.findViewById(R.id.mp)
            checkMark = itemView.findViewById(R.id.check_mark)
            heart = itemView.findViewById(R.id.heart)
            game = itemView.findViewById(R.id.game)
            gift = itemView.findViewById(R.id.gift)
            face1 = itemView.findViewById(R.id.face1)
            face2 = itemView.findViewById(R.id.face2)
            face3 = itemView.findViewById(R.id.face3)
            crown = itemView.findViewById(R.id.crown)
            burger = itemView.findViewById(R.id.burger)
        }
    }

    private fun updateMP(mp: ImageView, checkMark: ImageView, heart: ImageView, game: ImageView, gift: ImageView, face1: ImageView, face2: ImageView, face3: ImageView, crown: ImageView, burger: ImageView, profileId: String) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)

                    if (user!!.getMP() == "megapixel") {
                        mp.visibility = View.VISIBLE
                    }

                    if (user.getMP() == "check mark") {
                        checkMark.visibility = View.VISIBLE
                    }

                    if (user.getEmoji() == "heart") {
                        heart.visibility = View.VISIBLE
                    }

                    if (user.getEmoji() == "game")
                    {
                        game.visibility = View.VISIBLE
                    }

                    if (user.getEmoji() == "gift")
                    {
                        gift.visibility = View.VISIBLE
                    }

                    if (user.getEmoji() == "face1")
                    {
                        face1.visibility = View.VISIBLE
                    }

                    if (user.getEmoji() == "face2")
                    {
                        face2.visibility = View.VISIBLE
                    }

                    if (user.getEmoji() == "face3")
                    {
                        face3.visibility = View.VISIBLE
                    }

                    if (user.getEmoji() == "crown")
                    {
                        crown.visibility = View.VISIBLE
                    }

                    if (user.getEmoji() == "burger")
                    {
                        burger.visibility = View.VISIBLE
                    }

                    if (user.getEmoji() == "")
                    {
                        game.visibility = View.GONE
                        heart.visibility = View.GONE
                        gift.visibility = View.GONE
                        face1.visibility = View.GONE
                        face2.visibility = View.GONE
                        face3.visibility = View.GONE
                        crown.visibility = View.GONE
                        burger.visibility = View.GONE
                    }

                    if (user.getMP() == "") {
                        mp.visibility = View.GONE
                        checkMark.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun publisherInfo(
        profileImage: CircleImageView,
        userName: TextView,
        publisher: TextView,
        publisherID: String)
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)

                    if (user!!.getDeleted() != "true")
                    {
                        Glide.with(mContext.applicationContext).load(user!!.getImage()).into(profileImage)
                    }
                    else
                    {
                        Glide.with(mContext.applicationContext).load(R.drawable.profile_deleted_image).into(profileImage)
                    }
                    userName.text = user!!.getUsername()
                    publisher.text = user!!.getFullname()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun checkSavedStatus(postid: String, imageView: ImageView) {
        val savesRef = FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseUser!!.uid)

        savesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.saved_image)
                    imageView.tag = "Saved"
                    imageView.animate().scaleX(1.2f).scaleY(1.2f).duration
                } else {
                    imageView.setImageResource(R.drawable.save_image)
                    imageView.tag = "Save"
                    imageView.animate().scaleX(1f).scaleY(1f).duration
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun addNotification(userId: String, postId: String) {
        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(userId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "Liked your Content"
        notiMap["postid"] = postId
        notiMap["ispost"] = true

        notiRef.push().setValue(notiMap)
    }
}