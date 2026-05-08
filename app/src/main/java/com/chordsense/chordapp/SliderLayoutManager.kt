package com.chordsense.chordapp

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chordsense.chordapp.MainActivity.Companion.screenWidth

class SliderLayoutManager(context: Context?, val itemsPerPage: Int) : LinearLayoutManager(context) {

    var callback: OnItemSelectedListener? = null
    private lateinit var recyclerView:RecyclerView


    override fun onAttachedToWindow(view: RecyclerView?) {
        if (view != null) {
            recyclerView = view
        }
        super.onAttachedToWindow(view)
    }

    override fun setOrientation(orientation: Int) {
        super.setOrientation(orientation)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        scaleDownView()
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            scaleDownView()
            return scrolled
        } else {
            return 0
        }
    }

    private fun scaleDownView() {
        val mid = width / 2.0f
        for (i in 0 until childCount) {

            // Calculating the distance of the child from the center
            val child = getChildAt(i)!!
            val childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f
            val distanceFromCenter = Math.abs(mid - childMid)

            // The scaling formula
            if (distanceFromCenter>screenWidth/4) {
                child.alpha=0.5F
//                child.scaleX=0.75F
//                child.scaleY=0.75F

            } else {
                child.alpha=1.0F
                child.scaleX=1F
                child.scaleY=1F
            }
        }
    }


    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        // When scroll stops we notify on the selected item
        if (state.equals(RecyclerView.SCROLL_STATE_SETTLING)) {

            // Find the closest child to the recyclerView center --> this is the selected item.
            val recyclerViewCenterX = getRecyclerViewCenterX()
            var minDistance = recyclerView.width
            var position = -1
            for (i in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(i)
                val childCenterX = getDecoratedLeft(child) + (getDecoratedRight(child) - getDecoratedLeft(child)) / 2
                var childDistanceFromCenter = Math.abs(childCenterX - recyclerViewCenterX)
                if (childDistanceFromCenter < minDistance) {
                    minDistance = childDistanceFromCenter
                    position = recyclerView.getChildLayoutPosition(child)
                }
            }

            // Notify on the selected item
            callback?.onItemSelected(position)
        }
    }

    private fun getRecyclerViewCenterX() : Int {
        return (recyclerView.right - recyclerView.left)/2 + recyclerView.left
    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        return super.checkLayoutParams(lp) && lp!!.width == getItemSize()
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return setProperItemSize(super.generateDefaultLayoutParams())
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): RecyclerView.LayoutParams {
        return setProperItemSize(super.generateLayoutParams(lp))
    }

    private fun setProperItemSize(lp: RecyclerView.LayoutParams): RecyclerView.LayoutParams {
        val itemSize = getItemSize()
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            lp.width = itemSize
        } else {
            lp.height = itemSize
        }
        return lp
    }

    private fun getItemSize(): Int {
        val pageSize = if (orientation == LinearLayoutManager.HORIZONTAL) width else height
        return (Math.round(pageSize.toFloat() / itemsPerPage))
    }

    interface OnItemSelectedListener {
        fun onItemSelected(layoutPosition: Int)

    }
}