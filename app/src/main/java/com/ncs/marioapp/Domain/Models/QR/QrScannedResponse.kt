package com.ncs.marioapp.Domain.Models.QR

data class QrScannedResponse(
    val success: Boolean,
    val message: String,
    val rewards: List<QrScannedData>?,
    val points: Int?

)
