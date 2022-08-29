package com.megapixel.trashbench

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_add_post.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddPostActivity : AppCompatActivity()
{
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null
    var firebaseUser: FirebaseUser? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val addPostAnim = AnimationUtils.loadAnimation(this, R.anim.edit_profile_animation)
        val addPostAnimSecond = AnimationUtils.loadAnimation(this, R.anim.edit_profile_image)

        val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar_layout_add_image)
        val addPostLayout = findViewById<LinearLayout>(R.id.add_post_layout)

        appBarLayout.startAnimation(addPostAnim)
        addPostLayout.startAnimation(addPostAnimSecond)


        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Post Pictures")

        save_new_post_btn.setOnClickListener { uploadImage() }

        close_add_post_btn.setOnClickListener {

            val intent = Intent(this@AddPostActivity, MainActivity::class.java)
            startActivity(intent)
            finish()

            overridePendingTransition(R.anim.top_out, R.anim.none)
        }

        add_image_post.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AddPostActivity)
        }

        image_post.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AddPostActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode == Activity.RESULT_OK  &&  data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_post.setImageURI(imageUri)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadImage()
    {
        when (imageUri) {
            null -> Toast.makeText(this, R.string.select_image, Toast.LENGTH_SHORT).show()
            else -> {
                add_post_progress_bar?.visibility = View.VISIBLE
                add_post_background?.visibility = View.VISIBLE

                val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            add_post_progress_bar?.visibility = View.GONE
                            add_post_background?.visibility = View.GONE
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                })
                    .addOnCompleteListener ( OnCompleteListener<Uri> {task ->
                        if (task.isSuccessful)
                        {
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                            val postId = ref.push().key

                            val now = LocalDateTime.now()
                            var formatter = DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")

                            val postMap = HashMap<String, Any>()
                            postMap["postid"] = postId!!
                            postMap["description"] = description_post.text.toString()
                            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                            postMap["postimage"] = myUrl
                            postMap["time"] = formatter.format(now).toString()

                            ref.child(postId).updateChildren(postMap)

                            Toast.makeText(this, R.string.uploaded_successfully, Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@AddPostActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                            add_post_progress_bar?.visibility = View.GONE
                            add_post_background?.visibility = View.GONE
                        }
                        else
                        {
                            add_post_progress_bar?.visibility = View.GONE
                            add_post_background?.visibility = View.GONE
                        }
                    })
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this@AddPostActivity, MainActivity::class.java)
        startActivity(intent)
        finish()

         overridePendingTransition(R.anim.top_out, R.anim.none)
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