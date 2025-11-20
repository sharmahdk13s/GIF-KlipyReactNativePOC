package com.klipy.data.dto.request

import com.google.gson.annotations.SerializedName

data class ReportRequestDto(
    @SerializedName("customer_id")
    val customerId: String,
    @SerializedName("reason")
    val reason: String
)