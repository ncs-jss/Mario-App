package com.ncs.marioapp.UI.AdminScreen.RecordCreation

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ncs.marioapp.Domain.Models.Admin.FormItem
import com.ncs.marioapp.Domain.Models.Admin.FormType
import com.ncs.marioapp.R
import java.util.Calendar

class FormAdapter(
    private val context: Context,
    private val items: MutableList<FormItem>,
    private val onButtonClick: (item: FormItem, items: List<FormItem>) -> Unit,
    private val onRadioButtonClick: (position: Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_EDIT_TEXT = 0
        private const val TYPE_DROPDOWN = 1
        private const val TYPE_DATE_PICKER = 2
        private const val TYPE_SEPARATOR = 3
        private const val TYPE_BUTTON = 4
        private const val TYPE_RADIO_BUTTON = 5
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].type) {
            FormType.EDIT_TEXT -> TYPE_EDIT_TEXT
            FormType.DROPDOWN -> TYPE_DROPDOWN
            FormType.DATE_PICKER -> TYPE_DATE_PICKER
            FormType.SEPARATOR -> TYPE_SEPARATOR
            FormType.BUTTON -> TYPE_BUTTON
            FormType.RADIO -> TYPE_RADIO_BUTTON
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_EDIT_TEXT -> EditTextViewHolder(
                inflater.inflate(
                    R.layout.item_edit_text,
                    parent,
                    false
                )
            )

            TYPE_DROPDOWN -> DropdownViewHolder(
                inflater.inflate(
                    R.layout.item_dropdown,
                    parent,
                    false
                )
            )

            TYPE_DATE_PICKER -> DatePickerViewHolder(
                inflater.inflate(
                    R.layout.item_date_picker,
                    parent,
                    false
                )
            )

            TYPE_SEPARATOR -> SeparatorViewHolder(
                inflater.inflate(
                    R.layout.seperator_title,
                    parent,
                    false
                )
            )

            TYPE_BUTTON -> ButtonViewHolder(inflater.inflate(R.layout.item_button, parent, false))
            TYPE_RADIO_BUTTON -> RadioButtonViewHolder(
                inflater.inflate(
                    R.layout.radio_item,
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EditTextViewHolder -> holder.bind(items[position])
            is DropdownViewHolder -> holder.bind(items[position])
            is DatePickerViewHolder -> holder.bind(items[position])
            is SeparatorViewHolder -> holder.bind(items[position])
            is ButtonViewHolder -> holder.bind(items[position])
            is RadioButtonViewHolder -> holder.bind(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    inner class SeparatorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.separator_title)
        fun bind(item: FormItem) {
            title.text = item.title
        }
    }

    // ViewHolder for EditText
    inner class EditTextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val editText: EditText = view.findViewById(R.id.edit_text)
        fun bind(item: FormItem) {

            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    item.value = editText.text.toString()
                }
            }

            title.text = item.title
            editText.setText(item.value)
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    item.value = editText.text.toString()
                    updateList(item)
                }
            }
        }
    }

    inner class RadioButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val radioButton: RadioButton = view.findViewById(R.id.radio_button)

        fun bind(item: FormItem) {
            title.text = item.title
            radioButton.isChecked = item.value == "True"
            radioButton.setOnClickListener {
                onRadioButtonClick(bindingAdapterPosition)
            }
        }
    }

    inner class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val button: Button = view.findViewById(R.id.form_button)

        fun bind(item: FormItem) {
            button.text = item.title
            button.setOnClickListener {
                onButtonClick(item, items)
            }
        }
    }

    // ViewHolder for Dropdown
    inner class DropdownViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val spinner: Spinner = view.findViewById(R.id.dropdown)

        fun bind(item: FormItem) {
            title.text = item.title
            val options = item.options
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            if (options.indexOf(item.value) != -1) {
                spinner.setSelection(options.indexOf(item.value))
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    item.value = options[position]
                    updateList(item)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }


    private fun updateList(newFormItem: FormItem) {
        items.forEach {
            if (it.title == newFormItem.title) {
                it.value = newFormItem.value
            }
        }
    }

    // ViewHolder for DatePicker
    inner class DatePickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val dateText: TextView = view.findViewById(R.id.date_text)

        fun bind(item: FormItem) {
            title.text = item.title
            dateText.text = item.value

            dateText.setOnClickListener {
                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val date = "$dayOfMonth/${month + 1}/$year"
                        item.value = date
                        updateList(item)
                        dateText.text = date
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            }
        }
    }
}
