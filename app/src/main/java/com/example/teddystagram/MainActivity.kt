package com.example.teddystagram

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.teddystagram.databinding.ActivityMainBinding
import com.example.teddystagram.navigation.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val PICK_PROFILE_FROM_ALBUM = 10
    }

    private fun setToolbarDefault() {
        binding.toolbarUsername.visibility = View.GONE
        binding.toolbarBtnBack.visibility = View.GONE
        binding.toolbarTitleImage.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
        //registerPushToken()

        //TODO: 클릭할때마다 새로운 프래그먼트 생성
        binding.bottomNavigation.selectedItemId = R.id.action_home
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            setToolbarDefault()

            when (item.itemId) {
                R.id.action_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, HomeFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.action_search -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, SearchFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.action_add_photo -> {
                    if (ContextCompat.checkSelfPermission
                        (this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(Intent(this, AddPhotoActivity::class.java))
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.action_favorite_alarm -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, NotificationFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.action_account -> {
                    var userFragment = AccountFragment()
                    var bundle = Bundle()
                    var uid = FirebaseAuth.getInstance().currentUser?.uid
                    bundle.putString("destinationUid", uid)
                    userFragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, userFragment).commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }

    fun registerPushToken() {
        val pushToken = FirebaseInstanceId.getInstance().token
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val map = mutableMapOf<String, Any>()

        map["pushToken"] = pushToken!!
        FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
    }

    /*
     * push profileImageUrl to Firestore.collection(profileimage)
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val storageRef = FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)
            storageRef.putFile(imageUri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                val map = HashMap<String, Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map)
            }
        }
    }
}
