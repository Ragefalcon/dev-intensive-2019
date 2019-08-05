package ru.skillbranch.devintensive

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import ru.skillbranch.devintensive.repositories.PreferencesRepository
import ru.skillbranch.devintensive.ui.profile.ProfileActivity

class App : Application() {
    companion object{
        private var instance:App? = null
        fun applicationContext() : Context{
            return instance!!.applicationContext
        }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        PreferencesRepository.getAppTheme().also {
            AppCompatDelegate.setDefaultNightMode(it)
        }
        //TODO call once when application created
    }
}