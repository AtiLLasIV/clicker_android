package com.hwmipt.clicker3000

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private var clicks = 0
    private var level = 1

    private lateinit var gameButton: View
    private lateinit var gameCounter: TextView
    private lateinit var underText: TextView

    private lateinit var infoButton: View
    private lateinit var exitButton: View
    private lateinit var statsButton: View

    private lateinit var prefs: SharedPreferences

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val PREFS_NAME = "clicker_prefs"
    private val KEY_CLICKS = "clicks"
    private val KEY_LEVEL = "level"

    private val BUNDLE_KEY_CLICKS = "bundle_clicks"
    private val BUNDLE_KEY_LEVEL = "bundle_level"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        clicks = savedInstanceState?.getInt(BUNDLE_KEY_CLICKS) ?: prefs.getInt(KEY_CLICKS, 0)
        level = savedInstanceState?.getInt(BUNDLE_KEY_LEVEL) ?: prefs.getInt(KEY_LEVEL, 1)

        gameButton = findViewById(R.id.main_button)
        gameCounter = findViewById(R.id.main_counter)
        underText = findViewById(R.id.main_undertext)
        infoButton = findViewById(R.id.tg_button)
        exitButton = findViewById(R.id.exit_button)
        statsButton = findViewById(R.id.stat_button)


        setButtonImage()
        
        gameCounter.text = clicks.toString()
        underText.text = "Нажми на кнопку"

        gameButton.setOnClickListener {
            clicks += 1
            gameCounter.text = clicks.toString()
            prefs.edit().putInt(KEY_CLICKS, clicks).apply()

            val todayKey = "stats_${LocalDate.now().format(dateFormatter)}"
            val todayCount = prefs.getInt(todayKey, 0) + 1
            prefs.edit().putInt(todayKey, todayCount).apply()

            levelDetermine()

            underText.text = "До уровня ${level + 1} осталось ${levelToClicks[level]?.minus(clicks)} кликов"
        }

        statsButton.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }

        infoButton.setOnClickListener {
            val url = "https://t.me/AtiLLas"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        exitButton.setOnClickListener {
            finishAffinity()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_KEY_CLICKS, clicks)
        outState.putInt(BUNDLE_KEY_LEVEL, level)
    }


    private fun levelDetermine() {
        var newLevel = 0
        for (i in levelToClicks.keys) {
            if (clicks < levelToClicks[i]!!) {
                newLevel = i
                break
            }
        }
        if (newLevel != level) {
            level = newLevel
            prefs.edit().putInt(KEY_LEVEL, level).apply()
            setButtonImage()
        }
    }

    private fun setButtonImage() {
        if (level == 1) gameButton.setBackgroundResource(R.drawable.pretty_button_1)
        if (level == 2) gameButton.setBackgroundResource(R.drawable.pretty_button_2)
        if (level == 3) gameButton.setBackgroundResource(R.drawable.pretty_button_3)
        if (level == 4) gameButton.setBackgroundResource(R.drawable.pretty_button_4)
        if (level == 5) gameButton.setBackgroundResource(R.drawable.pretty_button_5)
        if (level == 6) gameButton.setBackgroundResource(R.drawable.pretty_button_6)
        if (level == 7) gameButton.setBackgroundResource(R.drawable.pretty_button_7)
    }

    private val levelToClicks: Map<Int, Int> = mapOf(
        1 to 10,
        2 to 30,
        3 to 50,
        4 to 100,
        5 to 200,
        6 to 300,
        7 to 1000000 // нет картинки)
    )
}