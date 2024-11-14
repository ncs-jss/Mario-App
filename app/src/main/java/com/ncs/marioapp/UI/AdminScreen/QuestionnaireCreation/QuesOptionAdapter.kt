package com.ncs.marioapp.UI.AdminScreen.QuestionnaireCreation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.R

class QuesOptionAdapter(private val options: MutableList<String>) : RecyclerView.Adapter<QuesOptionAdapter.OptionsViewHolder>() {

    inner class OptionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val optionEditText: EditText = view.findViewById(R.id.edit_text)
        val removeButton: ImageView = view.findViewById(R.id.remove_button)
        val optionNum:TextView=view.findViewById(R.id.option_num)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.radio_ques_item, parent, false)
        return OptionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
        holder.optionEditText.setText(options[position])
        holder.optionNum.text = "Option ${position + 1}"

        holder.optionEditText.addTextChangedListener {
            options[holder.bindingAdapterPosition] = it.toString()
        }

        holder.removeButton.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            options.removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos, options.size)
        }
    }

    fun getOptions(): List<String> = options


    override fun getItemCount(): Int = options.size
}