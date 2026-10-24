package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class Capacitacion(
    @SerializedName("id_capacitacion") val idCapacitacion: Int,
    @SerializedName("id_aula") val idAula: Int?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("nivel") val nivel: String?,
    @SerializedName("modalidad") val modalidad: String?,
    @SerializedName("fecha_inicio") val fechaInicio: String?,
    @SerializedName("hora_inicio") val horaInicio: String?,
    @SerializedName("hora_fin") val horaFin: String?
)
