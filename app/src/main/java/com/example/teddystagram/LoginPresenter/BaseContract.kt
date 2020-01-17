package com.example.teddystagram.LoginPresenter

class BaseContract {

    interface Presenter<T> {

        fun setView(view: T)

        fun releaseView()
    }

    interface View
}