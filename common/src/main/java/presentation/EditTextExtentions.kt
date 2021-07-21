package presentation

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

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

fun EditText.getAfterTextChangedWatcher(action: (String) -> Unit): TextWatcher {
    return object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //do nothing
        }

        override fun afterTextChanged(s: Editable?) {
            if (!this@getAfterTextChangedWatcher.isFocused)
                s?.toString()?.let(action)
        }
    }
}

fun EditText.silentApplyText(text: String, watcher: TextWatcher) {
    if (this.text.toString() == text)
        return
    removeTextChangedListener(watcher)
    text.let(this::setText)
    addTextChangedListener(watcher)
}