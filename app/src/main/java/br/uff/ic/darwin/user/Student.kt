package br.uff.ic.darwin.user

data class Student(
    var name: String,
    var picture: String,
    var cardNfcId: String,
    var uffRegistrationNumber: String,
    var course: String,
    var expiresAt: String,
    var uffFunds: Double = 0.0,
    var rioCardFunds: Double = 0.0
)