package com.example.teddystagram.model

//TODO: 데이터 타입에 대한 처리가 필요
data class HomeContent(
    var explain: String? = null,
    var profileImageUrl: String = "",
    var imageUrl: String? = null,
    var uid: String? = null,
    var snapshotId: String? = null,
    var timestamp: Long? = null,
    var userId: String? = null,
    var favoriteCount: Int? = null,
    var favorites: MutableMap<String, Boolean> = HashMap()
) {
    data class Comment(
        var uid: String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timestamp: Long? = null
    )
}
