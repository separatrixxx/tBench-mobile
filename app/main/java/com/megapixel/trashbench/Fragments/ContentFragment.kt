package com.megapixel.trashbench.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.AccountSettingsActivity
import com.megapixel.trashbench.Adapter.PostAdapter
import com.megapixel.trashbench.Adapter.StoryAdapter
import com.megapixel.trashbench.AddPostActivity
import com.megapixel.trashbench.MainActivity
import com.megapixel.trashbench.Model.Post
import com.megapixel.trashbench.Model.Story
import com.megapixel.trashbench.Model.User
import com.megapixel.trashbench.R
import kotlinx.android.synthetic.main.fragment_content.*
import kotlinx.android.synthetic.main.fragment_content.view.*
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ContentFragment : Fragment()
{
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<String>? = null

    private var startContent: TextView? = null
    private var progressBarContent: ProgressBar? = null

    private var storyAdapter: StoryAdapter? = null
    private var storyList: MutableList<Story>? = null

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
        val view = inflater.inflate(R.layout.fragment_content, container, false)


        startContent = view.findViewById(R.id.start_content)
        progressBarContent = view.findViewById(R.id.progress_bar_content)


        var recyclerView: RecyclerView? = null
        var recyclerViewStory: RecyclerView? = null


        recyclerView = view.findViewById(R.id.recycler_view_content)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setNestedScrollingEnabled(false)
        recyclerView.setHasFixedSize(true)


        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter


        recyclerViewStory = view.findViewById(R.id.recycler_view_story)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager2 = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewStory.layoutManager = linearLayoutManager2


        storyList = ArrayList()
        storyAdapter = context?.let { StoryAdapter(it, storyList as ArrayList<Story>) }
        recyclerViewStory.adapter = storyAdapter


        checkFollowings()


        view.content_name.setOnClickListener {
            startActivity(Intent(context, MainActivity::class.java))
        }


        return view
    }


    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    (followingList as ArrayList<String>).clear()

                    for (snapshot in p0.children)
                    {
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }

                    retrievePosts()
                    retrieveStories()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun retrievePosts()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
               postList?.clear()
               startContent!!.visibility = View.VISIBLE
               progressBarContent!!.visibility = View.GONE

                for (snapshot in p0.children)
                {
                    val post = snapshot.getValue(Post::class.java)

                    for (id in (followingList as ArrayList<String>))
                    {
                        if (post!!.getPublisher() == id)
                        {
                            postList!!.add(post)
                            startContent!!.visibility = View.GONE
                            progressBarContent!!.visibility = View.GONE
                        }
                    }
                }
                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun retrieveStories()
    {
        val storyRef = FirebaseDatabase.getInstance().reference.child("Story")

        storyRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                val timeCurrent = System.currentTimeMillis()

                (storyList as ArrayList<Story>).clear()

                (storyList as ArrayList<Story>).add(Story("", 0, 0, "", FirebaseAuth.getInstance().currentUser!!.uid))


                for (id in followingList!!)
                {
                    var countStory = 0

                    var story: Story? = null

                    for (snapshot in dataSnapshot.child(id).children)
                    {
                        story = snapshot.getValue(Story::class.java)

                        if (timeCurrent>story!!.getTimeStart() && timeCurrent<story!!.getTimeEnd())
                        {
                            countStory++
                        }
                    }
                    if (countStory>0)
                    {
                        (storyList as ArrayList<Story>).add(story!!)
                    }
                }
                storyAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    companion object {
        fun newInstance(param1: String, param2: String) =
            ContentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
