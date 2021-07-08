package com.example.teddystagram.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.teddystagram.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel: ViewModel() {
    private val _showToastMessage : MutableLiveData<Int> = MutableLiveData()
    val showToastMessage : LiveData<Int> = _showToastMessage

    private val _showErrorMessage : MutableLiveData<String> = MutableLiveData()
    val showErrorMessage : LiveData<String> = _showErrorMessage

    private val _onNavigateMainActivity : MutableLiveData<Unit> = MutableLiveData()
    val onNavigateMainActivity : LiveData<Unit> = _onNavigateMainActivity

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()// declare FirebaseAuth instance

    companion object {
        private const val TAG = "LoginViewModel"
    }

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
                _showErrorMessage.value = task.exception.toString()
            } else {
                loginEmail(email, password)
            }
        }
    }

    private fun loginEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _onNavigateMainActivity.value = Unit
            } else if (task.exception?.message.isNullOrEmpty()) {
                Log.e(TAG, task.exception.toString())
                _showErrorMessage.value = task.exception.toString()
            } else {
                _showToastMessage.value = R.string.error_login
            }
        }
    }
}