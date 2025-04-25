package com.hwmipt.clicker3000

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import java.time.LocalDate

class ClickerGraphView(context: Context) : View(context) {
    private var clickData: List<Int> = emptyList()
    private var maxValue = 1
    private var graphCoef = 1.2

    private var path = Path()
    private var xScale = 0f
    private var yScale = 0f
    private val labelCount = 6
    private val yArray = IntArray(labelCount)
    private val xLabels = mutableListOf<Pair<Float, String>>()
    private val yLabels = mutableListOf<Pair<Float, String>>()

    private val monthNames = listOf("января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря")

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 30f
    }

    fun setData(newData: List<Int>) {
        clickData = newData
        maxValue = clickData.maxOrNull() ?: 1
        if (maxValue < 1) maxValue = 1

        maxValue = (maxValue * graphCoef).toInt()
        for (i in 1 until labelCount) {
            yArray[i] = (i * maxValue / (labelCount - 1))
        }

        setupPath()
        setupXLabels()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        setupPath()
        setupXLabels()
        setupYLabels()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)

        for ((y, label) in yLabels) {
            canvas.drawText(label, 10f, y - 10f, textPaint)
        }

        for ((x, label) in xLabels) {
            canvas.drawText(label, x, height - 15f, textPaint)
        }

        canvas.drawPath(path, linePaint)
    }

    private fun setupPath() {
        path.reset()

        xScale = width / (clickData.size - 1).toFloat()
        yScale = height / maxValue.toFloat()

        path.moveTo(0f, height - clickData[0] * yScale)
        for (i in 1 until clickData.size) {
            val x = i * xScale
            val y = height - clickData[i] * yScale
            path.lineTo(x, y)
        }
    }

    private fun setupXLabels() {
        xLabels.clear()
        val today = LocalDate.now()
        for (i in clickData.indices step 7) {
            val iterDate = today.minusDays((clickData.size - 1 - i).toLong())
            val label = "${iterDate.dayOfMonth} ${monthNames[iterDate.monthValue - 1]}"
            val x = i * xScale
            xLabels.add(x to label)
        }
    }

    private fun setupYLabels() {
        yLabels.clear()
        for (i in 1 until yArray.size) {
            val y = height - yArray[i] * yScale
            yLabels.add(y to yArray[i].toString())
        }
    }
}