package com.sanjey.codestride.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.sanjey.codestride.data.model.Question
import com.sanjey.codestride.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.compose.runtime.State

enum class QuizResultState {
    None,
    Passed,
    Failed
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _questions = mutableStateOf<List<Question>>(emptyList())
    val questions: State<List<Question>> = _questions

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // ✅ Quiz Result State
    private val _quizResultState = MutableStateFlow(QuizResultState.None)
    val quizResultState: StateFlow<QuizResultState> = _quizResultState

    // ✅ Passing Score (temp static; later fetch from Firestore)
    var passingScore: Int = 3
        private set

    fun loadQuestions(roadmapId: String, moduleId: String, quizId: String) {
        firebaseRepository.getQuestionsByQuiz(
            roadmapId, moduleId, quizId,
            onSuccess = { _questions.value = it },
            onFailure = { _errorMessage.value = it.message }
        )
    }

    // ✅ Called when quiz ends
    fun onQuizCompleted(score: Int, passingScore: Int, roadmapId: String, moduleId: String) {
        if (score >= passingScore) {
            _quizResultState.value = QuizResultState.Passed

            // TODO: Update Firestore roadmap progress
            // firebaseRepository.markModuleCompleted(roadmapId, moduleId)
        } else {
            _quizResultState.value = QuizResultState.Failed
        }
    }

    // ✅ Reset quiz state for retry
    fun resetQuiz() {
        _quizResultState.value = QuizResultState.None
    }
}
