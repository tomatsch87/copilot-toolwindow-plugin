package io.github.tomatsch87.ui

import io.github.tomatsch87.actions.ToolWindowSearchAction
import io.github.tomatsch87.domain.ToolWindowEditor
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.ui.EditorTextField
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBLabel
import com.intellij.util.asSafely
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.*

// This class defines the editor panel which is the main panel of the ToolWindow with 10 editor text fields
class ToolWindowEditorPanel(private val toolWindow: ToolWindow, searchAction: ToolWindowSearchAction) : NonOpaquePanel() {

    // The content panel which contains the 10 editor panels
    private val content = JPanel()

    // Get intellij window height
    private val windowHeight = WindowManager.getInstance().getFrame(toolWindow.project)?.height ?: 0

    // List of the editor panels
    private val editorPanels: MutableList<JPanel> = mutableListOf()

    // List of the editor text fields
    private val editorTextFields: MutableList<EditorTextField> = mutableListOf()

    // List of the editor file path labels
    private val editorFilePathLabels: MutableList<JBLabel> = mutableListOf()

    // List of the feedback panel instances
    private val feedbackInstance: MutableList<ToolWindowFeedbackPanel> = mutableListOf()

    init {

        // Set the layout of the content panel to vertical box layout
        content.layout = BoxLayout(content, BoxLayout.Y_AXIS)

        // variable to store the language of the file
        var extension = "unknown"

        // Get the application name
        val app = ApplicationInfo.getInstance().fullApplicationName

        // Set the language of the file based on the application
        when {
            app.contains("IntelliJ") -> {
                extension = "java"
            }
            app.contains("PyCharm") -> {
                extension = "py"
            }
            app.contains("WebStorm") -> {
                extension = "js"
            }
        }

        // Add 10 editor panels to the content panel
        for (i in 0 until 10) {
            // Create a new ToolWindowFeedbackPanel instance to display the like and dislike buttons
            val feedbackPanel = ToolWindowFeedbackPanel(i, searchAction)
            feedbackInstance.add(feedbackPanel)

            // Create a new ToolWindowEditorPanel instance to display the editor text field
            val editorPanel = createEditorPanel(extension)

            // Create a new JBLabel instance to display the file path of the editor
            val editorLabel = JBLabel("")

            // Set the border of the editor label
            editorLabel.border = BorderFactory.createEmptyBorder(3, 5, 3, 5)

            // Add the editor label to the list of editor labels in the companion object
            editorFilePathLabels.add(editorLabel)

            // Add the editor label to the editor panel
            editorPanel.add(editorLabel, BorderLayout.NORTH)

            // Add the editor panel to the list of editor panels in the companion object
            editorPanels.add(editorPanel)

            // Create a new OnePixelSplitter instance to split the feedback panel and the editor panel
            val verticalSplitter = OnePixelSplitter(true, 0.95f)
            verticalSplitter.setResizeEnabled(false)

            // Add the feedback panel and the editor panel to the OnePixelSplitter
            verticalSplitter.firstComponent = editorPanel
            verticalSplitter.secondComponent = feedbackPanel

            // Add the OnePixelSplitter to the content of the editor panel
            content.add(verticalSplitter)

            // Add a horizontal line after every vertical splitter
            content.add(Box.createRigidArea(Dimension(0, 2)))
        }

        // Wrap content panel in a scroll pane for vertical scrolling
        val scrollPane = JBScrollPane(content)
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED

        // Add the scroll pane to the main panel
        layout = BorderLayout()
        add(scrollPane, BorderLayout.CENTER)
    }

