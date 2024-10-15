package com.example.surveyheartquiztest.RoomDB


class QuestionRepository(private val questionDao: QuestionDao) {

    suspend fun insertQuestions(questions: List<QuestionEntity>) {
        questionDao.insertQuestions(questions)
    }

    suspend fun getAllQuestions(): List<QuestionEntity> {
        return questionDao.getAllQuestions()
    }

    suspend fun clearQuestions() {
        questionDao.clearQuestions()
    }
}
