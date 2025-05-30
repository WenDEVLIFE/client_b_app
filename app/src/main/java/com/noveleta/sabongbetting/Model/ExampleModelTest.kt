package com.noveleta.sabongbetting.Model

import kotlinx.serialization.Serializable

@Serializable
data class BarcodePayload(
    val from: String, // e.g., "phone"
    val type: String, // e.g., "barcode"
    val data: String  // e.g., actual barcode string
)
