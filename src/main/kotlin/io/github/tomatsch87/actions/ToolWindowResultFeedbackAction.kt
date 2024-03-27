package io.github.tomatsch87.actions

import io.github.tomatsch87.services.AuthenticationService
import io.github.tomatsch87.services.PluginSettings
import com.google.gson.Gson
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.swing.JOptionPane

// This is the action that is triggered when the user clicks on the star rating buttons in the tool window
class ToolWindowResultFeedbackAction(private val panelNumber: Int, private val advancedFeedback: Int, private val searchAction: ToolWindowSearchAction) : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {

        // Get the response instance from the ToolWindowSearchAction
        val responseInstance = searchAction.getResponseInstance()

        // Get the request instance from the ToolWindowSearchAction
        val requestInstance = searchAction.getRequestInstance()

        // If the response instance is empty, return
        if (responseInstance.results.isEmpty()) {
            return
        }

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

        // Get the result from the response instance
        val result = responseInstance.results[panelNumber]

        // Create a map of the data to be sent
        val data = mapOf(
            "feedback" to mapOf(
                "result" to mapOf(
                    "rank" to result.rank,
                    "score" to result.score,
                    "content" to mapOf(
                        "code" to result.content.code,
                        "language" to result.content.language,
                        "contentType" to result.content.contentType,
                        "repository" to result.content.repository,
                        "repositoryUrl" to result.content.repositoryUrl,
                        "filepath" to result.content.filepath,
                        "filepathUrl" to result.content.filepathUrl,
                        "codeBefore" to result.content.codeBefore,
                        "codeAfter" to result.content.codeAfter,
                        "startByte" to result.content.startByte,
                        "startLine" to result.content.startLine,
                        "endByte" to result.content.endByte,
                        "endLine" to result.content.endLine,
                        "contextStartLine" to result.content.contextStartLine,
                        "contextEndLine" to result.content.contextEndLine
                    ),
                ),
                "simple_feedback" to 0,
                "advanced_feedback" to advancedFeedback
            ),
            "request" to mapOf(
                "request_id" to responseInstance.requestId,
                "filepath" to requestInstance["filepath"],
                "indices" to requestInstance["indices"],
                "max_results" to requestInstance["max_results"],
                "position" to requestInstance["position"],
                "code" to requestInstance["code"]
            )
        )

        // convert data to json
        val json = Gson().toJson(data)

        println(json)

        val response : HttpResponse<String>

        try {
            // send data as post request
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create("${PluginSettings.instance.state.connectionUri}/search/feedback/result"))
                .header("accept", "application/json")
                .header("Authorization", "Bearer ${PluginSettings.instance.state.accessToken}")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build()

            // get response, if no response is found, return
            response = client.send(request, HttpResponse.BodyHandlers.ofString()) ?: return
        } catch (e: Exception) {
            AuthenticationService().logoutUser()
            return
        }

        println(response.body())
    }
}