package com.example.grupoalfa.data.session

import com.example.grupoalfa.data.model.Usuario

/** Sesión en memoria del usuario autenticado mediante Retrofit. */
object SesionActual {

    @Volatile
    var usuario: Usuario? = null

    fun iniciar(usuario: Usuario) {
        this.usuario = usuario
    }

    fun cerrar() {
        usuario = null
    }
}
