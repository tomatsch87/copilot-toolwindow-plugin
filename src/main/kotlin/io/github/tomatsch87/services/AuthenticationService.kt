package io.github.tomatsch87.services

import io.github.tomatsch87.ui.ToolWindowActionsPanel
import com.google.gson.Gson
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.swing.JOptionPane

// Provides authentication service to register/login the user with the API
class AuthenticationService {

    // Register the user with the API
    fun registerUser() {
        val state = PluginSettings.instance.state

        val data = mapOf(
            "email" to state.userEmail,
            "password" to state.getPasswordFromSafe(),
            "is_active" to true,
            "name" to "${state.firstName} ${state.lastName}"
        )

        // convert data to json
        val json = Gson().toJson(data)

        // send data as post request
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .uri(URI.create("${PluginSettings.instance.state.connectionUri}/auth/register"))
            .header("accept", "application/json")
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build()

        // get response, if no response is found, return
        val response = client.send(request, HttpResponse.BodyHandlers.ofString()) ?: return

        // if response status code is not 201, show error message and return
        if (response.statusCode() != 201) {
            JOptionPane.showMessageDialog(
                null,
                "Registration failed ${response.statusCode()}: ${response.body()}",
                "Registration failed",
                JOptionPane.ERROR_MESSAGE
            )
        } else {
            JOptionPane.showMessageDialog(
                null,
                "Registration successful",
                "Registration successful",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    // Login the user with the API without showing any popup
    fun loginUserNoPopup() {
        val state = PluginSettings.instance.state

        val grantType = ""
        val username = state.userEmail
        val password = state.getPasswordFromSafe()
        val scope = ""
        val clientId = ""
        val clientSecret = ""

        val encodedUser = URLEncoder.encode(username, "UTF-8")
        val encodedPassword = URLEncoder.encode(password, "UTF-8")
        val data = "grant_type=$grantType&username=$encodedUser&password=$encodedPassword&scope=$scope&client_id=$clientId&client_secret=$clientSecret"

        val response: HttpResponse<String>

        try {
            // send data as post request
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create("${PluginSettings.instance.state.connectionUri}/auth/jwt/login"))
                .header("accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build()

            // get response, if no response is found, return
            response = client.send(request, HttpResponse.BodyHandlers.ofString()) ?: return
        } catch (e: Exception) {
            PluginSettings.instance.state.accessToken = ""
            ToolWindowActionsPanel.updateLoginStatusPanel()
            return
        }

        if (response.statusCode() == 200) {
            // Parse the JSON response
            val gson = Gson()
            val json = gson.fromJson(response.body(), Map::class.java)

            // Get the access_token value from the parsed JSON
            PluginSettings.instance.state.accessToken = json["access_token"].toString()

            // Show status in the actions panel
            ToolWindowActionsPanel.updateLoginStatusPanel()
        }
    }

    // Login the user with the API
    fun loginUser() {
        val state = PluginSettings.instance.state

        val grantType = ""
        val username = state.userEmail
        val password = state.getPasswordFromSafe()
        val scope = ""
        val clientId = ""
        val clientSecret = ""

        val encodedUser = URLEncoder.encode(username, "UTF-8")
        val encodedPassword = URLEncoder.encode(password, "UTF-8")
        val data = "grant_type=$grantType&username=$encodedUser&password=$encodedPassword&scope=$scope&client_id=$clientId&client_secret=$clientSecret"

        val response: HttpResponse<String>

        try {
            // send data as post request
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create("${PluginSettings.instance.state.connectionUri}/auth/jwt/login"))
                .header("accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build()

            // get response, if no response is found, return
            response = client.send(request, HttpResponse.BodyHandlers.ofString()) ?: return
        } catch (e: Exception) {
            PluginSettings.instance.state.accessToken = ""
            ToolWindowActionsPanel.updateLoginStatusPanel()
            JOptionPane.showMessageDialog(
                null,
                "Login failed, please check your network connection",
                "Login required",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }

        if (response.statusCode() == 200) {
            // Parse the JSON response
            val gson = Gson()
            val json = gson.fromJson(response.body(), Map::class.java)

            // Get the access_token value from the parsed JSON
            PluginSettings.instance.state.accessToken = json["access_token"].toString()

            // Show status in the actions panel
            ToolWindowActionsPanel.updateLoginStatusPanel()

            JOptionPane.showMessageDialog(
                null,
                "Successfully logged in",
                "Login",
                JOptionPane.INFORMATION_MESSAGE
            )
        } else {
            JOptionPane.showMessageDialog(
                null,
                "Login failed with code: ${response.statusCode()} \n ${response.body()}",
                "Login required",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    // Logout the user from the API
    fun logoutUser() {
        if (PluginSettings.instance.state.accessToken == "") {
            return
        }

        // Reset the access token
        PluginSettings.instance.state.accessToken = ""

        // Show status in the actions panel
        ToolWindowActionsPanel.updateLoginStatusPanel()

    }
}