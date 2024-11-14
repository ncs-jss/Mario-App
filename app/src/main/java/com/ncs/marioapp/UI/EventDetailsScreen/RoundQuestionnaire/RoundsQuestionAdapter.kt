package com.ncs.marioapp.UI.EventDetailsScreen.RoundQuestionnaire

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ncs.marioapp.Domain.Models.Answer
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Question
import com.ncs.marioapp.UI.EventDetailsScreen.QuestionFragment
import com.ncs.marioapp.UI.EventDetailsScreen.QuestionnaireSummaryFragment

class RoundsQuestionAdapter(
    fragment: Fragment,
    private val onAnswerSubmitted: (Answer) -> Unit,

    ) : FragmentStateAdapter(fragment) {
    private var questions = listOf<Question>()
    private var showSummary = false
    private var answers: List<Answer> = emptyList()

    fun setQuestions(questions: List<Question>) {
        this.questions = questions
        notifyDataSetChanged()
    }

    fun setSummaryPage(answers: List<Answer>) {
        this.answers = answers
        showSummary = true
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = if (showSummary) questions.size + 1 else questions.size

    override fun createFragment(position: Int): Fragment {
        return if (showSummary && position == questions.size) {
            RoundsQuestionnaireSummaryFragment.newInstance(answers, questions)
        } else {
            RoundQuestionFragment.newInstance(questions[position], onAnswerSubmitted)
        }
    }

}
