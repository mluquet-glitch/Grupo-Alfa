package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class Tema(
    @SerializedName("id_tema") val idTema: Int,
    @SerializedName("id_materia") val idMateria: Int?,
    @SerializedName("nombre_tema") val nombreTema: String?,
    @SerializedName("nombre_materia") val nombreMateria: String?
)
