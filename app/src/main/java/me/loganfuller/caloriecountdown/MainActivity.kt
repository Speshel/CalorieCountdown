package me.loganfuller.caloriecountdown

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import kotlinx.android.synthetic.main.activity_main.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.text.NumberFormat
import android.content.Intent
import android.widget.Toast
import android.content.SharedPreferences
import android.opengl.Visibility
import android.view.View
import androidx.preference.PreferenceManager.getDefaultSharedPreferences


class MainActivity : AppCompatActivity() {

    /* Libraries
        https://github.com/afollestad/material-dialogs
        https://github.com/yuriy-budiyev/circular-progress-bar
        https://github.com/DanielMartinus/Konfetti
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        btnRecord.setOnClickListener {
            MaterialDialog(this)
                .input(waitForPositiveButton = false, inputType = InputType.TYPE_CLASS_NUMBER) { dialog, text ->
                    val sharedPrefs = getDefaultSharedPreferences(this)
                    val inputField = dialog.getInputField()
                    if(inputField?.text.toString() != "") {
                        val isValid = (Integer.parseInt(text.toString()) < sharedPrefs.getInt(getString(R.string.key_starting_weight), 0))
                        inputField?.error = if (isValid) null else "Please enter a weight lower than your starting weight."
                        dialog.setActionButtonEnabled(WhichButton.POSITIVE, isValid)
                    }
                }
                .title(R.string.dialog_title_record_data)
                .positiveButton(R.string.log) { dialog ->
                    val sharedPrefs = getDefaultSharedPreferences(this)
                    sharedPrefs.edit().putInt(getString(R.string.key_current_weight), Integer.parseInt(dialog.getInputField()?.text.toString())).apply()
                    init()
                }
                .negativeButton(R.string.cancel) { dialog ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun init() {
        val sharedPrefs = getDefaultSharedPreferences(this)

        val startingWeight = sharedPrefs.getInt(getString(R.string.key_starting_weight), 0)
        val currentWeight = sharedPrefs.getInt(getString(R.string.key_current_weight), 0)
        val goalWeight = sharedPrefs.getInt(getString(R.string.key_goal_weight), 0)

        val caloriesToBurn = ((currentWeight * 3500f) - (goalWeight * 3500f))
        val caloriesBurnt = (startingWeight * 3500f) - (currentWeight * 3500f)

        Log.d("Values", "startingWeight: " + startingWeight + "\t"
                + "currentWeight: " + currentWeight + "\t"
            + "goalWeight: " + goalWeight + "\t"
            + "caloriesToBurn: " + caloriesToBurn + "\t"
            + "caloriesBurnt: " + caloriesBurnt)

        /* Convert number to formatted number with comma/period separators and set progress bar progress calculated as percentage returned from '(caloriesToBurn / totalCalories) * 100' */
        val percentProgress = (caloriesBurnt / (caloriesToBurn + caloriesBurnt)) * 100
        Log.d("Hello", "Setting progress to : $percentProgress")
        totalProgress.progress = percentProgress

        txtCaloriesCounter.text = NumberFormat.getInstance().format(caloriesToBurn).toString()

        btnShare.setOnClickListener {
            val formattedCaloriesBurnt = NumberFormat.getInstance().format(caloriesBurnt).toString()
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,
                    "I have burnt $formattedCaloriesBurnt calories using the Calorie Countdown application. Check the application out and keep track of how many calories you have to burn to meet your weight-loss goal!"
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        if(caloriesToBurn <= 0) {
            btnRestart.visibility = View.VISIBLE
            btnRecord.visibility = View.INVISIBLE
            viewKonfetti.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(Size(8))
                .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
                .streamFor(150, 5000L)
            MaterialDialog(this).show {
                title(R.string.dialog_title_congratulations)
                message(R.string.dialog_message_congratulations)
                positiveButton(R.string.yes) { dialog ->
                    sharedPrefs.edit().clear().apply()
                    val i = Intent(baseContext, InitActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                }
                negativeButton(R.string.no) { dialog ->
                    dialog.dismiss()
                }
            }
            btnRestart.setOnClickListener {
                btnRestart.visibility = View.INVISIBLE
                btnRecord.visibility = View.VISIBLE
                sharedPrefs.edit().clear().apply()
                val i = Intent(baseContext, InitActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            }
        }
    }
}
