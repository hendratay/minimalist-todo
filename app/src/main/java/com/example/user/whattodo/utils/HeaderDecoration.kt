package com.example.user.whattodo.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.header_item.view.*

class HeaderDecoration(context: Context,
                       parent: RecyclerView,
                       resId: Int,
                       header: String): RecyclerView.ItemDecoration() {

    private var layout: View = LayoutInflater.from(context).inflate(resId, parent, false)

    init {
        layout.text_view_header.text = header
        layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
    }

    override fun onDraw(c: Canvas?, parent: RecyclerView, state: RecyclerView.State?) {
        super.onDraw(c, parent, state)
        layout.layout(parent.left, 0, parent.right, layout.measuredHeight)
        for(i in 0..parent.childCount) {
            val view = parent.getChildAt(i)
            if(parent.getChildAdapterPosition(view) == 0) {
                c?.save()
                val height = layout.measuredHeight
                val top = view.top - height
                c?.translate(0f, top.toFloat())
                layout.draw(c)
                c?.restore()
                break
            }
        }
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView, state: RecyclerView.State?) {
        if(parent.getChildAdapterPosition(view) == 0) {
            outRect?.set(0, layout.measuredHeight, 0, 0)
        } else {
            outRect?.setEmpty()
        }
    }
}