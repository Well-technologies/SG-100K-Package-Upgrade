package org.singapore.ghru.ui.hlqself.languagelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.R
import org.singapore.ghru.databinding.QuestionnaireItemBinding
import org.singapore.ghru.databinding.QuestionnaireSelfItemBinding
import org.singapore.ghru.ui.common.DataBoundListAdapter
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Questionnaire
import org.singapore.ghru.vo.QuestionnaireSelf


class QuestionnaireListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((QuestionnaireSelf) -> Unit)?
) : DataBoundListAdapter<QuestionnaireSelf, QuestionnaireSelfItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<QuestionnaireSelf>() {
        override fun areItemsTheSame(oldItem: QuestionnaireSelf, newItem: QuestionnaireSelf): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QuestionnaireSelf, newItem: QuestionnaireSelf): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.language == newItem.language
        }
    }
) {

    override fun createBinding(parent: ViewGroup): QuestionnaireSelfItemBinding {
        val binding = DataBindingUtil
            .inflate<QuestionnaireSelfItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.questionnaire_self_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.singleClick { it ->
            binding.questionnaire?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: QuestionnaireSelfItemBinding, item: QuestionnaireSelf) {
        binding.questionnaire = item
    }
}
