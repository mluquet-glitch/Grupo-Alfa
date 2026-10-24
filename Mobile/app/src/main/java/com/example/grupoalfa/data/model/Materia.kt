package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class Materia(
    @SerializedName("id_materia") val idMateria: Int,
    @SerializedName("nombre_materia") val nombreMateria: String
)
