package com.megapixel.trashbench.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Adapter.UserAdapter
import com.megapixel.trashbench.Model.User
import com.megapixel.trashbench.R
import com.megapixel.trashbench.R.string.account_created_successfully
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SearchFragment : Fragment()
{
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    private val REQUEST_CODE_SPEECH_INPUT = 100

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null)
        {
            this.profileId = pref.getString("profileId", "none").toString()
        }

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<User>, true) }
        recyclerView?.adapter = userAdapter


        view.search_voice_btn.setOnClickListener {
            speak()
        }


        view.search_edit_text.setOnTouchListener { v, event ->
            view.search_edit_text_layout.setBackgroundResource(R.drawable.button_set)

            v?.onTouchEvent(event) ?: true
        }



            view.search_edit_text.addTextChangedListener(object: TextWatcher
            {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
                {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
                {
                    if (view.search_edit_text.text.toString() == "")
                    {

                    }
                    else {
                        recyclerView?.visibility = View.VISIBLE

                        retrieveUsers()
                        searchUser(s.toString())
                    }
                }

                override fun afterTextChanged(s: Editable?)
                {
                }
            })

        return view
    }

    private fun speak()
    {
        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the name of the person you want to find...")

        try {
            startActivityForResult(mIntent, REQUEST_CODE_SPEECH_INPUT)
        }
        catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data)
                {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    search_edit_text.setText(result[0])
                }
            }
        }
    }

    private fun searchUser(input: String)
    {
        val query = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("fullname")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                mUser?.clear()
                view?.search_big?.visibility = View.VISIBLE

                for (snapshot in dataSnapshot.children)
                {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null)
                    {
                        mUser?.add(user)
                        view?.search_big?.visibility = View.GONE
                    }
                    else
                    {
                        view?.search_big?.visibility = View.VISIBLE
                    }
                }

                userAdapter?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError)
            {

            }


        })
    }

    private fun retrieveUsers()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (view?.search_edit_text?.text.toString() == "")
                {
                    mUser?.clear()
                    view?.search_big?.visibility = View.VISIBLE

                    for (snapshot in dataSnapshot.children)
                    {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null)
                        {
                            mUser?.add(user)
                            view?.search_big?.visibility = View.GONE
                        }
                        else
                        {
                            view?.search_big?.visibility = View.VISIBLE
                        }
                    }

                    userAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}