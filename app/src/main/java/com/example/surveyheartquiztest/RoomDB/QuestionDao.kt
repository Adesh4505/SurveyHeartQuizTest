package com.example.surveyheartquiztest.RoomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuestionDao {

    @Insert
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<QuestionEntity>

    @Query("DELETE FROM questions")
    suspend fun clearQuestions()
}
