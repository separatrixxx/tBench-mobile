package com.megapixel.trashbench

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.megapixel.trashbench.Model.User
import com.megapixel.trashbench.Utils.firebaseUser
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_account_settings.birthday_profile_frag
import kotlinx.android.synthetic.main.activity_account_settings.city_profile_frag
import kotlinx.android.synthetic.main.activity_account_settings.full_name_profile_frag
import kotlinx.android.synthetic.main.activity_account_settings.info_profile_frag
import kotlinx.android.synthetic.main.activity_account_settings.specialty_profile_frag
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.HashMap

class AccountSettingsActivity : AppCompatActivity() {

    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")


        val editAnim = AnimationUtils.loadAnimation(this, R.anim.edit_profile_animation)
        val editImageAnim = AnimationUtils.loadAnimation(this, R.anim.edit_profile_image)

        val editLayout = findViewById<RelativeLayout>(R.id.edit_profile_layout)
        val editImageLayout = findViewById<RelativeLayout>(R.id.profile_image_view_profile_frag_layout)
        val editLayoutSecond = findViewById<RelativeLayout>(R.id.edit_layout_second)
        val logoutCV = findViewById<CardView>(R.id.logout_btn_layout)
        val deleteCV = findViewById<CardView>(R.id.delete_account_btn_layout)

        editLayout.startAnimation(editAnim)
        editLayoutSecond.startAnimation(editAnim)
        logoutCV.startAnimation(editAnim)
        deleteCV.startAnimation(editAnim)
        editImageLayout.startAnimation(editImageAnim)


        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingsActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

            overridePendingTransition(R.anim.top_out, R.anim.none)
        }

        delete_account_btn.setOnClickListener {
            startActivity(Intent(this, DeleteAccountActivity::class.java))

            val alpha = AnimationUtils.loadAnimation(this, R.anim.alpha_screen)
            delete_screen.startAnimation(alpha)
            delete_screen.visibility = View.VISIBLE
        }

        profile_image_view_profile_frag.setOnClickListener {
            checker = "clicked"

            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AccountSettingsActivity)
        }

        save_info_profile_btn.setOnClickListener {
            if (checker == "clicked")
            {
                uploadImageAndUpdateInfo()
            }
            else
            {
                updateUserInfoOnly()
            }
        }

        close_profile_btn.setOnClickListener {
            onBackPressed()
        }

        userInfo()

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        birthday_profile_frag.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->
                birthday_profile_frag.setText(""+ mDay +"-"+ (mMonth+1) +"-"+ mYear)
            }, year, month, day)

            dpd.show()
        }

        clear_birthday_text_view.setOnClickListener {
            birthday_profile_frag.setText("")
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode == Activity.RESULT_OK  &&  data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_image_view_profile_frag.setImageURI(imageUri)
        }
    }


    private fun updateUserInfoOnly()
    {
        when {
            TextUtils.isEmpty(full_name_profile_frag.text.toString()) -> {
                Toast.makeText(this, R.string.write_full_name, Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(username_profile_frag.text.toString()) -> {
                Toast.makeText(this, R.string.write_user_name, Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(info_profile_frag.text.toString()) -> {
                Toast.makeText(this, R.string.write_profile_info, Toast.LENGTH_SHORT).show()
            }
            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = full_name_profile_frag.text.toString()
                userMap["username"] = username_profile_frag.text.toString()
                userMap["bio"] = info_profile_frag.text.toString()
                userMap["city"] = city_profile_frag.text.toString()
                userMap["birthday"] = birthday_profile_frag.text.toString()
                userMap["specialty"] = specialty_profile_frag.text.toString()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, R.string.updated_successfully, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Glide.with(applicationContext).load(user!!.getImage()).into(profile_image_view_profile_frag)
                    username_profile_frag.setText(user!!.getUsername())
                    full_name_profile_frag.setText(user!!.getFullname())
                    info_profile_frag.setText(user!!.getBio())
                    city_profile_frag.setText(user!!.getCity())
                    birthday_profile_frag.text = user!!.getBirthday()
                    specialty_profile_frag.setText(user!!.getSpecialty())
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    private fun uploadImageAndUpdateInfo()
    {
        when
        {
            imageUri == null -> Toast.makeText(this, R.string.select_image, Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(full_name_profile_frag.text.toString()) -> {
                Toast.makeText(this, R.string.write_full_name, Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(username_profile_frag.text.toString()) -> {
                Toast.makeText(this, R.string.write_user_name, Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(info_profile_frag.text.toString()) -> {
                Toast.makeText(this, R.string.write_profile_info, Toast.LENGTH_SHORT).show()
            }

            else -> {
                settings_progress_bar?.visibility = View.VISIBLE
                settings_background?.visibility = View.VISIBLE

                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            settings_progress_bar?.visibility = View.GONE
                            settings_background?.visibility = View.GONE
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener ( OnCompleteListener<Uri> {task ->
                    if (task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = full_name_profile_frag.text.toString()
                        userMap["username"] = username_profile_frag.text.toString()
                        userMap["bio"] = info_profile_frag.text.toString()
                        userMap["city"] = city_profile_frag.text.toString()
                        userMap["birthday"] = birthday_profile_frag.text.toString()
                        userMap["specialty"] = specialty_profile_frag.text.toString()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, R.string.updated_successfully, Toast.LENGTH_SHORT).show()

                        settings_progress_bar?.visibility = View.GONE
                        settings_background?.visibility = View.GONE
                    }
                    else
                    {
                        settings_progress_bar?.visibility = View.GONE
                        settings_background?.visibility = View.GONE
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


        delete_screen?.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()

        updateStatus("offline")
    }
}