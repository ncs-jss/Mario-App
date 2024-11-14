package com.ncs.marioapp.Domain.HelperClasses

import com.ncs.marioapp.Domain.Models.Admin.Round

object Tester {

    fun getRounds(): List<Round> {
        return listOf(
            Round(
                description = "Teams need to submit a solution that addresses the problem statement. The solution should be a high-level overview with major milestones.",
                eventID = "event123",
                roundTitle = "Round 1: Executive Summary Submission",
                questionnaireID = "questionnaire123",
                requireSubmission = true,
                roundID = "round1",
                timeLine = mapOf(
                    "startCollege" to 1699000000,
                    "endCollege" to 1699000000
                ),
                venue = "Online",
                isLive = true,
                submissionButtonText = "Submit Idea",

                ),

            Round(
                description = "Teams are required to present a working prototype of their solution, demonstrating its key features and functionalities.",
                eventID = "event123",
                roundTitle = "Round 2: Prototype Presentation",
                questionnaireID = "questionnaire124",
                requireSubmission = true,
                roundID = "round2",
                timeLine = mapOf(
                    "startCollege" to 1699000000,
                    "endCollege" to 1699150000
                ),
                venue = "Online",
                isLive = false,
                submissionButtonText = "Present Prototype",
            ),

            Round(
                description = "Teams must submit their final solution along with a detailed report and a live demo of the product.",
                eventID = "event123",
                roundTitle = "Round 3: Final Submission",
                questionnaireID = "questionnaire125",
                requireSubmission = true,
                roundID = "round3",
                timeLine = mapOf(
                    "startCollege" to 1699000000,
                    "endCollege" to 1699000000
                ),
                venue = "On-site",
                isLive = false,
                submissionButtonText = "Submit Final",
            )
        )
    }

}