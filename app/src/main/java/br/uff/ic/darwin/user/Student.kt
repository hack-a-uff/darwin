package br.uff.ic.darwin.user

data class Student(
    val name: String,
    val picture: String,
    val cardNfcId: String,
    val uffRegistrationNumber: String,
    val course: String,
    val expiresAt: String,
    val uffFunds: Double = 0.0,
    val rioCardFunds: Double = 0.0
)