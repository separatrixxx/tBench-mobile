package com.megapixel.trashbench

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.megapixel.trashbench.Model.Clubs
import com.megapixel.trashbench.Model.User
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_add_club.*
import kotlinx.android.synthetic.main.activity_club_settings.*
import java.util.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ClubSettingsActivity : AppCompatActivity()
{

    var firebaseUser: FirebaseUser? = null
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private lateinit var storageClubPicRef: StorageReference
    private lateinit var clubId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        val intent = intent
        clubId = intent.getStringExtra("clubId")


        close_edit_club_btn.setOnClickListener {
            onBackPressed()
        }


        club_edit_image.setOnClickListener {
            checker = "clicked"

            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@ClubSettingsActivity)
        }

        save_edit_club_btn.setOnClickListener {
            if (checker == "clicked")
            {
                uploadImageAndUpdateClubInfo()
            }
            else
            {
                updateClubInfoOnly()
            }
        }



        userInfo()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode == Activity.RESULT_OK  &&  data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            club_edit_image.setImageURI(imageUri)
        }
    }

    private fun uploadImageAndUpdateClubInfo()
    {
        when
        {
            imageUri == null -> Toast.makeText(this, R.string.select_club_image, Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(name_club_edit.text.toString()) -> {
                Toast.makeText(this, R.string.write_club_name, Toast.LENGTH_SHORT).show()
            }

            else -> {
                club_edit_progress_bar?.visibility = View.VISIBLE
                club_edit_background?.visibility = View.VISIBLE

                val fileRef = storageClubPicRef.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            club_edit_progress_bar?.visibility = View.GONE
                            club_edit_background?.visibility = View.GONE
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
                            clubMap["clubname"] = name_club_edit.text.toString()
                            clubMap["clubdescription"] = description_club_edit.text.toString()
                            clubMap["clubimage"] = myUrl

                            ref.child(clubId).updateChildren(clubMap)

                            Toast.makeText(this, R.string.updated_club, Toast.LENGTH_SHORT).show()

                            club_edit_progress_bar?.visibility = View.GONE
                            club_edit_background?.visibility = View.GONE
                        }
                        else
                        {
                            club_edit_progress_bar?.visibility = View.GONE
                            club_edit_background?.visibility = View.GONE
                        }
                    })
            }
        }
    }


    private fun updateClubInfoOnly()
    {
        when {
            TextUtils.isEmpty(name_club_edit.text.toString()) -> {
                Toast.makeText(this, R.string.write_club_name, Toast.LENGTH_SHORT).show()
            }
            else -> {
                val clubRef = FirebaseDatabase.getInstance().reference.child("Clubs")

                val clubMap = HashMap<String, Any>()
                clubMap["clubname"] = name_club_edit.text.toString()
                clubMap["clubdescription"] = description_club_edit.text.toString()

                clubRef.child(clubId).updateChildren(clubMap)

                Toast.makeText(this, R.string.updated_club, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun userInfo()
    {
        val clubsRef = FirebaseDatabase.getInstance().reference.child("Clubs").child(clubId)

        clubsRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val club = p0.getValue<Clubs>(Clubs::class.java)

                    Glide.with(applicationContext).load(club!!.getClubimage()).into(club_edit_image)
                    name_club_edit.setText(club.getClubname())
                    description_club_edit.setText(club.getClubdescription())
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