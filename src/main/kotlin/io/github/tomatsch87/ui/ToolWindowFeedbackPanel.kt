package io.github.tomatsch87.ui

import io.github.tomatsch87.actions.ToolWindowResultFeedbackAction
import io.github.tomatsch87.actions.ToolWindowSearchAction
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.NonOpaquePanel
import org.jdesktop.swingx.geom.Star2D
import java.awt.*
import javax.swing.BoxLayout
import javax.swing.JButton
import kotlin.math.min

// This class defines the feedback panel at the bottom of each Editor panel
class ToolWindowFeedbackPanel(panelNumber: Int, private val searchAction: ToolWindowSearchAction) : NonOpaquePanel() {
    private val id = panelNumber
    private var star1: Star
    private var star2: Star
    private var star3: Star
    private var star4: Star
    private var star5: Star
    private var star = 0
    private var label: JBLabel

    class Star : JButton() {
        init {
            setContentAreaFilled(true)
            setCursor(Cursor(Cursor.HAND_CURSOR))
            border = null
            isOpaque = false
        }

        override fun paint(g: Graphics?) {
            super.paint(g)
            val g2 = g?.create() as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            val width = width
            val height = height
            val size = min(width, height) / 2
            val x = width / 2
            val y = height / 2
            val s = Star2D(x.toDouble(), y.toDouble() * 1.1, ((size / 2).toDouble()) * 0.9, size.toDouble() * 0.9, 5)
            g2.color = JBColor.GRAY
            g2.fill(s)
            if (isSelected) {
                g2.color = JBColor(0xFFF700, 0xFFF700)
                g2.fill(s)
            }
            g2.dispose()
        }

        override fun getPreferredSize(): Dimension {
            return Dimension(40, 40)
        }
    }

    init {
        // Set the layout of the panel
        this.layout = BorderLayout()

        val starPanel = NonOpaquePanel()
        starPanel.layout = BoxLayout(starPanel, BoxLayout.X_AXIS)

        // Add the 5 stars to the panel
        this.star1 = Star()
        this.star1.toolTipText = "Irrelevant: The snippet has nothing to do with my search."
        this.star1.addActionListener{
            this.star = 0
            this.star1ActionPerformed()
        }
        starPanel.add(star1)

        this.star2 = Star()
        this.star2.toolTipText = "Related: The snippet seems related to my search but does not answer it."
        this.star2.addActionListener{
            this.star = 1
            this.star2ActionPerformed()
        }
        starPanel.add(star2)

        this.star3 = Star()
        this.star3.toolTipText = "Remotely relevant: The snippet provides some information relevant to my search, which may be minimal."
        this.star3.addActionListener{
            this.star = 2
            this.star3ActionPerformed()
        }
        starPanel.add(star3)

        this.star4 = Star()
        this.star4.toolTipText = "Relevant: The snippet has some answer for my search, but may need substantial modification."
        this.star4.addActionListener{
            this.star = 3
            this.star4ActionPerformed()
        }
        starPanel.add(star4)

        this.star5 = Star()
        this.star5.toolTipText = "Highly relevant: The snippet is dedicated to my search and can be used with minimal modification."
        this.star5.addActionListener{
            this.star = 4
            this.star5ActionPerformed()
        }
        starPanel.add(star5)
        this.add(starPanel, BorderLayout.WEST)
        this.label = JBLabel("Select a star to provide feedback")
        this.label.foreground = JBColor.GRAY
        this.label.setAllowAutoWrapping(true)
        this.add(this.label, BorderLayout.EAST)
    }

    // This method is used to refresh the buttons of the panel
    fun refreshButtons() {
        // Perform the necessary refresh operations on the buttons
        for (component in this.components) {
            if (component is ActionButton) {
                component.update()
            }
        }
    }

    // This method is used to reset the feedback panel
    fun reset() {
        this.star1.setSelected(false)
        this.star2.setSelected(false)
        this.star3.setSelected(false)
        this.star4.setSelected(false)
        this.star5.setSelected(false)
        this.label.text = "Select a star to provide feedback"
    }

    private fun star1ActionPerformed() {
        star1.setSelected(true)
        star2.setSelected(false)
        star3.setSelected(false)
        star4.setSelected(false)
        star5.setSelected(false)
        this.label.text = "Irrelevant"
        val event = AnActionEvent(null, DataContext.EMPTY_CONTEXT, ActionPlaces.TOOLWINDOW_POPUP, Presentation(), ActionManager.getInstance(), 0)
        ToolWindowResultFeedbackAction(panelNumber = this.id, 0, searchAction).actionPerformed(event)
    }

    private fun star2ActionPerformed() {
        star1.setSelected(true)
        star2.setSelected(true)
        star3.setSelected(false)
        star4.setSelected(false)
        star5.setSelected(false)
        this.label.text = "Related"
        val event = AnActionEvent(null, DataContext.EMPTY_CONTEXT, ActionPlaces.TOOLWINDOW_POPUP, Presentation(), ActionManager.getInstance(), 0)
        ToolWindowResultFeedbackAction(panelNumber = this.id, 1, searchAction).actionPerformed(event)
    }

    private fun star3ActionPerformed() {
        star1.setSelected(true)
        star2.setSelected(true)
        star3.setSelected(true)
        star4.setSelected(false)
        star5.setSelected(false)
        this.label.text = "Remotely relevant"
        val event = AnActionEvent(null, DataContext.EMPTY_CONTEXT, ActionPlaces.TOOLWINDOW_POPUP, Presentation(), ActionManager.getInstance(), 0)
        ToolWindowResultFeedbackAction(panelNumber = this.id, 2, searchAction).actionPerformed(event)
    }

    private fun star4ActionPerformed() {
        star1.setSelected(true)
        star2.setSelected(true)
        star3.setSelected(true)
        star4.setSelected(true)
        star5.setSelected(false)
        this.label.text = "Relevant"
        val event = AnActionEvent(null, DataContext.EMPTY_CONTEXT, ActionPlaces.TOOLWINDOW_POPUP, Presentation(), ActionManager.getInstance(), 0)
        ToolWindowResultFeedbackAction(panelNumber = this.id, 3, searchAction).actionPerformed(event)
    }

    private fun star5ActionPerformed() {
        star1.setSelected(true)
        star2.setSelected(true)
        star3.setSelected(true)
        star4.setSelected(true)
        star5.setSelected(true)
        this.label.text = "Highly relevant"
        val event = AnActionEvent(null, DataContext.EMPTY_CONTEXT, ActionPlaces.TOOLWINDOW_POPUP, Presentation(), ActionManager.getInstance(), 0)
        ToolWindowResultFeedbackAction(panelNumber = this.id, 4, searchAction).actionPerformed(event)
    }
}
