package io.github.tomatsch87.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

// This is the action that is triggered when the user clicks on the "Copy" button in the tool window
class ToolWindowCopyAction(private val panelNumber: Int, private val searchAction: ToolWindowSearchAction) : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        // Get the response instance from the ToolWindowSearchAction
        val responseInstance = searchAction.getResponseInstance()

        // If the response instance is empty, return
        if (responseInstance.results.isEmpty()) {
            return
        }

        // Get the result from the response instance
        val result = responseInstance.results[panelNumber]

        // Get the code from the result
        val code = result.content.code

        // Copy the code to the clipboard
        val selection = StringSelection(code)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, null)

    }

}
