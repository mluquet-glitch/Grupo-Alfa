package com.example.grupoalfa.ui.llamarlista

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grupoalfa.data.model.AlumnoPaseLista
import com.example.grupoalfa.ui.theme.AlfaWhite
import com.example.grupoalfa.ui.theme.Amber
import com.example.grupoalfa.ui.theme.BlueWash
import com.example.grupoalfa.ui.theme.Navy
import com.example.grupoalfa.ui.theme.Present
import com.example.grupoalfa.ui.theme.PresentWash

@Composable
fun LlamarListaScreen(
    modifier: Modifier = Modifier,
    viewModel: LlamarListaViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.cargandoCatalogos && uiState.materias.isEmpty() -> {
                LoadingState(
                    texto = "Cargando catálogos…",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.errorCatalogos != null && uiState.materias.isEmpty() -> {
                ErrorState(
                    titulo = "No pudimos cargar los catálogos",
                    mensaje = uiState.errorCatalogos,
                    onRetry = viewModel::cargarCatalogos,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                PaseListaContent(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun PaseListaContent(
    uiState: PaseListaUiState,
    viewModel: LlamarListaViewModel
) {
    val form = uiState.form
    val cursoSeleccionado = uiState.capacitaciones
        .firstOrNull { it.idCapacitacion == form.idCurso }

    val materias = uiState.materias.map { SelectOption(it.idMateria, it.nombreMateria) }
    val temas = uiState.temas
        .filter { form.idMateria == null || it.idMateria == form.idMateria || it.idMateria == null }
        .map { SelectOption(it.idTema, it.nombreTema ?: "Tema ${it.idTema}") }
    val cursos = uiState.capacitaciones.map {
        SelectOption(
            it.idCapacitacion,
            listOfNotNull(it.nombre, it.nivel).joinToString(" · ")
        )
    }
    val asesores = uiState.asesores.map {
        SelectOption(
            it.idAsesor,
            it.nombreCompleto.ifBlank { "Asesor ${it.idAsesor}" }
        )
    }
    val aulas = uiState.aulas.map {
        SelectOption(
            it.idAula,
            "Aula ${it.idAula}" + (it.capacidad?.let { capacidad -> " · cap. $capacidad" } ?: "")
        )
    }

    val materiaSeleccionada = uiState.materias
        .firstOrNull { it.idMateria == form.idMateria }?.nombreMateria
    val temaSeleccionado = uiState.temas
        .firstOrNull { it.idTema == form.idTema }?.nombreTema
    val asesorSeleccionado = uiState.asesores
        .firstOrNull { it.idAsesor == form.idAsesor }?.nombreCompleto
    val aulaSeleccionada = uiState.aulas
        .firstOrNull { it.idAula == form.idAula }
        ?.let { "Aula ${it.idAula}" + (it.capacidad?.let { c -> " · cap. $c" } ?: "") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            IntroCard(cursoNombre = cursoSeleccionado?.nombre)
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionTitle("Qué se enseña")

                    DropdownField(
                        label = "Materia",
                        value = materiaSeleccionada,
                        placeholder = "Selecciona una materia",
                        options = materias,
                        onSelect = viewModel::onMateriaSelected
                    )

                    DropdownField(
                        label = "Tema",
                        value = temaSeleccionado,
                        placeholder = if (form.idMateria == null) {
                            "Elige primero una materia"
                        } else {
                            "Selecciona un tema"
                        },
                        options = temas,
                        onSelect = viewModel::onTemaSelected
                    )

                    if (!uiState.mostrarNuevoTema) {
                        TextButton(
                            onClick = viewModel::mostrarNuevoTema,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("＋ Nuevo tema para esta materia")
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = BlueWash
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiState.nuevoTemaNombre,
                                    onValueChange = viewModel::onNuevoTemaNombreChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Nombre del tema nuevo") },
                                    singleLine = true,
                                    enabled = !uiState.creandoTema
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = viewModel::crearTema,
                                        enabled = !uiState.creandoTema
                                    ) {
                                        if (uiState.creandoTema) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                        } else {
                                            Text("Adicionar tema")
                                        }
                                    }
                                    OutlinedButton(
                                        onClick = viewModel::cancelarNuevoTema,
                                        enabled = !uiState.creandoTema
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
                            }
                        }
                    }

                    SectionTitle("Quién y dónde")

                    DropdownField(
                        label = "Curso de capacitación",
                        value = cursoSeleccionado?.nombre,
                        placeholder = "Selecciona un curso",
                        options = cursos,
                        onSelect = viewModel::onCursoSelected
                    )

                    DropdownField(
                        label = "Asesor",
                        value = asesorSeleccionado,
                        placeholder = "Selecciona un asesor",
                        options = asesores,
                        onSelect = viewModel::onAsesorSelected
                    )

                    DropdownField(
                        label = "Aula",
                        value = aulaSeleccionada,
                        placeholder = "Selecciona un aula",
                        options = aulas,
                        onSelect = viewModel::onAulaSelected
                    )

                    SectionTitle("Cuándo")

                    OutlinedTextField(
                        value = form.fecha,
                        onValueChange = viewModel::onFechaChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Fecha") },
                        placeholder = { Text("AAAA-MM-DD") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = form.horaInicio,
                            onValueChange = viewModel::onHoraInicioChange,
                            modifier = Modifier.weight(1f),
                            label = { Text("Hora inicio") },
                            placeholder = { Text("HH:mm") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )
                        OutlinedTextField(
                            value = form.horaFin,
                            onValueChange = viewModel::onHoraFinChange,
                            modifier = Modifier.weight(1f),
                            label = { Text("Hora fin") },
                            placeholder = { Text("HH:mm") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )
                    }
                }
            }
        }

        item {
            StudentsHeader(
                uiState = uiState,
                onTodosPresentes = { viewModel.marcarTodos(true) },
                onTodosAusentes = { viewModel.marcarTodos(false) }
            )
        }

        when {
            form.idCurso == null -> {
                item {
                    InfoCard(
                        titulo = "Elige un curso para ver su lista",
                        texto = "Los estudiantes aparecerán aquí para marcarlos como presentes o ausentes."
                    )
                }
            }

            uiState.cargandoAlumnos -> {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Cargando estudiantes del curso…",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            uiState.alumnos.isEmpty() -> {
                item {
                    InfoCard(
                        titulo = "Sin estudiantes",
                        texto = "No hay estudiantes matriculados ni registrados para este curso."
                    )
                }
            }

            else -> {
                if (uiState.notaAlumnos != null) {
                    item {
                        InfoCard(
                            titulo = "Aviso",
                            texto = uiState.notaAlumnos
                        )
                    }
                }

                items(
                    items = uiState.alumnos,
                    key = { it.idEstudiante }
                ) { alumno ->
                    StudentRow(
                        alumno = alumno,
                        marca = uiState.marcas[alumno.idEstudiante],
                        onToggle = { presente ->
                            viewModel.toggleAsistencia(alumno.idEstudiante, presente)
                        }
                    )
                }
            }
        }

        item {
            SaveCard(
                uiState = uiState,
                onGuardar = viewModel::guardarLista,
                onLimpiar = viewModel::limpiarFormulario
            )
        }

        if (uiState.mensaje != null || uiState.error != null) {
            item {
                StatusCard(
                    mensaje = uiState.mensaje,
                    error = uiState.error
                )
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
        }
    }
}

private data class SelectOption(
    val id: Int,
    val label: String
)

@Composable
private fun IntroCard(cursoNombre: String?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Navy
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = cursoNombre ?: "Arma la sesión y pasa lista",
                color = AlfaWhite,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (cursoNombre == null) {
                    "Completa los datos de la clase y marca la asistencia."
                } else {
                    "Curso seleccionado. Elige el tema y marca a los estudiantes."
                },
                color = AlfaWhite.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SectionTitle(texto: String) {
    Text(
        text = texto,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun DropdownField(
    label: String,
    value: String?,
    placeholder: String,
    options: List<SelectOption>,
    onSelect: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 62.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = value ?: placeholder,
                        color = if (value == null) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (value == null) FontWeight.Normal else FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
                Text(
                    text = "▾",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Sin opciones disponibles") },
                    onClick = { expanded = false },
                    enabled = false
                )
            } else {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            expanded = false
                            onSelect(option.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentsHeader(
    uiState: PaseListaUiState,
    onTodosPresentes: () -> Unit,
    onTodosAusentes: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Estudiantes",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${uiState.marcados}/${uiState.alumnos.size} marcados",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Amber.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = "${uiState.presentes} presentes",
                        color = Navy,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onTodosPresentes,
                    modifier = Modifier.weight(1f),
                    enabled = uiState.alumnos.isNotEmpty()
                ) {
                    Text("Todos presentes")
                }
                OutlinedButton(
                    onClick = onTodosAusentes,
                    modifier = Modifier.weight(1f),
                    enabled = uiState.alumnos.isNotEmpty()
                ) {
                    Text("Todos ausentes")
                }
            }

            if (uiState.alumnos.isNotEmpty()) {
                Text(
                    text = "${uiState.presentes} presentes · ${uiState.ausentes} ausentes · ${uiState.sinMarcar} sin marcar",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun StudentRow(
    alumno: AlumnoPaseLista,
    marca: Boolean?,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = when (marca) {
                        true -> PresentWash
                        false -> MaterialTheme.colorScheme.errorContainer
                        null -> BlueWash
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = alumno.iniciales,
                            color = when (marca) {
                                true -> Present
                                false -> MaterialTheme.colorScheme.error
                                null -> Navy
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alumno.nombre,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (!alumno.colegio.isNullOrBlank()) {
                        Text(
                            text = alumno.colegio,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = marca == true,
                    onClick = { onToggle(true) },
                    label = { Text("Presente") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PresentWash,
                        selectedLabelColor = Present
                    )
                )
                FilterChip(
                    selected = marca == false,
                    onClick = { onToggle(false) },
                    label = { Text("Ausente") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.error
                    )
                )
            }
        }
    }
}

@Composable
private fun SaveCard(
    uiState: PaseListaUiState,
    onGuardar: () -> Unit,
    onLimpiar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onGuardar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !uiState.guardando && uiState.form.idCurso != null && uiState.alumnos.isNotEmpty(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Guardar lista · ${uiState.marcados}/${uiState.alumnos.size}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            OutlinedButton(
                onClick = onLimpiar,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.guardando
            ) {
                Text("Limpiar formulario")
            }
        }
    }
}

@Composable
private fun StatusCard(mensaje: String?, error: String?) {
    val esError = error != null
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (esError) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            PresentWash
        }
    ) {
        Text(
            text = error ?: mensaje.orEmpty(),
            color = if (esError) MaterialTheme.colorScheme.onErrorContainer else Present,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun InfoCard(titulo: String, texto: String?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BlueWash
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                color = Navy,
                style = MaterialTheme.typography.titleMedium
            )
            if (!texto.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = texto,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun LoadingState(texto: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator()
        Text(
            text = texto,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ErrorState(
    titulo: String,
    mensaje: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = mensaje.ifBlank { "Intenta nuevamente." },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        OutlinedButton(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}
