package io.github.tomatsch87.services

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*

/**
 * This class is used to store and retrieve the settings for the Copilot Toolwindow Intellij-Plugin.
 * It is a PersistentStateComponent implementation storing the application settings in a persistent way.
 *
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
    name = "io.github.tomatsch87.services.PluginSettings",
    storages = [Storage("copilotToolwindowSettings.xml", roamingType = RoamingType.DISABLED)]
)
class PluginSettings : PersistentStateComponent<PluginSettings.PluginState>{

    private var currentPluginState = PluginState()

    data class PluginState(var userEmail: String = "", var connectionUri: String = "", var firstName: String = "", var lastName: String = ""
                           , var passwordCredentialAttributes: CredentialAttributes = CredentialAttributes("io.github.tomatsch87.services.PluginSettings", userEmail)
                           , var accessToken: String = ""
    ) {
        // This method is used to get the password from the password safe
        fun getPasswordFromSafe(): String {
            return PasswordSafe.instance.getPassword(passwordCredentialAttributes) ?: ""
        }

        // This method is used to store the password in the password safe
        fun setPasswordToSafe(password: String) {
            return PasswordSafe.instance.setPassword(passwordCredentialAttributes, password)
        }
    }

    // Called by IDEA to get the current state of this service, so that it can be saved to persistence
    override fun getState(): PluginState {
        return currentPluginState
    }

    // Called by IDEA when new component state is loaded
    override fun loadState(state: PluginState) {
        currentPluginState = state
    }

    companion object {
        // This companion object is used to get a reference to the single instance of this service.
        val instance: PluginSettings
            get() = ApplicationManager.getApplication().getService(PluginSettings::class.java)
    }
}
