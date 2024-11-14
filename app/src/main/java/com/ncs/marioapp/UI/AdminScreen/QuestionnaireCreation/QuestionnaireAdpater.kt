package com.ncs.marioapp.UI.AdminScreen.RecordCreation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.Domain.Models.Admin.FormItem
import com.ncs.marioapp.Domain.Models.Admin.FormType
import com.ncs.marioapp.R
import androidx.recyclerview.widget.ItemTouchHelper
import kotlin.math.abs

class QuestionnaireAdapter(
    private val context: Context,
    val items: MutableList<FormItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_EDIT_TEXT = 0
        private const val TYPE_RADIO_BUTTON = 5
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].type) {
            FormType.EDIT_TEXT -> TYPE_EDIT_TEXT
            FormType.RADIO -> TYPE_RADIO_BUTTON
            FormType.DROPDOWN -> TODO()
            FormType.DATE_PICKER -> TODO()
            FormType.SEPARATOR -> TODO()
            FormType.BUTTON -> TODO()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_EDIT_TEXT -> EditTextViewHolder(
                inflater.inflate(R.layout.questionare_et_item, parent, false)
            )
            TYPE_RADIO_BUTTON -> RadioButtonViewHolder(
                inflater.inflate(R.layout.questionnare_radio_item, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EditTextViewHolder -> holder.bind(items[position], position)
            is RadioButtonViewHolder -> holder.bind(items[position], position)
        }
    }

    override fun getItemCount(): Int = items.size

    // ViewHolder for EditText
    inner class EditTextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ques_num: TextView = view.findViewById(R.id.ques_num_et)
        private val ques_title: TextView = view.findViewById(R.id.ques_title_et)

        fun bind(item: FormItem, position: Int) {
            ques_num.text = "Ques #${position + 1}" // Recalculate based on position
            ques_title.text = item.title
        }
    }

    // ViewHolder for RadioButton
    inner class RadioButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ques_num: TextView = view.findViewById(R.id.ques_num)
        private val ques_title: TextView = view.findViewById(R.id.ques_title)
        private val ques_options: TextView = view.findViewById(R.id.ques_options)

        fun bind(item: FormItem, position: Int) {
            ques_num.text = "Ques #${position + 1}" // Reset based on position
            ques_title.text = item.title
            ques_options?.text = item.options?.mapIndexed { index, option ->
                "${index + 1}. $option"
            }?.joinToString(separator = "\n") ?: "" // Reset options or clear
        }

    }

    // Helper function to update the entire list
    fun updateList(newList: MutableList<FormItem>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
class DragSwipeCallback(
    private val adapter: QuestionnaireAdapter
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = source.adapterPosition
        val toPosition = target.adapterPosition

        // Swap items in the list
        adapter.items.add(toPosition, adapter.items.removeAt(fromPosition))
        adapter.notifyItemMoved(fromPosition, toPosition)

        // Update the affected range
        adapter.notifyItemRangeChanged(minOf(fromPosition, toPosition), abs(fromPosition - toPosition) + 1)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        // Remove the item and notify remaining items
        adapter.items.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, adapter.items.size - position)
    }


    override fun isLongPressDragEnabled(): Boolean = true
    override fun isItemViewSwipeEnabled(): Boolean = true
}
