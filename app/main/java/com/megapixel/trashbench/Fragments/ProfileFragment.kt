package com.megapixel.trashbench.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
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
import com.megapixel.trashbench.Adapter.MyImagesAdapter
import com.megapixel.trashbench.HeartActivity
import com.megapixel.trashbench.Model.Post
import com.megapixel.trashbench.Model.User
import kotlinx.android.synthetic.main.activity_story.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.burger
import kotlinx.android.synthetic.main.fragment_profile.check_mark
import kotlinx.android.synthetic.main.fragment_profile.crown
import kotlinx.android.synthetic.main.fragment_profile.face1
import kotlinx.android.synthetic.main.fragment_profile.face2
import kotlinx.android.synthetic.main.fragment_profile.face3
import kotlinx.android.synthetic.main.fragment_profile.game
import kotlinx.android.synthetic.main.fragment_profile.gift
import kotlinx.android.synthetic.main.fragment_profile.heart
import kotlinx.android.synthetic.main.fragment_profile.mp
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.edit_profile_btn
import kotlinx.android.synthetic.main.fragment_profile.view.follow_btn
import kotlinx.android.synthetic.main.fragment_profile.view.following_btn
import kotlinx.android.synthetic.main.fragment_profile.view.pro_image_profile_frag
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    var postList: List<Post>? = null
    var myImagesAdapter: MyImagesAdapter? = null

    var myImagesAdapterSavedImg: MyImagesAdapter? = null
    var postListSaved: List<Post>? = null
    var mySavesImg: List<String>? = null

    var x: String = ""
    var y: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            down_post_btn?.visibility = View.GONE
            top_bar?.visibility = View.VISIBLE
            mid_bar?.visibility = View.VISIBLE
            more_info?.visibility = View.VISIBLE
            up_post_btn?.visibility = View.VISIBLE
            mid_bar_info?.visibility = View.GONE
            close_more_info?.visibility = View.GONE
        }

    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        down_post_btn?.visibility = View.GONE
        top_bar?.visibility = View.VISIBLE
        mid_bar?.visibility = View.VISIBLE
        more_info?.visibility = View.VISIBLE
        up_post_btn?.visibility = View.VISIBLE
        mid_bar_info?.visibility = View.GONE
        close_more_info?.visibility = View.GONE
        line_more_info?.visibility = View.GONE

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)



        view.mp.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, PopMPActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            alpha_screen.startAnimation(alpha)
            view.alpha_screen.visibility = View.VISIBLE
        })


        view.check_mark.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, PopCheckActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            alpha_screen.startAnimation(alpha)
            view.alpha_screen.visibility = View.VISIBLE
        })


        view.heart.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, HeartActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            alpha_screen.startAnimation(alpha)
            view.alpha_screen.visibility = View.VISIBLE
        })


        view.game.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, GameActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            alpha_screen.startAnimation(alpha)
            view.alpha_screen.visibility = View.VISIBLE
        })


        view.gift.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, GiftActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            alpha_screen.startAnimation(alpha)
            view.alpha_screen.visibility = View.VISIBLE
        })


        view.face1.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, Face1Activity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            alpha_screen.startAnimation(alpha)
            view.alpha_screen.visibility = View.VISIBLE
        })


        view.face2.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, Face2Activity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            alpha_screen.startAnimation(alpha)
            view.alpha_screen.visibility = View.VISIBLE
        })


        view.face3.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, Face3Activity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            alpha_screen.startAnimation(alpha)
            view.alpha_screen.visibility = View.VISIBLE
        })


        view.crown.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, CrownActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            alpha_screen.startAnimation(alpha)
            view.alpha_screen.visibility = View.VISIBLE
        })


        view.burger.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, BurgerActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            alpha_screen.startAnimation(alpha)
            view.alpha_screen.visibility = View.VISIBLE
        })


        view.profile_options.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, OptionsActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            options_screen.startAnimation(alpha)
            view.options_screen.visibility = View.VISIBLE
        })


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null)
        {
            this.profileId = pref.getString("profileId", "none").toString()
        }

        if (profileId == firebaseUser.uid)
        {
            view.edit_profile_btn.visibility = View.VISIBLE
            view.follow_btn.visibility = View.GONE
            view.following_btn.visibility = View.GONE
        }
        else if (profileId != firebaseUser.uid)
        {
            checkFollowAndFollowingButtonStatus()
        }


        view.back_profile.setOnClickListener {
            activity?.onBackPressed()
        }


        //recycler View for Uploaded Images
        var recyclerViewUploadImages: RecyclerView
        recyclerViewUploadImages = view.findViewById(R.id.recycler_view_upload_pic)
        recyclerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 2)
        recyclerViewUploadImages.layoutManager = linearLayoutManager

        postList = ArrayList()
        myImagesAdapter = context?.let { MyImagesAdapter(it, postList as ArrayList<Post>) }
        recyclerViewUploadImages.adapter = myImagesAdapter


        //recycler View for Saved Images
        var recyclerViewSavedImages: RecyclerView
        recyclerViewSavedImages = view.findViewById(R.id.recycler_view_saved_pic)
        recyclerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager2: LinearLayoutManager = GridLayoutManager(context, 2)
        recyclerViewSavedImages.layoutManager = linearLayoutManager2

        postListSaved = ArrayList()
        myImagesAdapterSavedImg = context?.let { MyImagesAdapter(it, postListSaved as ArrayList<Post>) }
        recyclerViewSavedImages.adapter = myImagesAdapterSavedImg



        recyclerViewSavedImages.visibility = View.GONE
        recyclerViewUploadImages.visibility = View.VISIBLE


        val uploadedImagesBtn: ImageButton
        uploadedImagesBtn = view.findViewById(R.id.images_grid_view_btn)
        uploadedImagesBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.GONE
            recyclerViewUploadImages.visibility = View.VISIBLE
            images_grid_view_btn.setImageResource(R.drawable.profile_content_set)
            images_save_btn.setImageResource(R.drawable.profile_photos)
        }

        val savedImagesBtn: ImageButton
        savedImagesBtn = view.findViewById(R.id.images_save_btn)
        savedImagesBtn.setOnClickListener {
            recyclerViewSavedImages.visibility = View.VISIBLE
            recyclerViewUploadImages.visibility = View.GONE
            images_grid_view_btn.setImageResource(R.drawable.profile_content)
            images_save_btn.setImageResource(R.drawable.profile_photos_set)
        }



        view.post_add_btn.setOnClickListener {
            val check: Boolean = false
            startActivity(Intent(context, AddPostActivity::class.java))

            post_add_btn.setImageResource(R.drawable.profile_add_post_set)
        }

        view.more_info.setOnClickListener {
            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra("profileId", profileId)
            startActivity(intent)

            val alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_screen)
            alpha_screen.startAnimation(alpha)
            view.alpha_screen.visibility = View.VISIBLE
        }

        view.close_more_info.setOnClickListener {
            mid_bar_info?.visibility = View.GONE
            close_more_info?.visibility = View.GONE
            more_info?.visibility = View.VISIBLE
            line_more_info?.visibility = View.GONE

            info_profile_frag.maxLines = 2
        }



        view.up_post_btn.setOnClickListener {
            down_post_btn?.visibility = View.VISIBLE
            top_bar?.visibility = View.GONE
            mid_bar?.visibility = View.GONE
            more_info?.visibility = View.GONE
            up_post_btn?.visibility = View.GONE
            mid_bar_info?.visibility = View.GONE
            close_more_info?.visibility = View.GONE
            line_more_info?.visibility = View.GONE
            follow_linear?.visibility = View.GONE
            profile_options?.visibility = View.GONE
            line_info_2?.visibility = View.GONE
            line_info_1?.visibility = View.GONE
        }

        view.down_post_btn.setOnClickListener {
            down_post_btn?.visibility = View.GONE
            top_bar?.visibility = View.VISIBLE
            mid_bar?.visibility = View.VISIBLE
            more_info?.visibility = View.VISIBLE
            up_post_btn?.visibility = View.VISIBLE
            mid_bar_info?.visibility = View.GONE
            close_more_info?.visibility = View.GONE
            line_more_info?.visibility = View.GONE
            follow_linear?.visibility = View.VISIBLE
            profile_options?.visibility = View.VISIBLE
            line_info_2?.visibility = View.VISIBLE
            line_info_1?.visibility = View.VISIBLE
        }


        view.total_followers.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }


        view.total_following.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "following")
            startActivity(intent)
        }


        view.followers.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }


        view.following.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "following")
            startActivity(intent)
        }

        view.edit_profile_btn.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }

        view.follow_btn.setOnClickListener {
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

            view.follow_btn.visibility = View.GONE
            view.following_btn.visibility = View.VISIBLE
            view.edit_profile_btn.visibility = View.GONE
        }


        view.following_btn.setOnClickListener {
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

            view.follow_btn.visibility = View.VISIBLE
            view.following_btn.visibility = View.GONE
            view.edit_profile_btn.visibility = View.GONE
        }

        getFollowers()
        getFollowings()
        userInfo()
        myPhotos()
        getTotalNumberOfPosts()
        mySaves()

        updateMP()


        return view

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
                        view?.follow_btn?.visibility = View.GONE
                        view?.following_btn?.visibility = View.VISIBLE
                        view?.edit_profile_btn?.visibility = View.GONE
                    }

                    else
                    {
                        view?.follow_btn?.visibility = View.VISIBLE
                        view?.following_btn?.visibility = View.GONE
                        view?.edit_profile_btn?.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError)
                {

                }
            })
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

                            view?.total_followers?.text = a.toString() + "." + c.toString() + "K"
                        }
                        totalFollowers > 999999 -> {
                            val a = totalFollowers/1000000
                            val b = totalFollowers%1000000
                            val c = b/100000

                            view?.total_followers?.text = a.toString() + "." + c.toString() + "M"
                        }
                        else -> {
                            view?.total_followers?.text = p0.childrenCount.toString()
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

                            view?.total_following?.text = a.toString() + "." + c.toString() + "K"
                        }
                        totalFollowings > 999999 -> {
                            val a = totalFollowings/1000000
                            val b = totalFollowings%1000000
                            val c = b/100000

                            view?.total_following?.text = a.toString() + "." + c.toString() + "M"
                        }
                        else -> {
                            view?.total_following?.text = p0.childrenCount.toString()
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
                    nothing_posts.visibility = View.VISIBLE

                    for (snapshot in p0.children)
                    {
                        val post = snapshot.getValue(Post::class.java)!!
                        if (post.getPublisher() == profileId)
                        {
                            (postList as ArrayList<Post>).add(post)
                            nothing_posts.visibility = View.GONE
                        }
                        Collections.reverse(postList)
                        myImagesAdapter!!.notifyDataSetChanged()

                        images_grid_view_btn.setImageResource(R.drawable.profile_content_set)
                        post_add_btn.setImageResource(R.drawable.profile_add_post)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun updateMP()
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
                        if (mp != null)
                        {
                            mp.visibility = View.VISIBLE
                        }
                    }

                    if (mp != null && user.getMP() == "")
                    {
                        if (check_mark != null)
                        {
                            mp.visibility = View.GONE
                            check_mark.visibility = View.GONE
                        }
                    }

                    if (user.getMP() == "check mark")
                    {
                        if (check_mark != null)
                        {
                            check_mark.visibility = View.VISIBLE
                        }
                    }

                    if (user.getEmoji() == "heart")
                    {
                        if (heart != null)
                        {
                            heart.visibility = View.VISIBLE
                        }
                    }

                    if (user.getEmoji() == "game")
                    {
                        if (game != null)
                        {
                            game.visibility = View.VISIBLE
                        }
                    }

                    if (game != null && user.getEmoji() == "")
                    {
                        if (game != null)
                        {
                            game.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "gift")
                    {
                        if (gift != null)
                        {
                            gift.visibility = View.VISIBLE
                        }
                    }

                    if (gift != null && user.getEmoji() == "")
                    {
                        if (gift != null)
                        {
                            gift.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "face1")
                    {
                        if (face1 != null)
                        {
                            face1.visibility = View.VISIBLE
                        }
                    }

                    if (face1 != null && user.getEmoji() == "")
                    {
                        if (face1 != null)
                        {
                            face1.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "face2")
                    {
                        if (face2 != null)
                        {
                            face2.visibility = View.VISIBLE
                        }
                    }

                    if (face2 != null && user.getEmoji() == "")
                    {
                        if (face2 != null)
                        {
                            face2.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "face3")
                    {
                        if (face3 != null)
                        {
                            face3.visibility = View.VISIBLE
                        }
                    }

                    if (face3 != null && user.getEmoji() == "")
                    {
                        if (face3 != null)
                        {
                            face3.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "crown")
                    {
                        if (crown != null)
                        {
                            crown.visibility = View.VISIBLE
                        }
                    }

                    if (crown != null && user.getEmoji() == "")
                    {
                        if (crown != null)
                        {
                            crown.visibility = View.GONE
                        }
                    }

                    if (user.getEmoji() == "burger")
                    {
                        if (burger != null)
                        {
                            burger.visibility = View.VISIBLE
                        }
                    }

                    if (burger != null && user.getEmoji() == "")
                    {
                        if (burger != null)
                        {
                            burger.visibility = View.GONE
                        }
                    }

                    if (heart != null && user.getEmoji() == "")
                    {
                        if (heart != null)
                        {
                            heart.visibility = View.GONE
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

                    view?.pro_image_profile_frag?.let {
                        Glide.with(context!!.applicationContext).load(user!!.getImage()).into(
                            it
                        )
                    }
                    view?.profile_fragment_username?.text = user!!.getUsername()
                    view?.full_name_profile_frag?.text = user!!.getFullname()
                    view?.info_profile_frag?.text = user!!.getBio()
                    view?.city_profile_frag?.text = user!!.getCity()
                    view?.birthday_profile_frag?.text = user!!.getBirthday()
                    view?.specialty_profile_frag?.text = user!!.getSpecialty()


                    x = user.getBirthday()
                    y = x.substringBeforeLast("-")

                    val now = LocalDateTime.now()
                    var formatter = DateTimeFormatter.ofPattern("dd-MM")

                    if (x.substringBeforeLast("-") == formatter.format(now).toString()) {
                        view?.party_hat?.visibility = View.VISIBLE
                    }

                    if (formatter.format(now).toString() == "19-12" ||
                        formatter.format(now).toString() == "20-12" ||
                        formatter.format(now).toString() == "21-12" ||
                        formatter.format(now).toString() == "22-12" ||
                        formatter.format(now).toString() == "23-12" ||
                        formatter.format(now).toString() == "24-12" ||
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
                        view?.winter_hat?.visibility = View.VISIBLE
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
                        view?.pumpkin?.visibility = View.VISIBLE
                    }
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
                    total_posts.text = " $postCounter"
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    private fun mySaves()
    {
        mySavesImg = ArrayList()

        val savedRef = FirebaseDatabase.getInstance()
            .reference
            .child("Saves").child(firebaseUser.uid)

        savedRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    for (snapshot in dataSnapshot.children)
                    {
                        (mySavesImg as ArrayList<String>).add(snapshot.key!!)
                    }
                    readSavedImagesData()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    private fun readSavedImagesData()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    (postListSaved as ArrayList<Post>).clear()

                    for (snapshot in dataSnapshot.children)
                    {
                        val post = snapshot.getValue(Post::class.java)

                        for (key in mySavesImg!!)
                        {
                            if (post!!.getPostid() == key)
                            {
                                (postListSaved as ArrayList<Post>).add(post!!)
                            }
                        }
                    }
                    myImagesAdapterSavedImg!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
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

        alpha_screen?.visibility = View.GONE
        options_screen?.visibility = View.GONE
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
}