package org.singapore.ghru.ui.hlqself.languagelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.QuestionnaireRepository
import org.singapore.ghru.repository.QuestionnaireSelfRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.Questionnaire
import org.singapore.ghru.vo.QuestionnaireSelf
import org.singapore.ghru.vo.Resource
import javax.inject.Inject


class QuestionnaireListViewModel
@Inject constructor(questionnaireSelfRepository: QuestionnaireSelfRepository) : ViewModel() {
    private val _language = MutableLiveData<QuestionnaireId>()

    val languageSelf: LiveData<Resource<List<QuestionnaireSelf>>>? = Transformations
        .switchMap(_language) { input ->
            input.ifExists { language, network ->
                questionnaireSelfRepository.getQuestionnaireSelfList(language = language, network = network)
            }
        }


    fun getQuestionnaire(
        language: String?,
        network: Boolean?
    ) {
        val update =
            QuestionnaireId(language, network)
        if (_language.value == update) {
            return
        }
        _language.value = update
    }


    data class QuestionnaireId(
        val language: String?,
        val network: Boolean?
    ) {
        fun <T> ifExists(f: (String, Boolean) -> LiveData<T>): LiveData<T> {
            return if (language == null || network == null) {
                AbsentLiveData.create()
            } else {
                f(language, network)
            }
        }
    }

    fun retry() {
        _language.value?.let {
            _language.value = it
        }
    }
}
