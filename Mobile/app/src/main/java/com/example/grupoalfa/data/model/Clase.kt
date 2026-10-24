package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class Clase(
    @SerializedName("id_clase") val idClase: Int,
    @SerializedName("id_curso_capacitacion") val idCursoCapacitacion: Int,
    @SerializedName("id_asesor") val idAsesor: Int?,
    @SerializedName("id_tema") val idTema: Int?,
    @SerializedName("inicioClase") val inicioClase: String?
)
