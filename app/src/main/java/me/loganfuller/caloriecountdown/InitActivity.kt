package me.loganfuller.caloriecountdown

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import androidx.preference.PreferenceManager


class InitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val intent: Intent

        intent = if(!sharedPrefs.getBoolean(getString(R.string.key_setup_completed), false)) {
            Intent(this, SetupActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
