package com.example.grupoalfa.ui.llamarlista

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupoalfa.data.model.AlumnoPaseLista
import com.example.grupoalfa.data.model.Asesor
import com.example.grupoalfa.data.model.AsignacionMateria
import com.example.grupoalfa.data.model.Aula
import com.example.grupoalfa.data.model.Capacitacion
import com.example.grupoalfa.data.model.Estudiante
import com.example.grupoalfa.data.model.Marcado
import com.example.grupoalfa.data.model.Materia
import com.example.grupoalfa.data.model.Tema
import com.example.grupoalfa.data.model.Usuario
import com.example.grupoalfa.data.remote.mensajeAmigable
import com.example.grupoalfa.data.repository.PaseListaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PaseListaForm(
    val idMateria: Int? = null,
    val idTema: Int? = null,
    val idCurso: Int? = null,
    val idAsesor: Int? = null,
    val idAula: Int? = null,
    val fecha: String = fechaDeHoy(),
    val horaInicio: String = "",
    val horaFin: String = ""
) {
    companion object {
        fun fechaDeHoy(): String =
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
}

private fun fechaDeHoy(): String = PaseListaForm.fechaDeHoy()

data class PaseListaUiState(
    val cargandoCatalogos: Boolean = true,
    val errorCatalogos: String? = null,
    val materias: List<Materia> = emptyList(),
    val asesores: List<Asesor> = emptyList(),
    val capacitaciones: List<Capacitacion> = emptyList(),
    val temas: List<Tema> = emptyList(),
    val aulas: List<Aula> = emptyList(),
    val estudiantes: List<Estudiante> = emptyList(),
    val usuarios: List<Usuario> = emptyList(),
    val asignacionesMaterias: List<AsignacionMateria> = emptyList(),
    val form: PaseListaForm = PaseListaForm(),
    val alumnos: List<AlumnoPaseLista> = emptyList(),
    val cargandoAlumnos: Boolean = false,
    val notaAlumnos: String? = null,
    val marcas: Map<Int, Boolean> = emptyMap(),
    val guardando: Boolean = false,
    val creandoTema: Boolean = false,
    val mostrarNuevoTema: Boolean = false,
    val nuevoTemaNombre: String = "",
    val mensaje: String? = null,
    val error: String? = null
) {
    val presentes: Int get() = marcas.count { it.value }
    val ausentes: Int get() = marcas.count { !it.value }
    val sinMarcar: Int get() = (alumnos.size - marcas.size).coerceAtLeast(0)
    val marcados: Int get() = marcas.size
}

class LlamarListaViewModel(
    private val repository: PaseListaRepository = PaseListaRepository()
) : ViewModel() {

    var uiState by mutableStateOf(PaseListaUiState())
        private set

    private var alumnosJob: Job? = null

    init {
        cargarCatalogos()
    }

    fun cargarCatalogos() {
        viewModelScope.launch {
            uiState = uiState.copy(cargandoCatalogos = true, errorCatalogos = null)
            runCatching { repository.cargarCatalogos() }
                .onSuccess { catalogos ->
                    uiState = uiState.copy(
                        cargandoCatalogos = false,
                        errorCatalogos = null,
                        materias = catalogos.materias,
                        asesores = catalogos.asesores,
                        capacitaciones = catalogos.capacitaciones,
                        temas = catalogos.temas,
                        aulas = catalogos.aulas,
                        estudiantes = catalogos.estudiantes,
                        usuarios = catalogos.usuarios,
                        asignacionesMaterias = catalogos.asignacionesMaterias
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        cargandoCatalogos = false,
                        errorCatalogos = error.mensajeAmigable()
                    )
                }
        }
    }

    fun onMateriaSelected(idMateria: Int?) {
        val temaActual = uiState.temas.firstOrNull { it.idTema == uiState.form.idTema }
        val conservarTema = temaActual?.idMateria == idMateria
        updateForm(
            uiState.form.copy(
                idMateria = idMateria,
                idTema = if (conservarTema) uiState.form.idTema else null
            )
        )
    }

    fun onTemaSelected(idTema: Int?) = updateForm(uiState.form.copy(idTema = idTema))

    fun onCursoSelected(idCurso: Int?) {
        if (idCurso == null) {
            alumnosJob?.cancel()
            uiState = uiState.copy(
                form = uiState.form.copy(idCurso = null),
                alumnos = emptyList(),
                marcas = emptyMap(),
                cargandoAlumnos = false,
                notaAlumnos = null
            )
            return
        }

        val curso = uiState.capacitaciones.firstOrNull { it.idCapacitacion == idCurso }
        val materiaDelCurso = uiState.asignacionesMaterias
            .firstOrNull { it.idCursoCapacitacion == idCurso }
            ?.idMateria

        val materiaNueva = uiState.form.idMateria ?: materiaDelCurso
        val temaActual = uiState.temas.firstOrNull { it.idTema == uiState.form.idTema }
        val conservarTema = temaActual == null || temaActual.idMateria == materiaNueva

        val nuevoForm = uiState.form.copy(
            idCurso = idCurso,
            idAula = curso?.idAula ?: uiState.form.idAula,
            idMateria = materiaNueva,
            idTema = if (conservarTema) uiState.form.idTema else null,
            horaInicio = curso?.horaInicio?.take(5)?.takeIf { it.isNotBlank() }
                ?: uiState.form.horaInicio,
            horaFin = curso?.horaFin?.take(5)?.takeIf { it.isNotBlank() }
                ?: uiState.form.horaFin
        )

        uiState = uiState.copy(
            form = nuevoForm,
            alumnos = emptyList(),
            marcas = emptyMap(),
            mensaje = null,
            error = null
        )
        cargarAlumnos(idCurso)
    }

    fun onAsesorSelected(idAsesor: Int?) = updateForm(uiState.form.copy(idAsesor = idAsesor))

    fun onAulaSelected(idAula: Int?) = updateForm(uiState.form.copy(idAula = idAula))

    fun onFechaChange(valor: String) {
        updateForm(uiState.form.copy(fecha = valor.filter { it.isDigit() || it == '-' }.take(10)))
    }

    fun onHoraInicioChange(valor: String) {
        updateForm(uiState.form.copy(horaInicio = limitarHora(valor)))
    }

    fun onHoraFinChange(valor: String) {
        updateForm(uiState.form.copy(horaFin = limitarHora(valor)))
    }

    fun toggleAsistencia(idEstudiante: Int, presente: Boolean) {
        val actual = uiState.marcas[idEstudiante]
        val nuevas = if (actual == presente) {
            uiState.marcas - idEstudiante
        } else {
            uiState.marcas + (idEstudiante to presente)
        }
        uiState = uiState.copy(marcas = nuevas, mensaje = null, error = null)
    }

    fun marcarTodos(presente: Boolean) {
        if (uiState.alumnos.isEmpty()) return
        uiState = uiState.copy(
            marcas = uiState.alumnos.associate { it.idEstudiante to presente },
            mensaje = null,
            error = null
        )
    }

    fun mostrarNuevoTema() {
        if (uiState.form.idMateria == null) {
            uiState = uiState.copy(error = "Elige primero una materia para crear el tema.")
            return
        }
        uiState = uiState.copy(mostrarNuevoTema = true, error = null, mensaje = null)
    }

    fun cancelarNuevoTema() {
        uiState = uiState.copy(mostrarNuevoTema = false, nuevoTemaNombre = "")
    }

    fun onNuevoTemaNombreChange(valor: String) {
        uiState = uiState.copy(nuevoTemaNombre = valor.take(150))
    }

    fun crearTema() {
        val nombre = uiState.nuevoTemaNombre.trim()
        val idMateria = uiState.form.idMateria
        if (idMateria == null) {
            uiState = uiState.copy(error = "Elige una materia antes de crear el tema.")
            return
        }
        if (nombre.isBlank()) {
            uiState = uiState.copy(error = "Escribe el nombre del tema nuevo.")
            return
        }
        if (uiState.creandoTema) return

        viewModelScope.launch {
            uiState = uiState.copy(creandoTema = true, error = null, mensaje = null)
            runCatching { repository.crearTema(idMateria, nombre) }
                .onSuccess { tema ->
                    val temas = runCatching { repository.recargarTemas() }
                        .getOrDefault(uiState.temas + tema)
                    uiState = uiState.copy(
                        temas = temas,
                        form = uiState.form.copy(idTema = tema.idTema),
                        creandoTema = false,
                        mostrarNuevoTema = false,
                        nuevoTemaNombre = "",
                        mensaje = "Tema \"${tema.nombreTema ?: nombre}\" creado y seleccionado."
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        creandoTema = false,
                        error = error.mensajeAmigable()
                    )
                }
        }
    }

    fun guardarLista() {
        if (uiState.guardando) return

        val form = uiState.form
        val validacion = validarFormulario(form)
        if (validacion != null) {
            uiState = uiState.copy(error = validacion, mensaje = null)
            return
        }

        val marcados = uiState.marcas.map { (id, presente) ->
            Marcado(idEstudiante = id, presente = presente)
        }

        viewModelScope.launch {
            uiState = uiState.copy(guardando = true, error = null, mensaje = null)
            runCatching {
                repository.guardarLista(
                    idCurso = form.idCurso!!,
                    idAsesor = form.idAsesor!!,
                    idTema = form.idTema,
                    fecha = form.fecha,
                    marcados = marcados
                )
            }.onSuccess { resultado ->
                if (resultado.fallos == 0) {
                    uiState = uiState.copy(
                        guardando = false,
                        marcas = emptyMap(),
                        mensaje = "Lista guardada: ${resultado.total} asistencia(s) registradas en la clase #${resultado.idClase}."
                    )
                } else {
                    uiState = uiState.copy(
                        guardando = false,
                        marcas = emptyMap(),
                        error = "La clase #${resultado.idClase} se creó, pero ${resultado.fallos} de ${resultado.total} asistencias no se guardaron."
                    )
                }
            }.onFailure { error ->
                uiState = uiState.copy(
                    guardando = false,
                    error = error.mensajeAmigable()
                )
            }
        }
    }

    fun limpiarFormulario() {
        alumnosJob?.cancel()
        uiState = uiState.copy(
            form = PaseListaForm(),
            alumnos = emptyList(),
            marcas = emptyMap(),
            cargandoAlumnos = false,
            notaAlumnos = null,
            mostrarNuevoTema = false,
            nuevoTemaNombre = "",
            mensaje = null,
            error = null
        )
    }

    private fun cargarAlumnos(idCurso: Int) {
        alumnosJob?.cancel()
        alumnosJob = viewModelScope.launch {
            uiState = uiState.copy(cargandoAlumnos = true, notaAlumnos = null)
            runCatching {
                repository.cargarAlumnos(
                    idCurso = idCurso,
                    estudiantes = uiState.estudiantes,
                    usuarios = uiState.usuarios
                )
            }.onSuccess { resultado ->
                if (uiState.form.idCurso != idCurso) return@onSuccess
                uiState = uiState.copy(
                    cargandoAlumnos = false,
                    alumnos = resultado.alumnos,
                    notaAlumnos = resultado.nota,
                    marcas = emptyMap()
                )
            }.onFailure { error ->
                if (uiState.form.idCurso != idCurso) return@onFailure
                uiState = uiState.copy(
                    cargandoAlumnos = false,
                    alumnos = emptyList(),
                    error = error.mensajeAmigable()
                )
            }
        }
    }

    private fun updateForm(form: PaseListaForm) {
        uiState = uiState.copy(form = form, mensaje = null, error = null)
    }

    private fun limitarHora(valor: String): String =
        valor.filter { it.isDigit() || it == ':' }.take(5)

    private fun validarFormulario(form: PaseListaForm): String? = when {
        form.idMateria == null -> "Elige una materia."
        form.idTema == null -> "Elige un tema para la clase."
        form.idCurso == null -> "Elige un curso de capacitación."
        form.idAsesor == null -> "Elige un asesor."
        form.idAula == null -> "Elige un aula."
        !fechaValida(form.fecha) -> "La fecha debe tener el formato AAAA-MM-DD."
        !horaValida(form.horaInicio) || !horaValida(form.horaFin) ->
            "Completa la hora de inicio y fin con formato HH:mm."
        form.horaFin <= form.horaInicio ->
            "La hora de fin debe ser mayor que la hora de inicio."
        uiState.marcas.isEmpty() ->
            "Marca al menos un estudiante como presente o ausente."
        else -> null
    }

    private fun fechaValida(valor: String): Boolean = runCatching {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
        formato.parse(valor) != null && valor.length == 10
    }.getOrDefault(false)

    private fun horaValida(valor: String): Boolean =
        Regex("^([01]\\d|2[0-3]):[0-5]\\d$").matches(valor)
}
