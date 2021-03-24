package com.example.teddystagram

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
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
import java.util.*

//TODO: 하나의 클래스에 모든 로직이 모여있어 수정하기 쉽지 않음. MVVM으로 리팩토링. 테스트 코드 추가 필요
class LoginActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()// declare FirebaseAuth instance
    private val callbackManager = CallbackManager.Factory.create() // declare Facebook CallbackManager
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    // 세션 콜백 구현
/*    private val sessionCallback: ISessionCallback = object : ISessionCallback() {
        fun onSessionOpened() {
            Log.i("KAKAO_SESSION", "로그인 성공")
        }

        fun onSessionOpenFailed(exception: KakaoException?) {
            Log.e("KAKAO_SESSION", "로그인 실패", exception)
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel

        setGoogleSigninClient()
        observeViewModel()

        // SDK 초기화
        /*KakaoSDK.init(object : KakaoAdapter() {
            val applicationConfig: IApplicationConfig?
                get() = object : IApplicationConfig() {
                    val applicationContext: Context?
                        get() = this@MyApplication
                }
        })*/

        //Session.getCurrentSession().addCallback(sessionCallback)
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
        /*
         * 서버의 클라이언트 ID를 requestIdToken에 전달
         */
        googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("576141085480-s11e99saiuilf1vffocv9n7ak4o82fra.apps.googleusercontent.com")
                .requestEmail()
                .build())
    }

    private fun navigateMainActivity(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun googleLogin(view: View) {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    fun facebookLogin(view: View) {
        LoginManager
            .getInstance()
            .logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result?.accessToken)
            }

            override fun onCancel() {
                println("취소되었습니다.")
            }

            override fun onError(error: FacebookException?) {
                println("에러가 발생했습니다.")
            }

        })
    }

    fun handleFacebookAccessToken(token: AccessToken?) {
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navigateMainActivity(auth.currentUser)
            } else if (task.exception?.message.isNullOrEmpty()) {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        navigateMainActivity(auth.currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)

        /*
         * 구글 로그인
         */
        if (requestCode == GOOGLE_LOGIN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {

            }
        }
    }

    /*
     * GoogleSignInAccount에서 ID토큰을 받아와서
     * Firebase 사용자 인증 정보로 교환하고
     * 해당 정보를 사용해 Firebase 인증을 받는다.
     */
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null) // 사용자 인증 정보
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navigateMainActivity(auth.currentUser)
                } else if (task.exception?.message.isNullOrEmpty()) {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    companion object {
        private const val GOOGLE_LOGIN_CODE = 9001
    }
}
