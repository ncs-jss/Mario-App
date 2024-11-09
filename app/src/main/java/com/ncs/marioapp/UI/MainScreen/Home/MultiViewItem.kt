package com.ncs.marioapp.UI.MainScreen.Home

sealed class MultiViewItem {
    data class poll(
        val _id: String,
        val question: String,
        val options:List<Option>

    ):MultiViewItem()

    data class PostItem(
        val _id:String,
        val imageUrl: String,
        val registration_link:String,
    ) : MultiViewItem()

    data class Option (
        val _id:String,
        val title:String,
        val votes:Int
    )
}