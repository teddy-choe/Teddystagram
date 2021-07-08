package com.example.teddystagram.model

data class ContentDTO(
    var explain: String? = "",
    var imageUrl: String? = "" ,
    var uid: String? = "",
    var timestamp: Long? = 0,
    var userId: String?  = "",
    var favoriteCount: Int? = 0,
    var favorites: MutableMap<String, Boolean> = HashMap()
) {
    data class Comment(
        var uid: String? = "",
        var userId: String? = "",
        var comment: String? = "",
        var timestamp: Long? = 0
    )
}