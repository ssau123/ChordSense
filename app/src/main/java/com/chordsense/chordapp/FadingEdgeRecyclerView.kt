package com.chordsense.chordapp

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class FadingEdgeRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    var topFadingStrength: Float? = null
    var bottomFadingStrength: Float? = null
    var leftFadingStrength: Float? = null
    var rightFadingStrength: Float? = null


    override fun getTopFadingEdgeStrength(): Float {
        return 0.0f
    }

    override fun getBottomFadingEdgeStrength(): Float {
        return bottomFadingStrength ?: super.getBottomFadingEdgeStrength()
    }

    override fun getLeftFadingEdgeStrength(): Float {
        return leftFadingStrength ?: super.getLeftFadingEdgeStrength()
    }

    override fun getRightFadingEdgeStrength(): Float {
        return rightFadingStrength ?: super.getRightFadingEdgeStrength()
    }

    fun disableTopFade() {
        topFadingStrength = 0f
    }

    fun disableBottomFade() {
        bottomFadingStrength = 0f
    }

    fun disableLeftFade() {
        leftFadingStrength = 0f
    }

    fun disableRightFade() {
        rightFadingStrength = 0f
    }


}
   