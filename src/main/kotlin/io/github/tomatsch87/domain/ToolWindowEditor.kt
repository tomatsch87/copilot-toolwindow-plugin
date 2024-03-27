package io.github.tomatsch87.domain

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.FoldRegion
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.fileTypes.FileType
import com.intellij.ui.EditorTextField
import com.intellij.openapi.editor.ex.FoldingListener
import com.intellij.openapi.wm.ToolWindow

// Class to define a custom editor text field for the toolWindow
class ToolWindowEditor(private val toolWindow: ToolWindow, document: Document, project: Project, filetype: FileType) : EditorTextField(document, project, filetype) {

    // The editor instance
    private var editorInstance: EditorEx? = null

    // The folding model
    class MyFoldingListener(private val editorTextField: EditorTextField) : FoldingListener {
        override fun onFoldRegionStateChange(foldRegion: FoldRegion) {
            super.onFoldRegionStateChange(foldRegion)
            editorTextField.revalidate()
            editorTextField.repaint()
        }
    }

    // Customize the editor
    public override fun createEditor() : EditorEx {
        // Create a new editor
        val editor = super.createEditor()
        editorInstance = editor

        // Enable line numbers
        editor.settings.isLineNumbersShown = true

        // Enable folding outline
        editor.settings.isFoldingOutlineShown = true

        // Add a folding listener
        editor.foldingModel.addListener(MyFoldingListener(this), toolWindow.disposable)

        // Allow multiple lines
        this.isOneLineMode = false

        // Change font size to default font size from the IDE
        this.setFontInheritedFromLAF(false)

        // Set the editor to read-only
        this.isViewer = true

        // Set the editor to not focusable
        this.isFocusable = false

        // Add MouseWheelListener for font scaling
        editor.contentComponent.addMouseWheelListener { e ->
            if (e.isControlDown) {
                val fontSize = editor.colorsScheme.editorFontSize
                val direction = if (e.wheelRotation < 0) 1 else -1
                val newFontSize = fontSize + direction

                if (newFontSize > 0) {
                    editor.colorsScheme.editorFontSize = newFontSize
                }
            } else {
                editor.contentComponent.parent.dispatchEvent(e)
            }
        }
        return editor
    }

    // Function to reset the font size to the IDE's current setting
    fun resetFontSize() {
        editorInstance?.let { editor ->
            val globalFontSize = EditorColorsManager.getInstance().globalScheme.editorFontSize
            editor.colorsScheme.editorFontSize = globalFontSize
        }
    }

    // Dispose the editor
    fun dispose() {
        editorInstance?.let {
            EditorFactory.getInstance().releaseEditor(it)
        }
        editorInstance = null
    }

}
