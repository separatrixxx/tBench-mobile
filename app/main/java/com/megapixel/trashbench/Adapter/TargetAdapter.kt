package com.megapixel.trashbench.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.megapixel.trashbench.Model.Targets
import com.megapixel.trashbench.Model.User
import com.megapixel.trashbench.R
import com.megapixel.trashbench.Utils.firebaseUser
import com.megapixel.trashbench.Utils.isInCurrentYear
import com.megapixel.trashbench.Utils.isToday
import java.text.SimpleDateFormat
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class TargetAdapter(
    private val mContext: Context,
    private val mTarget: ArrayList<Targets>
) : RecyclerView.Adapter<TargetAdapter.ViewHolder>()
{

    var currentUserId: String = ""
    var isEnabled: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.target_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mTarget.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        val target = mTarget[position]

        holder.targetText.text = target.getTarget()
        holder.targetText2.text = target.getTarget()

        val formatter = SimpleDateFormat("hh:mm a dd-MM-yyyy", Locale.getDefault())

        val date = formatter.parse(target.getTime())

        if (date.isToday()) {
            formatter.applyPattern("HH:mm")
        } else if (!date.isToday() && date.isInCurrentYear()
        ) {
            formatter.applyPattern("d MMMM,  HH:mm")
        } else {
            formatter.applyPattern("d MMMM yyyy,  HH:mm")
        }

        holder.timeTarget.text = formatter.format(date)
        holder.timeTarget2.text = formatter.format(date)



        holder.target_field.setOnLongClickListener(View.OnLongClickListener {

            if (holder.targetText.maxLines == 1)
            {
                holder.target_field_second.visibility = View.VISIBLE
                holder.target_field.visibility = View.GONE
                holder.targetText2.maxLines = 1
            }
            else
            {
                holder.target_field_second.visibility = View.VISIBLE
                holder.target_field.visibility = View.GONE
                holder.targetText2.maxLines = 1000
            }

            true
        })

        holder.cancel_target.setOnClickListener {
            holder.target_field_second.visibility = View.GONE
            holder.target_field.visibility = View.VISIBLE
        }

        holder.delete_target.setOnClickListener {
            holder.delete_target.visibility = View.GONE
            holder.confirm_delete_target.visibility = View.VISIBLE
        }

        holder.confirm_delete_target.setOnClickListener {
            holder.delete_target.visibility = View.VISIBLE
            holder.confirm_delete_target.visibility = View.GONE
            holder.options_target.visibility = View.GONE

            val ref = FirebaseDatabase.getInstance().reference
                .child("Targets")
                .child(target.getTargetid())

            ref.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(mContext, R.string.deleted, Toast.LENGTH_SHORT).show()
                }
            }

            restartActivity(act = mContext as Activity)
        }

        holder.target_field_second.setOnClickListener {
            holder.target_field_second.visibility = View.GONE
            holder.target_field.visibility = View.VISIBLE
        }

        holder.target_field_second.setOnLongClickListener(View.OnLongClickListener {
            holder.target_field_second.visibility = View.GONE
            holder.target_field.visibility = View.VISIBLE

            true
        })


        holder.target_field.setOnClickListener {
            if (!isEnabled)
            {
                holder.targetText.maxLines = 10000
                isEnabled = true
            }
            else
            {
                holder.targetText.maxLines = 1
                isEnabled = false
            }
        }

    }




    fun restartActivity(act: Activity) {
        val intent = Intent()
        intent.setClass(act, act.javaClass)
        act.startActivity(intent)
        act.finish()
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var targetText: TextView
        var timeTarget: TextView
        var targetText2: TextView
        var timeTarget2: TextView
        var delete_target: ImageView
        var confirm_delete_target: ImageView
        var cancel_target: ImageView
        var options_target: LinearLayout
        var target_field: CardView
        var target_field_second: CardView

        init {
            targetText = itemView.findViewById(R.id.publisher_target)
            timeTarget = itemView.findViewById(R.id.time_target)
            targetText2 = itemView.findViewById(R.id.publisher_target_second)
            timeTarget2 = itemView.findViewById(R.id.time_target_second)
            delete_target = itemView.findViewById(R.id.delete_target)
            confirm_delete_target = itemView.findViewById(R.id.confirm_delete_target)
            cancel_target = itemView.findViewById(R.id.cancel_target)
            options_target = itemView.findViewById(R.id.options_target)
            target_field = itemView.findViewById(R.id.target_field)
            target_field_second = itemView.findViewById(R.id.target_field_second)
        }
    }
}