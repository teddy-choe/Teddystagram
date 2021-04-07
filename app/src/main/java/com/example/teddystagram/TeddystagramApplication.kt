package com.example.teddystagram

import android.app.Application
import android.content.Context

//TODO: companionObject로 globalContext를 제공하는 것이 구조적으로 문제 없는지 검토 필요
class TeddystagramApplication: Application() {
    companion object {
        fun getGlobalApplicationContext(): Context {
            return this.getGlobalApplicationContext()
        }
    }
}