package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class AsignacionMateria(
    @SerializedName("id_asignacion_materia") val idAsignacionMateria: Int,
    @SerializedName("id_materia") val idMateria: Int,
    @SerializedName("id_curso_capacitacion") val idCursoCapacitacion: Int
)
