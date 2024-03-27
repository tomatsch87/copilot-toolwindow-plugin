package io.github.tomatsch87.ui

import io.github.tomatsch87.services.AuthenticationService
import io.github.tomatsch87.services.PluginSettings
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.*

// Provides dialog to access the plugin settings from the toolbar settings item
class SettingsDialogWrapper : DialogWrapper(true) {

    // Initialize text fields for userEmail and connectionUri
    private val firstName = JTextField(13)
    private val lastName = JTextField(13)
    private val userEmail = JTextField(30)
    private val connectionUri = JTextField(30)
    private val password = JPasswordField(30)

    init {
        // Call superclass constructor and set dialog title
        init()
        title = "Copilot Toolwindow Settings"

        // Get plugin settings state
        val state = PluginSettings.instance.state

        // Initialize text fields with saved state
        firstName.text = state.firstName
        lastName.text = state.lastName
        userEmail.text = state.userEmail
        connectionUri.text = state.connectionUri
        password.text = state.getPasswordFromSafe()
    }

    // Create the center panel of the dialog
    override fun createCenterPanel(): JComponent {
        // Create a new panel with a border layout
        val panel = JPanel(BorderLayout())

        // Create a form panel with a box layout
        val formPanel = JPanel()
        formPanel.layout = BoxLayout(formPanel, BoxLayout.Y_AXIS)

        // Create a new labels and text fields
        val fn = JLabel("First Name:")
        val ln = JLabel("Last Name:")

        // Create a new panels with a border layout
        val namePanel = JPanel(BorderLayout())
        val fnPanel = JPanel(BorderLayout())
        val lnPanel = JPanel(BorderLayout())

        // Layout for the name panel
        fnPanel.add(fn, BorderLayout.WEST)
        fnPanel.add(firstName, BorderLayout.EAST)
        lnPanel.add(ln, BorderLayout.WEST)
        lnPanel.add(lastName, BorderLayout.EAST)
        namePanel.add(fnPanel, BorderLayout.WEST)
        namePanel.add(lnPanel, BorderLayout.EAST)

        // Add text field panels to the form panel
        formPanel.add(namePanel)
        formPanel.add(createTextFieldPanel("User-Email:", userEmail))
        formPanel.add(createTextFieldPanel("Password:", password))
        formPanel.add(createTextFieldPanel("Connection-Address:", connectionUri))

        // Add the form panel to the center of the main panel
        panel.add(formPanel, BorderLayout.CENTER)

        return panel
    }

    // Create the south panel with custom buttons
    override fun createSouthPanel(): JComponent {
        // Create a panel with a flow layout
        val panel = JPanel(FlowLayout(FlowLayout.RIGHT))

        // Create the default "Cancel" and "OK" buttons
        val cancelButton = JButton(cancelAction)
        val okButton = JButton(okAction)

        // Create the custom "Register" button
        val registerButton = JButton("Register")
        registerButton.addActionListener {
            registerUserDialog()
        }

        // Create the custom "Login" button
        val loginButton = JButton("Login")
        loginButton.addActionListener {
            loginUserDialog()
        }

        // Add the buttons to the panel
        panel.add(registerButton)
        panel.add(loginButton)
        panel.add(cancelButton)
        panel.add(okButton)

        return panel
    }

    // Write settings to state and login user when "Login" button is pressed
    private fun loginUserDialog() {
        val state = PluginSettings.instance.state
        // Write current input from text fields to state
        state.firstName = firstName.text
        state.lastName = lastName.text
        state.userEmail = userEmail.text
        state.connectionUri = connectionUri.text
        state.setPasswordToSafe(String(password.password))

        // Login user
        AuthenticationService().logoutUser()
        AuthenticationService().loginUser()
    }

    // Write settings to state and register user when "Register" button is pressed
    private fun registerUserDialog() {
        val state = PluginSettings.instance.state
        // Write current input from text fields to state
        state.firstName = firstName.text
        state.lastName = lastName.text
        state.userEmail = userEmail.text
        state.connectionUri = connectionUri.text
        state.setPasswordToSafe(String(password.password))

        // Register user
        AuthenticationService().logoutUser()
        AuthenticationService().registerUser()
        AuthenticationService().loginUser()
    }

    // Save settings and close dialog when OK button is pressed
    override fun doOKAction() {
        val state = PluginSettings.instance.state
        // Write current input from text fields to state
        state.firstName = firstName.text
        state.lastName = lastName.text
        state.userEmail = userEmail.text
        state.connectionUri = connectionUri.text
        state.setPasswordToSafe(String(password.password))

        this.close(OK_EXIT_CODE)
    }

    // Helper function to create a text field panel with a label
    private fun createTextFieldPanel(labelText: String, textField: JTextField): JPanel {
        // Create a new label with the given text
        val label = JLabel(labelText)

        // Create a new panel with a border layout
        val textFieldPanel = JPanel(BorderLayout())

        // Add the label to the left of the text field
        textFieldPanel.add(label, BorderLayout.WEST)
        textFieldPanel.add(textField, BorderLayout.EAST)

        return textFieldPanel
    }
}
