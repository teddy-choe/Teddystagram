package com.example.teddystagram.LoginPresenter

import com.google.firebase.firestore.auth.User


interface LoginContract {

    interface View : BaseContract.View {

        fun showProgress()

        fun hideProgress()

        fun showToast(message: String)

        fun setItems(items: ArrayList<User>)

        fun updateView(user: User)

    }

    interface Presenter : BaseContract.Presenter<View> {

        override fun setView(view: View)

        fun attachView(view: View?)

        fun detachView()
    }
}