package com.shimmermare.stuffiread.ui.components.form

data class ValidationResult(
    val valid: Boolean,
    val error: String? = null,
) {
    companion object {
        val Valid = ValidationResult(true)
    }
}