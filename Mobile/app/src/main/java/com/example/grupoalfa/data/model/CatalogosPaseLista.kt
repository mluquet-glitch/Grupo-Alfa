package com.example.grupoalfa.data.model

data class CatalogosPaseLista(
    val materias: List<Materia>,
    val asesores: List<Asesor>,
    val capacitaciones: List<Capacitacion>,
    val temas: List<Tema>,
    val aulas: List<Aula>,
    val estudiantes: List<Estudiante>,
    val usuarios: List<Usuario>,
    val asignacionesMaterias: List<AsignacionMateria>
)
