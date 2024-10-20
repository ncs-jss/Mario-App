package com.ncs.mario.UI.SettingsScreen.Notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ncs.mario.Domain.HelperClasses.PrefManager

class NotificationsPrefViewModel : ViewModel() {

    private val _postNotification = MutableLiveData<Boolean>()
    val postNotification: LiveData<Boolean> get() = _postNotification

    private val _pollNotification = MutableLiveData<Boolean>()
    val pollNotification: LiveData<Boolean> get() = _pollNotification

    private val _eventNotification = MutableLiveData<Boolean>()
    val eventNotification: LiveData<Boolean> get() = _eventNotification

    private val _merchNotification = MutableLiveData<Boolean>()
    val merchNotification: LiveData<Boolean> get() = _merchNotification

    private val _allNotifications = MutableLiveData<Boolean>()
    val allNotifications: LiveData<Boolean> get() = _allNotifications

    init {
        _postNotification.value = PrefManager.getPostNotifPref()
        _pollNotification.value = PrefManager.getPollNotifPref()
        _eventNotification.value = PrefManager.getEventNotifPref()
        _merchNotification.value = PrefManager.getMerchNotifPref()

        updateAllNotificationsState()
    }

    fun setPostNotification(isChecked: Boolean) {
        _postNotification.value = isChecked
        PrefManager.setPostNotifPref(isChecked)
        updateAllNotificationsState()
    }

    fun setPollNotification(isChecked: Boolean) {
        _pollNotification.value = isChecked
        PrefManager.setPollNotifPref(isChecked)
        updateAllNotificationsState()
    }

    fun setEventNotification(isChecked: Boolean) {
        _eventNotification.value = isChecked
        PrefManager.setEventNotifPref(isChecked)
        updateAllNotificationsState()
    }

    fun setMerchNotification(isChecked: Boolean) {
        _merchNotification.value = isChecked
        PrefManager.setMerchNotifPref(isChecked)
        updateAllNotificationsState()
    }

    fun setAllNotifications(isChecked: Boolean) {
        _allNotifications.value = isChecked
        setPostNotification(isChecked)
        setPollNotification(isChecked)
        setEventNotification(isChecked)
        setMerchNotification(isChecked)
    }

    private fun updateAllNotificationsState() {
        val allChecked = _postNotification.value == true &&
                _pollNotification.value == true &&
                _eventNotification.value == true &&
                _merchNotification.value == true

        _allNotifications.value = allChecked
    }
}
