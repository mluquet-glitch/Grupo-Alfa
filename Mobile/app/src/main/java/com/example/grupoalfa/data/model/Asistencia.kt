package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class Asistencia(
    @SerializedName("id_asistencia") val idAsistencia: Int,
    @SerializedName("id_clase") val idClase: Int,
    @SerializedName("id_estudiante") val idEstudiante: Int,
    @SerializedName("hora_entrada") val horaEntrada: String?,
    @SerializedName("salida_antes_de_tiempo") val salidaAntesDeTiempo: String?,
    @SerializedName("permiso") val permiso: Boolean?,
    @SerializedName("razon_permiso") val razonPermiso: String?
)