    // This function creates an editor panel with a single editor text field
    private fun createEditorPanel(extension: String): JPanel {
        // Create the editor text field
        val editorTextField = createEditorTextField(extension)

        // Add the editor text field to the list of editor text fields in the companion object
        editorTextFields.add(editorTextField)

        // Create the editor panel
        val editorPanel = JPanel()

        // Set the layout of the editor panel to border layout
        editorPanel.layout = BorderLayout()

        // Set the maximum size of the editor panel
        editorPanel.maximumSize = Dimension(Int.MAX_VALUE, if (windowHeight > 0) windowHeight / 2 else 600)

        // Add an empty WEST component with a BoxLayout to enforce a minimum height
        val westPanel = JPanel()
        westPanel.layout = BoxLayout(westPanel, BoxLayout.Y_AXIS)
        westPanel.add(Box.createVerticalGlue())
        editorPanel.add(westPanel, BorderLayout.WEST)

        // Add the editor text field in a scroll pane to the CENTER component
        val editorScrollPane = JBScrollPane(editorTextField)
        editorScrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        editorScrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        editorPanel.add(editorScrollPane, BorderLayout.CENTER)

        // Add an empty EAST component with a BoxLayout to enforce a minimum height
        val eastPanel = JPanel()
        eastPanel.layout = BoxLayout(eastPanel, BoxLayout.Y_AXIS)
        eastPanel.add(Box.createVerticalGlue())
        editorPanel.add(eastPanel, BorderLayout.EAST)

        // Add a ComponentListener to the editorTextField to listen for changes in its size
        editorTextField.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                // Calculate the preferred height of the editorTextField based on its content
                val prefHeight = editorTextField.preferredSize.height

                // Calculate the height of the editorPanel
                val panelHeight = westPanel.preferredSize.height + prefHeight + eastPanel.preferredSize.height

                // Set the height of the editorPanel based on the calculated height
                editorPanel.preferredSize = Dimension(Int.MAX_VALUE, panelHeight.coerceIn(if (windowHeight > 0) windowHeight / 7 else 180, if (windowHeight > 0) windowHeight / 2 else 600))

                // Revalidate and repaint the editorPanel to reflect the new size
                editorPanel.revalidate()
                editorPanel.repaint()
            }
        })

        return editorPanel
    }

    // This function creates an editor text field
    private fun createEditorTextField(extension: String): EditorTextField {
        // Get the file type from the language and create the document for the editor
        val fileType: FileType = FileTypeManager.getInstance().getFileTypeByExtension(extension)
        val editorFactory = EditorFactory.getInstance()
        val document = editorFactory.createDocument("")

        // Create the editor text field
        val twEditor = ToolWindowEditor(toolWindow, document, toolWindow.project, fileType)

        // Set up the editor
        twEditor.createEditor()

        // Dispose the editor
        twEditor.dispose()

        return twEditor
    }

    // This method is used to reset all the feedback panels
    fun resetAllFeedbackPanels() {
        for (panel in feedbackInstance) {
            panel.reset()
        }
    }

    // This function updates the code of the editor text fields
    fun updateEditorTextFields(code: List<String>, before: List<Int>, after: List<Int>, filePaths: List<String>) {

        // Remove folding regions
        clearEditorTextFields()

        for (i in editorTextFields.indices) {
            if (i < code.size) {
                editorTextFields[i].text = code[i]
                editorFilePathLabels[i].text = "Result ${i+1} from " + filePaths[i]
                // Add folding regions for codeBefore and codeAfter
                val editor = editorTextFields[i].editor
                val foldingModel = editor?.foldingModel
                if (editor != null) {
                    foldingModel?.runBatchFoldingOperation {
                        val startBeforeOffset = editor.document.getLineStartOffset(0)
                        val endBeforeOffset = if ((before[i] - 1) < 0) {
                            editor.document.getLineEndOffset(0)
                        } else {
                            editor.document.getLineEndOffset(before[i] - 1)
                        }
                        val startAfterOffset = editor.document.getLineStartOffset(after[i])
                        val endAfterOffset = if ((editor.document.lineCount -1) < 0) {
                            editor.document.getLineEndOffset(0)
                        } else {
                            editor.document.getLineEndOffset(editor.document.lineCount - 1)
                        }

                        val foldrBefore = foldingModel.addFoldRegion(startBeforeOffset, endBeforeOffset, "...")
                        if (foldrBefore != null) {
                            foldrBefore.isExpanded = false
                        }
                        val foldrAfter = foldingModel.addFoldRegion(startAfterOffset, endAfterOffset, "...")
                        if (foldrAfter != null) {
                            foldrAfter.isExpanded = false
                        }
                    }
                }
                editorPanels[i].revalidate()
                editorPanels[i].repaint()
            } else {
                break
            }
        }
    }

    // This function clears the text of the editor text fields
    fun clearEditorTextFields() {
        for (i in editorTextFields.indices) {
            editorTextFields[i].text = ""
            editorFilePathLabels[i].text = ""
            editorTextFields[i].asSafely<ToolWindowEditor>()?.resetFontSize()
            editorPanels[i].revalidate()
            editorPanels[i].repaint()
        }
    }

}
