package com.example.teddystagram

import com.google.firebase.firestore.auth.User


interface MainContract {

    interface View : BaseContract.View {

        fun showProgress()

        fun hideProgress()

        fun showToast(message: String)

        fun setItems(items: ArrayList<User>)

        fun updateView(user: User)

    }

    interface Presenter : BaseContract.Presenter<View> {

        override fun setView(view: View)

        override fun releaseView()

        fun loadData()

        fun setRxEvent()
    }
}