package com.megapixel.trashbench


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.megapixel.trashbench.Adapter.ChatsAdapter
import com.megapixel.trashbench.Fragments.OtherPeopleProfileFragment
import com.megapixel.trashbench.Model.Chat
import com.megapixel.trashbench.Model.User
import kotlinx.android.synthetic.main.activity_messages_chat.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MessagesChatActivity : AppCompatActivity()
{
    var userId: String = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapter: ChatsAdapter? = null
    var mChatList: List<Chat>? = null
    lateinit var recycler_view_messages: RecyclerView
    var reference: DatabaseReference? = null

    private val REQUEST_CODE_SPEECH_INPUT = 100

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_chat)

        overridePendingTransition(R.anim.chat_right, R.anim.chat_right)

        intent = intent
        userId = intent.getStringExtra("userId")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        recycler_view_messages= findViewById(R.id.recycler_view_messages)
        recycler_view_messages.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager (applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycler_view_messages.layoutManager = linearLayoutManager
        

        reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userId)
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                val user: User? = p0.getValue(User::class.java)

                messages_full_name.text = user!!.getFullname()
                messages_username.text = user.getUsername()
                messages_chat_username.text = user.getUsername()

                if (user.getDeleted() != "true")
                {
                    Glide.with(applicationContext).load(user.getImage()).into(user_profile_image_messages)
                }
                else
                {
                    Glide.with(applicationContext).load(R.drawable.profile_deleted_image).into(user_profile_image_messages)
                }


                retrieveMessages(firebaseUser!!.uid, userId, user.getImage())
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })





        message_chat_layout.setOnClickListener {
            val editor = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("profileId", userId)
            editor.apply()

            message_chat_layout.visibility = View.INVISIBLE
            user_profile_image_messages.visibility = View.INVISIBLE
            user_profile_image_progress_bar.visibility = View.INVISIBLE
            messages_chat_username.visibility = View.VISIBLE

            val fragmentTrans = supportFragmentManager.beginTransaction()
            fragmentTrans.replace(R.id.fragment_container_chat, OtherPeopleProfileFragment())
            fragmentTrans.commit()
        }

        user_profile_image_messages.setOnClickListener {
            val editor = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()

            editor.putString("profileId", userId)
            editor.apply()

            message_chat_layout.visibility = View.INVISIBLE
            user_profile_image_messages.visibility = View.INVISIBLE
            user_profile_image_progress_bar.visibility = View.INVISIBLE
            messages_chat_username.visibility = View.VISIBLE

            val fragmentTrans = supportFragmentManager.beginTransaction()
            fragmentTrans.replace(R.id.fragment_container_chat, OtherPeopleProfileFragment())
            fragmentTrans.commit()
        }


        voice_message.setOnClickListener {
            speak()
        }

        back_messages.setOnClickListener {
            onBackPressed()
        }

        send_message.setOnClickListener{
            val message = type_message.text.toString()

            if (message == "")
            {
                Toast.makeText(this@MessagesChatActivity, R.string.write_message_first, Toast.LENGTH_SHORT).show()
            }
            else
            {
                sendMessageToUser(firebaseUser!!.uid, userId, message)
            }
            type_message.setText("")
        }

        attach_file.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"), 438)
        }


        emoji_messages.setOnClickListener {
            val emojiRL: RelativeLayout = findViewById(R.id.emoji_layout)
            val params: RelativeLayout.LayoutParams = emojiRL.layoutParams as RelativeLayout.LayoutParams // получаем параметры

            val height = 900

            params.height = height

            emojiRL.layoutParams = params


            emoji_messages.visibility = View.INVISIBLE
            emoji_messages_set.visibility = View.VISIBLE

            hideKeyboard()
        }

        emoji_messages_set.setOnClickListener {
            val emojiRL: RelativeLayout = findViewById(R.id.emoji_layout)
            val params: RelativeLayout.LayoutParams = emojiRL.layoutParams as RelativeLayout.LayoutParams // получаем параметры

            val height = 0

            params.height = height

            emojiRL.layoutParams = params

            emoji_messages.visibility = View.VISIBLE
            emoji_messages_set.visibility = View.INVISIBLE

            hideKeyboard()
        }

        type_message.setOnTouchListener { v, event ->
            val emojiRL: RelativeLayout = findViewById(R.id.emoji_layout)
            val params: RelativeLayout.LayoutParams = emojiRL.layoutParams as RelativeLayout.LayoutParams // получаем параметры

            val height = 0

            params.height = height

            emojiRL.layoutParams = params

            emoji_messages.visibility = View.VISIBLE
            emoji_messages_set.visibility = View.INVISIBLE

            v?.onTouchEvent(event) ?: true
        }


        seenMessage(userId)

        updateMP()

        deletedUser()
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    private fun deletedUser()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                   if (user!!.getDeleted() == "true")
                    {
                        send_layout.visibility = View.INVISIBLE
                        deleted_layout.visibility = View.VISIBLE
                    }
                    else
                   {
                       send_layout.visibility = View.VISIBLE
                       deleted_layout.visibility = View.INVISIBLE
                   }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    private fun speak()
    {
        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the message you want to send...")

        try {
            startActivityForResult(mIntent, REQUEST_CODE_SPEECH_INPUT)
        }
        catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMP()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

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

                    if (heart != null && user.getEmoji() == "")
                    {
                        if (heart != null)
                        {
                            heart.visibility = View.GONE
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

                    if (mp != null && user.getMP() == "")
                    {
                        if (check_mark != null)
                        {
                            mp.visibility = View.GONE
                            check_mark.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String)
    {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val now = LocalDateTime.now()
        var formatter = DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey
        messageHashMap["time"] = formatter.format(now).toString()
        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val chatsListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userId)

                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot)
                        {
                            if (!p0.exists())
                            {
                                chatsListReference.child("id").setValue(userId)
                            }

                            val chatsListReceiverRef = FirebaseDatabase.getInstance()
                                .reference
                                .child("ChatList")
                                .child(userId)
                                .child(firebaseUser!!.uid)
                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(error: DatabaseError)
                        {

                        }
                    })


                    val reference = FirebaseDatabase.getInstance().reference
                        .child("Users").child(firebaseUser!!.uid)
                }
            }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data)
                {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    type_message.setText(result[0])
                }
            }
        }



        if (requestCode==438 && resultCode==RESULT_OK && data!=null && data!!.data!=null) {
            messages_progress_bar?.visibility = View.VISIBLE
            messages_background?.visibility = View.VISIBLE


            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val now = LocalDateTime.now()
                    var formatter = DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "Sent you an image"
                    messageHashMap["receiver"] = userId
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId
                    messageHashMap["time"] = formatter.format(now).toString()

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
                                messages_progress_bar?.visibility = View.GONE
                                messages_background?.visibility = View.GONE

                                val reference = FirebaseDatabase.getInstance().reference
                                    .child("Users").child(firebaseUser!!.uid)
                                reference.addValueEventListener(object : ValueEventListener{
                                    override fun onDataChange(p0: DataSnapshot)
                                    {

                                    }

                                    override fun onCancelled(p0: DatabaseError) {

                                    }
                                })
                            }
                        }

                }
            }
        }


    }

    private fun retrieveMessages(senderId: String, receiverId: String?, receiverImageUrl: String)
    {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()
                for (snapshot in p0.children)
                {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(senderId) && chat.getSender().equals(receiverId)
                        || chat.getReceiver().equals(receiverId) && chat.getSender().equals(senderId))
                    {
                        (mChatList as ArrayList<Chat>).add((chat))
                    }

                    chatsAdapter = ChatsAdapter(this@MessagesChatActivity, (mChatList as ArrayList<Chat>), receiverImageUrl!!)
                    recycler_view_messages.adapter = chatsAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    var seenListener: ValueEventListener? = null

    private fun seenMessage(userId: String)
    {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener = reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                for (dataSnapshot in p0.children)
                {
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat!!.getSender().equals(userId))
                    {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

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

        reference!!.removeEventListener(seenListener!!)
        updateStatus("offline")
    }
}