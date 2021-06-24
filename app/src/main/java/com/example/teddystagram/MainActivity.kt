package com.example.teddystagram

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.teddystagram.databinding.ActivityMainBinding
import com.example.teddystagram.navigation.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val PICK_PROFILE_FROM_ALBUM = 10
    }

    //TODO: 클릭할때마다 새로운 프래그먼트 생성
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        with(binding) {
            bottomNavigation.setOnNavigationItemSelectedListener { item ->
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
                            (this@MainActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(Intent(this@MainActivity, AddPhotoActivity::class.java))
                        }
                        return@setOnNavigationItemSelectedListener true
                    }

                    R.id.action_favorite_alarm -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, NotificationFragment()).commit()
                        return@setOnNavigationItemSelectedListener true
                    }

                    R.id.action_account -> {
                        val userFragment = AccountFragment()
                        val bundle = Bundle()
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        bundle.putString("destinationUid", uid)
                        userFragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, userFragment).commit()
                        return@setOnNavigationItemSelectedListener true
                    }
                }
                return@setOnNavigationItemSelectedListener false
            }

            bottomNavigation.selectedItemId = R.id.action_home
        }
    }

    /*
     * push profileImageUrl to Firestore.collection(profileimage)
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                //TODO : 프로필 이미지 로드 시, 에러 처리
            }

            val uid: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val storageRef = FirebaseStorage.getInstance()
                .reference.child(getString(R.string.collection_path_user_profile_images)).child(uid)

            storageRef.putFile(data!!.data!!).continueWithTask { _ ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                val map = HashMap<String, Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance()
                    .collection(getString(R.string.collection_path_user_profile_images)).document(uid).set(map)
            }
        }
    }
}
