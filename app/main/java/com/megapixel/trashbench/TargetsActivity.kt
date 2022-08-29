package com.megapixel.trashbench

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Adapter.TargetAdapter
import com.megapixel.trashbench.Model.Targets
import kotlinx.android.synthetic.main.activity_targets.*


@Suppress("UNCHECKED_CAST")
class TargetsActivity : AppCompatActivity()
{
    private var targetAdapter: TargetAdapter? = null
    private var targetList: MutableList<com.megapixel.trashbench.Model.Targets>? = null

    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_targets)


        firebaseUser = FirebaseAuth.getInstance().currentUser

        var recyclerView: RecyclerView = findViewById(R.id.recycler_view_targets)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        targetList = ArrayList()
        targetAdapter = TargetAdapter(this, targetList as ArrayList<Targets>)
        recyclerView.adapter = targetAdapter




        back_target.setOnClickListener {
            finish()
        }

        add_target.setOnClickListener {
            val intent = Intent(this@TargetsActivity, AddTargetActivity::class.java)
            startActivity(intent)
            finish()
        }

        retrieveTargets()
    }


    private fun retrieveTargets()
    {
        val targetsRef = FirebaseDatabase.getInstance()
            .reference.child("Targets")

        targetsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    targetList!!.clear()
                    target_big.visibility = View.VISIBLE

                    for (snapshot in p0.children)
                    {
                        val target = snapshot.getValue(com.megapixel.trashbench.Model.Targets::class.java)
                        targetList!!.add(target!!)
                        target_big.visibility = View.GONE
                    }

                    targetAdapter!!.notifyDataSetChanged()
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

        val hashMap = java.util.HashMap<String, Any>()
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