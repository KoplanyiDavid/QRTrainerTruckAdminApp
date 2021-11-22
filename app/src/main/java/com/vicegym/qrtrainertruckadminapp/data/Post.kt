package com.vicegym.qrtrainertruckadminapp.data

data class Post(
    var authorId: String? = null,
    var author: String? = null,
    var title: String? = null,
    var description: String? = null,
    var imageUrl: String? = null,
    var sorter: Long? = null
)