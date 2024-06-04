package com.shimmermare.stuffiread.importer.ponyfiction

import org.jsoup.Jsoup
import org.jsoup.parser.Parser

actual object PonyfictionAuthorNameProvider {
    actual fun getAuthorName(authorId: UInt, rssFeedText: String): String {
        val feed = Jsoup.parse(rssFeedText, Parser.xmlParser())
        // Because feed countains co-authors, also check for author ID
        return feed.select("entry > author")
            .find { it.select("uri")[0].text().endsWith("accounts/$authorId/") }
            .let { it!!.select("name")[0].text() }
    }
}