package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class Asesor(
    @SerializedName("id_asesor") val idAsesor: Int,
    @SerializedName("id_usuario") val idUsuario: Int?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("apellido_paterno") val apellidoPaterno: String?,
    @SerializedName("apellido_materno") val apellidoMaterno: String?
) {
    val nombreCompleto: String
        get() = listOfNotNull(nombre, apellidoPaterno, apellidoMaterno)
            .filter { it.isNotBlank() }
            .joinToString(" ")
}
