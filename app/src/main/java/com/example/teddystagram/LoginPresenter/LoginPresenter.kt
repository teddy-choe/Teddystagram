package com.example.teddystagram.LoginPresenter

class LoginPresenter : LoginContract.Presenter {

    private var view: LoginContract.View? = null

    override fun setView(view: LoginContract.View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun attachView(view: LoginContract.View?) {
        this.view = view
    }

    override fun detachView() {
        view = null;
    }

    override fun releaseView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}