package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class Aula(
    @SerializedName("id_aula") val idAula: Int,
    @SerializedName("capacidad") val capacidad: Int?
)
