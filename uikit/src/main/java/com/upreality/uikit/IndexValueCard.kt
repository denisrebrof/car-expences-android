package com.upreality.uikit

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


/**
 * TODO: document your custom view class.
 */
class IndexValueCard : LinearLayout  {

    private var mValue: View? = null
    private var mImage: ImageView? = null

    var exampleDrawable: Drawable? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.IndexValueCard, 0, 0
        )
        val titleText = a.getString(R.styleable.IndexValueCard_titleText)
        val valueColor = a.getColor(
            R.styleable.IndexValueCard_valueColor,
            0
        )
        a.recycle()

        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.index_value_card, this, true)

        val title = getChildAt(0) as TextView
        title.text = titleText

        mValue = getChildAt(1)
        mImage?.setBackgroundColor(valueColor)

        mImage = getChildAt(2) as ImageView
    }
}