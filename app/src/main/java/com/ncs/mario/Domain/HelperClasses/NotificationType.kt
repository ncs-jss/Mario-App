package com.ncs.mario.Domain.HelperClasses

enum class NotificationType(val title: String, val priority : Int) {
    GENERAL_NOTIFICATION("general",3),
    POLL_NOTIFICATION("poll",3),
    POST_NOTIFICATION("post",3),
    MERCH_NOTIFICATION("merch",3),
    EVENT_NOTIFICATION("event",3),
}