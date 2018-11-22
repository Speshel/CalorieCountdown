package me.loganfuller.caloriecountdown

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat


class MainActivity : AppCompatActivity() {

    /* Libraries
        https://github.com/afollestad/material-dialogs
        https://github.com/yuriy-budiyev/circular-progress-bar
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        btnRecord.setOnClickListener {
            MaterialDialog(this)
                .input(inputType = InputType.TYPE_CLASS_NUMBER) { _, text ->
                    val sharedPrefs = this.getSharedPreferences(getString(R.string.user_data_preferences), Context.MODE_PRIVATE)
                    sharedPrefs.edit().putInt(getString(R.string.key_current_weight), Integer.parseInt(text.toString())).apply()
                    init()
                }
                .title(R.string.dialog_title_record_data)
                .positiveButton(R.string.log)
                .negativeButton(R.string.cancel) {dialog ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun init() {
        val sharedPrefs = this.getSharedPreferences(getString(R.string.user_data_preferences), Context.MODE_PRIVATE)

        val startingWeight = sharedPrefs.getInt(getString(R.string.key_starting_weight), 0)
        val currentWeight = sharedPrefs.getInt(getString(R.string.key_current_weight), 0)
        val goalWeight = sharedPrefs.getInt(getString(R.string.key_goal_weight), 0)

        val caloriesToBurn = ((currentWeight * 3500f) - (goalWeight * 3500f))
        val caloriesBurnt = (startingWeight * 3500f) - (currentWeight * 3500f)

        Log.d("Oh no", "Sending " + caloriesToBurn + " | " + caloriesBurnt)

        /* Convert number to formatted number with comma/period separators and set progress bar progress calculated as percentage returned from '(caloriesToBurn / totalCalories) * 100' */
        val percentProgress = (caloriesBurnt / caloriesToBurn) * 100
        Log.d("Hello", "Setting progress to : " + percentProgress)
        totalProgress.progress = percentProgress

        txtCaloriesCounter.text = NumberFormat.getInstance().format(caloriesToBurn).toString()
    }
}
