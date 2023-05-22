package com.example.composeapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GHUsers(
    @SerialName("id")
    val id : Int,
    @SerialName("login")
    val login : String,
    @SerialName("public_repos")
    val publicRepos : Int,
    @SerialName("repos_url")
    val reposUrl : String,
    @SerialName("updated_at")
    val updatedAt : String,
)
