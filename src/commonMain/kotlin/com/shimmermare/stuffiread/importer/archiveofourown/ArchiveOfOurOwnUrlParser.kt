package com.shimmermare.stuffiread.importer.archiveofourown

import com.shimmermare.stuffiread.importer.UrlParser

object ArchiveOfOurOwnUrlParser : UrlParser<WorkId> {
    private val regex = Regex(".*archiveofourown\\.org/works/(\\d+).*")

    override fun matches(url: String): Boolean {
        return regex.matches(url)
    }

    override fun parse(url: String): WorkId {
        val match = regex.matchEntire(url)
            ?: throw IllegalArgumentException("Given URL is not a Archive of Our Own work: $url")
        return WorkId(match.groupValues[1].toUInt())
    }
}