package com.shimmermare.stuffiread.importer

interface UrlParser<StoryIdentifier> {
    fun matches(url: String): Boolean

    fun parse(url: String): StoryIdentifier
}