package io.github.tomatsch87.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.BorderLayout
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.*

// Provides dialog for request feedback from the action bar feedback item
class RequestFeedbackDialogWrapper(private val initMessage: String) : DialogWrapper(true) {
    private val feedbackField = JBTextArea(10, 25)

    init {
        title = "Provide Feedback"
        init()
    }

    override fun createCenterPanel(): JComponent {
        // Create a new panel with a border layout
        val panel = JPanel(BorderLayout())

        // Create a form panel with a box layout
        val formPanel = JPanel()
        formPanel.layout = BoxLayout(formPanel, BoxLayout.Y_AXIS)

        formPanel.add(JLabel("Help us improve!"), BorderLayout.WEST)
        formPanel.add(JLabel("Please explain your motivation behind this request."), BorderLayout.WEST)
        formPanel.add(Box.createVerticalStrut(20), BorderLayout.WEST)
        val initialText = initMessage
        feedbackField.text = initialText
        feedbackField.addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent) {
                if (feedbackField.text == "I wanted to find...") {
                    feedbackField.text = ""
                }
            }

            override fun focusLost(e: FocusEvent) {
                if (feedbackField.text.isEmpty()) {
                    feedbackField.text = initialText
                }
            }
        })
        formPanel.add(JBScrollPane(feedbackField), BorderLayout.WEST)

        // Add the form panel to the center of the main panel
        panel.add(formPanel, BorderLayout.WEST)

        return panel
    }

    // Get feedback from the text field
    fun getFeedback(): String {
        return feedbackField.text
    }
}
