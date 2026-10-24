package com.example.grupoalfa.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupoalfa.data.model.Usuario
import com.example.grupoalfa.data.remote.mensajeAmigable
import com.example.grupoalfa.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

data class LoginUiState(
    val identificador: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null
)

class LoginViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onIdentificadorChange(valor: String) {
        uiState = uiState.copy(identificador = valor, error = null)
    }

    fun onContrasenaChange(valor: String) {
        uiState = uiState.copy(contrasena = valor, error = null)
    }

    fun toggleContrasenaVisible() {
        uiState = uiState.copy(contrasenaVisible = !uiState.contrasenaVisible)
    }

    fun login(onSuccess: (Usuario) -> Unit) {
        val identificador = uiState.identificador.trim()
        val contrasena = uiState.contrasena

        if (identificador.isBlank() || contrasena.isBlank()) {
            uiState = uiState.copy(error = "Completa tu correo o carnet y tu contraseña para continuar.")
            return
        }
        if (uiState.cargando) return

        viewModelScope.launch {
            uiState = uiState.copy(cargando = true, error = null)
            runCatching { repository.login(identificador, contrasena) }
                .onSuccess { usuario ->
                    uiState = uiState.copy(cargando = false, contrasena = "")
                    onSuccess(usuario)
                }
                .onFailure { error ->
                    uiState = uiState.copy(cargando = false, error = error.mensajeAmigable())
                }
        }
    }
}
