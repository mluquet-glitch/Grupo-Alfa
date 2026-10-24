package com.example.grupoalfa

import android.app.Application
import com.example.grupoalfa.data.remote.RetrofitClient
import com.example.grupoalfa.data.session.SecureCredentialStore

class GrupoAlfaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        SecureCredentialStore.init(this)
        RetrofitClient.authToken = SecureCredentialStore.getToken()
    }
}
