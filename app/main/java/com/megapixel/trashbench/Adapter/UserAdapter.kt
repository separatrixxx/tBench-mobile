package com.megapixel.trashbench.Adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
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
import com.megapixel.trashbench.MainActivity
import com.megapixel.trashbench.MessagesChatActivity
import com.megapixel.trashbench.Model.User
import com.megapixel.trashbench.R
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter (private var mContext: Context,
                   private var mUser: List<User>,
                   isChatCheck: Boolean,
                   private var isFragment: Boolean = true) : RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var isChatCheck: Boolean = isChatCheck

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]

        holder.userNameTextView.text = user.getUsername()
        holder.userFullnameTextView.text = user.getFullname()

        if (user.getDeleted() != "true")
        {
            Glide.with(mContext.applicationContext).load(user.getImage()).into(holder.userProfileImage)
        }
        else
        {
            Glide.with(mContext.applicationContext).load(R.drawable.profile_deleted_image).into(holder.userProfileImage)
        }

        checkFollowingStatus(user.getUID(), holder.followButton, holder.followingButton)


        if (isChatCheck && user.getDeleted() != "true")
        {
            if (user.getStatus() == "online")
            {
                holder.onlineImageView.visibility = View.VISIBLE
            }
            else
            {
                holder.onlineImageView.visibility = View.GONE
            }
        }
        else
        {
            holder.onlineImageView.visibility = View.GONE
        }


        holder.userProfileImage.setOnClickListener(View.OnClickListener {
            if (isFragment)
            {
                val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId", user.getUID())
                pref.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_profile, OtherPeopleProfileFragment()).commit()
            }
            else
            {
                val intent = Intent(mContext, MainActivity::class.java)
                intent.putExtra("publisherId", user.getUID())
                mContext.startActivity(intent)
            }
        })


        holder.userFullnameTextView.setOnClickListener(View.OnClickListener {
            if (isFragment)
            {
                val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId", user.getUID())
                pref.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_profile, OtherPeopleProfileFragment()).commit()
            }
            else
            {
                val intent = Intent(mContext, MainActivity::class.java)
                intent.putExtra("publisherId", user.getUID())
                mContext.startActivity(intent)
            }
        })


        holder.userNameTextView.setOnClickListener(View.OnClickListener {
            if (isFragment)
            {
                val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId", user.getUID())
                pref.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_profile, OtherPeopleProfileFragment()).commit()
            }
            else
            {
                val intent = Intent(mContext, MainActivity::class.java)
                intent.putExtra("publisherId", user.getUID())
                mContext.startActivity(intent)
            }
        })


        holder.linearUser.setOnClickListener(View.OnClickListener {
            if (isFragment)
            {
                val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId", user.getUID())
                pref.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_profile, OtherPeopleProfileFragment()).commit()
            }
            else
            {
                val intent = Intent(mContext, MainActivity::class.java)
                intent.putExtra("publisherId", user.getUID())
                mContext.startActivity(intent)
            }
        })


        holder.userMessages.setOnClickListener {
            val intent = Intent(mContext, MessagesChatActivity::class.java)
            intent.putExtra("userId", user.getUID())
            mContext.startActivity(intent)
        }


        holder.followButton.setOnClickListener {

                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUID())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }

            holder.followingButton.visibility = View.VISIBLE
            holder.followButton.visibility = View.GONE

                addNotification(user.getUID())
        }

        holder.followingButton.setOnClickListener {

            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(it1.toString())
                    .child("Following").child(user.getUID())
                    .removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseUser?.uid.let { it1 ->
                                FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(user.getUID())
                                    .child("Followers").child(it1.toString())
                                    .removeValue().addOnCompleteListener { task ->
                                        if (task.isSuccessful) {

                                        }
                                    }
                            }
                        }
                    }
            }

            holder.followingButton.visibility = View.GONE
            holder.followButton.visibility = View.VISIBLE
        }

        updateMP(holder.mp, holder.checkMark, holder.heart, holder.game, holder.gift, holder.face1, holder.face2, holder.face3, holder.crown, holder.burger, user.getUID())
    }


    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var userProfileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
        var userFullnameTextView: TextView = itemView.findViewById(R.id.user_full_name_search)
        var userNameTextView: TextView = itemView.findViewById(R.id.user_name_search)
        var followButton: ImageButton = itemView.findViewById(R.id.follow_btn_search)
        var followingButton: ImageButton = itemView.findViewById(R.id.following_btn_search)
        var userMessages: ImageButton = itemView.findViewById(R.id.user_messages)
        var linearUser: LinearLayout = itemView.findViewById(R.id.linear_user)
        var lastMessageTxt: TextView = itemView.findViewById(R.id.message_last)
        var onlineImageView: CircleImageView = itemView.findViewById(R.id.image_online)
        var mp: ImageView = itemView.findViewById(R.id.mp)
        var checkMark: ImageView = itemView.findViewById(R.id.check_mark)
        var heart: ImageView = itemView.findViewById(R.id.heart)
        var game: ImageView = itemView.findViewById(R.id.game)
        var gift: ImageView = itemView.findViewById(R.id.gift)
        var face1: ImageView = itemView.findViewById(R.id.face1)
        var face2: ImageView = itemView.findViewById(R.id.face2)
        var face3: ImageView = itemView.findViewById(R.id.face3)
        var crown: ImageView = itemView.findViewById(R.id.crown)
        var burger: ImageView = itemView.findViewById(R.id.burger)
        var timeMessage: TextView = itemView.findViewById(R.id.message_time)
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


    private fun checkFollowingStatus(uid: String, followButton: ImageButton, followingButton: ImageButton)
    {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (dataSnapshot.child(uid).exists())
                {
                    followingButton.visibility = View.VISIBLE
                    followButton.visibility = View.GONE
                }
                else
                {
                    followingButton.visibility = View.GONE
                    followButton.visibility = View.VISIBLE
                }


            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun addNotification(userId: String)
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(userId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "Started following you"
        notiMap["postid"] = ""
        notiMap["ispost"] = false

        notiRef.push().setValue(notiMap)
    }

}