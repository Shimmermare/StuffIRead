package com.shimmermare.stuffiread.importer.ponyfiction

/**
 * Parse author RSS feed and extract the name.
 */
expect object PonyfictionAuthorNameProvider {
    fun getAuthorName(authorId: UInt, rssFeedText: String): String
}