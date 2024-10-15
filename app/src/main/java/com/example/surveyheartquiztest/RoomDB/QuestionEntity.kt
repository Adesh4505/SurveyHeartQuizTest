package com.example.surveyheartquiztest.RoomDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "question")
    val question: String,
    @ColumnInfo(name = "correct_answer")
    val correctAnswer: String,
    @ColumnInfo(name = "incorrect_answers")
    val incorrectAnswers: String
)
