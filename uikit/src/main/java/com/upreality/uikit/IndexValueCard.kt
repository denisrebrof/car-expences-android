package com.upreality.uikit

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.upreality.uikit.databinding.IndexValueCardBinding

class IndexValueCard : CardView {

    private lateinit var binding: IndexValueCardBinding

    companion object {
        private const val DEF_VALUE_TEXT = "0"
        private const val DEF_TITLE_TEXT = "Title"
        private val DEF_ICON = R.drawable.ic_credit_card
        private val DEF_COLOR = R.color.colorPrimary
    }

    private var valueText = DEF_VALUE_TEXT
    private var title = DEF_TITLE_TEXT
    private var iconId = DEF_ICON
    private var iconTint = DEF_COLOR

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        if (attrs != null) {
            val styleable = R.styleable.IndexValueCard
            val attrArray = context.theme.obtainStyledAttributes(attrs, styleable, 0, 0)
            attrArray.also(this::fillAttributes).recycle()
        }
        setUpView(context)
    }

    @SuppressLint("ResourceAsColor")
    private fun fillAttributes(attrs: TypedArray) {
        valueText = attrs.getString(R.styleable.IndexValueCard_valueText) ?: DEF_VALUE_TEXT
        title = attrs.getString(R.styleable.IndexValueCard_titleText) ?: DEF_TITLE_TEXT
        iconId = attrs.getResourceId(R.styleable.IndexValueCard_icon, DEF_ICON)
        iconTint = attrs.getColor(R.styleable.IndexValueCard_iconColor, DEF_COLOR)
    }

    private fun setUpView(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = IndexValueCardBinding.inflate(inflater, this, true)
        applyIcon(iconId)
        applyIconTint(iconTint)
        applyTitle(title)
        applyValueText(valueText)
    }

    fun setValue(text: String) {
        valueText = text.also(this::applyValueText)
    }

    private fun applyIcon(iconId: Int) {
        try {
            AppCompatResources
                .getDrawable(context, iconId)
                .let(binding.icon::setImageDrawable)
        } catch (exception: Throwable) {
            //skip
        }
    }

    private fun applyIconTint(@ColorRes color: Int) {
        binding.icon.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
    }

    private fun applyValueText(text: String) {
        binding.value.text = text
    }

    private fun applyTitle(text: String) {
        binding.title.text = text
    }
}