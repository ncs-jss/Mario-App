package com.ncs.marioapp.UI.AdminScreen.QuestionnaireCreation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncs.marioapp.Domain.Models.Admin.Questionnaire
import com.ncs.marioapp.Domain.Models.Admin.Round
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Repository.EventRepository
import com.ncs.marioapp.Domain.Repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionnaireViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _postQuestionnaireResponse = MutableLiveData<ServerResult<String>>()
    val postQuestionnaireResponse: LiveData<ServerResult<String>> = _postQuestionnaireResponse

    suspend fun postQuestionnaire(questionnaire: Questionnaire) {
        viewModelScope.launch {

            firestoreRepository.postQuestionnaire(questionnaire) {
                when (it) {
                    is ServerResult.Failure -> {
                        _postQuestionnaireResponse.postValue(ServerResult.Failure(it.message))
                    }

                    ServerResult.Progress -> {
                        _postQuestionnaireResponse.postValue(ServerResult.Progress)
                    }

                    is ServerResult.Success -> {
                        _postQuestionnaireResponse.postValue(ServerResult.Success("Successfully added questionnaire.."))
                    }
                }
            }
        }
    }

}