package com.example.teddystagram.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.teddystagram.MainActivity
import com.example.teddystagram.R
import com.example.teddystagram.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.orhanobut.logger.Logger
import java.util.*

//TODO: 하나의 클래스에 모든 로직이 모여있어 수정하기 쉽지 않음. MVVM으로 리팩토링. 테스트 코드 추가 필요
class LoginActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val facebookCallbackManager = CallbackManager.Factory.create()
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    companion object {
        private const val GOOGLE_LOGIN_CODE = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel

        setGoogleSigninClient()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.showToastMessage.observe(this, Observer { message ->
            Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
        })

        viewModel.showErrorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })

        viewModel.onNavigateMainActivity.observe(this, Observer { currentUser ->
            navigateMainActivity(currentUser)
        })
    }

    private fun setGoogleSigninClient() {
        googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(resources.getString(R.string.request_token))
                .requestEmail()
                .build())
    }

    fun googleLogin(view: View) {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    fun facebookLogin(view: View) {
        LoginManager
            .getInstance()
            .logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        LoginManager.getInstance().registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                if (result == null) {
                    Logger.d("facebookLogin token : " + result)
                    return
                }

                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(this@LoginActivity, "취소되었습니다", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@LoginActivity, "에러가 발생했습니다", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        firebaseAuth.signInWithCredential(
            FacebookAuthProvider.getCredential(token.token)).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navigateMainActivity(firebaseAuth.currentUser)
            } else if (task.exception?.message.isNullOrEmpty()) {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookCallbackManager?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_LOGIN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java) ?: return
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                e.toString()
            }
        }
    }

    /*
     * GoogleSignInAccount에서 ID토큰을 받아와서
     * Firebase 사용자 인증 정보로 교환하고
     * 해당 정보를 사용해 Firebase 인증을 받는다.
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null) // 사용자 인증 정보
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navigateMainActivity(firebaseAuth.currentUser)
                } else if (task.exception?.message.isNullOrEmpty()) {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun navigateMainActivity(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
