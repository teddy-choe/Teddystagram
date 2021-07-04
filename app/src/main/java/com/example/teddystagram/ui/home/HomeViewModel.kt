package com.example.teddystagram.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.teddystagram.model.AlarmDTO
import com.example.teddystagram.model.HomeContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.item_detail.view.*

class HomeViewModel : ViewModel(), HomeEventListener {
    companion object {
        private const val TAG = "HomeViewModel"
        private const val IMAGE = "images"
        private const val PROFILE_IMAGE = "profileImages"
        private const val TIME_STAMP = "timestamp"
    }

    private val firebaseDb = Firebase.firestore

    private val _homeContents: MutableLiveData<ArrayList<HomeContent>> = MutableLiveData()
    val homeContent: LiveData<ArrayList<HomeContent>> = _homeContents

    private val _navigateProfileFragment: MutableLiveData<Pair<String, String>> = MutableLiveData()
    val navigateProfileFragment: LiveData<Pair<String, String>> = _navigateProfileFragment

    private val _navigateCommentActivity: MutableLiveData<Pair<String, String>> = MutableLiveData()
    val navigateCommentActivity: LiveData<Pair<String, String>> = _navigateCommentActivity

    fun getHomeData() {
        firebaseDb.collection(IMAGE)
            .get()
            .addOnSuccessListener { result ->
                val homeContents: ArrayList<HomeContent> = ArrayList()

                for (snapshot in result) {
                    Logger.d(snapshot)
                    val item = snapshot.toObject(HomeContent::class.java)
                    item.snapshotId = snapshot.id
                    homeContents.add(item)

                    //TODO: RxJava로 연쇄적인 API호출을 깔끔하게 처리할 수 없는지
                    firebaseDb
                        .collection(PROFILE_IMAGE)
                        .document(item.uid!!)
                        .get()
                        .addOnSuccessListener {
                            val url = it.data?.get(IMAGE) ?: ""
                            item.profileImageUrl = url as String
                        }
                        .addOnFailureListener {
                            it.toString()
                        }
                }
                _homeContents.value = homeContents
            }
            .addOnFailureListener {
                it.toString()
            }
    }

    override fun onClickProfileImage(homeContent: HomeContent) {
        _navigateProfileFragment.value = Pair(homeContent.uid!!, homeContent.userId!!)
    }

    override fun onClickCommentImage(homeContent: HomeContent) {
        _navigateCommentActivity.value = Pair(homeContent.snapshotId!!, homeContent.uid!!)
    }

    override fun onClickLikeImage(homeContent: HomeContent) {
        val tsDoc = firebaseDb.collection(IMAGE).document(homeContent.snapshotId!!)
        firebaseDb.runTransaction { transaction ->
            val dto = transaction.get(tsDoc).toObject(HomeContent::class.java)

            if (dto!!.favorites.containsKey(homeContent.uid)) {
                //When the button is clicked
                dto.favoriteCount = dto.favoriteCount?.minus(1)
                dto.favorites.remove(homeContent.uid)
            } else {
                //When the button is not clicked
                dto.favoriteCount = dto.favoriteCount?.plus(1)
                dto.favorites[homeContent.uid!!] = true
                favoriteAlarm(dto.uid!!)
            }
            transaction.set(tsDoc, dto)
        }
    }

    private fun favoriteAlarm(destinationUid: String) {
        val alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.kind = 0
        alarmDTO.timestamp = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

//        var message =
//            FirebaseAuth.getInstance()?.currentUser?.email + getString(R.string.alarm_favorite)
//        fcmPush?.sendMessage(destinationUid, "알림 메세지 입니다.", message)
    }
}

interface HomeEventListener {
    fun onClickProfileImage(homeContent: HomeContent)

    fun onClickCommentImage(homeContent: HomeContent)

    fun onClickLikeImage(homeContent: HomeContent)
}