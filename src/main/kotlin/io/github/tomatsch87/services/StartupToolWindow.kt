package io.github.tomatsch87.services

import io.github.tomatsch87.ui.ToolWindowActionsPanel
import io.github.tomatsch87.ui.ToolWindowEditorPanel
import io.github.tomatsch87.ui.ToolWindowFeedbackPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import java.awt.Component
import java.awt.Container

// This class is used to refresh the tool window when the project is opened
class StartupToolWindow : StartupActivity {
    // This method is run when the project is opened
    override fun runActivity(project: Project) {

        // Log in user on startup
        AuthenticationService().loginUserNoPopup()

        // Get the tool window by its ID
        val toolWindowId = "Copilot Toolwindow"
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow(toolWindowId)

        // Check if the tool window exists and is visible
        if (toolWindow != null && toolWindow.isVisible) {
            // Get the content of the tool window
            val contentManager = toolWindow.contentManager
            val content = contentManager.getContent(0)

            // Check if the content exists
            if (content != null) {
                // Get the component of the content
                val component = content.component

                // Check if the component is a SimpleToolWindowPanel
                if (component is SimpleToolWindowPanel) {
                    // Get the content of the SimpleToolWindowPanel
                    val contentComponent = component.getComponent(0)

                    // Check if the content of the SimpleToolWindowPanel is a OnePixelSplitter
                    if (contentComponent is OnePixelSplitter) {
                        // Get the first component of the OnePixelSplitter
                        val firstComponent = contentComponent.getComponent(1)

                        // Check if the first component of the OnePixelSplitter is a ToolWindowActionsPanel
                        if (firstComponent is ToolWindowActionsPanel) {
                            // Refresh buttons of the ToolWindowActionsPanel
                            firstComponent.refreshButtons()
                        }

                        // Get the second component of the OnePixelSplitter
                        val secondComponent = contentComponent.getComponent(2)

                        // Check if the second component of the OnePixelSplitter is a ToolWindowEditorPanel
                        if (secondComponent is ToolWindowEditorPanel) {
                            // Get the editor panel splitters of the ToolWindowEditorPanel
                            val editorScrollPane = secondComponent.getComponent(0)

                            if (editorScrollPane is JBScrollPane) {
                                val vertSplitters = editorScrollPane.viewport.view

                                // Create a list to store all components
                                val componentsList = mutableListOf<Component>()

                                // Check if the view is a container (such as JPanel, JComponent, etc.)
                                if (vertSplitters is Container) {
                                    for (c in vertSplitters.components) {
                                        componentsList.add(c)
                                    }
                                }

                                componentsList.forEach {
                                    // Check if the editor panel splitter is a OnePixelSplitter
                                    if (it is OnePixelSplitter) {

                                        // Get the second component of the editor panel splitter
                                        val editorSecondComponent = it.getComponent(2)

                                        // Check if the second component of the editor panel splitter is a ToolWindowFeedbackPanel
                                        if (editorSecondComponent is ToolWindowFeedbackPanel) {

                                            // Refresh the buttons of the ToolWindowFeedbackPanel
                                            editorSecondComponent.refreshButtons()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}