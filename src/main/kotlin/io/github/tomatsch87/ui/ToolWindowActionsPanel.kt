package io.github.tomatsch87.ui

import io.github.tomatsch87.actions.SettingsAction
import io.github.tomatsch87.actions.ToolWindowClearAction
import io.github.tomatsch87.actions.ToolWindowRequestFeedbackAction
import io.github.tomatsch87.actions.ToolWindowSearchAction
import io.github.tomatsch87.services.PluginSettings
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.ActionButtonWithText
import com.intellij.ui.JBColor
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.NonOpaquePanel
import java.awt.*
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JTextField

class ToolWindowActionsPanel(searchAction: ToolWindowSearchAction, clearAction: ToolWindowClearAction, requestFeedbackAction: ToolWindowRequestFeedbackAction) : NonOpaquePanel() {

    // Store the filter text fields
    private val filterTextFields: MutableList<JTextField> = mutableListOf()

    init {
        // Set the layout of the panel
        this.layout = BorderLayout()

        // Create a panel for the login status
        val loginStatusPanel = JPanel()

        // Create a label for the login status icon
        val loginStatusLabel = JBLabel()
        if (PluginSettings.instance.state.accessToken == "") {
            loginStatusLabel.text = "No Connection"
            loginStatusLabel.icon = AllIcons.Debugger.Db_set_breakpoint
        } else {
            loginStatusLabel.text = "Connected"
            loginStatusLabel.icon = AllIcons.General.InspectionsOK
        }

        // Add the label to the login status panel
        loginStatusPanel.add(loginStatusLabel)
        loginLabels.add(loginStatusLabel)

        // Create a panel for the action buttons
        val actionButtonsPanel = JPanel()
        actionButtonsPanel.layout = BorderLayout()

        val helperPanel = JPanel()
        helperPanel.layout = FlowLayout(FlowLayout.LEFT)

        // Add an action button for the search action to the panel
        val searchPresentation = Presentation("Search")
        searchPresentation.icon = AllIcons.Actions.Search

        helperPanel.add(ActionButtonWithText(
            searchAction,
            searchPresentation,
            ActionPlaces.TOOLWINDOW_CONTENT,
            Dimension(75, 30)
        ))

        // Add an action button for the clear results action to the panel
        val clearPresentation = Presentation("Clear results")
        clearPresentation.icon = AllIcons.Actions.Refresh

        helperPanel.add(ActionButtonWithText(
            clearAction,
            clearPresentation,
            ActionPlaces.TOOLWINDOW_CONTENT,
            Dimension(75, 30)
        ))

        // Add an action button for the settings action to the panel
        val settingsPresentation = Presentation("Settings")
        settingsPresentation.icon = AllIcons.General.Settings

        helperPanel.add(ActionButtonWithText(
            SettingsAction(),
            settingsPresentation,
            ActionPlaces.TOOLWINDOW_CONTENT,
            Dimension(75, 30)
        ))

        // Add an action button for the settings action to the panel
        val feedbackPresentation = Presentation("Feedback")
        feedbackPresentation.icon = AllIcons.General.ContextHelp

        helperPanel.add(ActionButtonWithText(
            requestFeedbackAction,
            feedbackPresentation,
            ActionPlaces.TOOLWINDOW_CONTENT,
            Dimension(75, 30)
        ))


        actionButtonsPanel.add(helperPanel, BorderLayout.WEST)
        actionButtonsPanel.add(loginStatusPanel, BorderLayout.EAST)

        // Create a panel for the filter
        val filterPanel = JPanel()
        filterPanel.layout = BoxLayout(filterPanel, BoxLayout.X_AXIS)

        // Path filter
        val pathFilter = JTextField(18)
        pathFilter.foreground = JBColor.GRAY
        val pathFilterText = "Enter path filter"
        pathFilter.text = pathFilterText
        pathFilter.addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent) {
                if (pathFilter.text == pathFilterText) {
                    pathFilter.foreground = JBColor.BLACK
                    pathFilter.text = ""
                }
            }

            override fun focusLost(e: FocusEvent) {
                if (pathFilter.text.isEmpty()) {
                    pathFilter.foreground = JBColor.GRAY
                    pathFilter.text = pathFilterText
                }
            }
        })
        filterTextFields.add(pathFilter)

        // Include filter
        val includeFilter = JTextField(18)
        includeFilter.foreground = JBColor.GRAY
        val includeFilterText = "Enter keywords to include"
        includeFilter.text = includeFilterText
        includeFilter.addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent) {
                if (includeFilter.text == includeFilterText) {
                    includeFilter.foreground = JBColor.BLACK
                    includeFilter.text = ""
                }
            }

            override fun focusLost(e: FocusEvent) {
                if (includeFilter.text.isEmpty()) {
                    includeFilter.foreground = JBColor.GRAY
                    includeFilter.text = includeFilterText
                }
            }
        })
        filterTextFields.add(includeFilter)

        // Exclude filter
        val excludeFilter = JTextField(18)
        excludeFilter.foreground = JBColor.GRAY
        val excludeFilterText = "Enter keywords to exclude"
        excludeFilter.text = excludeFilterText
        excludeFilter.addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent) {
                if (excludeFilter.text == excludeFilterText) {
                    excludeFilter.foreground = JBColor.BLACK
                    excludeFilter.text = ""
                }
            }

            override fun focusLost(e: FocusEvent) {
                if (excludeFilter.text.isEmpty()) {
                    excludeFilter.foreground = JBColor.GRAY
                    excludeFilter.text = excludeFilterText
                }
            }
        })
        filterTextFields.add(excludeFilter)

        // Add the filter text fields to the filter panel
        filterPanel.add(pathFilter)
        filterPanel.add(includeFilter)
        filterPanel.add(excludeFilter)

        // Create a new OnePixelSplitter instance to split the action buttons panel and the filter panel
        val horizontalSplitter = OnePixelSplitter(true, 0.0f)
        horizontalSplitter.setResizeEnabled(false)

        // Add the action buttons panel and the filter panel to the OnePixelSplitter
        horizontalSplitter.firstComponent = actionButtonsPanel
        horizontalSplitter.secondComponent = filterPanel

        // Add the action buttons panel to the main panel on the left side
        this.add(horizontalSplitter, BorderLayout.WEST)
    }

    // This method is used to refresh the buttons of the panel
    fun refreshButtons() {
        // Perform the necessary refresh operations on the buttons
        for (component in this.components) {
            if (component is ActionButtonWithText) {
                component.update()
            }
        }
    }

    // This method is used to get the text from all filter text fields
    fun getFilterText(): List<String> {
        val texts = filterTextFields.map { it.text }.toMutableList()

        if (texts[0] == "Enter path filter") {
            texts[0] = ""
        }
        if (texts[1] == "Enter keywords to include") {
            texts[1] = ""
        }
        if (texts[2] == "Enter keywords to exclude") {
            texts[2] = ""
        }

        return texts
    }

    companion object {
        // Store the login status labels
        val loginLabels: MutableList<JBLabel> = mutableListOf()

        // This method is used change the login status of the panel
        fun updateLoginStatusPanel() {
            loginLabels.forEach {
                if (PluginSettings.instance.state.accessToken == "") {
                    it.text = "No Connection"
                    it.icon = AllIcons.Debugger.Db_set_breakpoint
                } else {
                    it.text = "Connected"
                    it.icon = AllIcons.General.InspectionsOK
                }
            }
        }
    }

}
