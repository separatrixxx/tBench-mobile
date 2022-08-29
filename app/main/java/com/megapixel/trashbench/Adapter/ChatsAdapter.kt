package com.megapixel.trashbench.Adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.megapixel.trashbench.Model.Chat
import com.megapixel.trashbench.R
import com.megapixel.trashbench.Utils.isInCurrentYear
import com.megapixel.trashbench.Utils.isToday
import com.megapixel.trashbench.ViewFullImageActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ChatsAdapter(
    mContext: Context,
    mChatList: List<Chat>,
    imageUrl: String

) : RecyclerView.Adapter<ChatsAdapter.ViewHolder?>()

{
    private val mContext: Context
    private val mChatList: List<Chat>
    private val imageUrl: String
    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    var isEnabled: Boolean = false

    var x: String = ""
    var y: String = ""
    var r: String = ""
    var o: String = ""

    init {
        this.mChatList = mChatList
        this.mContext = mContext
        this.imageUrl = imageUrl
    }



    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder
    {
        return if (position == 1)
        {
            val view: View = LayoutInflater.from(mContext).inflate(com.megapixel.trashbench.R.layout.message_item_right, parent, false)
            ViewHolder(view)
        }
        else
        {
            val view: View = LayoutInflater.from(mContext).inflate(com.megapixel.trashbench.R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val chat: Chat = mChatList[position]

        holder.profile_image?.let { Picasso.get().load(imageUrl).fit().into(it) }

        if (chat.getMessage().equals("Sent you an image") && !chat.getUrl().equals(""))
        {
            if (chat.getSender().equals(firebaseUser!!.uid))
            {
                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_card_view!!.visibility = View.VISIBLE
                holder.options_image?.visibility = View.VISIBLE
                holder.right_image_view?.let {
                    Glide.with(mContext.applicationContext).load(chat.getUrl()).into(
                        it
                    )
                }

                holder.right_image_view!!.setOnClickListener {
                    val intent = Intent(mContext, ViewFullImageActivity::class.java)
                    intent.putExtra("url", chat.getUrl())
                    mContext.startActivity(intent)
                }


                holder.right_image_view!!.setOnLongClickListener(OnLongClickListener {

                    deleteSentMessage(position, holder)

                    true
                })
            }

            else if (!chat.getSender().equals(firebaseUser!!.uid))
            {
                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_card_view!!.visibility = View.VISIBLE
                Glide.with(mContext.applicationContext).load(chat.getUrl()).into(holder.left_image_view!!)

                holder.left_image_view!!.setOnClickListener {
                    val intent = Intent(mContext, ViewFullImageActivity::class.java)
                    intent.putExtra("url", chat.getUrl())
                    mContext.startActivity(intent)
                }

            }
        }
        else
        {
            holder.show_text_message!!.text = chat.getMessage()

            if (firebaseUser!!.uid == chat.getSender())
            {
                holder.show_text_message!!.setOnLongClickListener(OnLongClickListener {

                    holder.options_message?.visibility = View.VISIBLE
                    holder.show_text_message?.setBackgroundResource(R.drawable.background_right_set)

                    true
                })
            }
        }

        if (firebaseUser!!.uid == chat.getSender() )
        {
            holder.show_text_message?.setOnClickListener {
                holder.options_message?.visibility = View.GONE
                holder.show_text_message?.setBackgroundResource(R.drawable.background_right)
                holder.delete_messages?.visibility = View.VISIBLE
                holder.confirm_delete_messages?.visibility = View.GONE
            }
        }




        holder.show_text_message!!.setOnClickListener {
            if (!isEnabled)
            {
                holder.time!!.visibility = View.VISIBLE
                isEnabled = true
            }
            else
            {
                holder.time!!.visibility = View.GONE
                isEnabled = false
            }
        }


        val formatter = SimpleDateFormat("hh:mm a dd-MM-yyyy", Locale.getDefault())

        val date = formatter.parse(chat.getTime())

        if (date.isToday()) {
            formatter.applyPattern("HH:mm")
        } else if (!date.isToday() && date.isInCurrentYear()
        ) {
            formatter.applyPattern("d MMMM,  HH:mm")
        } else {
            formatter.applyPattern("d MMMM yyyy,  HH:mm")
        }

        holder.time!!.text = formatter.format(date)


        holder.cancel_messages?.setOnClickListener {
            holder.options_message?.visibility = View.GONE
            holder.show_text_message?.setBackgroundResource(R.drawable.background_right)
            holder.delete_messages?.visibility = View.VISIBLE
            holder.confirm_delete_messages?.visibility = View.GONE
        }

        holder.delete_messages?.setOnClickListener {
            holder.delete_messages?.visibility = View.GONE
            holder.confirm_delete_messages?.visibility = View.VISIBLE
        }

        holder.confirm_delete_messages?.setOnClickListener {
            holder.options_message?.visibility = View.GONE
            holder.show_text_message?.setBackgroundResource(R.drawable.background_right)
            deleteSentMessage(position, holder)
        }


        if (position == mChatList.size-1)
        {
            if (chat.isIsSeen())
            {
                holder.text_seen!!.text = "Seen"
                holder.image_seen!!.visibility = View.GONE

                if (chat.getMessage().equals("Sent you an image") && chat.getUrl().equals(""))
                {
                    val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 145, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }
            }
            else
            {
                holder.text_seen!!.text = "Sent"
                holder.image_seen!!.visibility = View.VISIBLE

                if (chat.getMessage().equals("Sent you an image") && chat.getUrl().equals(""))
                {
                    val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 145, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }
            }

        }
        else
        {
            holder.text_seen!!.visibility = View.GONE
            holder.image_seen!!.visibility = View.GONE
        }

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profile_image: CircleImageView? = null
        var show_text_message: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: TextView? = null
        var right_image_view: ImageView? = null
        var right_image_card_view: CardView? = null
        var left_image_card_view: CardView? = null
        var options_message: LinearLayout? = null
        var delete_messages: ImageView? = null
        var confirm_delete_messages: ImageView? = null
        var cancel_messages: ImageView? = null
        var options_image: LinearLayout? = null
        var delete_image: ImageView? = null
        var confirm_delete_image: ImageView? = null
        var cancel_image: ImageView? = null
        var image_seen: ImageView? = null
        var time: TextView? = null

        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)
            right_image_card_view = itemView.findViewById(R.id.right_image_card_view)
            left_image_card_view = itemView.findViewById(R.id.left_image_card_view)
            options_message = itemView.findViewById(R.id.options_message)
            delete_messages = itemView.findViewById(R.id.delete_messages)
            confirm_delete_messages = itemView.findViewById(R.id.confirm_delete_messages)
            cancel_messages = itemView.findViewById(R.id.cancel_messages)
            options_image = itemView.findViewById(R.id.options_message)
            delete_image = itemView.findViewById(R.id.delete_messages)
            confirm_delete_image = itemView.findViewById(R.id.confirm_delete_messages)
            cancel_image = itemView.findViewById(R.id.cancel_messages)
            image_seen = itemView.findViewById(R.id.image_seen)
            time = itemView.findViewById(R.id.time)
        }

    }

    override fun getItemViewType(position: Int): Int
    {

        return if (mChatList[position].getSender().equals(firebaseUser!!.uid))
        {
            1
        }
        else
        {
            0
        }
    }


    private fun deleteSentMessage(position: Int, holder: ViewHolder)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList.get(position).getMessageId()!!)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(holder.itemView.context, R.string.deleted, Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(holder.itemView.context, R.string.not_deleted, Toast.LENGTH_SHORT).show()
                }
            }
    }
}