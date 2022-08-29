package com.megapixel.trashbench

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_add_story.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddStoryActivity : AppCompatActivity()
{
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageStoryPicRef: StorageReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        storageStoryPicRef = FirebaseStorage.getInstance().reference.child("Story Pictures")


        CropImage.activity()
            .setAspectRatio(5,6)
            .start(this@AddStoryActivity)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode == Activity.RESULT_OK  &&  data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri

            uploadStory()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadStory()
    {
        when (imageUri) {
            null -> Toast.makeText(this, R.string.select_image, Toast.LENGTH_SHORT).show()
            else -> {
                story_progress_bar?.visibility = View.VISIBLE
                story_background?.visibility = View.VISIBLE

                val fileRef = storageStoryPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            story_progress_bar?.visibility = View.GONE
                            story_background?.visibility = View.GONE
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                })
                    .addOnCompleteListener ( OnCompleteListener<Uri> {task ->
                        if (task.isSuccessful)
                        {
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val ref = FirebaseDatabase.getInstance().reference
                                .child("Story")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)

                            val storyId = (ref.push().key).toString()

                            val timeEnd = System.currentTimeMillis() + 86400000 //one day later

                            val now = LocalDateTime.now()
                            var formatter = DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy")

                            val storyMap = HashMap<String, Any>()
                            storyMap["userid"] = FirebaseAuth.getInstance().currentUser!!.uid
                            storyMap["timestart"] = ServerValue.TIMESTAMP
                            storyMap["timeend"] = timeEnd
                            storyMap["imageurl"] = myUrl
                            storyMap["storyid"] = storyId
                            storyMap["time"] = formatter.format(now).toString()

                            ref.child(storyId).updateChildren(storyMap)

                            Toast.makeText(this, R.string.story_uploaded_successfully, Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                            story_progress_bar?.visibility = View.GONE
                            story_background?.visibility = View.GONE
                        }
                        else
                        {
                            story_progress_bar?.visibility = View.GONE
                            story_background?.visibility = View.GONE
                        }
                    })
            }
        }
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