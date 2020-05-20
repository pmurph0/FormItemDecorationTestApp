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
            object: FormItem {
                override val rowType: FormItem.RowType
                    get() = TOP_ROW
            },
            object: FormItem {
                override val rowType: FormItem.RowType
                    get() = MIDDLE_ROW
            },
            object: FormItem {
                override val rowType: FormItem.RowType
                    get() = BOTTOM_ROW
            },
            Any(),
            Any(),
            Any(),
            object: FormItem {
                override val rowType: FormItem.RowType
                    get() = SINGULAR_ROW
            }
        ))
        recyclerView.addItemDecoration(FormItemDecoration(resources.getDimensionPixelOffset(R.dimen.dp_4)))
    }
}

class Adapter(override val items: List<*>): RecyclerView.Adapter<RecyclerView.ViewHolder>(), RecyclerViewAdapter {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object: RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)){}
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

    val itemViewType: Int get() =rowType.name.hashCode()



    companion object {
        fun rowTypeFor(collectionSize: Int, itemIndex: Int): RowType {
            if (collectionSize == 1) return RowType.SINGULAR_ROW

            if (collectionSize == 2) return if (itemIndex == 0) RowType.TOP_ROW else RowType.BOTTOM_ROW

            return when (itemIndex) {
                0 -> RowType.TOP_ROW
                collectionSize - 1 -> RowType.BOTTOM_ROW
                else -> MIDDLE_ROW
            }
        }

        fun fromItemViewType(viewType: Int): RowType? {
            return when (viewType) {
                RowType.TOP_ROW.name.hashCode() -> RowType.TOP_ROW
                MIDDLE_ROW.name.hashCode() -> MIDDLE_ROW
                RowType.BOTTOM_ROW.name.hashCode() -> MIDDLE_ROW
                RowType.SINGULAR_ROW.name.hashCode() -> MIDDLE_ROW
                else -> null
            }
        }
    }
}

class FormItemDecoration(val radius: Int): RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        strokeWidth = 1f    //TODO
        color = Color.BLACK //TODO
        style = Paint.Style.STROKE
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        for (i in 0 until parent.childCount) {
            val view: View = parent.getChildAt(i)

            val adapterPosition = parent.getChildAdapterPosition(view)
            val rowType = ((parent.adapter as? RecyclerViewAdapter ?: return).items[adapterPosition] as? FormItem)?.rowType ?: continue

            when (rowType) {
                FormItem.RowType.TOP_ROW -> drawTopRow(c, view)
                MIDDLE_ROW -> drawMiddleRow(c, view)
                FormItem.RowType.BOTTOM_ROW -> drawBottomRow(c, view)
                FormItem.RowType.SINGULAR_ROW -> drawSingularRow(c, view)
            }
        }
    }

    private fun drawSingularRow(c: Canvas, view: View) {
        c.drawRoundRect(view.left.toFloat(), view.top.toFloat(), view.right.toFloat(), view.bottom.toFloat(), radius.toFloat(), radius.toFloat(), paint)
    }

    private fun drawBottomRow(c: Canvas, view: View) {
        //todo rounded corners
        c.drawLine(view.left.toFloat(), view.top.toFloat(), view.left.toFloat(), view.bottom.toFloat(), paint)
        c.drawLine(view.right.toFloat(), view.top.toFloat(), view.right.toFloat(), view.bottom.toFloat(), paint)
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

//        c.drawLine(view.left.toFloat(), view.top.toFloat(), view.left.toFloat(), view.bottom.toFloat(), paint)
//        c.drawLine(view.right.toFloat(), view.top.toFloat(), view.right.toFloat(), view.bottom.toFloat(), paint)
//        c.drawLine(view.left.toFloat(), view.bottom.toFloat(), view.right.toFloat(), view.bottom.toFloat(), paint)
    }

    private fun drawTopRow(c: Canvas, view: View) {
        val path = roundedRect(view.left.toFloat(), view.top.toFloat(), view.right.toFloat(), view.bottom.toFloat(), radius.toFloat(), radius.toFloat(), topCornersOnly = true)
        c.drawPath(path, paint)

//        //todo rounded corners
//        c.drawLine(view.left.toFloat(), view.top.toFloat(), view.left.toFloat(), view.bottom.toFloat(), paint)  //todo rounded corners
//        c.drawLine(view.right.toFloat(), view.top.toFloat(), view.right.toFloat(), view.bottom.toFloat(), paint)
//        c.drawLine(view.left.toFloat(), view.bottom.toFloat(), view.right.toFloat(), view.bottom.toFloat(), paint)
    }

}

fun roundedRect(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    rx: Float,
    ry: Float,
    topCornersOnly: Boolean
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
    path.arcTo(right - 2 * rx, top, right, top + 2 * ry, 0f, -90f, false) //top-right-corner
    path.rLineTo(-widthMinusCorners, 0f)
    path.arcTo(left, top, left + 2 * rx, top + 2 * ry, 270f, -90f, false) //top-left corner.
    path.rLineTo(0f, heightMinusCorners)
    if (topCornersOnly) {
        path.rLineTo(0f, ry)
        path.rLineTo(width, 0f)
        path.rLineTo(0f, -ry)
    } else {
        path.arcTo(
            left,
            bottom - 2 * ry,
            left + 2 * rx,
            bottom,
            180f,
            -90f,
            false
        ) //bottom-left corner
        path.rLineTo(widthMinusCorners, 0f)
        path.arcTo(
            right - 2 * rx,
            bottom - 2 * ry,
            right,
            bottom,
            90f,
            -90f,
            false
        ) //bottom-right corner
    }
    path.rLineTo(0f, -heightMinusCorners)
    path.close() //Given close, last lineto can be removed.
    return path
}


interface RecyclerViewAdapter {
    val items: List<*>
}
