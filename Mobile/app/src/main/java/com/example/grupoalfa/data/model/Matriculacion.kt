package com.example.grupoalfa.data.model

import com.google.gson.annotations.SerializedName

data class Matriculacion(
    @SerializedName("id_matriculacion") val idMatriculacion: Int,
    @SerializedName("id_estudiante") val idEstudiante: Int,
    @SerializedName("id_curso_capacitacion") val idCursoCapacitacion: Int,
    @SerializedName("esta_vigente") val estaVigente: Boolean? = null
)
