package com.example.teddystagram.LoginPresenter

import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class LoginPresenter : LoginContract.Presenter {

    private var view : LoginContract.View? = null

    private var auth: FirebaseAuth? = null // declare FirebaseAuth instance

    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var callbackManager: CallbackManager? = null

    var loginPresenter : LoginPresenter? = null

    override fun doGoogleLogin() {

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("576141085480-s11e99saiuilf1vffocv9n7ak4o82fra.apps.googleusercontent.com")
                .requestEmail()
                .build()

    }

    override fun doFacebookLogin() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doEmailLogin() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doEmailSignup() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}