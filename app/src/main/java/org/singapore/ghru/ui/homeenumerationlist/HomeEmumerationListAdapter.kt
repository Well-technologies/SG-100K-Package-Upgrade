package org.singapore.ghru.ui.homeenumerationlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.R
import org.singapore.ghru.databinding.HomeEmumerationListItemBinding
import org.singapore.ghru.ui.common.DataBoundListAdapter
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.HomeEmumerationListItem


class HomeEmumerationListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((HomeEmumerationListItem) -> Unit)?
) : DataBoundListAdapter<HomeEmumerationListItem, HomeEmumerationListItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<HomeEmumerationListItem>() {
        override fun areItemsTheSame(oldItem: HomeEmumerationListItem, newItem: HomeEmumerationListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HomeEmumerationListItem, newItem: HomeEmumerationListItem): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.name == newItem.name
        }
    }
) {

    override fun createBinding(parent: ViewGroup): HomeEmumerationListItemBinding {
        val binding = DataBindingUtil
            .inflate<HomeEmumerationListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.home_emumeration_list_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.singleClick {
            binding.homeEmumerationListItem?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: HomeEmumerationListItemBinding, item: HomeEmumerationListItem) {
        binding.homeEmumerationListItem = item
    }
}
