package com.example.minisumoaj.database

import android.app.Application
import android.os.Bundle

class DatabaseDevice: Application() {
    companion object {
        lateinit var preference:Preference
    }

    override fun onCreate(){
        super.onCreate()
        preference = Preference(applicationContext)
    }
}