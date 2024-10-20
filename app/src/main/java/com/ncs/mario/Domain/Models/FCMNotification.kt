package com.ncs.mario.Domain.Models

import com.ncs.mario.Domain.HelperClasses.NotificationType

data class FCMNotification constructor(
    val notificationType: NotificationType,
    val title: String = "",
    val body: String = "",
)