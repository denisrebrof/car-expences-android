package com.upreality.car.expenses.presentation.list

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upreality.car.expenses.databinding.ExpenseListDateSeparatorItemBinding
import com.upreality.car.expenses.databinding.ExpenseListItemBinding
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.presentation.list.ExpensesListAdapter.ExpenseListModel
import com.upreality.car.expenses.presentation.list.ExpensesListAdapter.ExpenseListModel.DateSeparator
import com.upreality.car.expenses.presentation.list.ExpensesListAdapter.ExpenseListModel.ExpenseModel
import com.upreality.car.expenses.presentation.list.ExpensesListAdapter.ViewHolder
import com.upreality.car.expenses.presentation.list.ExpensesListAdapter.ViewHolder.DateSeparatorHolder
import com.upreality.car.expenses.presentation.list.ExpensesListAdapter.ViewHolder.ExpenseItemHolder
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@Singleton
class ExpensesListAdapter(
    private val provider: ExpenseTypeDataProvider,
    private val onItemClicked: (Expense) -> Unit
) : PagingDataAdapter<ExpenseListModel, ViewHolder>(DiffCallback) {

    companion object {
        private const val EXPENSE = 1
        private const val SEPARATOR = 2
    }

    //    private val differ = AsyncPagingDataDiffer(DiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            EXPENSE -> ExpenseListItemBinding
                .inflate(inflater, parent, false)
                .let(::ExpenseItemHolder)
            else -> ExpenseListDateSeparatorItemBinding
                .inflate(inflater, parent, false)
                .let(::DateSeparatorHolder)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DateSeparator -> SEPARATOR
            is ExpenseModel -> EXPENSE
            null -> -1
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is DateSeparatorHolder -> (getItem(position) as? DateSeparator)?.date?.let {
                holder.bindDateItem(
                    it
                )
            }
            is ExpenseItemHolder -> holder.bindExpenseItem(position)
        }
    }

    private fun ExpenseItemHolder.bindExpenseItem(pos: Int) {
        val item = (getItem(position) as? ExpenseModel)?.expense ?: return
        binding.apply {
            expenseTypeIcon.background = provider.getIcon(item)
            expenseTypeCaption.text = provider.getTypeLabel(item)
            expenseDetailsShort.text = provider.getDetails(item)
            //Debug, replace with cost
            expenseCost.text = item.cost.toInt().toString()
            root.setOnClickListener {
                onItemClicked(item)
            }
        }
    }

    private fun DateSeparatorHolder.bindDateItem(date: Date) {
        binding.apply {
            dateCaption.text = date.getText()
        }
    }

    fun getItemByPosition(position: Int) = (getItem(position) as? ExpenseModel)?.expense

    private fun Date.getText(): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(this)
    }

    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        class ExpenseItemHolder(val binding: ExpenseListItemBinding) : ViewHolder(binding.root)
        class DateSeparatorHolder(
            val binding: ExpenseListDateSeparatorItemBinding
        ) : ViewHolder(binding.root)
    }

    sealed class ExpenseListModel {
        data class DateSeparator(val date: Date) : ExpenseListModel()
        data class ExpenseModel(val expense: Expense) : ExpenseListModel()
    }

    object DiffCallback : DiffUtil.ItemCallback<ExpenseListModel>() {

        override fun areItemsTheSame(
            oldItem: ExpenseListModel,
            newItem: ExpenseListModel
        ): Boolean {
            return when {
                oldItem is DateSeparator && newItem is DateSeparator -> oldItem.date == newItem.date
                oldItem is ExpenseModel && newItem is ExpenseModel -> oldItem.expense.id == newItem.expense.id
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: ExpenseListModel,
            newItem: ExpenseListModel
        ): Boolean {
            return when {
                oldItem is DateSeparator && newItem is DateSeparator -> oldItem.date == newItem.date
                oldItem is ExpenseModel && newItem is ExpenseModel -> {
                    oldItem.expense.date == newItem.expense.date
                            && oldItem.expense::class == newItem.expense::class
                }
                else -> false
            }
        }
    }

    interface ExpenseTypeDataProvider {
        fun getIcon(expense: Expense): Drawable?
        fun getTypeLabel(expense: Expense): String?
        fun getDetails(expense: Expense): String?
    }
}