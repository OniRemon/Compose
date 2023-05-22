package com.example.composeapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GHRepos(
    @SerialName("id")
    val id : Int,
    @SerialName("name")
    val name : String,
    @SerialName("updated_at")
    val updatedAt : String,
)
