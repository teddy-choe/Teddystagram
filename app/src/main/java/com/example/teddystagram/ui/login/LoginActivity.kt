package com.example.teddystagram.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.teddystagram.MainActivity
import com.example.teddystagram.R
import com.example.teddystagram.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

//TODO: 하나의 클래스에 모든 로직이 모여있어 수정하기 쉽지 않음. MVVM으로 리팩토링. 테스트 코드 추가 필요
class LoginActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.showToastMessage.observe(this, Observer { message ->
            Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
        })

        viewModel.showErrorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })

        viewModel.onNavigateMainActivity.observe(this, Observer {
            navigateMainActivity()
        })
    }

    private fun navigateMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
