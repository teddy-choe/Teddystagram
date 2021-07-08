package com.example.teddystagram.model

data class HomeUiData(
    var profileImageUrl: String = "",
    var snapshotId: String? = null,
    var contentDTO: ContentDTO
) {
    data class Comment(
        var uid: String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timestamp: Long? = null
    )
}