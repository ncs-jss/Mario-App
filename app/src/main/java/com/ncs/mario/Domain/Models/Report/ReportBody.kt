package com.ncs.mario.Domain.Models.Report

data class ReportBody(
    val type:String,
    val images:List<String>,
    val description:String
)
