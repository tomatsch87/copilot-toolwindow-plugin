package io.github.tomatsch87.ui

import io.github.tomatsch87.actions.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.content.ContentFactory
import javax.swing.JComponent
import javax.swing.JPanel


// Defines a ToolWindowFactory that creates a CopilotToolWindow when the tool window is opened
class CopilotToolWindowFactory : ToolWindowFactory {

    // Creates a content factory instance to create content for the CopilotToolWindow
    private val contentFactory = ContentFactory.getInstance()

    // This function is called when the tool window is created
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = CopilotToolWindow(toolWindow)

        val content = contentFactory.createContent(myToolWindow.getContent(), null, false)

        // Add the content to the tool window's content manager
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class CopilotToolWindow(toolWindow: ToolWindow) {

        // Create a new SimpleToolWindow instance to hold the content
        private val content: JPanel = SimpleToolWindowPanel(true, true)

        init {
            // Create all the actions and panels that are used in the CopilotToolWindow
            // use dependency injection to pass the actions to the panels
            val searchAction = ToolWindowSearchAction()
            val editorPanel = ToolWindowEditorPanel(toolWindow, searchAction)
            searchAction.setEditorPanel(editorPanel)

            val clearAction = ToolWindowClearAction(editorPanel, searchAction)
            val requestFeedbackAction = ToolWindowRequestFeedbackAction(searchAction)
            val actionsPanel = ToolWindowActionsPanel(searchAction, clearAction, requestFeedbackAction)
            searchAction.setActionsPanel(actionsPanel)

            // Create a new OnePixelSplitter instance to split the actions panel and the editor panel
            val horizontalSplitter = OnePixelSplitter(true, 0.0f)
            horizontalSplitter.setResizeEnabled(false)

            // Add the actions panel and the editor panel to the OnePixelSplitter
            horizontalSplitter.firstComponent = actionsPanel
            horizontalSplitter.secondComponent = editorPanel

            // Add the OnePixelSplitter to the content of the ToolWindow
            content.add(horizontalSplitter)
        }

        // This function returns the content of the ToolWindow, which is a JComponent
        fun getContent() : JComponent {
            return content
        }
    }

}
