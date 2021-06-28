package com.example.teddystagram.model

data class ContentDTO(var explain: String? = null,
                      var imageUrl: String? = null,
                      var uid: String? = null,
                      var timestamp: Long? = null,
                      var userId: String? = null,
                      var favoriteCount: Int? = null,
                      var favorites: MutableMap<String, Boolean> = HashMap()) {
    data class Comment(var uid: String? = null,
                       var userId: String? = null,
                       var comment: String? = null,
                       var timestamp: Long? = null)
}