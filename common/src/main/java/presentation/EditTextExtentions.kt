package presentation

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView

fun EditText.addAfterTextChangedListener(action: (String) -> Unit) {
    object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //do nothing
        }

        override fun afterTextChanged(s: Editable?) {
            if (!this@addAfterTextChangedListener.isFocused)
                s?.toString()?.let(action)
        }
    }.let(this::addTextChangedListener)
}

class AfterTextChangedWatcher(private val action: (String) -> Unit) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //do nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //do nothing
    }

    override fun afterTextChanged(s: Editable?) {
        s?.toString()?.let(action)
    }
}

fun TextView.applyWithDisabledTextWatcher(
    textWatcher: TextWatcher,
    codeBlock: TextView.() -> Unit
) {
    this.removeTextChangedListener(textWatcher)
    codeBlock()
    this.addTextChangedListener(textWatcher)
}