package com.example.surveyheartquiztest.RoomDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch

class QuizViewModel(private val repository: QuestionRepository) : ViewModel() {

    private val _questions = MutableLiveData<List<QuestionEntity>>()
    val questions: LiveData<List<QuestionEntity>> = _questions

    fun fetchQuestions() {
        viewModelScope.launch {
            _questions.value = repository.getAllQuestions()
        }
    }

    fun insertQuestions(questions: List<QuestionEntity>) {
        viewModelScope.launch {
            repository.insertQuestions(questions)
        }
    }

    fun clearQuestions() {
        viewModelScope.launch {
            repository.clearQuestions()
        }
    }
}

