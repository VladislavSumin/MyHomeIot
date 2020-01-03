package ru.vladislavsumin.myhomeiot.ui.lamp.control

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import ru.vladislavsumin.myhomeiot.R

class OnOffButton : AppCompatImageButton {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    companion object {
        private val STATE_IS_ON = intArrayOf(R.attr.is_on)
    }

    var isOn = true
        set(isOn) {
            field = isOn
            refreshDrawableState()
        }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        return if (isOn) { // We are going to add 1 extra state.
            val drawableState = super.onCreateDrawableState(extraSpace + 1)
            mergeDrawableStates(drawableState, STATE_IS_ON)
            drawableState
        } else {
            super.onCreateDrawableState(extraSpace)
        }
    }
}