package com.example.surveyheartquiztest.RoomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface QuestionDao {

    @Insert
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Transaction
    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<QuestionEntity>

    @Query("SELECT DISTINCT question FROM questions")
    fun getUniqueQuestions(): List<String>

    @Query("DELETE FROM questions")
    suspend fun clearQuestions()
}
