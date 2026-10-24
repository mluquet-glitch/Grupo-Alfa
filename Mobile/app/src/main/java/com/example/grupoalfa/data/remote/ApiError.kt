package com.example.grupoalfa.data.remote

import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

/** Convierte errores de Retrofit/OkHttp en mensajes útiles para la UI. */
fun Throwable.mensajeAmigable(): String = when (this) {
    is HttpException -> {
        val body = runCatching { response()?.errorBody()?.string() }.getOrNull()
        val mensajeApi = body
            ?.let {
                runCatching {
                    val json = JsonParser.parseString(it).asJsonObject
                    json.get("error")?.asString ?: json.get("message")?.asString
                }.getOrNull()
            }
            ?.takeIf { it.isNotBlank() }
        mensajeApi ?: when (code()) {
            400 -> "Los datos enviados no son válidos."
            401 -> "Usuario o contraseña incorrectos."
            403 -> "No tienes permisos para realizar esta acción."
            409 -> "La información entra en conflicto con datos existentes."
            else -> "Error del servidor (${code()})."
        }
    }

    is SocketTimeoutException -> "El servidor tardó demasiado en responder. Intenta nuevamente."
    is ConnectException -> "No se pudo conectar con el servidor. Verifica la IP de la laptop y que la API esté corriendo."
    is IOException -> "Sin conexión. Revisa tu red e intenta nuevamente."
    else -> message ?: "Ocurrió un error inesperado."
}
