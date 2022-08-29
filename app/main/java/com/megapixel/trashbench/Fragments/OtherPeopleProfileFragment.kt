package com.megapixel.trashbench.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.*
import com.megapixel.trashbench.Adapter.OtherImagesAdapter
import com.megapixel.trashbench.HeartActivity
import com.megapixel.trashbench.Model.Post
import com.megapixel.trashbench.Model.User
import kotlinx.android.synthetic.main.activity_story.*
import kotlinx.android.synthetic.main.fragment_other_people_profile.*
import kotlinx.android.synthetic.main.fragment_other_people_profile.view.*
import kotlinx.android.synthetic.main.fragment_other_people_profile.view.other_close_more_info
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.burger
import kotlinx.android.synthetic.main.fragment_profile.crown
import kotlinx.android.synthetic.main.fragment_profile.face1
import kotlinx.android.synthetic.main.fragment_profile.face2
import kotlinx.android.synthetic.main.fragment_profile.face3
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class OtherPeopleProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    var postList: List<Post>? = null
    var otherImagesAdapter: OtherImagesAdapter? = null

    var x: String = ""
    var y: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)


            other_down_post_btn?.visibility = View.GONE
            other_top_bar?.visibility = View.VISIBLE
            other_mid_bar?.visibility = View.VISIBLE
            other_more_info?.visibility = View.VISIBLE
            other_up_post_btn?.visibility = View.VISIBLE
            other_mid_bar_info?.visibility = View.GONE
            other_close_more_info?.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        other_down_post_btn?.visibility = View.GONE
        other_top_bar?.visibility = View.VISIBLE
        other_mid_bar?.visibility = View.VISIBLE
        other_more_info?.visibility = View.VISIBLE
        other_up_post_btn?.visibility = View.VISIBLE
        other_mid_bar_info?.visibility = View.GONE
        other_close_more_info?.visibility = View.GONE
        other_line_more_info?.visibility = View.GONE


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_other_people_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null)
        {
            this.profileId = pref.getString("profileId", "none").toString()
        }

        if (profileId == firebaseUser.uid)
        {
            view.other_edit_profile_btn.visibility = View.VISIBLE
            view.other_follow_btn.visibility = View.GONE
            view.other_following_btn.visibility = View.GONE
            view.other_messages.visibility = View.GONE
        }
        else if (profileId != firebaseUser.uid)
        {
            checkFollowAndFollowingButtonStatus()
        }


        view.other_messages.setOnClickListener {
            val intent = Intent(context, MessagesChatActivity::class.java)
            intent.putExtra("userId", profileId)
            context?.startActivity(intent)
        }


        view.other_mp.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, PopMPActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            other_alpha_screen.startAnimation(alpha)
            view.other_alpha_screen.visibility = View.VISIBLE
        })


        view.other_check_mark.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, PopCheckActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            other_alpha_screen.startAnimation(alpha)
            view.other_alpha_screen.visibility = View.VISIBLE
        })


        view.other_heart.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, HeartActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            other_alpha_screen.startAnimation(alpha)
            view.other_alpha_screen.visibility = View.VISIBLE
        })


        view.other_game.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, GameActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            other_alpha_screen.startAnimation(alpha)
            view.other_alpha_screen.visibility = View.VISIBLE
        })


        view.other_gift.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, GiftActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            other_alpha_screen.startAnimation(alpha)
            view.other_alpha_screen.visibility = View.VISIBLE
        })


        view.other_face1.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, Face1Activity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            other_alpha_screen.startAnimation(alpha)
            view.other_alpha_screen.visibility = View.VISIBLE
        })


        view.other_face2.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, Face2Activity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            other_alpha_screen.startAnimation(alpha)
            view.other_alpha_screen.visibility = View.VISIBLE
        })


        view.other_face3.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, Face3Activity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            other_alpha_screen.startAnimation(alpha)
            view.other_alpha_screen.visibility = View.VISIBLE
        })


        view.other_crown.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, CrownActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            other_alpha_screen.startAnimation(alpha)
            view.other_alpha_screen.visibility = View.VISIBLE
        })


        view.other_burger.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, BurgerActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            other_alpha_screen.startAnimation(alpha)
            view.other_alpha_screen.visibility = View.VISIBLE
        })


        var recyclerViewUploadImages: RecyclerView
        recyclerViewUploadImages = view.findViewById(R.id.other_recycler_view_upload_pic)
        recyclerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 2)
        recyclerViewUploadImages.layoutManager = linearLayoutManager

        postList = ArrayList()
        otherImagesAdapter = context?.let { OtherImagesAdapter(it, postList as ArrayList<Post>) }
        recyclerViewUploadImages.adapter = otherImagesAdapter


        view.other_more_info.setOnClickListener {
            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra("profileId", profileId)
            startActivity(intent)

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            other_alpha_screen.startAnimation(alpha)
            view.other_alpha_screen.visibility = View.VISIBLE
        }


        view.other_total_followers.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }


        view.other_total_following.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "following")
            startActivity(intent)
        }


        view.other_followers.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }


        view.other_following.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "following")
            startActivity(intent)
        }


        view.back_other_profile.setOnClickListener {
            activity?.onBackPressed()
        }


        view.other_close_more_info.setOnClickListener {
            other_mid_bar_info?.visibility = View.GONE
            other_close_more_info?.visibility = View.GONE
            other_more_info?.visibility = View.VISIBLE
            other_line_more_info?.visibility = View.GONE

            other_info_profile_frag.maxLines = 2
        }


        view.other_up_post_btn.setOnClickListener {
            other_down_post_btn?.visibility = View.VISIBLE
            other_top_bar?.visibility = View.GONE
            other_mid_bar?.visibility = View.GONE
            other_more_info?.visibility = View.GONE
            other_up_post_btn?.visibility = View.GONE
            other_mid_bar_info?.visibility = View.GONE
            other_close_more_info?.visibility = View.GONE
            other_line_more_info?.visibility = View.GONE
            other_follow_linear?.visibility = View.GONE
            other_line_123?.visibility = View.GONE
            other_line_1234?.visibility = View.GONE
        }

        view.other_down_post_btn.setOnClickListener {
            other_down_post_btn?.visibility = View.GONE
            other_top_bar?.visibility = View.VISIBLE
            other_mid_bar?.visibility = View.VISIBLE
            other_more_info?.visibility = View.VISIBLE
            other_up_post_btn?.visibility = View.VISIBLE
            other_mid_bar_info?.visibility = View.GONE
            other_close_more_info?.visibility = View.GONE
            other_line_more_info?.visibility = View.GONE
            other_follow_linear?.visibility = View.VISIBLE
            other_line_123?.visibility = View.VISIBLE
            other_line_1234?.visibility = View.VISIBLE
        }


        view.other_edit_profile_btn.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }

        view.other_follow_btn.setOnClickListener {
            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(it1.toString())
                    .child("Following").child(profileId)
                    .setValue(true)
            }

            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(profileId)
                    .child("Followers").child(it1.toString())
                    .setValue(true)
            }

            addNotification()

            view.other_follow_btn.visibility = View.GONE
            view.other_following_btn.visibility = View.VISIBLE
            view.other_edit_profile_btn.visibility = View.GONE
        }


        view.other_following_btn.setOnClickListener {
            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(it1.toString())
                    .child("Following").child(profileId)
                    .removeValue()
            }

            firebaseUser?.uid.let { it1 ->
                FirebaseDatabase.getInstance().reference
                    .child("Follow").child(profileId)
                    .child("Followers").child(it1.toString())
                    .removeValue()
            }

            view.other_follow_btn.visibility = View.VISIBLE
            view.other_following_btn.visibility = View.GONE
            view.other_edit_profile_btn.visibility = View.GONE
        }

        getFollowers()
        getFollowings()
        userInfo()
        myPhotos()
        getTotalNumberOfPosts()

        updateMP()

        deletedUser()

        return view
    }

    private fun deletedUser()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    if (user!!.getDeleted() == "true")
                    {
                        if (other_scroll_view_profile != null)
                        {
                            other_scroll_view_profile.visibility = View.INVISIBLE
                            other_nothing_posts.visibility = View.INVISIBLE
                            other_recycler_view_upload_pic.visibility = View.INVISIBLE
                            other_recycler_view_saved_pic.visibility = View.GONE
                            other_alpha_screen.visibility = View.GONE
                            page_deleted.visibility = View.VISIBLE
                            no_information.visibility = View.VISIBLE
                        }
                    }
                    else
                    {
                        if (other_scroll_view_profile != null)
                        {
                            other_scroll_view_profile.visibility = View.VISIBLE
                            other_recycler_view_upload_pic.visibility = View.VISIBLE
                            other_recycler_view_saved_pic.visibility = View.GONE
                            other_alpha_screen.visibility = View.GONE
                            page_deleted.visibility = View.GONE
                            no_information.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    private fun checkFollowAndFollowingButtonStatus()
    {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        if (followingRef != null)
        {
            followingRef.addValueEventListener(object : ValueEventListener
            {
                override fun onDataChange(p0: DataSnapshot)
                {
                    if (p0.child(profileId).exists())
                    {
                        view?.other_follow_btn?.visibility = View.GONE
                        view?.other_following_btn?.visibility = View.VISIBLE
                        view?.other_edit_profile_btn?.visibility = View.GONE
                    }

                    else
                    {
                        view?.other_follow_btn?.visibility = View.VISIBLE
                        view?.other_following_btn?.visibility = View.GONE
                        view?.other_edit_profile_btn?.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError)
                {

                }
            })
        }
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getFollowers()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")


        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val totalFollowers: Int = p0.childrenCount.toInt()

                    when {
                        totalFollowers in 1000..999999 -> {
                            val a = totalFollowers/1000
                            val b = totalFollowers%1000
                            val c = b/100

                            view?.other_total_followers?.text = a.toString() + "." + c.toString() + "K"
                        }
                        totalFollowers > 999999 -> {
                            val a = totalFollowers/1000000
                            val b = totalFollowers%1000000
                            val c = b/100000

                            view?.other_total_followers?.text = a.toString() + "." + c.toString() + "M"
                        }
                        else -> {
                            view?.other_total_followers?.text = p0.childrenCount.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun getFollowings()
    {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")


        followersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val totalFollowings: Int = p0.childrenCount.toInt()

                    when {
                        totalFollowings in 1000..999999 -> {
                            val a = totalFollowings/1000
                            val b = totalFollowings%1000
                            val c = b/100

                            view?.other_total_following?.text = a.toString() + "." + c.toString() + "K"
                        }
                        totalFollowings > 999999 -> {
                            val a = totalFollowings/1000000
                            val b = totalFollowings%1000000
                            val c = b/100000

                            view?.total_following?.text = a.toString() + "." + c.toString() + "M"
                        }
                        else -> {
                            view?.other_total_following?.text = p0.childrenCount.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun myPhotos()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    (postList as ArrayList<Post>).clear()
                    other_nothing_posts.visibility = View.VISIBLE

                    for (snapshot in p0.children)
                    {
                        val post = snapshot.getValue(Post::class.java)!!
                        if (post.getPublisher() == profileId)
                        {
                            (postList as ArrayList<Post>).add(post)
                            other_nothing_posts.visibility = View.GONE
                        }
                        Collections.reverse(postList)
                        otherImagesAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun updateMP() {
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
                        if (other_mp != null)
                        {
                            other_mp.visibility = View.VISIBLE
                        }
                    }

                    if (user.getMP() == "check mark")
                    {
                        if (other_check_mark != null)
                        {
                            other_check_mark.visibility = View.VISIBLE
                        }
                    }

                    if (user.getEmoji() == "heart")
                    {
                        if (other_heart != null)
                        {
                            other_heart.visibility = View.VISIBLE
                        }
                    }

                    if (other_heart != null && user.getEmoji() == "")
                    {
                        if (other_heart != null)
                        {
                            other_heart.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "game")
                    {
                        if (other_game != null)
                        {
                            other_game.visibility = View.VISIBLE
                        }
                    }

                    if (other_game != null && user.getEmoji() == "")
                    {
                        if (other_game != null)
                        {
                            other_game.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "gift")
                    {
                        if (other_gift != null)
                        {
                            other_gift.visibility = View.VISIBLE
                        }
                    }

                    if (other_gift != null && user.getEmoji() == "")
                    {
                        if (other_gift != null)
                        {
                            other_gift.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "face1")
                    {
                        if (other_face1 != null)
                        {
                            other_face1.visibility = View.VISIBLE
                        }
                    }

                    if (other_face1 != null && user.getEmoji() == "")
                    {
                        if (other_face1 != null)
                        {
                            other_face1.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "face2")
                    {
                        if (other_face2 != null)
                        {
                            other_face2.visibility = View.VISIBLE
                        }
                    }

                    if (other_face2 != null && user.getEmoji() == "")
                    {
                        if (other_face2 != null)
                        {
                            other_face2.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "face3")
                    {
                        if (other_face3 != null)
                        {
                            other_face3.visibility = View.VISIBLE
                        }
                    }

                    if (other_face3 != null && user.getEmoji() == "")
                    {
                        if (other_face3 != null)
                        {
                            other_face3.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "crown")
                    {
                        if (other_crown != null)
                        {
                            other_crown.visibility = View.VISIBLE
                        }
                    }

                    if (other_crown != null && user.getEmoji() == "")
                    {
                        if (other_crown != null)
                        {
                            other_crown.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "burger")
                    {
                        if (other_burger != null)
                        {
                            other_burger.visibility = View.VISIBLE
                        }
                    }

                    if (other_burger != null && user.getEmoji() == "")
                    {
                        if (other_burger != null)
                        {
                            other_burger.visibility = View.GONE
                        }
                    }

                    if (other_mp != null && user.getMP() == "")
                    {
                        if (other_check_mark != null)
                        {
                            other_mp.visibility = View.GONE
                            other_check_mark.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun userInfo()
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

                    view?.other_pro_image_profile_frag?.let {
                        Glide.with(context!!.applicationContext).load(user!!.getImage()).into(
                            it
                        )
                    }

                    view?.other_profile_fragment_username?.text = user!!.getUsername()
                    view?.other_full_name_profile_frag?.text = user!!.getFullname()
                    view?.other_info_profile_frag?.text = user!!.getBio()
                    view?.other_city_profile_frag?.text = user!!.getCity()
                    view?.other_birthday_profile_frag?.text = user!!.getBirthday()
                    view?.other_specialty_profile_frag?.text = user!!.getSpecialty()


                    x = user!!.getBirthday()
                    y = x.substringBeforeLast("-")

                    val now = LocalDateTime.now()
                    var formatter = DateTimeFormatter.ofPattern("dd-MM")

                    if (y == formatter.format(now).toString())
                    {
                        view?.other_party_hat?.visibility = View.VISIBLE
                    }

                    if (formatter.format(now).toString() == "24-12" ||
                        formatter.format(now).toString() == "25-12" ||
                        formatter.format(now).toString() == "26-12" ||
                        formatter.format(now).toString() == "27-12" ||
                        formatter.format(now).toString() == "28-12" ||
                        formatter.format(now).toString() == "29-12" ||
                        formatter.format(now).toString() == "30-12" ||
                        formatter.format(now).toString() == "31-12" ||
                        formatter.format(now).toString() == "01-01" ||
                        formatter.format(now).toString() == "02-01" ||
                        formatter.format(now).toString() == "03-01" ||
                        formatter.format(now).toString() == "04-01" ||
                        formatter.format(now).toString() == "05-01" ||
                        formatter.format(now).toString() == "06-01" ||
                        formatter.format(now).toString() == "07-01" ||
                        formatter.format(now).toString() == "08-01")
                    {
                        view?.other_winter_hat?.visibility = View.VISIBLE
                    }

                    if (formatter.format(now).toString() == "24-10" ||
                        formatter.format(now).toString() == "25-10" ||
                        formatter.format(now).toString() == "26-10" ||
                        formatter.format(now).toString() == "27-10" ||
                        formatter.format(now).toString() == "28-10" ||
                        formatter.format(now).toString() == "29-10" ||
                        formatter.format(now).toString() == "30-10" ||
                        formatter.format(now).toString() == "31-10" ||
                        formatter.format(now).toString() == "01-11")
                    {
                        view?.other_pumpkin?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    private fun getTotalNumberOfPosts()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener( object : ValueEventListener
        {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    var postCounter = 0

                    for (snapshot in dataSnapshot.children)
                    {
                        val post = snapshot.getValue(Post::class.java)!!

                        if (post.getPublisher() == profileId)
                        {
                            postCounter++
                        }
                    }
                    other_total_posts.text = " $postCounter"
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }


    private fun addNotification()
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(profileId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["text"] = "Started following you"
        notiMap["postid"] = ""
        notiMap["ispost"] = false

        notiRef.push().setValue(notiMap)
    }

    override fun onResume() {
        super.onResume()

        other_alpha_screen?.visibility = View.GONE
    }
}