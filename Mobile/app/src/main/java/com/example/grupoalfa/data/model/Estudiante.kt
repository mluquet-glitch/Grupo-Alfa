package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class Estudiante(
    @SerializedName("id_estudiante") val idEstudiante: Int,
    @SerializedName("id_usuario") val idUsuario: Int?,
    @SerializedName("colegio_de_procedencia") val colegioDeProcedencia: String? = null
)
