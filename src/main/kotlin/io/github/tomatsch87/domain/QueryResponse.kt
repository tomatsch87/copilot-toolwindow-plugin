package io.github.tomatsch87.domain

import com.google.gson.annotations.SerializedName

// This data class is used to parse the response to the search query from the server
data class QueryResponse(
    // The request id is used to identify the request
    @SerializedName("request_id") val requestId: String,

    // The models specified in the indices field of the request
    @SerializedName("results") val results: List<Results>
) {

    // Second constructor without parameters
    constructor() : this("", listOf<Results>())

    // A results is a completion returned by the server
    data class Results(
        val rank: Int,
        val score: Double,
        val content: Content,
        val index: String
    )

    // The content of a snippet
    data class Content(
        val code: String,
        val language: String,
        val contentType: Int,
        val repository: String,
        val repositoryUrl: String,
        val filepath: String,
        val filepathUrl: String,
        val codeBefore: Int,
        val codeAfter: Int,
        val startByte: Int?,
        val startLine: Int,
        val endByte: Int?,
        val endLine: Int,
        val contextStartLine: Int,
        val contextEndLine: Int
    )

}
