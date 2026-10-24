package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class CreateAsistenciaRequest(
    @SerializedName("id_clase") val idClase: Int,
    @SerializedName("id_estudiante") val idEstudiante: Int,
    @SerializedName("hora_entrada") val horaEntrada: String,
    @SerializedName("permiso") val permiso: Boolean? = null,
    @SerializedName("razon_permiso") val razonPermiso: String? = null
)
