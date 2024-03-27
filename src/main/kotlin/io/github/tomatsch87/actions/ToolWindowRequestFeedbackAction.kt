package io.github.tomatsch87.actions

import io.github.tomatsch87.services.AuthenticationService
import io.github.tomatsch87.services.PluginSettings
import io.github.tomatsch87.ui.RequestFeedbackDialogWrapper
import com.google.gson.Gson
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.swing.JOptionPane

// This is the action that is triggered when the user clicks on the "Feedback" button in the action bar
class ToolWindowRequestFeedbackAction(private val searchAction: ToolWindowSearchAction) : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {

        // Get the response instance from the ToolWindowSearchAction
        val responseInstance = searchAction.getResponseInstance()

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

        // If the response instance is empty, return error
        if (responseInstance.results.isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "Please search for code snippets first.",
                "Search request required",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }

        val feedbackMessage: String

        val getResponse : HttpResponse<String>

        try {
            // get feedback message from backend
            val client = HttpClient.newHttpClient()
            val getRequest = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create("${PluginSettings.instance.state.connectionUri}/search/requests/${responseInstance.requestId}/feedback"))
                .header("accept", "application/json")
                .header("Authorization", "Bearer ${PluginSettings.instance.state.accessToken}")
                .header("Content-Type", "application/json")
                .GET()
                .build()

            // get response, if no response is found, return
            getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString()) ?: return
        } catch (e: Exception) {
            AuthenticationService().logoutUser()
            return
        }


        // convert response to json
        val getJson = Gson().fromJson(getResponse.body(), Map::class.java)

        val feedbackDialog: RequestFeedbackDialogWrapper = if (getJson["message"] == null) {
            RequestFeedbackDialogWrapper("I wanted to find...")
        } else {
            val initMessage = getJson["message"].toString()
            RequestFeedbackDialogWrapper(initMessage)
        }

        if (feedbackDialog.showAndGet()) {
            // Get the feedback message from the dialog
            feedbackMessage = feedbackDialog.getFeedback()
        } else {
            // If the user cancels the dialog, return
            return
        }

        // Create a map of the data to be sent
        val data : Map<String, Any>

        data = mapOf(
            "message" to feedbackMessage
        )

        // convert data to json
        val json = Gson().toJson(data)

        println(json)

        val response : HttpResponse<String>

        if(getJson["message"] == null) {
            try {
                // send data as post request
                val client = HttpClient.newHttpClient()
                val request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("${PluginSettings.instance.state.connectionUri}/search/requests/${responseInstance.requestId}/feedback"))
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
        } else {
            try {
                // send data as put request
                val client = HttpClient.newHttpClient()
                val request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .uri(URI.create("${PluginSettings.instance.state.connectionUri}/search/requests/${responseInstance.requestId}/feedback"))
                    .header("accept", "application/json")
                    .header("Authorization", "Bearer ${PluginSettings.instance.state.accessToken}")
                    .header("Content-Type", "application/json")
                    .method("PATCH",HttpRequest.BodyPublishers.ofString(json))
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

}
