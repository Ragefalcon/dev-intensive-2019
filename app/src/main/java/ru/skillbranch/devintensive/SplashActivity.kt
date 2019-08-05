package ru.skillbranch.devintensive

import android.content.Context
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import ru.skillbranch.devintensive.repositories.PreferencesRepository
import ru.skillbranch.devintensive.ui.profile.ProfileActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        PreferencesRepository.getAppTheme().also {
//            AppCompatDelegate.setDefaultNightMode(it)
//        }

        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}