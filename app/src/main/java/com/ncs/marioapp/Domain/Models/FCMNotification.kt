package com.ncs.marioapp.Domain.Models

import com.ncs.marioapp.Domain.HelperClasses.NotificationType

data class FCMNotification constructor(
    val notificationType: NotificationType,
    val title: String = "",
    val body: String = "",
)