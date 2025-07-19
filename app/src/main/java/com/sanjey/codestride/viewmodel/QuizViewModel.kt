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
import com.sanjey.codestride.data.model.Quiz

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
    private val _quizDetails = MutableStateFlow<Quiz?>(null)
    val quizDetails: StateFlow<Quiz?> = _quizDetails


    fun loadQuestions(roadmapId: String, moduleId: String, quizId: String) {
        Log.d("QUIZ_DEBUG", "Fetching quiz and questions from Firestore")

        // ✅ Fetch full quiz details (including badge)
        firebaseRepository.getQuizDetails(
            roadmapId,
            moduleId,
            quizId,
            onSuccess = { quiz ->
                passingScore = quiz.passingScore
                _quizDetails.value = quiz
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
        Log.d("QUIZ_DEBUG", "ViewModel → Received score = $score, Passing score = $passingScore")

        if (score >= passingScore) {
            Log.d("QUIZ_DEBUG", "Result: PASSED")

            _quizResultState.value = QuizResultState.Passed

            // ✅ Update Firestore roadmap progress
            firebaseRepository.markModuleCompleted(
                roadmapId,
                moduleId,
                onSuccess = { println("Module marked as completed!") },
                onFailure = { e -> println("Failed to update progress: ${e.message}") }
            )
        } else {
            Log.d("QUIZ_DEBUG", "Result: FAILED")
            _quizResultState.value = QuizResultState.Failed
        }
    }

    fun resetQuiz() {
        _quizResultState.value = QuizResultState.None
    }
}
