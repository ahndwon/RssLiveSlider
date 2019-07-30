package xyz.thingapps.rssliveslider.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class DividerItemDecoration(context: Context, colorId: Int) : RecyclerView.ItemDecoration() {
    private val paint = Paint()

    init {
        paint.color = ContextCompat.getColor(context, colorId)
        paint.strokeWidth = 1f
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) == 0) return

    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val position = params.viewAdapterPosition

            if (position < state.itemCount - 1) {
                canvas.drawLine(
                    (child.left.toFloat()),
                    (child.bottom + 20).toFloat(),
                    (child.right.toFloat()),
                    (child.bottom + 20).toFloat(),
                    paint
                )
            }
        }
    }
}