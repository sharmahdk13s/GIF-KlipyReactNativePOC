package com.klipy.domain.models

data class Category(
    val title: String,
    val url: String
)

fun Category.isRecent() = this.title.lowercase() == "recent"