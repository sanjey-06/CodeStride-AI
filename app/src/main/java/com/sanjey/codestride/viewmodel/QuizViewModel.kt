package com.sanjey.codestride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.data.model.Question
import com.sanjey.codestride.data.model.Quiz
import com.sanjey.codestride.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class QuizResultState {
    None, Passed, Failed
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _questionsState = MutableStateFlow<UiState<List<Question>>>(UiState.Idle)
    val questionsState: StateFlow<UiState<List<Question>>> = _questionsState

    private val _quizDetailsState = MutableStateFlow<UiState<Quiz>>(UiState.Idle)
    val quizDetailsState: StateFlow<UiState<Quiz>> = _quizDetailsState

    private val _quizResultState = MutableStateFlow(QuizResultState.None)
    val quizResultState: StateFlow<QuizResultState> = _quizResultState

    private var passingScore: Int = 0

    // ✅ Internal state for current quiz progress
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _selectedOption = MutableStateFlow<String?>(null)
    val selectedOption: StateFlow<String?> = _selectedOption

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    val currentQuestion: StateFlow<Question?> = MutableStateFlow(null)

    // ✅ Load quiz details and questions
    fun loadQuizData(roadmapId: String, moduleId: String, quizId: String) {
        viewModelScope.launch {
            _quizDetailsState.value = UiState.Loading
            _questionsState.value = UiState.Loading
            try {
                val quiz = firebaseRepository.getQuizDetails(roadmapId, moduleId, quizId)
                if (quiz != null) {
                    passingScore = quiz.passingScore
                    _quizDetailsState.value = UiState.Success(quiz)
                } else {
                    _quizDetailsState.value = UiState.Error("Quiz details not found")
                }

                val questions = firebaseRepository.getQuestionsByQuiz(roadmapId, moduleId, quizId)
                if (questions.isNotEmpty()) {
                    _questionsState.value = UiState.Success(questions)
                    (currentQuestion as MutableStateFlow).value = questions.first()
                } else {
                    _questionsState.value = UiState.Empty
                }
            } catch (e: Exception) {
                _quizDetailsState.value = UiState.Error("Failed to load quiz")
                _questionsState.value = UiState.Error("Failed to load questions")
            }
        }
    }

    fun selectOption(option: String) {
        _selectedOption.value = option
    }

    fun submitCurrentAnswer() {
        val questions = (questionsState.value as? UiState.Success)?.data ?: return
        val currentQ = questions.getOrNull(_currentIndex.value) ?: return

        if (_selectedOption.value == currentQ.correctAnswer) {
            _score.value += 1
        }

        if (_currentIndex.value < questions.size - 1) {
            _currentIndex.value += 1
            (currentQuestion as MutableStateFlow).value = questions[_currentIndex.value]
            _selectedOption.value = null
        } else {
            if (_score.value >= passingScore) {
                _quizResultState.value = QuizResultState.Passed
            } else {
                _quizResultState.value = QuizResultState.Failed
            }
        }
    }

    fun resetQuiz() {
        _currentIndex.value = 0
        _score.value = 0
        _selectedOption.value = null
        _quizResultState.value = QuizResultState.None
    }
}
