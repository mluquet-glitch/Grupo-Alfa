package com.example.grupoalfa.ui.usuario

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupoalfa.data.model.Usuario
import com.example.grupoalfa.data.remote.mensajeAmigable
import com.example.grupoalfa.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

data class UsuarioUiState(
    val usuario: Usuario? = null,
    val cargando: Boolean = true,
    val error: String? = null
)

class UsuarioViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    var uiState by mutableStateOf(UsuarioUiState())
        private set

    init {
        cargarPerfil()
    }

    fun cargarPerfil() {
        viewModelScope.launch {
            uiState = uiState.copy(cargando = true, error = null)
            runCatching { repository.getPerfil() }
                .onSuccess { usuario ->
                    uiState = UsuarioUiState(usuario = usuario, cargando = false)
                }
                .onFailure { error ->
                    uiState = uiState.copy(cargando = false, error = error.mensajeAmigable())
                }
        }
    }
}
