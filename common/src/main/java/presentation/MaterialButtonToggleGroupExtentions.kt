package presentation

import com.google.android.material.button.MaterialButtonToggleGroup

fun MaterialButtonToggleGroup.applyWithDisabledOnButtonCheckedListener(
    listener: MaterialButtonToggleGroup.OnButtonCheckedListener,
    codeBlock: MaterialButtonToggleGroup.() -> Unit
) {
    this.removeOnButtonCheckedListener(listener)
    codeBlock()
    this.addOnButtonCheckedListener(listener)
}