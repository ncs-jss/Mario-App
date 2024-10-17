package com.ncs.mario.Domain.Models.EVENTS

data class PollItem(
    val question: String,  // The poll question
    val options: List<Option>  // A list of options for the poll
) {
    data class Option(
        val text: String,   // The option text
        var votes: Int      // Number of votes for this option (modifiable)
    )
}
