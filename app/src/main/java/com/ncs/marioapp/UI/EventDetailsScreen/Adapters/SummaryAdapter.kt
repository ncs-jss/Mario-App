package com.ncs.marioapp.UI.EventDetailsScreen.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.Domain.Models.Answer
import com.ncs.marioapp.Domain.Models.Events.EventDetails.Question
import com.ncs.marioapp.databinding.ItemSummaryBinding

class SummaryAdapter(
    private val answers: List<Answer>,
    private val questions: List<Question>
) : RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>() {

    inner class SummaryViewHolder(private val binding: ItemSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: Answer, question: Question,position: Int) {
            binding.tvQuestion.text = "Q${position+1}: ${question.question}"
            binding.tvAnswer.text = "A: ${answer.answer}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val binding = ItemSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SummaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        holder.bind(answers[position], questions[position],position)
    }

    override fun getItemCount(): Int = answers.size
}
