package com.megapixel.trashbench

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
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
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.birthday_profile_frag
import kotlinx.android.synthetic.main.activity_account_settings.city_profile_frag
import kotlinx.android.synthetic.main.activity_account_settings.clear_birthday_text_view
import kotlinx.android.synthetic.main.activity_account_settings.info_profile_frag
import kotlinx.android.synthetic.main.activity_account_settings.profile_image_view_profile_frag
import kotlinx.android.synthetic.main.activity_account_settings.save_info_profile_btn
import kotlinx.android.synthetic.main.activity_account_settings.specialty_profile_frag
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register_settings.*
import java.util.*
import kotlin.collections.HashMap

class RegisterSettingsActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var recyclerView: RecyclerView? = null
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")


        profile_image_view_profile_frag.setOnClickListener {
            checker = "clicked"

            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@RegisterSettingsActivity)

            info_profile_frag.setBackgroundResource(R.drawable.button_main)
            city_profile_frag.setBackgroundResource(R.drawable.button_main)
            birthday_profile_frag.setBackgroundResource(R.drawable.button_main)
            specialty_profile_frag.setBackgroundResource(R.drawable.button_main)
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

            info_profile_frag.setBackgroundResource(R.drawable.button_main)
            city_profile_frag.setBackgroundResource(R.drawable.button_main)
            birthday_profile_frag.setBackgroundResource(R.drawable.button_main)
            specialty_profile_frag.setBackgroundResource(R.drawable.button_main)
        }


        userInfo()

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        birthday_profile_frag.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->
                birthday_profile_frag.setText(""+ mDay +"/"+ (mMonth+1) +"/"+ mYear)
            }, year, month, day)

            dpd.show()
        }

        clear_birthday_text_view.setOnClickListener {
            birthday_profile_frag.text = ""
        }



        info_profile_frag.setOnTouchListener { v, event ->
            info_profile_frag.setBackgroundResource(R.drawable.button_set)
            city_profile_frag.setBackgroundResource(R.drawable.button_main)
            birthday_profile_frag.setBackgroundResource(R.drawable.button_main)
            specialty_profile_frag.setBackgroundResource(R.drawable.button_main)

            v?.onTouchEvent(event) ?: true
        }

        city_profile_frag.setOnTouchListener { v, event ->
            info_profile_frag.setBackgroundResource(R.drawable.button_main)
            city_profile_frag.setBackgroundResource(R.drawable.button_set)
            birthday_profile_frag.setBackgroundResource(R.drawable.button_main)
            specialty_profile_frag.setBackgroundResource(R.drawable.button_main)

            v?.onTouchEvent(event) ?: true
        }

        birthday_profile_frag.setOnTouchListener { v, event ->
            info_profile_frag.setBackgroundResource(R.drawable.button_main)
            city_profile_frag.setBackgroundResource(R.drawable.button_main)
            birthday_profile_frag.setBackgroundResource(R.drawable.button_set)
            specialty_profile_frag.setBackgroundResource(R.drawable.button_main)

            v?.onTouchEvent(event) ?: true
        }

        specialty_profile_frag.setOnTouchListener { v, event ->
            info_profile_frag.setBackgroundResource(R.drawable.button_main)
            city_profile_frag.setBackgroundResource(R.drawable.button_main)
            birthday_profile_frag.setBackgroundResource(R.drawable.button_main)
            specialty_profile_frag.setBackgroundResource(R.drawable.button_set)

            v?.onTouchEvent(event) ?: true
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

            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

                val userMap = HashMap<String, Any>()
                userMap["bio"] = info_profile_frag.text.toString()
                userMap["city"] = city_profile_frag.text.toString()
                userMap["birthday"] = birthday_profile_frag.text.toString()
                userMap["specialty"] = specialty_profile_frag.text.toString()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, R.string.updated_successfully, Toast.LENGTH_SHORT).show()


                registration_settings_background.visibility = View.VISIBLE
                registration_settings_progress_bar.visibility = View.VISIBLE


                val intent = Intent(this@RegisterSettingsActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
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

                    Picasso.get().load(user!!.getImage()).fit().into(profile_image_view_profile_frag)
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

            else -> {
                registration_settings_progress_bar?.visibility = View.VISIBLE
                registration_settings_background?.visibility = View.VISIBLE

                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            registration_settings_progress_bar?.visibility = View.GONE
                            registration_settings_background?.visibility = View.GONE
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
                        userMap["bio"] = info_profile_frag.text.toString()
                        userMap["city"] = city_profile_frag.text.toString()
                        userMap["birthday"] = birthday_profile_frag.text.toString()
                        userMap["specialty"] = specialty_profile_frag.text.toString()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, R.string.updated_successfully, Toast.LENGTH_SHORT).show()

                        registration_settings_progress_bar?.visibility = View.GONE
                        registration_settings_background?.visibility = View.GONE
                        val intent = Intent(this@RegisterSettingsActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        registration_settings_progress_bar?.visibility = View.GONE
                        registration_settings_background?.visibility = View.GONE
                    }
                })
            }
        }

    }
}