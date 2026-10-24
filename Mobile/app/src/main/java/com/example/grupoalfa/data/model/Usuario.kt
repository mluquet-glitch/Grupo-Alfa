package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id_usuario") val idUsuario: Int,
    @SerializedName("nro_carnet") val nroCarnet: Long?,
    @SerializedName("apellido_paterno") val apellidoPaterno: String?,
    @SerializedName("apellido_materno") val apellidoMaterno: String?,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String?,
    @SerializedName("correo_electronico") val correoElectronico: String?,
    @SerializedName("usuario_vigente") val usuarioVigente: Boolean?,
    @SerializedName("roles") val roles: List<String>? = null
) {
    val nombreCompleto: String
        get() = listOfNotNull(nombre, apellidoPaterno, apellidoMaterno).joinToString(" ")

    val rolPrincipal: String?
        get() = roles?.firstOrNull()
}
