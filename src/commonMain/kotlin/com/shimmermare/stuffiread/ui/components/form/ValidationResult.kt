package com.shimmermare.stuffiread.ui.components.form

data class ValidationResult(
    val valid: Boolean,
    val error: String? = null,
) {
    companion object {
        val Valid = ValidationResult(true)

        fun fromException(validate: () -> Unit): ValidationResult {
            return try {
                validate()
                Valid
            } catch (e: Exception) {
                ValidationResult(false, e.message)
            }
        }
    }
}