package org.singapore.ghru.ui.questionnaire.languagelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.R
import org.singapore.ghru.databinding.QuestionnaireItemBinding
import org.singapore.ghru.ui.common.DataBoundListAdapter
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Questionnaire


class QuestionnaireListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((Questionnaire) -> Unit)?
) : DataBoundListAdapter<Questionnaire, QuestionnaireItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Questionnaire>() {
        override fun areItemsTheSame(oldItem: Questionnaire, newItem: Questionnaire): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Questionnaire, newItem: Questionnaire): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.language == newItem.language
        }
    }
) {

    override fun createBinding(parent: ViewGroup): QuestionnaireItemBinding {
        val binding = DataBindingUtil
            .inflate<QuestionnaireItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.questionnaire_item,
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

    override fun bind(binding: QuestionnaireItemBinding, item: Questionnaire) {
        binding.questionnaire = item
    }
}
