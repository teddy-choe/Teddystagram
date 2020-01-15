package com.example.teddystagram

class BaseContract {

    interface Presenter<T> {

        fun setView(view: T)

        fun releaseView()
    }

    interface View
}