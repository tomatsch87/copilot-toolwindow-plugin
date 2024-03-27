package io.github.tomatsch87.ui

import io.github.tomatsch87.services.AuthenticationService
import io.github.tomatsch87.services.PluginSettings
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.*


// Provides integration of the plugin settings into the system settings from intellij.
class SettingsConfigurable : Configurable {

    private var panel: JPanel?
    private val registerButton = JButton("Register")
    private val loginButton = JButton("Login")

    // Initialize text fields for userEmail and connectionUri
    private val firstName = JTextField(15)
    private val lastName = JTextField(15)
    private val userEmail = JTextField(30)
    private val connectionUri = JTextField(30)
    private val password = JPasswordField(30)

    init {
        // Create the Swing form
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("First Name: "), firstName, 1, false)
            .addComponent(firstName, 1)
            .addLabeledComponent(JBLabel("Last Name: "), lastName, 1, false)
            .addComponent(lastName, 1)
            .addLabeledComponent(JBLabel("User-Email: "), userEmail, 1, false)
            .addComponent(userEmail, 1)
            .addLabeledComponent(JBLabel("Password: "), password, 1, false)
            .addComponent(password, 1)
            .addLabeledComponent(JBLabel("Connection-Address: "), connectionUri, 1, false)
            .addComponent(connectionUri, 1)
            .addComponent(loginButton)
            .addComponent(registerButton)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        // Add action listener to register button
        registerButton.addActionListener {
            registerUserDialog()
        }

        // Add action listener to login button
        loginButton.addActionListener {
            loginUserDialog()
        }
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

    // Returns the visible name of the configurable component
    override fun getDisplayName(): String {
        return "Copilot Toolwindow Settings"
    }

    // Returns Swing form that enables the user to configure settings
    override fun createComponent(): JComponent? {
        return panel
    }

    // Indicates whether the Swing form was modified or not
    override fun isModified(): Boolean {
        // Get plugin settings state
        val state = PluginSettings.instance.state

        return !firstName.text.equals(state.firstName) or !lastName.text.equals(state.lastName) or !userEmail.text.equals(state.userEmail) or !connectionUri.text.equals(state.connectionUri) or (String(password.password) != state.getPasswordFromSafe())
    }

    // Write current input from text fields to state
    override fun apply() {
        val state = PluginSettings.instance.state
        // Write current input from text fields to state
        state.firstName = firstName.text
        state.lastName = lastName.text
        state.userEmail = userEmail.text
        state.connectionUri = connectionUri.text
        state.setPasswordToSafe(String(password.password))

        // Apply changes from dialog
        AuthenticationService().logoutUser()
        AuthenticationService().loginUserNoPopup()
    }

    // Initialize text fields with saved state
    override fun reset() {
        val state = PluginSettings.instance.state

        firstName.text = state.firstName
        lastName.text = state.lastName
        userEmail.text = state.userEmail
        connectionUri.text = state.connectionUri
        password.text = state.getPasswordFromSafe()
    }

    // Notifies the configurable component that the Swing form will be closed
    override fun disposeUIResources() {
        panel = null
    }
}