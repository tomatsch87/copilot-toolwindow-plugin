package io.github.tomatsch87.actions

import io.github.tomatsch87.domain.QueryResponse
import io.github.tomatsch87.services.AuthenticationService
import io.github.tomatsch87.services.PluginSettings
import io.github.tomatsch87.ui.ToolWindowActionsPanel
import io.github.tomatsch87.ui.ToolWindowEditorPanel
import com.google.gson.Gson
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiDocumentManager
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.swing.JOptionPane

// This is the action that is triggered when the user clicks on the "Search" button in the tool window
class ToolWindowSearchAction : AnAction() {
    // Response and request instances are used to get a reference to search results from the tool window
    private var responseInstance: QueryResponse = QueryResponse()
    private var requestInstance = mapOf<String, Any?>()

    // The editor panel is used to update the editor text fields
    private var editorPanel: ToolWindowEditorPanel? = null

    // The actions panel is used to get the filters from the tool window
    private var actionsPanel: ToolWindowActionsPanel? = null

    override fun actionPerformed(event: AnActionEvent) {

        // Get the project context, if no project is found, return
        val project = event.project ?: return

        // Get the active editor from the project context, if no editor is found, return
        val fileEditorManager = FileEditorManager.getInstance(project)
        val editor = fileEditorManager.selectedTextEditor ?: return

        // Get the text from the editor
        val text = editor.document.text

        // Get the position of the caret
        val line = editor.caretModel.logicalPosition.line.plus(1)
        val column = editor.caretModel.logicalPosition.column.plus(1)

        // obtain the psiFile, if no psiFile is found, return
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return

        // obtain filepath from active editor file
        val filepath = psiFile.virtualFile.path

        if (PluginSettings.instance.state.accessToken == "") {
            AuthenticationService().loginUserNoPopup()
            if (PluginSettings.instance.state.accessToken == "") {
                JOptionPane.showMessageDialog(
                    null,
                    "Automatic login failed. Please login manually through the settings.",
                    "Login required",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
        }

        // create a map of the data to be sent
        val maxResults = 10

        val data = mutableMapOf(
            "filepath" to filepath,
            "max_results" to maxResults,
            "position" to mapOf(
                "line" to line,
                "column" to column
            ),
            "code" to text,
            "filters" to null
        )

        // get the filters from the tool window
        val filters = actionsPanel!!.getFilterText()

        if (filters[0].isNotBlank()) {
            data["filters"] = mapOf(
                "must" to listOf(mapOf("key" to "filepath", "match" to mapOf("text" to filters[0])))
            )
        }

        // Clear the feedback panel
        editorPanel?.resetAllFeedbackPanels()

        // store the request instance
        requestInstance = data

        // convert data to json
        val json = Gson().toJson(data)

        val response: HttpResponse<String>
        try {
            // send data as post request
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create("${PluginSettings.instance.state.connectionUri}/search"))
                .header("accept", "application/json")
                .header("Authorization", "Bearer ${PluginSettings.instance.state.accessToken}")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build()

            // get response, if no response is found, return
            response = client.send(request, HttpResponse.BodyHandlers.ofString()) ?: return
        } catch (e: Exception) {
            return
        }

        // parse the response
        responseInstance = Gson().fromJson(response.body(), QueryResponse::class.java)

        // create a list to store the results
        val results = mutableListOf<String>()

        // create a list to store file paths
        val filePaths = mutableListOf<String>()

        // Get number of lines number of the codeBefore and codeAfter parts
        val beforeLineNumber = mutableListOf<Int>()
        val afterLineNumber = mutableListOf<Int>()
        var tempNum: Int

        // add the results from each model from all languages to the results list
        responseInstance.results.forEach {
            results.add("${it.content.code}")
            beforeLineNumber.add(it.content.codeBefore)
            afterLineNumber.add(it.content.codeAfter)
            filePaths.add(it.content.filepath)
        }

        // update the editor text fields with the results
        editorPanel?.updateEditorTextFields(results, beforeLineNumber, afterLineNumber, filePaths)
    }

    // This function returns the request instance
    fun getRequestInstance(): Map<String, Any?> {
        return requestInstance
    }

    // This function returns the response instance
    fun getResponseInstance(): QueryResponse {
        return responseInstance
    }

    // This function sets the response instance
    fun setResponseInstance(response: QueryResponse) {
        responseInstance = response
    }

    // This function sets the editor panel
    fun setEditorPanel(editorPanel: ToolWindowEditorPanel) {
        this.editorPanel = editorPanel
    }

    // This function sets the actions panel
    fun setActionsPanel(actionsPanel: ToolWindowActionsPanel) {
        this.actionsPanel = actionsPanel
    }

}
