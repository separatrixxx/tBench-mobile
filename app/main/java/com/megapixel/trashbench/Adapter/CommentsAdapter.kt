package com.megapixel.trashbench.Adapter

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Fragments.OtherPeopleProfileFragment
import com.megapixel.trashbench.Model.Comment
import com.megapixel.trashbench.Model.Post
import com.megapixel.trashbench.Model.User
import com.megapixel.trashbench.R
import com.megapixel.trashbench.Utils.isInCurrentYear
import com.megapixel.trashbench.Utils.isToday
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CommentAdapter(private val mContext: Context,
                     private val mComment: MutableList<Comment>?
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null

    var x: String = ""
    var y: String = ""
    var r: String = ""
    var o: String = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comments_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComment!!.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val comment = mComment!![position]
        holder.commentTV.text = comment.getComment()
        getuserInfo(holder.imageProfile, holder.fullNameTV, comment.getPublisher())


        holder.imageProfile.setOnClickListener {

            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("profileId", comment.getPublisher())
            editor.apply()

            (mContext as FragmentActivity).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_comments, OtherPeopleProfileFragment()).commit()
        }

        holder.fullNameTV.setOnClickListener {

            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("profileId", comment.getPublisher())
            editor.apply()

            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_comments, OtherPeopleProfileFragment()).commit()
        }

        val formatter = SimpleDateFormat("hh:mm a dd-MM-yyyy", Locale.getDefault())

        val date = formatter.parse(comment.getTime())

        if (date.isToday()) {
            formatter.applyPattern("HH:mm")
        } else if (!date.isToday() && date.isInCurrentYear()
        ) {
            formatter.applyPattern("d MMMM,  HH:mm")
        } else {
            formatter.applyPattern("d MMMM yyyy,  HH:mm")
        }


        holder.time.text = formatter.format(date)

        updateMP(holder.mp, holder.checkMark, holder.heart, holder.game, holder.gift, holder.face1, holder.face2, holder.face3, holder.crown, holder.burger, comment.getPublisher())
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var imageProfile: CircleImageView
        var fullNameTV: TextView
        var commentTV: TextView
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
            imageProfile = itemView.findViewById(R.id.user_profile_image_comment)
            fullNameTV = itemView.findViewById(R.id.full_name_comment)
            commentTV = itemView.findViewById(R.id.comment_comment)
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


    private fun updateMP(mp: ImageView, checkMark: ImageView, heart: ImageView, game: ImageView, gift: ImageView, face1: ImageView, face2: ImageView, face3: ImageView, crown: ImageView, burger: ImageView, profileId: String)
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

                    if (user!!.getMP() == "megapixel")
                    {
                        mp.visibility = View.VISIBLE
                    }

                    if (user.getMP() == "check mark")
                    {
                        checkMark.visibility = View.VISIBLE
                    }

                    if (user.getEmoji() == "heart")
                    {
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

                    if (user.getMP() == "")
                    {
                        mp.visibility = View.GONE
                        checkMark.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    private fun getuserInfo(imageProfile: CircleImageView, fullNameTV: TextView, publisher: String)
    {
        val userRef = FirebaseDatabase.getInstance()
            .reference.child("Users")
            .child(publisher)

        userRef.addValueEventListener(object  : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue(User::class.java)

                    if (user!!.getDeleted() != "true")
                    {
                        Glide.with(mContext.applicationContext).load(user!!.getImage()).into(imageProfile)
                    }
                    else
                    {
                        Glide.with(mContext.applicationContext).load(R.drawable.profile_deleted_image).into(imageProfile)
                    }

                    fullNameTV.text = user!!.getFullname()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }
}