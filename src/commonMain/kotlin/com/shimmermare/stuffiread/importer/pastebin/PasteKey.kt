package com.shimmermare.stuffiread.importer.pastebin

@JvmInline
value class PasteKey(val value: String) {
    init {
        require(value.isNotBlank()) {
            "Paste key can't be blank"
        }
        require(FORMAT.matches(value)) {
            "Invalid paste key format"
        }
    }

    companion object {
        private val FORMAT = Regex("[a-zA-Z0-9]+")
    }
}