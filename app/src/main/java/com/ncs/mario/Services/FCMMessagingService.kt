package com.ncs.mario.Services


import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ncs.mario.Domain.HelperClasses.NotificationBuilderUtil.showNotification
import com.ncs.mario.Domain.HelperClasses.NotificationType
import com.ncs.mario.Domain.HelperClasses.PrefManager
import com.ncs.mario.Domain.Models.FCMNotification
import timber.log.Timber

class FCMessagingService : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag("SellerFirebaseService ").i("Refreshed token :: %s", token)
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (PrefManager.getFCMToken()!!.isNotEmpty()) {
            Timber.tag("FirebaseService").i("Message :: %s", remoteMessage.data)

            val data = remoteMessage.data
            val type = data["type"]
            val title=data["title"]
            val body=data["body"]

            if (type != null) {
                when (type) {
                    NotificationType.POST_NOTIFICATION.name -> {
                        val notification = FCMNotification(
                            notificationType = NotificationType.POST_NOTIFICATION,
                            title = title!!,
                            body = body!!,
                            )
                        if (PrefManager.getPostNotifPref()) {
                            showNotification(notification, applicationContext)
                        }
                    }
                    NotificationType.POLL_NOTIFICATION.name -> {
                        val notification = FCMNotification(
                            notificationType = NotificationType.POLL_NOTIFICATION,
                            title = title!!,
                            body = body!!,
                        )
                        if (PrefManager.getPollNotifPref()) {
                            showNotification(notification, applicationContext)
                        }
                    }
                    NotificationType.MERCH_NOTIFICATION.name -> {
                        val notification = FCMNotification(
                            notificationType = NotificationType.MERCH_NOTIFICATION,
                            title = title!!,
                            body = body!!,
                        )
                        if (PrefManager.getPollNotifPref()) {
                            showNotification(notification, applicationContext)
                        }
                    }
                    NotificationType.EVENT_NOTIFICATION.name -> {
                        val notification = FCMNotification(
                            notificationType = NotificationType.EVENT_NOTIFICATION,
                            title = title!!,
                            body = body!!,
                        )
                        if (PrefManager.getPollNotifPref()) {
                            showNotification(notification, applicationContext)
                        }
                    }
                    else -> {
                        val notification = FCMNotification(
                            notificationType = NotificationType.GENERAL_NOTIFICATION,
                            title = title!!,
                            body = body!!,
                        )
                        showNotification(notification, applicationContext)
                    }
                }
            }
        }
    }


}