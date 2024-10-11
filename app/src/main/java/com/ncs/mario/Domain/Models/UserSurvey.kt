package com.ncs.mario.Domain.Models

data class UserSurvey(
    var name: String="",
    var admissionNum:String="",
    var branch:String="",
    var year:String="",
    var domains:List<String> = emptyList(),
    var links:List<String> = emptyList(),
    var userImg:String="",
    var collegeIdImg:String=""
)