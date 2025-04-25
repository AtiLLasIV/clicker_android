package com.hwmipt.clicker3000

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale


class StatsActivity : AppCompatActivity() {

    private lateinit var exitButton: View

    private val monthNames = listOf(
        "январь", "февраль", "март", "апрель", "май", "июнь",
        "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"
    )

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val isFromDeeplink = intent?.action == Intent.ACTION_VIEW && intent.data != null
        exitButton = findViewById(R.id.back_button)
        if (isFromDeeplink) {
            exitButton.visibility = View.GONE
        }

        val prefs = getSharedPreferences("clicker_prefs", MODE_PRIVATE)

        dateSetup(prefs)

        makeChart(prefs)

        exitButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun dateSetup(prefs : SharedPreferences) {
        val today = LocalDate.now()
        val weekFields = WeekFields.of(Locale.getDefault())
        val currentWeek = today.get(weekFields.weekOfWeekBasedYear())
        val currentMonth = today.month
        val currentYear = today.year

        var clicksToday = 0
        var clicksThisWeek = 0
        var clicksThisMonth = 0
        var clicksThisYear = 0

        var date = LocalDate.of(currentYear, 1, 1)
        while (!date.isAfter(today)) {
            val key = "stats_${date.format(dateFormatter)}"
            val value = prefs.getInt(key, 0)

            val iterWeek = date.get(weekFields.weekOfWeekBasedYear())

            if (date == today) clicksToday += value
            if (iterWeek == currentWeek) clicksThisWeek += value
            if (date.month == currentMonth) clicksThisMonth += value
            if (date.year == currentYear) clicksThisYear += value

            date = date.plusDays(1)
        }
        findViewById<TextView>(R.id.stats_today).text = clicksToday.toString()
        findViewById<TextView>(R.id.stats_week).text = clicksThisWeek.toString()
        findViewById<TextView>(R.id.stats_month).text = clicksThisMonth.toString()
        findViewById<TextView>(R.id.stats_year).text = clicksThisYear.toString()

        val monthName = monthNames[today.monthValue - 1]
        findViewById<TextView>(R.id.stats_name_month).text = "Кликов за $monthName"
    }

    private fun makeChart(prefs : SharedPreferences) {
        val clickChart = findViewById<LinearLayout>(R.id.click_chart)
        val graphView = ClickerGraphView(this)

        val today = LocalDate.now()
        val monthData = List(30) { i ->
            val date = today.minusDays((29 - i).toLong())
            val key = "stats_${date.format(dateFormatter)}"
            prefs.getInt(key, 0)
        }

        // val randomData = List(30) { (5..200).random() }
        graphView.setData(monthData)

        clickChart.addView(graphView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f))
    }

}