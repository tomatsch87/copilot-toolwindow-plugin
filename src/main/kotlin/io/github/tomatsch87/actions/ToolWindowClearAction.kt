package io.github.tomatsch87.actions

import io.github.tomatsch87.domain.QueryResponse
import io.github.tomatsch87.ui.ToolWindowEditorPanel
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

// This is the action that is triggered when the user clicks the "Clear" button in the tool window
class ToolWindowClearAction(private val editorPanel: ToolWindowEditorPanel, private val searchAction: ToolWindowSearchAction) : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // Clear the text in the EditorTextFields of the ToolWindowEditorPanel
        editorPanel.clearEditorTextFields()

        // Clear the feedback panel
        editorPanel.resetAllFeedbackPanels()

        // Clear the response instance
        searchAction.setResponseInstance(QueryResponse())
    }

}
