package com.megapixel.trashbench.Adapter

import android.content.Context
import android.content.Intent
import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.ClubsActivity
import com.megapixel.trashbench.Fragments.ClubFragment
import com.megapixel.trashbench.Fragments.OtherPeopleProfileFragment
import com.megapixel.trashbench.Fragments.PostDetailsFragment
import com.megapixel.trashbench.MainActivity
import com.megapixel.trashbench.Model.Clubs
import com.megapixel.trashbench.R
import com.mikepenz.materialize.util.KeyboardUtil.hideKeyboard
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_club.view.*

class ClubsAdapter(private var mContext: Context,
                   private var mClub: List<Clubs>) : RecyclerView.Adapter<ClubsAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubsAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.club_item_layout, parent, false)
        return ClubsAdapter.ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mClub.size
    }

    override fun onBindViewHolder(holder: ClubsAdapter.ViewHolder, position: Int) {
        val club = mClub[position]

        holder.clubName.text = club.getClubname()
        Glide.with(mContext.applicationContext).load(club.getClubimage()).into(holder.clubImage)


        holder.clubLayout.setOnClickListener {

            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("joinerId", club.getOwnerId())
            editor.putString("clubId", club.getIdClub())
            editor.apply()


            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.clubs_container, ClubFragment()).commit()


        }


        holder.joinClub.setOnClickListener {
            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(it1.toString())
                    .child("Joined").child(club.getIdClub())
                    .setValue(true)
            }

            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(club.getIdClub())
                    .child("Joined").child(it1.toString())
                    .setValue(true)
            }

            holder.joinClub.visibility = View.GONE
            holder.joinedClub.visibility = View.VISIBLE
        }


        holder.joinedClub.setOnClickListener {
            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(it1.toString())
                    .child("Joined").child(club.getIdClub())
                    .removeValue()
            }

            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(club.getIdClub())
                    .child("Joined").child(it1.toString())
                    .removeValue()
            }

            holder.joinClub.visibility = View.VISIBLE
            holder.joinedClub.visibility = View.GONE
        }


        checkJoin(club.getIdClub(), holder.joinClub, holder.joinedClub)
        getJoiners(club.getIdClub(), holder.clubMembers)
    }


    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var clubImage: CircleImageView = itemView.findViewById(R.id.club_image_search)
        var clubName: TextView = itemView.findViewById(R.id.club_name_search)
        var clubLayout: RelativeLayout = itemView.findViewById(R.id.club_layout)
        var joinClub: ImageButton = itemView.findViewById(R.id.join_club_btn)
        var joinedClub: ImageButton = itemView.findViewById(R.id.joined_club_btn)
        var clubMembers: TextView = itemView.findViewById(R.id.club_members_search)
    }


    private fun getJoiners(clubId: String, clubMembers: TextView)
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

                            clubMembers.text = a.toString() + "." + c.toString() + "K"
                        }
                        totalJoiners > 999999 -> {
                            val a = totalJoiners/1000000
                            val b = totalJoiners%1000000
                            val c = b/100000

                            clubMembers.text = a.toString() + "." + c.toString() + "M"
                        }
                        else -> {
                            clubMembers.text = p0.childrenCount.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun checkJoin(idClub: String, joinClub: ImageButton, joinedClub: ImageButton)
    {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Join").child(it1.toString())
                .child("Joined")
        }

        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (dataSnapshot.child(idClub).exists())
                {
                    joinedClub.visibility = View.VISIBLE
                    joinClub.visibility = View.GONE
                }
                else
                {
                    joinedClub.visibility = View.GONE
                    joinClub.visibility = View.VISIBLE
                }


            }

            override fun onCancelled(error: DatabaseError)
            {


            }
        })
    }

}