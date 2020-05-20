package com.example.testapp

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.FormItem.RowType.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        recyclerView.adapter = Adapter(listOf(
            Any(),
            Any(),
            object : FormItem {
                override val rowType: FormItem.RowType
                    get() = TOP_ROW
            },
            object : FormItem {
                override val rowType: FormItem.RowType
                    get() = MIDDLE_ROW
            },
            object : FormItem {
                override val rowType: FormItem.RowType
                    get() = BOTTOM_ROW
            },
            Any(),
            Any(),
            Any(),
            object : FormItem {
                override val rowType: FormItem.RowType
                    get() = SINGULAR_ROW
            }
        ))
        recyclerView.addItemDecoration(FormItemDecoration(resources.getDimensionPixelOffset(R.dimen.dp_4),
            resources.getDimensionPixelOffset(R.dimen.dp_1)))
    }
}

class Adapter(override val items: List<*>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    RecyclerViewAdapter {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item,
                parent,
                false
            )
        ) {}
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }
}

interface FormItem {
    val rowType: RowType

    enum class RowType {
        TOP_ROW, MIDDLE_ROW, BOTTOM_ROW, SINGULAR_ROW
    }
}

class FormItemDecoration(private val radius: Int, thickness: Int) : RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        strokeWidth = thickness.toFloat()
        color = Color.BLACK
        style = Paint.Style.STROKE
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        for (i in 0 until parent.childCount) {
            val view: View = parent.getChildAt(i)

            val adapterPosition = parent.getChildAdapterPosition(view)
            val rowType = ((parent.adapter as? RecyclerViewAdapter
                ?: return).items[adapterPosition] as? FormItem)?.rowType ?: continue

            when (rowType) {
                TOP_ROW -> drawTopRow(c, view)
                MIDDLE_ROW -> drawMiddleRow(c, view)
                BOTTOM_ROW -> drawBottomRow(c, view)
                SINGULAR_ROW -> drawSingularRow(c, view)
            }
        }
    }

    private fun drawSingularRow(c: Canvas, view: View) {
        c.drawRoundRect(
            view.left.toFloat(),
            view.top.toFloat(),
            view.right.toFloat(),
            view.bottom.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
    }

    private fun drawBottomRow(c: Canvas, view: View) {
    val path = roundedRect(view.left.toFloat(), view.top.toFloat(), view.right.toFloat(),
        view.bottom.toFloat(), radius.toFloat(), radius.toFloat(),
            tl = false, tr = false, br = true, bl = true)
        c.drawPath(path, paint)

    }

    private fun drawMiddleRow(c: Canvas, view: View) {
        val path = Path().apply {
            moveTo(view.left.toFloat(), view.top.toFloat())
            lineTo(view.left.toFloat(), view.bottom.toFloat())
            moveTo(view.right.toFloat(), view.top.toFloat())
            lineTo(view.right.toFloat(), view.bottom.toFloat())
            moveTo(view.left.toFloat(), view.bottom.toFloat())
            lineTo(view.right.toFloat(), view.bottom.toFloat())
        }
        c.drawPath(path, paint)
    }

    private fun drawTopRow(c: Canvas, view: View) {
        val path = roundedRect(view.left.toFloat(), view.top.toFloat(), view.right.toFloat(),
            view.bottom.toFloat(), radius.toFloat(), radius.toFloat(),
            tl = true, tr = true, br = false, bl = false)
        c.drawPath(path, paint)
    }

}

fun roundedRect(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    rx: Float,
    ry: Float,
    tl: Boolean,
    tr: Boolean,
    br: Boolean,
    bl: Boolean
): Path {
    var rx = rx
    var ry = ry
    val path = Path()
    if (rx < 0) rx = 0f
    if (ry < 0) ry = 0f
    val width = right - left
    val height = bottom - top
    if (rx > width / 2) rx = width / 2
    if (ry > height / 2) ry = height / 2
    val widthMinusCorners = width - 2 * rx
    val heightMinusCorners = height - 2 * ry
    path.moveTo(right, top + ry)
    if (tr) path.rQuadTo(0f, -ry, -rx, -ry) //top-right corner
    else {
        path.rLineTo(0f, -ry)
        path.rLineTo(-rx, 0f)
    }
    path.rLineTo(-widthMinusCorners, 0f)
    if (tl) path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
    else {
        path.rLineTo(-rx, 0f)
        path.rLineTo(0f, ry)
    }
    path.rLineTo(0f, heightMinusCorners)
    if (bl) path.rQuadTo(0f, ry, rx, ry) //bottom-left corner
    else {
        path.rLineTo(0f, ry)
        path.rLineTo(rx, 0f)
    }
    path.rLineTo(widthMinusCorners, 0f)
    if (br) path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
    else {
        path.rLineTo(rx, 0f)
        path.rLineTo(0f, -ry)
    }
    path.rLineTo(0f, -heightMinusCorners)
    path.close() //Given close, last lineto can be removed.
    return path
}


interface RecyclerViewAdapter {
    val items: List<*>
}
