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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_club.*
import kotlinx.android.synthetic.main.activity_add_post.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.HashMap

class AddClubActivity : AppCompatActivity()
{

    private var myUrl = ""
    private var imageUri: Uri? = null
    var firebaseUser: FirebaseUser? = null
    private var storageClubPicRef: StorageReference? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_club)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        close_add_club_btn.setOnClickListener {
            onBackPressed()
        }

        storageClubPicRef = FirebaseStorage.getInstance().reference.child("Club Pictures")

        save_club_btn.setOnClickListener { uploadClub() }

        club_image.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AddClubActivity)
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode == Activity.RESULT_OK  &&  data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            club_image.setImageURI(imageUri)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadClub()
    {
        when (imageUri) {
            null -> Toast.makeText(this, R.string.select_club_image, Toast.LENGTH_SHORT).show()
            else -> {
                club_progress_bar?.visibility = View.VISIBLE
                club_background?.visibility = View.VISIBLE

                val fileRef = storageClubPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            club_progress_bar?.visibility = View.GONE
                            club_background?.visibility = View.GONE
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                })
                    .addOnCompleteListener ( OnCompleteListener<Uri> {task ->
                        if (task.isSuccessful)
                        {
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val ref = FirebaseDatabase.getInstance().reference.child("Clubs")
                            val clubId = (ref.push().key).toString()

                            val idClub = clubId

                            val clubMap = HashMap<String, Any>()
                            clubMap["clubId"] = clubId
                            clubMap["clubname"] = name_club.text.toString()
                            clubMap["clubdescription"] = description_club.text.toString()
                            clubMap["clubimage"] = myUrl
                            clubMap["ownerId"] = firebaseUser!!.uid
                            clubMap["idClub"] = idClub

                            ref.child(clubId).updateChildren(clubMap)

                            Toast.makeText(this, R.string.club_created_successfully, Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@AddClubActivity, ClubsActivity::class.java)
                            startActivity(intent)
                            finish()

                            club_progress_bar?.visibility = View.GONE
                            club_background?.visibility = View.GONE
                        }
                        else
                        {
                            club_progress_bar?.visibility = View.GONE
                            club_background?.visibility = View.GONE
                        }
                    })
            }
        }
    }


    private fun updateStatus(status: String)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val hashMap = HashMap<String, Any>()
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