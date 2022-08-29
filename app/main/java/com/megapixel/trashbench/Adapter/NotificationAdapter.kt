package com.megapixel.trashbench.Adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Fragments.NotificationsPostDetailsFragment
import com.megapixel.trashbench.Fragments.OtherPeopleProfileFragment
import com.megapixel.trashbench.Fragments.PostDetailsFragment
import com.megapixel.trashbench.Model.NotificationModel
import com.megapixel.trashbench.Model.Post
import com.megapixel.trashbench.Model.User
import com.megapixel.trashbench.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NotificationAdapter(private val mContext: Context,
                          private val mNotification: List<NotificationModel>)
    : RecyclerView.Adapter<NotificationAdapter.ViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notifications_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNotification.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val notification = mNotification[position]


        if (notification.getText().equals("Started following you"))
        {
           holder.text.text = "Started following you"
        }
        else if (notification.getText().equals("Liked your Content"))
        {
            holder.text.text = "Liked your Content"
        }
        else if (notification.getText().contains("Commented your Content:"))
        {
            holder.text.text = notification.getText().replace("Commented your Content:","Commented your Content: ")
        }
        else
        {
            holder.text.text = notification.getText()
        }


        userInfo(holder.profileImage, holder.fullNameTV, notification.getUserId())


        if (notification.isIsPost())
        {
            holder.postImage.visibility = View.VISIBLE
            holder.imageCV.visibility = View.VISIBLE
            getPostImage(holder.postImage, notification.getPostId())
        }
        else
        {
            holder.postImage.visibility = View.GONE
            holder.imageCV.visibility = View.GONE
        }


        holder.itemView.setOnClickListener {
            if (notification.isIsPost())
            {
                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

                editor.putString("postId", notification.getPostId())
                editor.apply()

                (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container_new, NotificationsPostDetailsFragment()).commit()


            }
            else
            {
                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

                editor.putString("profileId", notification.getUserId())
                editor.apply()

                (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container_new, OtherPeopleProfileFragment()).commit()
            }
        }

        updateMP(holder.mp, holder.checkMark, holder.heart, holder.game, holder.gift, holder.face1, holder.face2, holder.face3, holder.crown, holder.burger, notification.getUserId())
    }



    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var postImage: ImageView
        var profileImage: CircleImageView
        var fullNameTV: TextView
        var text: TextView
        var imageCV: CardView
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
            postImage = itemView.findViewById(R.id.notification_post_image)
            profileImage = itemView.findViewById(R.id.notification_profile_image)
            fullNameTV = itemView.findViewById(R.id.full_name_notification)
            text = itemView.findViewById(R.id.comment_notification)
            imageCV = itemView.findViewById(R.id.notification_post_image_card_view)
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


    private fun userInfo(imageView: ImageView, fullName: TextView, publisherId: String)
    {
        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(publisherId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Glide.with(mContext.applicationContext).load(user!!.getImage()).into(imageView)
                    fullName.text = user!!.getFullname()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun getPostImage(imageView: ImageView, postID: String)
    {
        val postRef = FirebaseDatabase.getInstance()
            .reference.child("Posts")
            .child(postID)

        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val post = p0.getValue<Post>(Post::class.java)

                    Glide.with(mContext.applicationContext).load(post!!.getPostimage()).into(imageView)
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }
}