package org.singapore.ghru.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.R
import org.singapore.ghru.databinding.AllStationsItemBinding
import org.singapore.ghru.databinding.NghruItemBinding
import org.singapore.ghru.ui.common.DataBoundListAdapter
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.HomeItem


class AllStationsAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((HomeItem) -> Unit)?
) : DataBoundListAdapter<HomeItem, AllStationsItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<HomeItem>() {
        override fun areItemsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.name == newItem.name
        }
    }
) {

    override fun createBinding(parent: ViewGroup): AllStationsItemBinding {
        val binding = DataBindingUtil
            .inflate<AllStationsItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.all_stations_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.singleClick {
            binding.homeItem?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: AllStationsItemBinding, item: HomeItem) {
        binding.homeItem = item
    }
}
