package com.upreality.car.expenses.presentation

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.upreality.car.expenses.databinding.ExpenseListItemBinding
import com.upreality.car.expenses.domain.model.expence.Expense
import com.upreality.car.expenses.presentation.ExpensesListAdapter.ViewHolder
import javax.inject.Singleton

@Singleton
class ExpensesListAdapter(
    private val provider: ExpenseTypeDataProvider,
    private val onItemClicked: (Expense) -> Unit
) : PagingDataAdapter<Expense, ViewHolder>(DiffCallback) {

    //    private val differ = AsyncPagingDataDiffer(DiffCallback)

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
            root.setOnClickListener {
                onItemClicked(item)
            }
        }
    }

    fun getItemByPosition(position: Int) = getItem(position)

    inner class ViewHolder(
        val binding: ExpenseListItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    object DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
//            val same = oldItem == newItem
            val same = oldItem.date == newItem.date && oldItem::class == newItem::class
            Log.d("Same", "$same")
            return same
        }
    }

    interface ExpenseTypeDataProvider {
        fun getIcon(expense: Expense): Drawable?
        fun getTypeLabel(expense: Expense): String?
        fun getDetails(expense: Expense): String?
    }
}