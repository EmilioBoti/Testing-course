package com.embot.testingcourse.core.domain.model

sealed class AppError: Exception() {
    data object NetworkError: AppError()
    data object NotFoundError: AppError()
    data object DatabaseError: AppError()
//    data class ValidationError(
//        override val message: String
//    ): AppError()

    sealed class Validation: AppError() {
        data object QuantityMustBePositive: Validation() {
            private fun readResolve(): Any = QuantityMustBePositive
        }

        data class InsufficientStock(
            val available: Int
        ): Validation()
    }

    data class UnKnownError(
        override val message: String?
    ): AppError()
}