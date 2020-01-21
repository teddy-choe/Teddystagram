package com.example.teddystagram.LoginPresenter

import com.google.firebase.firestore.auth.User


interface LoginContract {

    interface View {

        fun goToMain()

        fun completeSignup()

    }

    interface Presenter {

        fun doGoogleLogin()

        fun doFacebookLogin()

        fun doEmailLogin()

        fun doEmailSignup()

    }
}