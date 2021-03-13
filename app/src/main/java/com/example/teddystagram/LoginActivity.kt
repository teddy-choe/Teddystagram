package com.example.teddystagram

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()// declare FirebaseAuth instance
    val callbackManager = CallbackManager.Factory.create() // declare Facebook CallbackManager
    var googleSignInClient : GoogleSignInClient? = null
    val GOOGLE_LOGIN_CODE = 9001

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
        setContentView(R.layout.activity_login)

        setGoogleSigninClient()

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

    private fun setGoogleSigninClient() {
        /*
         * 서버의 클라이언트 ID를 requestIdToken에 전달
         */
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("576141085480-s11e99saiuilf1vffocv9n7ak4o82fra.apps.googleusercontent.com")
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser) // 사용자가 현재 로그인되있는지 확인, 되있으면 moveMainPage
    }

    /*
     * 이메일 주소를 통해 회원가입을 합니다.
     */
    fun createAndLoginEmail(view: View){
        if (email_edittext.text.toString().isEmpty() ||
            password_edittext.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_email_password), Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this,getString(R.string.complete_id), Toast.LENGTH_SHORT).show()
            } else if (task.exception?.message.isNullOrEmpty()){
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
            } else {
                signinEmail()
            }
        }
    }

    /*
     * 이메일 주소를 통해 로그인을 합니다.
     */
    private fun signinEmail(){
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
                ?.addOnCompleteListener {
                    task ->
                    if (task.isSuccessful){
                        moveMainPage(auth?.currentUser)
                    } else if (task.exception?.message.isNullOrEmpty()) {
                        Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun moveMainPage(user : FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    fun googleLogin(view: View){
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }

    fun facebookLogin(view: View){
        LoginManager
                .getInstance()
                .logInWithReadPermissions(this,Arrays.asList("email","public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager,object : FacebookCallback<LoginResult>{
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

    fun handleFacebookAccessToken(token: AccessToken?){
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)?.addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                moveMainPage(auth?.currentUser)
            } else if (task.exception?.message.isNullOrEmpty()) {
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        moveMainPage(auth?.currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode,resultCode,data)

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
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null) // 사용자 인증 정보
        auth?.signInWithCredential(credential)
                ?.addOnCompleteListener {
                    task ->
                    if (task.isSuccessful){
                        moveMainPage(auth?.currentUser)
                    } else if (task.exception?.message.isNullOrEmpty()) {
                        Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                    }
                }
    }
}
