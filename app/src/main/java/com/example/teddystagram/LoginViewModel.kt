package com.example.teddystagram

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel: ViewModel() {
    private val _showToastMessage : MutableLiveData<Int> = MutableLiveData()
    val showToastMessage : LiveData<Int> = _showToastMessage

    private val _onNavigateMainActivity : MutableLiveData<FirebaseUser> = MutableLiveData()
    val onNavigateMainActivity : LiveData<FirebaseUser> = _onNavigateMainActivity

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()// declare FirebaseAuth instance

    /*
     * 이메일 주소를 통해 회원가입을 합니다.
     */
    fun createAndLoginEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _showToastMessage.value = R.string.empty_email_password
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _showToastMessage.value = R.string.complete_id
            } else if (task.exception?.message.isNullOrEmpty()) {
                Log.e(TAG, task.exception.toString())
                _showToastMessage.value = R.string.error_login
            } else {
                loginEmail(email, password)
            }
        }
    }

    /*
     * 이메일 주소를 통해 로그인을 합니다.
     */
    //TODO: 에러 발생 시 문구 수정 필요
    private fun loginEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _onNavigateMainActivity.value = auth.currentUser
            } else if (task.exception?.message.isNullOrEmpty()) {
                _showToastMessage.value = R.string.error_login
            } else {
                _showToastMessage.value = R.string.error_login
            }
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}