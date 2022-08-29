package com.megapixel.trashbench

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Adapter.*
import com.megapixel.trashbench.Model.Clubs
import com.megapixel.trashbench.Model.Targets
import com.megapixel.trashbench.Model.User
import kotlinx.android.synthetic.main.activity_clubs.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ClubsActivity : AppCompatActivity()
{

    private var recyclerView: RecyclerView? = null
    private var clubAdapter: ClubsAdapter? = null
    private var mClub: MutableList<Clubs>? = null
    var clubList: List<Clubs>? = null
    var joinList: List<String>? = null
    var joinAdapter: ShowJoinersAdapter? = null
    var firebaseUser: FirebaseUser? = null

    private val REQUEST_CODE_SPEECH_INPUT = 100

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clubs)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        recyclerView = findViewById(R.id.clubs_recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        mClub = ArrayList()
        clubAdapter = ClubsAdapter(this, mClub as ArrayList<Clubs>)
        recyclerView?.adapter = clubAdapter


        val recyclerViewJoin: RecyclerView = findViewById(R.id.clubs_join_recycler_view)
        recyclerViewJoin.setHasFixedSize(true)
        recyclerViewJoin.layoutManager = LinearLayoutManager(this)
        clubList = ArrayList()
        joinAdapter = ShowJoinersAdapter(this, clubList as ArrayList<Clubs>)
        recyclerViewJoin.adapter = joinAdapter


        joinList = ArrayList()


        if (club_edit_text.text == null)
        {
            clubs_join_recycler_view.visibility = View.VISIBLE
        }
        else
        {
            clubs_join_recycler_view.visibility = View.GONE
        }


        club_edit_text.setOnTouchListener { v, event ->
            club_edit_text_layout.setBackgroundResource(R.drawable.button_set)

            v?.onTouchEvent(event) ?: true
        }


        clubs_search_voice_btn.setOnClickListener {
            speak()
        }


        club_edit_text.addTextChangedListener(object: TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                if (club_edit_text.text.toString() == "")
                {

                }
                else {
                    recyclerView?.visibility = View.VISIBLE

                    retrieveClubs()
                    searchClub(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?)
            {
            }
        })


        back_clubs.setOnClickListener {
            onBackPressed()
        }

        add_club.setOnClickListener {
            startActivity(Intent(this, AddClubActivity::class.java))
            finish()
        }

        getJoiners()
    }


    private fun speak()
    {
        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Name the club you want to find...")

        try {
            startActivityForResult(mIntent, REQUEST_CODE_SPEECH_INPUT)
        }
        catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data)
                {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    club_edit_text.setText(result[0])
                }
            }
        }
    }


    private fun getJoiners()
    {
        val joinersRef = FirebaseDatabase.getInstance().reference
            .child("Join").child(firebaseUser!!.uid)
            .child("Joiners")


        joinersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                (joinList as ArrayList<String>).clear()

                for (snapshot in p0.children)
                {
                    (joinList as ArrayList<String>).add(snapshot.key!!)
                }
                showJoiners()
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun showJoiners()
    {
        val clubsRef = FirebaseDatabase.getInstance().getReference().child("Clubs")

        clubsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                (clubList as ArrayList<Clubs>).clear()

                for (snapshot in dataSnapshot.children)
                {
                    val club = snapshot.getValue(Clubs::class.java)

                    for (id in joinList!!)
                    {
                        if (club!!.getClubid() == id)
                        {
                            (clubList as ArrayList<Clubs>).add(club)
                        }
                    }
                }

                joinAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun searchClub(input: String)
    {
        val query = FirebaseDatabase.getInstance().reference
            .child("Clubs")
            .orderByChild("clubname")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                mClub?.clear()

                for (snapshot in dataSnapshot.children)
                {
                    val club = snapshot.getValue(Clubs::class.java)
                    if (club != null)
                    {
                        mClub?.add(club)
                    }
                    else
                    {
                    }
                }

                clubAdapter?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError)
            {

            }


        })
    }


    private fun retrieveClubs()
    {
        val clubsRef = FirebaseDatabase.getInstance().getReference().child("Clubs")

        clubsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (club_edit_text?.text.toString() == "")
                {
                    mClub?.clear()

                    for (snapshot in dataSnapshot.children)
                    {
                        val club = snapshot.getValue(Clubs::class.java)
                        if (club != null)
                        {
                            mClub?.add(club)
                        }
                        else
                        {
                        }
                    }

                    clubAdapter?.notifyDataSetChanged()
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

        val hashMap = HashMap<String, Any>()
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