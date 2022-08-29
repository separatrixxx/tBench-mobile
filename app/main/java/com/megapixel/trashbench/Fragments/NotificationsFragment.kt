package com.megapixel.trashbench.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Adapter.NotificationAdapter
import com.megapixel.trashbench.Model.NotificationModel
import com.megapixel.trashbench.R
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class NotificationsFragment : Fragment()
{
    private var notificationList: List<NotificationModel>? = null
    private var notificationAdapter: NotificationAdapter? = null


    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_notifications, container, false)


        val notificationAnimRV = AnimationUtils.loadAnimation(view.context, R.anim.recycler_view_comments)

        val notificationRV = view?.findViewById<RecyclerView>(R.id.recycler_view_notifications)

        notificationRV?.startAnimation(notificationAnimRV)


        var recyclerView: RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_notifications)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)


        notificationList = ArrayList()


        notificationAdapter = NotificationAdapter(context!!, notificationList as ArrayList<NotificationModel>)
        recyclerView.adapter = notificationAdapter



        readNotifications()

        view.back_notifications.setOnClickListener {
            activity?.onBackPressed()
        }


        return view
    }


    private fun readNotifications()
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        notiRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(datasnapshot: DataSnapshot)
            {
                if (datasnapshot.exists())
                {
                    (notificationList as ArrayList<NotificationModel>).clear()
                    notifications_big?.visibility = View.VISIBLE

                    for (snapshot in datasnapshot.children)
                    {
                        val notification = snapshot.getValue(NotificationModel::class.java)

                        (notificationList as ArrayList<NotificationModel>).add(notification!!)
                        notifications_big?.visibility = View.GONE
                    }

                    Collections.reverse(notificationList)
                    notificationAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    companion object {
        fun newInstance(param1: String, param2: String) =
            NotificationsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}