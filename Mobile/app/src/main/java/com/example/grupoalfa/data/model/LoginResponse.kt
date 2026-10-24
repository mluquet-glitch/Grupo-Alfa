package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("estado") val estado: String?,
    @SerializedName("datos") val datos: Usuario,
    @SerializedName("token") val token: String
)
