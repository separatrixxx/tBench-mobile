package com.megapixel.trashbench.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
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
import com.megapixel.trashbench.Fragments.ClubFragment
import com.megapixel.trashbench.Model.Clubs
import com.megapixel.trashbench.R
import de.hdodenhof.circleimageview.CircleImageView

class ShowJoinersAdapter(private var mContext: Context,
                         private var mClub: List<Clubs>) : RecyclerView.Adapter<ShowJoinersAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowJoinersAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.club_item_layout, parent, false)
        return ShowJoinersAdapter.ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mClub.size
    }

    override fun onBindViewHolder(holder: ShowJoinersAdapter.ViewHolder, position: Int) {
        val club = mClub[position]

        holder.clubName.text = club.getClubname()
        Glide.with(mContext.applicationContext).load(club.getClubimage()).into(holder.clubImage)


        holder.clubLayout.setOnClickListener {

            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("clubId", club.getClubid())
            editor.putString("joinerId", club.getOwnerId())
            editor.apply()

            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.clubs_container, ClubFragment()).commit()

        }


        holder.joinClub.setOnClickListener {
            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(it1.toString())
                    .child("Joined").child(club.getOwnerId())
                    .setValue(true)
            }

            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(club.getOwnerId())
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
                    .child("Joined").child(club.getOwnerId())
                    .removeValue()
            }

            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(club.getOwnerId())
                    .child("Joined").child(it1.toString())
                    .removeValue()
            }

            holder.joinClub.visibility = View.VISIBLE
            holder.joinedClub.visibility = View.GONE
        }


        checkJoin(club.getOwnerId(), holder.joinClub, holder.joinedClub)
    }


    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var clubImage: CircleImageView = itemView.findViewById(R.id.club_image_search)
        var clubName: TextView = itemView.findViewById(R.id.club_name_search)
        var clubLayout: RelativeLayout = itemView.findViewById(R.id.club_layout)
        var joinClub: ImageButton = itemView.findViewById(R.id.join_club_btn)
        var joinedClub: ImageButton = itemView.findViewById(R.id.joined_club_btn)
    }


    private fun checkJoin(ownerId: String, joinClub: ImageButton, joinedClub: ImageButton)
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
                if (dataSnapshot.child(ownerId).exists())
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