package com.megapixel.trashbench.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Adapter.UserAdapter
import com.megapixel.trashbench.Adapter.UserAdapterSecond
import com.megapixel.trashbench.Model.Chat
import com.megapixel.trashbench.Model.ChatList
import com.megapixel.trashbench.Model.User
import com.megapixel.trashbench.R
import kotlinx.android.synthetic.main.fragment_messages.*
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MessagesFragment : Fragment() {

    private var userAdapterSecond: UserAdapterSecond? = null
    private var mUsers: List<User>? = null
    private var usersChatList: List<ChatList>? = null
    lateinit var recycler_view_messages: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_messages, container, false)


        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        recycler_view_messages = view.findViewById(R.id.recycler_view_messages)
        recycler_view_messages.setHasFixedSize(true)
        recycler_view_messages.layoutManager = LinearLayoutManager(context)
        recycler_view_messages.layoutManager = linearLayoutManager


        firebaseUser = FirebaseAuth.getInstance().currentUser


        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                (usersChatList as ArrayList).clear()

                for (dataSnapshot in p0.children)
                {
                    val chatList = dataSnapshot.getValue(ChatList::class.java)

                    (usersChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


        return view
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            MessagesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun retrieveChatList()
    {
        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot)
            {
                (mUsers as ArrayList).clear()

                for (dataSnapshot in p0.children)
                {
                    val user = dataSnapshot.getValue(User::class.java)

                    for (eachChatList in usersChatList!!)
                    {
                        if (user!!.getUID() == eachChatList.getId())
                        {
                            (mUsers as ArrayList).add(user)
                        }
                    }
                }

                if (context != null)
                {
                    userAdapterSecond = UserAdapterSecond(context!!, (mUsers as ArrayList<User>), true)
                    recycler_view_messages.adapter = userAdapterSecond
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun retrieveChatList2()
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot)
            {
//                mUsers.clear()

                for (dataSnapshot in p0.children)
                {
                    val user = dataSnapshot.getValue(User::class.java)

                    for (eachChatList in usersChatList!!)
                    {
                        if (user!!.getUID() == eachChatList.getId())
                        {
//                            mUsers.add(user!!)
                        }
                    }
                }

//                userAdapterSecond.isChatCheck = true
//                userAdapterSecond.setData(mUsers)

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}