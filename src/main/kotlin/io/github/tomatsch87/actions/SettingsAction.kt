package io.github.tomatsch87.actions

import io.github.tomatsch87.ui.SettingsDialogWrapper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class SettingsAction : AnAction() {

    // This method is called when the action is triggered.
    override fun actionPerformed(e: AnActionEvent) {
        // Create a new instance of the SettingsDialogWrapper and display it to the user.
        SettingsDialogWrapper().showAndGet()
    }
}