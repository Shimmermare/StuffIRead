package com.shimmermare.stuffiread.util

import io.github.aakira.napier.Napier
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream

object AppVersionUtils {
    val CURRENT_VERSION: String? = System.getProperty("jpackage.app-version")

    /**
     * Return newer version if there's one.
     * Does nothing if [CURRENT_VERSION] is null.
     */
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun checkForUpdates(): NewUpdate? {
        if (CURRENT_VERSION == null) {
            return null
        }

        try {
            /**
             * https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28#get-the-latest-release
             * Returns NON-DRAFT and NON-PRERELEASE release.
             */
            val response = AppHttpClient.get("https://api.github.com/repos/Shimmermare/StuffIRead/releases/latest") {
                header("X-GitHub-Api-Version", "2022-11-28")
            }

            if (response.status == HttpStatusCode.NotFound) {
                Napier.i { "No new releases" }
                return null
            }

            if (response.status != HttpStatusCode.OK) {
                error("Latest release endpoint responded with ${response.status}")
            }

            val latestRelease = response.bodyAsChannel().toInputStream().use { AppJson.decodeFromStream<Release>(it) }
            // Should be ok for 0.0.0 format
            if (latestRelease.tagName > CURRENT_VERSION) {
                return NewUpdate(latestRelease.tagName, latestRelease.createdAt, latestRelease.htmlUrl)
            }
        } catch (e: Exception) {
            Napier.e(e) { "Failed to check for updates" }
        }

        return null
    }

    @Serializable
    private data class Release(
        @SerialName("tag_name")
        val tagName: String,
        @SerialName("created_at")
        val createdAt: Instant,
        @SerialName("html_url")
        val htmlUrl: String,
    )
}

data class NewUpdate(
    val version: String,
    val date: Instant,
    val url: String,
)