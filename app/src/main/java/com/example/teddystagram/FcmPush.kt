package com.example.teddystagram

import com.example.teddystagram.navigation.model.PushDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException

class FcmPush(){
    var JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    var url = "https://fcm.googleapis.com/fcm/send"
    var serverKey = "AAAAhiSvSyg:APA91bHst6Bm1l0JbkLF2qQ5NSTjlNxvUBtCB7Bl5v-TYNyYK3mSyorxa6cpI_SI3DUw2wdH3x3cPZbe22YTrwdNG6VL4Jz6DywWGxMOkqdyLWvIiGY7Jf7zse6QZTkGvAN1sBtB9JjF"

    var okHttpClient : OkHttpClient? = null
    var gson : Gson? = null
    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid:String, title: String?,message: String?){
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                println("pushToken" + task.result!!["pushToken"])
                var token = task.result!!["pushToken"].toString()

                var pushDTO = PushDTO()
                pushDTO.to = token
                pushDTO.notification?.title = title
                pushDTO.notification?.body = message

                var body = RequestBody.create(JSON,gson?.toJson(pushDTO)!!)
                var request = Request.Builder()
                        .addHeader("Content-Type","application/json")
                        .addHeader("Authorization", "key="+serverKey)
                        .url(url)
                        .post(body)
                        .build()
                okHttpClient?.newCall(request)?.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    override fun onResponse(call: Call, response: Response) {
                        println(response?.body?.string())
                    }

                })
            }
        }
    }
}