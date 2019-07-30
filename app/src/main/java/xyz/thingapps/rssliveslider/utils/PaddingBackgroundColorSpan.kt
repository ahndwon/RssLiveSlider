package xyz.thingapps.rssliveslider.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.LineBackgroundSpan


class PaddingBackgroundColorSpan(
    private val backgroundColor: Int = 0,
    private val padding: Int = 0
) : LineBackgroundSpan {

    private val rect = RectF()

    override fun drawBackground(
        c: Canvas,
        p: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lnum: Int
    ) {
        val textWidth = Math.round(p.measureText(text, start, end))
        val paintColor = p.color

        rect.set(
            (left - padding).toFloat(),
            (top - if (lnum == 0) padding / 2 else -(padding / 2)).toFloat(),
            (left + textWidth + padding).toFloat(),
            (bottom + padding / 2).toFloat()
        )
        p.color = backgroundColor
        c.drawRect(rect, p)
        p.color = paintColor
    }

}