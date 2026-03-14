package com.embot.testingcourse.core.domain.model

sealed class AppError: Exception() {
    data object NetworkError: AppError()
    data object NotFoundError: AppError()
    data object DatabaseError: AppError()
    data class ValidationError(
        override val message: String
    ): AppError()
    data class UnKnownError(
        override val message: String?
    ): AppError()
}