package com.example.teddystagram.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.teddystagram.model.AlarmDTO
import com.example.teddystagram.model.ContentDTO
import com.example.teddystagram.model.HomeUiData
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

    private val _homeUiData: MutableLiveData<ArrayList<HomeUiData>> = MutableLiveData()
    val homeUiData: LiveData<ArrayList<HomeUiData>> = _homeUiData

    private val _navigateProfileFragment: MutableLiveData<Pair<String, String>> = MutableLiveData()
    val navigateProfileFragment: LiveData<Pair<String, String>> = _navigateProfileFragment

    private val _navigateCommentActivity: MutableLiveData<Pair<String, String>> = MutableLiveData()
    val navigateCommentActivity: LiveData<Pair<String, String>> = _navigateCommentActivity

    fun getHomeData() {
        firebaseDb.collection(IMAGE)
            .get()
            .addOnSuccessListener { result ->
                val homeUiDataList: ArrayList<HomeUiData> = ArrayList()

                for (snapshot in result) {
                    val contentDTO = snapshot.toObject(ContentDTO::class.java)
                    val homeUiData = HomeUiData(snapshotId = snapshot.id, contentDTO = contentDTO)

                    firebaseDb
                        .collection(PROFILE_IMAGE)
                        .document(contentDTO.uid!!)
                        .get()
                        .addOnSuccessListener {
                            val url = it.data?.get(IMAGE) ?: ""
                            homeUiData.profileImageUrl = url as String
                        }
                        .addOnFailureListener {
                            it.toString()
                        }

                    homeUiDataList.add(homeUiData)
                }
                _homeUiData.value = homeUiDataList
            }
            .addOnFailureListener {
                it.toString()
            }
    }

    override fun onClickProfileImage(homeUiData: HomeUiData) {
        _navigateProfileFragment.value = Pair(homeUiData.contentDTO.uid!!, homeUiData.contentDTO.userId!!)
    }

    override fun onClickCommentImage(homeUiData: HomeUiData) {
        _navigateCommentActivity.value = Pair(homeUiData.snapshotId!!, homeUiData.contentDTO.uid!!)
    }

    override fun onClickLikeImage(homeUiData: HomeUiData) {
        val tsDoc = firebaseDb.collection(IMAGE).document(homeUiData.snapshotId!!)
        firebaseDb.runTransaction { transaction ->
            val dto = transaction.get(tsDoc).toObject(HomeUiData::class.java)

            if (dto!!.contentDTO.favorites.containsKey(homeUiData.contentDTO.uid)) {
                //When the button is clicked
                dto.contentDTO.favoriteCount = dto.contentDTO.favoriteCount?.minus(1)
                dto.contentDTO.favorites.remove(homeUiData.contentDTO.uid)
            } else {
                //When the button is not clicked
                dto.contentDTO.favoriteCount = dto.contentDTO.favoriteCount?.plus(1)
                dto.contentDTO.favorites[homeUiData.contentDTO.uid!!] = true
                favoriteAlarm(dto.contentDTO.uid!!)
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
    fun onClickProfileImage(homeUiData: HomeUiData)

    fun onClickCommentImage(homeUiData: HomeUiData)

    fun onClickLikeImage(homeUiData: HomeUiData)
}