package com.example.grupoalfa.data.model

data class AlumnoPaseLista(
    val idEstudiante: Int,
    val nombre: String,
    val colegio: String?
) {
    val iniciales: String
        get() = nombre
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
}

data class AlumnosCurso(
    val alumnos: List<AlumnoPaseLista>,
    val nota: String?
)

data class Marcado(
    val idEstudiante: Int,
    val presente: Boolean
)

data class ResultadoGuardado(
    val idClase: Int,
    val total: Int,
    val fallos: Int
)
