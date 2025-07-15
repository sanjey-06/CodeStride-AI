package com.sanjey.codestride.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.sanjey.codestride.data.model.Question
import com.sanjey.codestride.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State


@HiltViewModel
class QuizViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _questions = mutableStateOf<List<Question>>(emptyList())
    val questions: State<List<Question>> = _questions

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun loadQuestions(roadmapId: String, moduleId: String, quizId: String) {
        firebaseRepository.getQuestionsByQuiz(
            roadmapId, moduleId, quizId,
            onSuccess = { _questions.value = it },
            onFailure = { _errorMessage.value = it.message }
        )
    }
}
