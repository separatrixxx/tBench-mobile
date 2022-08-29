package com.megapixel.trashbench.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Adapter.PostAdapter
import com.megapixel.trashbench.Model.Post
import com.megapixel.trashbench.R
import kotlinx.android.synthetic.main.fragment_post_details.view.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



@Suppress("UNREACHABLE_CODE")
class NotificationsPostDetailsFragment : Fragment()
{
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var postId: String = ""

    private var param1: String? = null
    private var param2: String? = null


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

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post_details, container, false)


        val preferences = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (preferences != null)
        {
            postId = preferences.getString("postId", "none").toString()
        }

        var recyclerView: RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_post_details)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter


        view.back_post_details.setOnClickListener {
            val newFragment: Fragment = NotificationsFragment()

            val transaction =
                requireFragmentManager().beginTransaction()
            transaction.replace(
                R.id.fragment_container_new,
                newFragment)

            transaction.commit()
        }

        retrievePosts()

        return view
    }



    private fun retrievePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")
            .child(postId)

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    postList?.clear()

                    val post = p0.getValue(Post::class.java)

                    postList!!.add(post!!)

                    postAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }




    companion object {

        fun newInstance(param1: String, param2: String) =
            PostDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}