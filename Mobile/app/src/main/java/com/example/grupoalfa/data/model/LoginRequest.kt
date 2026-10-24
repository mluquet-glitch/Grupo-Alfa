package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("correo_electronico") val correoElectronico: String? = null,
    @SerializedName("nro_carnet") val nroCarnet: Long? = null,
    @SerializedName("contraseña") val contrasena: String
)
