package me.loganfuller.caloriecountdown

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_setup.*

class SetupActivity : AppCompatActivity() {

    private val TAG = "SetupActivity"

    private var checkStartingWeight = false
    private var checkGoalWeight = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        btnStart.setOnClickListener {
            if(!txtStartingWeight.text!!.isEmpty()) {
                val startingWeight = Integer.parseInt(txtStartingWeight.text.toString())
                Log.i(TAG, "Setting starting weight to $startingWeight")
                sharedPrefs.edit().putInt(getString(R.string.key_starting_weight), startingWeight).apply()
                sharedPrefs.edit().putInt(getString(R.string.key_current_weight), startingWeight).apply()
                checkStartingWeight = true
            }
            if(!txtGoalWeight.text!!.isEmpty()) {
                val goalWeight = Integer.parseInt(txtGoalWeight.text.toString())
                Log.i(TAG, "Setting goal weight to $goalWeight")
                sharedPrefs.edit().putInt(getString(R.string.key_goal_weight), goalWeight).apply()
                checkGoalWeight = true
            }
            if(checkStartingWeight && checkGoalWeight) {
                if(Integer.parseInt(txtGoalWeight.text.toString()) < Integer.parseInt(txtStartingWeight.text.toString())) {
                    goalWeightLayout.error = null
                    sharedPrefs.edit().putBoolean(getString(R.string.key_setup_completed), true).apply()
                    val intent = Intent(this@SetupActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    goalWeightLayout.error = getString(R.string.error_higher_goal_weight)
                }
            }
        }
    }
}
