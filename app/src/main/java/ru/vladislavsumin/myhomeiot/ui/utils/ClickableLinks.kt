package ru.vladislavsumin.myhomeiot.ui.utils

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import java.util.regex.Pattern

object ClickableLinks {
    private val LINK_PATTERN = Pattern.compile("#link([0-9])+\\((.*?)\\)")

    fun setClickableLinkListener(
        textView: TextView, text: CharSequence, listener: (linkId: Int) -> Unit

    ) {
        val ssb = SpannableStringBuilder(text)
        while (replaceNextLinkForSpan(ssb, listener)) continue

        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(ssb, TextView.BufferType.SPANNABLE)
    }

    private fun replaceNextLinkForSpan(
        sb: SpannableStringBuilder,
        listener: (linkId: Int) -> Unit
    ): Boolean {
        val m = LINK_PATTERN.matcher(sb)
        if (!m.find()) return false

        val mStart = m.start()
        val mEnd = m.end()
        val id = m.group(1)!!.toInt()
        val label = m.group(2)!!
        sb.delete(mStart, mEnd)
        sb.insert(mStart, label)

        val span = ClickableUrlSpan(id, listener)

        sb.setSpan(span, mStart, mStart + label.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return true
    }

    private class ClickableUrlSpan(
        private val mLinkId: Int,
        private val listener: (linkId: Int) -> Unit
    ) : ClickableSpan() {
        override fun onClick(widget: View) {
            listener(mLinkId)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
        }
    }
}

fun TextView.setClickableLinkListener(listener: (linkId: Int) -> Unit) {
    ClickableLinks.setClickableLinkListener(this, this.text, listener)
}

fun CheckBox.setClickableLinkListener(listener: (linkId: Int) -> Unit) {
    ClickableLinks.setClickableLinkListener(this, this.text, listener)
}