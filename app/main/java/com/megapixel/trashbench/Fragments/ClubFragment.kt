package com.megapixel.trashbench.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.ClubInfoActivity
import com.megapixel.trashbench.ClubSettingsActivity
import com.megapixel.trashbench.Model.Clubs
import com.megapixel.trashbench.R
import kotlinx.android.synthetic.main.fragment_club.*
import kotlinx.android.synthetic.main.fragment_club.view.*
import android.content.Context as Context1


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@Suppress("UNREACHABLE_CODE")
class ClubFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var clubId: String
    private lateinit var joinerId: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_club, container, false)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val preferences = context?.getSharedPreferences("PREFS", Context1.MODE_PRIVATE)
        if (preferences != null)
        {
            joinerId = preferences.getString("joinerId", "none").toString()
            clubId = preferences.getString("clubId", "none").toString()
        }



        if (joinerId == firebaseUser.uid)
        {
            checkJoinStatus()
        }
        else if (joinerId != firebaseUser.uid)
        {
            checkJoinStatus()
        }


        view.back_club_fragment.setOnClickListener {
            activity?.onBackPressed()
        }


        if (joinerId == firebaseUser.uid)
        {
            view.edit_club.visibility = View.VISIBLE
        }
        else
        {
            view.edit_club.visibility = View.GONE
        }


        view.edit_club.setOnClickListener {
            val intent = Intent(context, ClubSettingsActivity::class.java)
            intent.putExtra("clubId", clubId)
            startActivity(intent)
        }


        view.more_club_info.setOnClickListener {
            val intent = Intent(context, ClubInfoActivity::class.java)
            intent.putExtra("clubId", clubId)
            intent.putExtra("ownerId", joinerId)
            startActivity(intent)

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            alpha_screen_club.startAnimation(alpha)
            view.alpha_screen_club.visibility = View.VISIBLE
        }


        view.join_btn.setOnClickListener {
            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(it1.toString())
                    .child("Joined").child(clubId)
                    .setValue(true)
            }

            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(clubId)
                    .child("Joined").child(it1.toString())
                    .setValue(true)
            }

            view.join_btn_layout.visibility = View.INVISIBLE
            view.joined_btn_layout.visibility = View.VISIBLE
        }


        view.join_btn_text.setOnClickListener {
            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(it1.toString())
                    .child("Joined").child(clubId)
                    .setValue(true)
            }

            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(clubId)
                    .child("Joined").child(it1.toString())
                    .setValue(true)
            }

            view.join_btn_layout.visibility = View.INVISIBLE
            view.joined_btn_layout.visibility = View.VISIBLE
        }





        view.joined_btn.setOnClickListener {
            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(it1.toString())
                    .child("Joined").child(clubId)
                    .removeValue()
            }

            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(clubId)
                    .child("Joined").child(it1.toString())
                    .removeValue()
            }

            view.join_btn_layout.visibility = View.VISIBLE
            view.joined_btn_layout.visibility = View.INVISIBLE
        }


        view.joined_btn_text.setOnClickListener {
            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(it1.toString())
                    .child("Joined").child(clubId)
                    .removeValue()
            }

            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Join").child(clubId)
                    .child("Joined").child(it1.toString())
                    .removeValue()
            }

            view.join_btn_layout.visibility = View.VISIBLE
            view.joined_btn_layout.visibility = View.INVISIBLE
        }


        clubInfo()
        getJoiners()

        return view
    }


    private fun clubInfo()
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


                    view?.club_image?.let {
                        Glide.with(context!!.applicationContext).load(club!!.getClubimage()).into(
                            it
                        )
                    }

                    club_fragment_club_name.text = club!!.getClubname()
                    club_fragment_club_description.text = club.getClubdescription()
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

                            view?.total_joiners?.text = a.toString() + "." + c.toString() + "K"
                        }
                        totalJoiners > 999999 -> {
                            val a = totalJoiners/1000000
                            val b = totalJoiners%1000000
                            val c = b/100000

                            view?.total_joiners?.text = a.toString() + "." + c.toString() + "M"
                        }
                        else -> {
                            view?.total_joiners?.text = p0.childrenCount.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun checkJoinStatus()
    {
        val joinRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Join").child(it1.toString())
                .child("Joined")
        }

        if (joinRef != null)
        {
            joinRef.addValueEventListener(object : ValueEventListener
            {
                override fun onDataChange(p0: DataSnapshot)
                {
                    if (p0.child(clubId).exists())
                    {
                        view?.join_btn_layout?.visibility = View.INVISIBLE
                        view?.joined_btn_layout?.visibility = View.VISIBLE
                    }

                    else
                    {
                        view?.join_btn_layout?.visibility = View.VISIBLE
                        view?.joined_btn_layout?.visibility = View.INVISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError)
                {

                }
            })
        }
    }


    override fun onResume() {
        super.onResume()

        alpha_screen_club?.visibility = View.GONE
    }


    companion object {
        fun newInstance(param1: String, param2: String) =
            ClubFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}