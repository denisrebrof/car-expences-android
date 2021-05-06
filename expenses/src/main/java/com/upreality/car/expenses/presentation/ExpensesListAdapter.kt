package com.upreality.car.expenses.presentation

import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.upreality.car.expenses.databinding.ExpenseListItemBinding
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.presentation.ExpensesListAdapter.ViewHolder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpensesListAdapter(
    private val provider: ExpenseTypeDataProvider
) : PagingDataAdapter<Expense, ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context)
            .let { ExpenseListItemBinding.inflate(it, parent, false) }
            .let(::ViewHolder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.binding.apply {
            expenseTypeIcon.background = provider.getIcon(item)
            expenseTypeCaption.text = provider.getTypeLabel(item)
            expenseDetailsShort.text = provider.getDetails(item)
            //Debug, replace with cost
            expenseCost.text = item.id.toString()
        }
    }

    inner class ViewHolder(
        val binding: ExpenseListItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    object DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }
    }

    interface ExpenseTypeDataProvider{
        fun getIcon(expense: Expense): Drawable?
        fun getTypeLabel(expense: Expense): String?
        fun getDetails(expense: Expense): String?
    }
}