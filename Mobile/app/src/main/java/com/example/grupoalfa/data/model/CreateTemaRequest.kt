package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class CreateTemaRequest(
    @SerializedName("nombre_tema") val nombreTema: String,
    @SerializedName("id_materia") val idMateria: Int?
)
