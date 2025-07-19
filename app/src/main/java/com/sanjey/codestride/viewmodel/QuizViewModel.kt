package com.sanjey.codestride.viewmodel

import android.util.Log
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

    private val _quizResultState = MutableStateFlow(QuizResultState.None)
    val quizResultState: StateFlow<QuizResultState> = _quizResultState

    var passingScore: Int = 3
        private set

    fun loadQuestions(roadmapId: String, moduleId: String, quizId: String) {
        Log.d("QUIZ_DEBUG", "Fetching from: roadmaps/$roadmapId/modules/$moduleId/quizzes/$quizId/questions")

        // ✅ Load quiz details first
        firebaseRepository.getQuizDetails(
            roadmapId,
            moduleId,
            quizId,
            onSuccess = { fetchedScore ->
                passingScore = fetchedScore
            },
            onFailure = {
                _errorMessage.value = it.message
            }
        )

        // ✅ Load questions
        firebaseRepository.getQuestionsByQuiz(
            roadmapId,
            moduleId,
            quizId,
            onSuccess = { _questions.value = it },
            onFailure = { _errorMessage.value = it.message }
        )
    }

    fun onQuizCompleted(score: Int, roadmapId: String, moduleId: String) {
        if (score >= passingScore) {
            _quizResultState.value = QuizResultState.Passed

            // ✅ Update Firestore roadmap progress
            firebaseRepository.markModuleCompleted(
                roadmapId,
                moduleId,
                onSuccess = { println("Module marked as completed!") },
                onFailure = { e -> println("Failed to update progress: ${e.message}") }
            )
        } else {
            _quizResultState.value = QuizResultState.Failed
        }
    }

    fun resetQuiz() {
        _quizResultState.value = QuizResultState.None
    }
}
